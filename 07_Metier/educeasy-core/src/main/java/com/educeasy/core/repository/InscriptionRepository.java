package com.educeasy.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educeasy.core.entity.Inscription;
import com.educeasy.core.entity.Pupil;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
	
	@Query("select i.eleve from Inscription i where i.classe.id = :classeId and i.dateSortie is null")
	List<Pupil> findPupilsActifsByClassroom(@Param("classeId") Long classeId);
}
