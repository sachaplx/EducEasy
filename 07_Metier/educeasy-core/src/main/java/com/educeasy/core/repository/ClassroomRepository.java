package com.educeasy.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educeasy.core.entity.Classroom;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

	List<Classroom> findBySchoolId(Long schoolId);

	List<Classroom> findByMaitreUserEmailIgnoreCase(String email);

	@Query("select c from Classroom c left join fetch c.maitre m left join fetch c.school s where c.id = :id")
	Optional<Classroom> findByIdWithTeacherAndSchool(@Param("id")
	Long id);
}
