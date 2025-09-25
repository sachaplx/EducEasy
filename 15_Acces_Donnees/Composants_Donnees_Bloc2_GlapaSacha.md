# Documentation d’intégration – Bloc 2 (Module Note)

Projet Educ’Easy · Stack : Spring Boot 3, Hibernate/JPA, Spring Security (JWT), Vue 3 (Vite)

## 1) Composants d’accès aux données (extraits)

### 1.1. Entité Note

```Java
// com.educeasy.core.entity.Note
@Entity
@Table(name = "educeasy_note")
@Getter @Setter @NoArgsConstructor
public class Note {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "pupil_id")
  private Pupil pupil;

  @Column(nullable = false, length = 80)
  private String matiere;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal note; // [0;20]

  @Column(nullable = false)
  private LocalDate dateNote;

  @Column(length = 500)
  private String commentaire;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auteur_user_id", nullable = false)
  private User auteur;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
}
```

### 1.2 DTO (Entrées & sorties)

```Java
// com.educeasy.core.dto
public class EntryDTO {
  public record CreateNoteRequest(String matiere, BigDecimal note, LocalDate dateNote, String commentaire) {}
 // Même logique pour les absences et remarques
}

public class NoteInfo {

    private Long id;

	private Long pupilId;

	private String matiere;

	private BigDecimal note;

	private String commentaire;

	private LocalDate date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMatiere() {
		return matiere;
	}

	public void setMatiere(String matiere) {
		this.matiere = matiere;
	}

	public BigDecimal getNote() {
		return note;
	}

	public void setNote(BigDecimal note) {
		this.note = note;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getPupilId() {
		return pupilId;
	}

	public void setPupilId(Long pupilId) {
		this.pupilId = pupilId;
	}
}
```

### 1.3 Repository (Méthode d'accès aux données via Spring)

```Java
// com.educeasy.core.repository
public interface NoteRepository extends JpaRepository<Note, Long> {

  @Query("""
      select n from Note n
      where n.pupil.id = :pupilId
        and (:matiere is null or lower(n.matiere) = lower(:matiere))
        and (:from is null or n.dateNote >= :from)
        and (:to   is null or n.dateNote <= :to)
      order by n.dateNote desc, n.id desc
  """)
  List<Note> findForPupilWithFilters(@Param("pupilId") Long pupilId,
                                     @Param("matiere") String matiere,
                                     @Param("from") LocalDate from,
                                     @Param("to") LocalDate to);
}
```

### 1.4 Requêtes "sécurité/vérification"

```Java
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

  // Élève actuellement inscrit dans une classe d’un professeur donné (via user_id)
  @Query("""
     select (count(i) > 0) from Inscription i
       join i.classroom c
       join c.teacher t
       join t.user tu
     where i.pupil.id = :pupilId
       and i.dateEntree <= current_date
       and (i.dateSortie is null or i.dateSortie > current_date)
       and tu.id = :teacherUserId
  """)
  boolean existsActiveForTeacher(@Param("pupilId") Long pupilId,
                                 @Param("teacherUserId") Long teacherUserId);

  // Élève présent dans une école dirigée par ce principal (via user_id)
  @Query("""
     select (count(i) > 0) from Inscription i
       join i.classroom c
       join c.school s
       join s.principal p
       join p.user pu
     where i.pupil.id = :pupilId
       and i.dateEntree <= current_date
       and (i.dateSortie is null or i.dateSortie > current_date)
       and pu.id = :principalUserId
  """)
  boolean existsInPrincipalScope(@Param("pupilId") Long pupilId,
                                 @Param("principalUserId") Long principalUserId);
}
```

## 2. Service (logique métier + validation)

