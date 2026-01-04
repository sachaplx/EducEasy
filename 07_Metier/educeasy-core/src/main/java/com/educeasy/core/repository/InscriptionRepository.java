package com.educeasy.core.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educeasy.core.entity.Inscription;
import com.educeasy.core.entity.Pupil;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

	@Query("select p from Pupil p join Inscription i on i.pupil = p and i.dateSortie is null where i.classroom.id = :classroomId order by i.pupil.nom asc, i.pupil.prenom asc")
	List<Pupil> findPupilsActifsByClassroom(@Param("classroomId")
	Long classroomId);

	@Query("select i from Inscription i join fetch i.classroom c join fetch c.school s where i.pupil.id = :pupilId and i.dateSortie is null order by i.dateEntree desc")
	Optional<Inscription> findCurrentByPupil(@Param("pupilId")
	Long pupilId);

	@Query("select (count(i) > 0) from Inscription i join i.classroom c join c.maitre prof join prof.user u where i.pupil.id = :pupilId and lower(u.username) = lower(:username) and i.dateSortie is null")
	boolean isPupilVisibleToTeacher(@Param("pupilId")
	Long pupilId, @Param("username")
	String username);

	@Query("select p.id as pupilId, c.id as classroomId, c.nom as classroomName, s.id as schoolId, s.nom as schoolName from Inscription i join i.pupil p join i.classroom c left join c.school s where p.id in :pupilIds and i.dateSortie is null")
	List<PupilClassSchoolRow> findActiveClassSchoolByPupilIds(@Param("pupilIds")
	List<Long> pupilIds);

	@Query("select p.id as pupilId, c.id as classroomId, c.nom as classroomName, s.id as schoolId, s.nom as schoolName from Inscription i join i.pupil p join i.classroom c left join c.school s where p.id = :pupilId and i.dateSortie is null")
	Optional<PupilClassSchoolRow> findActiveClassSchoolByPupilId(@Param("pupilId")
	Long pupilId);

	@Query("select count(i) > 0 from Inscription i join i.classroom c where i.pupil.id = :pupilId and i.dateEntree <= CURRENT_DATE and (i.dateSortie is null or i.dateSortie > CURRENT_DATE) and (:classroomId is null or c.id = :classroomId)")
	boolean existsActiveByPupilAndClassroom(@Param("pupilId")
	Long pupilId, @Param("classroomId")
	Long classroomId);

	@Query("select (count(i) > 0) from Inscription i join i.classroom c join c.maitre prof join prof.user u where i.pupil.id = :pupilId and i.dateEntree <= CURRENT_DATE and (i.dateSortie is null or i.dateSortie > CURRENT_DATE) and u.id = :teacherUserId")
	boolean existsActiveForTeacher(@Param("pupilId")
	Long pupilId, @Param("teacherUserId")
	Long teacherUserId);

	@Query("select (count(i) > 0) from Inscription i join i.classroom c join c.school s join s.principal pr join pr.user pu where i.pupil.id = :pupilId and i.dateEntree <= CURRENT_DATE and (i.dateSortie is null or i.dateSortie > CURRENT_DATE) and pu.id = :principalUserId")
	boolean existsActiveForPrincipal(@Param("pupilId")
	Long pupilId, @Param("principalUserId")
	Long principalUserId);

	@Query("select distinct s.id from Inscription i join i.classroom c join c.school s join s.principal pr join pr.user pu where pu.id = :principalUserId and i.dateEntree <= :to and (i.dateSortie is null or i.dateSortie > :from)")
	List<Long> findSchoolIdsForPrincipalInPeriod(@Param("principalUserId")
	Long principalUserId, @Param("from")
	LocalDate from, @Param("to")
	LocalDate to);

	@Query("select count(distinct i.pupil.id) from Inscription i join i.classroom c join c.school s join s.principal pr join pr.user pu where pu.id = :principalUserId and i.dateEntree <= :to and (i.dateSortie is null or i.dateSortie > :from)")
	long countActivePupilsForPrincipalInPeriod(@Param("principalUserId")
	Long principalUserId, @Param("from")
	LocalDate from, @Param("to")
	LocalDate to);

	interface PupilClassSchoolRow {
		Long getPupilId();

		Long getClassroomId();

		String getClassroomName();

		Long getSchoolId();

		String getSchoolName();
	}
}
