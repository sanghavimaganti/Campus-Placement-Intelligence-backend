package com.campus.placement.model;
import jakarta.persistence.*;
@Entity @Table(name="companies")
public class Company {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String name,role,branches,requiredSkills,description,location,website,salary;
 private Double minCgpa,minTenth,minIntermediate;
 private Integer maxBacklogs,maxEducationGap;
 private String launchDate,applicationDeadline,driveDate;
 public Company(){}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getRole(){return role;} public void setRole(String v){role=v;}
 public String getBranches(){return branches;} public void setBranches(String v){branches=v;}
 public String getRequiredSkills(){return requiredSkills;} public void setRequiredSkills(String v){requiredSkills=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public String getWebsite(){return website;} public void setWebsite(String v){website=v;}
 public String getSalary(){return salary;} public void setSalary(String v){salary=v;}
 public Double getMinCgpa(){return minCgpa;} public void setMinCgpa(Double v){minCgpa=v;}
 public Double getMinTenth(){return minTenth;} public void setMinTenth(Double v){minTenth=v;}
 public Double getMinIntermediate(){return minIntermediate;} public void setMinIntermediate(Double v){minIntermediate=v;}
 public Integer getMaxBacklogs(){return maxBacklogs;} public void setMaxBacklogs(Integer v){maxBacklogs=v;}
 public Integer getMaxEducationGap(){return maxEducationGap;} public void setMaxEducationGap(Integer v){maxEducationGap=v;}
 public String getLaunchDate(){return launchDate;} public void setLaunchDate(String v){launchDate=v;}
 public String getApplicationDeadline(){return applicationDeadline;} public void setApplicationDeadline(String v){applicationDeadline=v;}
 public String getDriveDate(){return driveDate;} public void setDriveDate(String v){driveDate=v;}
}
