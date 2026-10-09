package com.campus.placement.model;
import jakarta.persistence.*;
@Entity @Table(name="placement_drives")
public class PlacementDrive {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String companyName,role,driveDate,eligibleBranches,process;
 public PlacementDrive(){}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getCompanyName(){return companyName;} public void setCompanyName(String v){companyName=v;}
 public String getRole(){return role;} public void setRole(String v){role=v;}
 public String getDriveDate(){return driveDate;} public void setDriveDate(String v){driveDate=v;}
 public String getEligibleBranches(){return eligibleBranches;} public void setEligibleBranches(String v){eligibleBranches=v;}
 public String getProcess(){return process;} public void setProcess(String v){process=v;}
}
