package com.educeasy.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educeasy.core.entity.Pupil;

public interface PupilRepository extends JpaRepository<Pupil, Long> {
	
	@Query("select p from Pupil p where lower(p.nom) like lower(concat('%', :q, '%')) or lower(p.prenom) like lower(concat('%', :q, '%'))")
	List<Pupil> search(@Param("q") String q);
}