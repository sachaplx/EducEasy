package com.educeasy.core.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.educeasy.core.dto.AuthInfo.AuthResponse;
import com.educeasy.core.dto.AuthInfo.ChangePasswordRequest;
import com.educeasy.core.dto.AuthInfo.ChangePasswordResponse;
import com.educeasy.core.dto.AuthInfo.ForgotPasswordRequest;
import com.educeasy.core.dto.AuthInfo.ForgotPasswordResponse;
import com.educeasy.core.dto.AuthInfo.LoginRequest;
import com.educeasy.core.dto.AuthInfo.ProfileResponse;
import com.educeasy.core.dto.AuthInfo.RegisterRequest;
import com.educeasy.core.dto.AuthInfo.RegisterResponse;
import com.educeasy.core.dto.AuthInfo.ResetPasswordRequest;
import com.educeasy.core.dto.AuthInfo.ResetPasswordResponse;
import com.educeasy.core.dto.AuthInfo.ResetPasswordValidateRequest;
import com.educeasy.core.dto.AuthInfo.ResetPasswordValidateResponse;
import com.educeasy.core.dto.AuthInfo.UpdateEmailRequest;
import com.educeasy.core.dto.AuthInfo.UpdateEmailResponse;
import com.educeasy.core.entity.PasswordResetToken;
import com.educeasy.core.entity.Principal;
import com.educeasy.core.entity.Professor;
import com.educeasy.core.entity.Role;
import com.educeasy.core.entity.User;
import com.educeasy.core.entity.VerificationToken;
import com.educeasy.core.repository.ClassroomRepository;
import com.educeasy.core.repository.PasswordResetTokenRepository;
import com.educeasy.core.repository.PrincipalRepository;
import com.educeasy.core.repository.ProfessorRepository;
import com.educeasy.core.repository.UserRepository;
import com.educeasy.core.repository.VerificationTokenRepository;
import com.educeasy.core.security.JwtService;

@Service
public class AuthService {

	private final ClassroomRepository classroomRepository;

	private final AuthenticationManager authManager;

	private final UserDetailsService userDetailsService;

	private final UserRepository userRepository;

	private final ProfessorRepository professorRepository;

	private final PrincipalRepository principalRepository;

	private final JwtService jwtService;

	private final PasswordEncoder passwordEncoder;

	private final VerificationTokenRepository verificationTokenRepository;

	private final PasswordResetTokenRepository passwordResetTokenRepository;

