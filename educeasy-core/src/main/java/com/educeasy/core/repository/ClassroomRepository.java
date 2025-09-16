package com.educeasy.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educeasy.core.entity.Classroom;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

	List<Classroom> findByEcoleId(Long ecoleId);
}
