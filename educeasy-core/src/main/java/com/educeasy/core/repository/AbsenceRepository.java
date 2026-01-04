package com.educeasy.core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educeasy.core.entity.Absence;
import com.educeasy.core.entity.HalfDay;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
	@Query("select a from Absence a where a.pupil.id = :pupilId and (:from is null or a.date >= :from) and (:to is null or a.date <= :to) and (:halfDay is null or a.halfDay = :halfDay) and (:justifie is null or a.justifie = :justifie) order by a.date desc, a.id desc")
	List<Absence> findByPupil(@Param("pupilId")
	Long pupilId, @Param("from")
	LocalDate from, @Param("to")
	LocalDate to, @Param("halfDay")
	HalfDay halfDay, @Param("justifie")
	Boolean justifie);

	@Query("select count(a) from Absence a where a.date between :from and :to and exists (select 1 from Inscription i join i.classroom c join c.school s join s.principal pr join pr.user pu where i.pupil.id = a.pupil.id and pu.id = :principalUserId and i.dateEntree <= a.date and (i.dateSortie is null or i.dateSortie > a.date))")
	long countAbsentSlotsForPrincipal(@Param("principalUserId")
	Long principalUserId, @Param("from")
	LocalDate from, @Param("to")
	LocalDate to);
}
