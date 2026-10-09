package com.campus.placement.repository;
import com.campus.placement.model.Application; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ApplicationRepository extends JpaRepository<Application,Long>{ List<Application> findByStudentId(String studentId); Optional<Application> findByStudentIdAndCompanyName(String studentId,String companyName); }
