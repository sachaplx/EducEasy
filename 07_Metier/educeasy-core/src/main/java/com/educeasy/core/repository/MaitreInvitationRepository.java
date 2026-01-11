package com.educeasy.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.educeasy.core.entity.MaitreInvitation;

public interface MaitreInvitationRepository extends JpaRepository<MaitreInvitation, Long> {
	Optional<MaitreInvitation> findByToken(String token);

	@Query("""
			  select i from MaitreInvitation i
			  where lower(i.email) = lower(:email)
			    and i.classroom.id = :classroomId
			    and i.usedAt is null
			    and (i.expiresAt is null or i.expiresAt > CURRENT_TIMESTAMP)
			  order by i.id desc
			""")
	Optional<MaitreInvitation> findActiveForEmailAndClassroom(String email, Long classroomId);
}