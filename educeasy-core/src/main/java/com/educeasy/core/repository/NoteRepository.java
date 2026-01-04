package com.educeasy.core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educeasy.core.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

	@Query("select n from Note n where n.pupil.id = :pupilId and (:matiere is null or n.matiere = :matiere) and (:from is null or n.dateNote >= :from) and (:to is null or n.dateNote <= :to) order by n.dateNote desc, n.id desc")
	List<Note> findByPupil(@Param("pupilId") Long pupilId, @Param("matiere") String matiere, @Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("select n from Note n where n.pupil.id = :pupilId and (:matiere is null or lower(n.matiere) = lower(:matiere)) and (:from is null or n.dateNote >= :from) and (:to   is null or n.dateNote <= :to) order by n.dateNote desc, n.id desc")
	List<Note> findForPupilWithFilters(@Param("pupilId") Long pupilId, @Param("matiere") String matiere, @Param("from") LocalDate from, @Param("to") LocalDate to);

	List<Note> findByPupilIdOrderByDateNoteDesc(Long pupilId);
}