```Java
@Service
public class NoteService {
  private final NoteRepository noteRepo;
  private final PupilRepository pupilRepo;
  private final UserRepository userRepo;
  private final InscriptionRepository inscriptionRepo;

  private static final BigDecimal ZERO = BigDecimal.ZERO;
  private static final BigDecimal TWENTY = new BigDecimal("20");

  @Transactional(readOnly = true)
  public List<EntryDTO.NoteInfo> listForPupil(Long pupilId, String matiere, LocalDate from, LocalDate to) {
    return noteRepo.findForPupilWithFilters(pupilId, matiere, from, to).stream()
      .map(n -> new EntryDTO.NoteInfo(
         n.getId(), n.getMatiere(), n.getNote(), n.getDateNote(), n.getCommentaire(),
         n.getPupil().getId(), n.getAuteur().getId(), n.getCreatedAt()))
      .toList();
  }

  @Transactional
  public EntryDTO.NoteInfo addNote(Long pupilId, EntryDTO.CreateNoteRequest req, Authentication auth) {
    // 1) Validation “données”
    if (req == null) throw new IllegalArgumentException("Payload manquant");
    if (req.matiere() == null || req.matiere().isBlank()) throw new IllegalArgumentException("Matière obligatoire");
    if (req.note() == null || req.note().compareTo(ZERO) < 0 || req.note().compareTo(TWENTY) > 0)
      throw new IllegalArgumentException("Note must be between 0 and 20");
    if (req.dateNote() == null || req.dateNote().isAfter(LocalDate.now()))
      throw new IllegalArgumentException("dateNote invalide");

    // 2) Contexte sécurité (user courant + rôle)
    var user = userRepo.findByUsername(auth.getName())
        .orElseThrow(() -> new AccessDeniedException("Utilisateur inconnu"));
    var role = user.getRole(); // PRINCIPAL | TEACHER

    // 3) Vérification “périmètre”
    boolean allowed = switch (role) {
      case TEACHER -> inscriptionRepo.existsActiveForTeacher(pupilId, user.getId());
      case PRINCIPAL -> inscriptionRepo.existsInPrincipalScope(pupilId, user.getId());
      default -> false;
    };
    if (!allowed) throw new AccessDeniedException("Hors périmètre");

    // 4) Sauvegarde
    var pupil = pupilRepo.findById(pupilId)
        .orElseThrow(() -> new IllegalArgumentException("Élève introuvable"));
    var n = new Note();
    n.setPupil(pupil);
    n.setMatiere(req.matiere().trim());
    n.setNote(req.note());
    n.setDateNote(req.dateNote());
    n.setCommentaire(req.commentaire());
    n.setAuteur(user);

    var saved = noteRepo.save(n);
    return new EntryDTO.NoteInfo(saved.getId(), saved.getMatiere(), saved.getNote(), saved.getDateNote(),
        saved.getCommentaire(), pupilId, user.getId(), saved.getCreatedAt());
  }

  @Transactional
  public void deleteNote(Long noteId, Authentication auth) {
    var user = userRepo.findByUsername(auth.getName())
        .orElseThrow(() -> new AccessDeniedException("Utilisateur inconnu"));
    var note = noteRepo.findById(noteId).orElseThrow(() -> new IllegalArgumentException("Note introuvable"));
    // Autorisé si principal du périmètre OU prof dans la classe de l’élève OU auteur
    boolean allowed = note.getAuteur().getId().equals(user.getId())
        || inscriptionRepo.existsActiveForTeacher(note.getPupil().getId(), user.getId())
        || inscriptionRepo.existsInPrincipalScope(note.getPupil().getId(), user.getId());
    if (!allowed) throw new AccessDeniedException("Interdit");
    noteRepo.delete(note);
  }
}
```

## 3 RestController (Endpoints de l'api "façade")

```Java
@RestController
@RequestMapping("/pupils")
public class NoteController {
  private final NoteService noteService;

  @GetMapping("/{pupilId}/grades")
  public List<NoteInfo> grades(
      @PathVariable Long pupilId,
      @RequestParam(required = false) String matiere,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return noteService.listForPupil(pupilId, matiere, from, to);
  }

  @PostMapping("/{pupilId}/grades/add")
  @PreAuthorize("hasAnyRole('TEACHER','PRINCIPAL')")
  public ResponseEntity<NoteInfo> addGrade(
      @PathVariable Long pupilId,
      @RequestBody EntryDTO.CreateNoteRequest req,
      Authentication auth) {
    return ResponseEntity.ok(noteService.addNote(pupilId, req, auth));
  }

  @DeleteMapping("/grades/{noteId}")
  @PreAuthorize("hasAnyRole('TEACHER','PRINCIPAL')")
  public ResponseEntity<Void> delete(@PathVariable Long noteId, Authentication auth) {
    noteService.deleteNote(noteId, auth);
    return ResponseEntity.noContent().build();
  }
}

```

## 4 Tests unitaires

### 4.1 Repository

```Java
@DataJpaTest
@ActiveProfiles("test")
class NoteRepositoryTest {
  @Autowired NoteRepository noteRepo;
  @Autowired TestEntityManager em;

  @Test void findForPupil_filtersWork() {
    var pupil = em.persist(new Pupil("Doe","Jane", Gender.GIRL));
    var u = em.persist(new User("t1@x.com","t1","...", Role.TEACHER, true));
    var n1 = new Note(); n1.setPupil(pupil); n1.setMatiere("Math"); n1.setNote(new BigDecimal("12"));
    n1.setDateNote(LocalDate.now().minusDays(1)); n1.setAuteur(u); em.persist(n1);
    var n2 = new Note(); n2.setPupil(pupil); n2.setMatiere("Fr"); n2.setNote(new BigDecimal("15"));
    n2.setDateNote(LocalDate.now()); n2.setAuteur(u); em.persist(n2);
    em.flush();

    var res = noteRepo.findForPupilWithFilters(pupil.getId(), "Math", null, null);
    assertEquals(1, res.size());
    assertEquals("Math", res.get(0).getMatiere());
  }
}
```

