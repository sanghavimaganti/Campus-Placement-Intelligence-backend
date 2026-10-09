package com.campus.placement.repository;
import com.campus.placement.model.LiveOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LiveOpportunityRepository extends JpaRepository<LiveOpportunity,Long>{
 Optional<LiveOpportunity> findByCompanyIgnoreCaseAndRoleIgnoreCaseAndPostedDate(String company,String role,String postedDate);
 List<LiveOpportunity> findTop30ByOrderByPostedDateDescIdDesc();
}
