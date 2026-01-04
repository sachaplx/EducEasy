package com.educeasy.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educeasy.core.entity.RemarkLevel;
import com.educeasy.core.entity.Remarque;

public interface RemarqueRepository extends JpaRepository<Remarque, Long> {
	
  @Query("select r from Remarque r left join fetch r.auteur u where r.pupil.id = :pupilId and (:type is null or r.type =:type) order by r.createdAt desc, r.id desc")
  List<Remarque> findByPupil(@Param("pupilId") Long pupilId, @Param("type") RemarkLevel type);
}