	private final EmailService emailService;

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest req) {
		Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.identifier(), req.password()));

		UserDetails principal = (UserDetails) auth.getPrincipal();
		String token = jwtService.generateToken(principal);

		User user = userRepository.findByUsernameOrEmailIgnoreCase(req.identifier(), req.identifier()).orElseGet(() -> userRepository.findByUsernameIgnoreCase(principal.getUsername()).orElseThrow());

		return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
	}

	@Transactional
	public RegisterResponse register(RegisterRequest req) {
		String email = req.email().trim();
		String username = req.username().trim();

		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new IllegalArgumentException("Email already exists");
		}

		if (userRepository.existsByUsernameIgnoreCase(username)) {
			throw new IllegalArgumentException("Username already exists");
		}

		Role role;
		try {
			role = Role.valueOf(req.role().trim().toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid role");
		}

		if (req.firstName() == null || req.firstName().isBlank() || req.lastName() == null || req.lastName().isBlank()) {
			throw new IllegalArgumentException("FirstName and LastName are required");
		}

		User u = new User();
		u.setEmail(email);
		u.setUsername(username);
		u.setPassword(passwordEncoder.encode(req.password()));
		u.setRole(role);
		u.setActif(false);
		u.setCreatedAt(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		userRepository.save(u);

		if (role == Role.PRINCIPAL) {
			if (!principalRepository.existsByUserId(u.getId())) {
				Principal p = new Principal();
				p.setUser(u);
				p.setNom(req.lastName());
				p.setPrenom(req.firstName());
				principalRepository.save(p);
			}
		}

		if (role == Role.TEACHER) {
			Professor p = new Professor();
			p.setUser(u);
			p.setPrenom(req.firstName());
			p.setNom(req.lastName());
			professorRepository.save(p);
		}

		String token = UUID.randomUUID().toString();

		VerificationToken vt = VerificationToken.create(u, token, 30);
		verificationTokenRepository.save(vt);

		emailService.sendEmailConfirmation(u.getEmail(), token);

		return new RegisterResponse(u.getId(), u.getUsername(), u.getRole().name(), "Compte créé. Vérifiez vos e-mails pour l'activer.");
	}

	@Transactional
	public AuthResponse confirm(String token) {
		VerificationToken v = verificationTokenRepository.findByToken(token).orElseThrow(() -> new IllegalArgumentException("Invalid token."));

		if (v.isExpired()) {
			throw new IllegalArgumentException("Expired token.");
		}

		User user = v.getUser();
		if (!user.isActif()) {
			user.setActif(true);
			userRepository.save(user);
		}

		v.setUsed(true);
		verificationTokenRepository.save(v);

		UserDetails details = userDetailsService.loadUserByUsername(user.getUsername());
		String jwt = jwtService.generateToken(details);

		return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getRole().name());
	}

	@Transactional
	public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest req) {
		String identifier = req.identifier().trim();

		var userOpt = userRepository.findByEmailIgnoreCase(identifier);
		if (userOpt.isEmpty()) {
			return new ForgotPasswordResponse("Si un compte existe avec cet identifiant, un e-mail de réinitialisation a été envoyé.");
		}

		User user = userOpt.get();

		if (!user.isActif()) {
			return new ForgotPasswordResponse("Si un compte existe avec cet identifiant, un e-mail de réinitialisation a été envoyé.");
		}

		passwordResetTokenRepository.deleteByUser(user);

		String token = UUID.randomUUID().toString();
		PasswordResetToken prt = PasswordResetToken.create(user, token, 30);

		passwordResetTokenRepository.save(prt);

		emailService.sendPasswordResetEmail(user.getEmail(), token);

		return new ForgotPasswordResponse("Si un compte existe avec cet identifiant, un e-mail de réinitialisation a été envoyé.");
	}

	@Transactional
	public ResetPasswordResponse resetPassword(ResetPasswordRequest req) {
		PasswordResetToken prt = getValidPasswordResetToken(req.token());

		User user = prt.getUser();
		user.setPassword(passwordEncoder.encode(req.newPassword()));
		userRepository.save(user);

		prt.setUsed(true);
		passwordResetTokenRepository.save(prt);

		return new ResetPasswordResponse("Mot de passe réinitialisé avec succès.");
	}

	@Transactional(readOnly = true)
	public ResetPasswordValidateResponse validateResetPasswordToken(ResetPasswordValidateRequest req) {
		getValidPasswordResetToken(req.token());
		return new ResetPasswordValidateResponse(true, "Valid token");
	}

	@Transactional(readOnly = true)
	public boolean isTeacherOfClassroom(Long classroomId, String username) {
		return classroomRepository.findById(classroomId).map(c -> c.getMaitre() != null && username.equals(c.getMaitre().getUser().getUsername())).orElse(false);
	}

	@Transactional(readOnly = true)
	public ProfileResponse getProfile() {
		User u = getCurrentUser();
		return new ProfileResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name(), null, null);
	}

	@Transactional
	public UpdateEmailResponse updateEmail(UpdateEmailRequest req) {
		User u = getCurrentUser();

		if (!passwordEncoder.matches(req.currentPassword(), u.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe incorrect");
		}

		String oldEmail = u.getEmail();
		String newEmail = req.email().trim();

		if (newEmail.equals(oldEmail)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette adresse est déjà utilisée par votre compte");
		}

		if (userRepository.existsByEmailIgnoreCase(newEmail) && !newEmail.equalsIgnoreCase(u.getEmail())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet e-mail est déjà utilisé.");
		}

		u.setEmail(newEmail);
		userRepository.save(u);

		emailService.sendEmailChangeNotifications(oldEmail, newEmail, u.getUsername());

		return new UpdateEmailResponse(u.getEmail(), "Adresse e-mail mise à jour avec succès.");
	}

	@Transactional
	public ChangePasswordResponse changePassword(ChangePasswordRequest req) {
		User u = getCurrentUser();

		if (!passwordEncoder.matches(req.currentPassword(), u.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe actuel incorrect");
		}

		u.setPassword(passwordEncoder.encode(req.newPassword()));
		userRepository.save(u);

		return new ChangePasswordResponse("Mot de passe mis à jour avec succès.");
	}

	private PasswordResetToken getValidPasswordResetToken(String token) {
		PasswordResetToken prt = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token."));

		if (prt.isUsed()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Used token.");
		}

		if (prt.isExpired()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Expired token.");
		}

		return prt;
	}

	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof UserDetails principal)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthentified");
		}
		String username = principal.getUsername();
		return userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Could not find user"));
	}

	public AuthService(ClassroomRepository classroomRepository, AuthenticationManager authManager, UserDetailsService uds, UserRepository userRepository, ProfessorRepository professorRepository, JwtService jwtService, PasswordEncoder passwordEncoder, PrincipalRepository principalRepository, VerificationTokenRepository verificationTokenRepository, EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository) {
		this.classroomRepository = classroomRepository;
		this.authManager = authManager;
		this.userDetailsService = uds;
		this.userRepository = userRepository;
		this.professorRepository = professorRepository;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
		this.principalRepository = principalRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.emailService = emailService;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
	}
}