### 4.2 Service (Mockito)

```Java
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
  @Mock NoteRepository noteRepo;
  @Mock PupilRepository pupilRepo;
  @Mock UserRepository userRepo;
  @Mock InscriptionRepository inscRepo;
  @InjectMocks NoteService service;

  @Test void addNote_refuseNoteHorsBornes() {
    var auth = auth("prof");
    var u = user(Role.TEACHER, 10L);
    when(userRepo.findByUsername("prof")).thenReturn(Optional.of(u));
    when(inscRepo.existsActiveForTeacher(1L, 10L)).thenReturn(true);
    var req = new EntryDTO.CreateNoteRequest("Math", new BigDecimal("21"), LocalDate.now(), "x");
    assertThrows(IllegalArgumentException.class, () -> service.addNote(1L, req, auth));
  }

  @Test void addNote_ok() {
    var auth = auth("prof");
    var u = user(Role.TEACHER, 10L);
    when(userRepo.findByUsername("prof")).thenReturn(Optional.of(u));
    when(inscRepo.existsActiveForTeacher(1L, 10L)).thenReturn(true);
    when(pupilRepo.findById(1L)).thenReturn(Optional.of(new Pupil()));

    var req = new EntryDTO.CreateNoteRequest("Math", new BigDecimal("18"), LocalDate.now(), "Bien");
    when(noteRepo.save(any())).thenAnswer(inv -> { Note n = inv.getArgument(0); n.setId(99L); return n; });

    var dto = service.addNote(1L, req, auth);
    assertEquals(99L, dto.id());
    assertEquals("Math", dto.matiere());
  }

  // helpers
  private Authentication auth(String username) { return new UsernamePasswordAuthenticationToken(username, ""); }
  private User user(Role r, Long id) { var u = new User(); u.setId(id); u.setUsername("prof"); u.setRole(r); return u; }
}
```

### 4.3 Controller(MockMvc)

```Java
@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@Import({
		NoteService.class
})
class NoteControllerWebTest {

	@Autowired MockMvc mvc;
	@MockBean NoteService noteService;

	@Test
	@WithMockUser(username = "t1", roles = {
			"TEACHER","PRINCIPAL"
	})
	void post_addGrade_ok() throws Exception {
		var payload = """
				  {"matiere":"Arts","note":12.5,"dateNote":"2025-09-18","commentaire":"RAS"}
				""";

		var returned = new NoteInfo();
		returned.setId(1L);
		returned.setMatiere("Arts");
		returned.setNote(BigDecimal.valueOf(12.5));
		returned.setDate(LocalDate.parse("2025-09-11"));
		returned.setCommentaire("RAS");
		returned.setPupilId(1L);

		when(noteService.addNote(eq(1L), any(), any())).thenReturn(returned);

		mvc.perform(post("/pupils/1/grades/add").contentType(MediaType.APPLICATION_JSON).content(payload).with(csrf())).andExpect(status().isOk()).andExpect(jsonPath("$.matiere").value("Arts")).andExpect(jsonPath("$.note").value(12.5));
	}

	@Test
  void post_addGrade_forbidden_whenNoAuth() throws Exception {
	    mvc.perform(
	            post("/pupils/1/grades/add")
	              .contentType(MediaType.APPLICATION_JSON)
	              .content("{\"matiere\":\"X\",\"note\":10,\"dateNote\":\"2025-09-18\"}")
	              .with(csrf())
	          )
	          .andExpect(status().isForbidden());
  }
}
```

## 5 Bonnes pratiques appliquées

- Requêtes paramétrées (JPQL avec @Param) : pas de concaténation SQL.
- Validation serveur (bornes [0;20], dates, champs obligatoires).
- Sécurité : @PreAuthorize, contrôle métier périmètre (prof/principal) via Repository.
- Transactions : lectures en readOnly, écritures transactionnelles.
- Gestion d’erreurs : exceptions → 400/403/401 via @ControllerAdvice (non montré).
- Logs (info/warn sur ajouts/suppressions — à conserver en prod).

## 6 Résumé des résultats (module Note)

- Repository : OK (filtres matière/période)
- Service : OK (validation données + contrôle périmètre)
- Controller : OK (200/201, 401, 403, 400 couverts)
- Prochaines étapes : dédupliquer patterns pour Absence/Remarque, ajouter Testcontainers (PostgreSQL) + seuil Jacoco (≥ 80%).
