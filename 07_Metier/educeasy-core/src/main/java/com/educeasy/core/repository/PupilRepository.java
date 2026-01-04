package com.educeasy.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educeasy.core.entity.Pupil;

public interface PupilRepository extends JpaRepository<Pupil, Long> {

	@Query("select p from Pupil p where lower(p.nom) like lower(concat('%', :q, '%')) or lower(p.prenom) like lower(concat('%', :q, '%'))")
	public List<Pupil> search(@Param("q") String q);

	@Query("select p from Pupil p join Inscription i on i.pupil = p and i.dateSortie is null join Classroom c on c = i.classroom join Professor prof on prof = c.maitre join User u on u = prof.user where u.id = :userId and (lower(p.nom) like lower(concat('%',:q,'%')) or lower(p.prenom) like lower(concat('%',:q,'%'))) order by p.nom asc, p.prenom asc")
	List<Pupil> searchVisibleForTeacher(@Param("userId") Long userId, @Param("q") String q);

	@Query("select p from Pupil p join Inscription i on i.pupil = p and i.dateSortie is null join Classroom c on c = i.classroom join School s on s = c.school join Principal pr on pr = s.principal join User u on u = pr.user where u.id = :userId and (lower(p.nom) like lower(concat('%',:q,'%')) or lower(p.prenom) like lower(concat('%',:q,'%'))) order by p.nom asc, p.prenom asc")
	List<Pupil> searchVisibleForPrincipal(@Param("userId") Long userId, @Param("q") String q);

	public List<Pupil> findAllByOrderByNomAsc();

	@Query("select (count(p) > 0) from Pupil p join Inscription i on i.pupil = p and i.dateSortie is null join Classroom c on c = i.classroom join Professor prof on prof = c.maitre join User u on u = prof.user  where p.id = :pupilId and u.id = :userId")
	boolean existsByIdAndActiveInTeacherClassrooms(@Param("pupilId") Long pupilId, @Param("userId") Long userId);

	@Query("select (count(p) > 0) from Pupil p join Inscription i on i.pupil = p and i.dateSortie is null join Classroom c on c = i.classroom join School s on s = c.school join Principal pr on pr = s.principal join User u on u = pr.user where p.id = :pupilId and u.id = :userId")
	boolean existsByIdAndActiveInSchoolsOfPrincipal(@Param("pupilId") Long pupilId, @Param("userId") Long userId);
}