package com.campus.placement.repository;
import com.campus.placement.model.LearningResource; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface LearningResourceRepository extends JpaRepository<LearningResource,Long>{ List<LearningResource> findBySkillIgnoreCase(String skill); }
