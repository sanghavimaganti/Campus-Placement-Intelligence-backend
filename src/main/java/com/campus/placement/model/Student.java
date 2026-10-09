package com.campus.placement.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name="students")
public class Student {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String studentId,name,email,branch,skills,placementStatus;
 @JsonIgnore
 private String password;
 private Double cgpa,tenthPercentage,intermediatePercentage,codingScore,aptitudeScore,communicationScore,resumeScore,interviewScore,readinessScore;
 private Integer backlogs,educationGap,projects,internships;
 public Student(){}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getStudentId(){return studentId;} public void setStudentId(String v){studentId=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPassword(){return password;} public void setPassword(String v){password=v;}
 public String getBranch(){return branch;} public void setBranch(String v){branch=v;}
 public String getSkills(){return skills;} public void setSkills(String v){skills=v;}
 public Double getCgpa(){return cgpa;} public void setCgpa(Double v){cgpa=v;}
 public Double getTenthPercentage(){return tenthPercentage;} public void setTenthPercentage(Double v){tenthPercentage=v;}
 public Double getIntermediatePercentage(){return intermediatePercentage;} public void setIntermediatePercentage(Double v){intermediatePercentage=v;}
 public Integer getBacklogs(){return backlogs;} public void setBacklogs(Integer v){backlogs=v;}
 public Integer getEducationGap(){return educationGap;} public void setEducationGap(Integer v){educationGap=v;}
 public Integer getProjects(){return projects;} public void setProjects(Integer v){projects=v;}
 public Integer getInternships(){return internships;} public void setInternships(Integer v){internships=v;}
 public Double getCodingScore(){return codingScore;} public void setCodingScore(Double v){codingScore=v;}
 public Double getAptitudeScore(){return aptitudeScore;} public void setAptitudeScore(Double v){aptitudeScore=v;}
 public Double getCommunicationScore(){return communicationScore;} public void setCommunicationScore(Double v){communicationScore=v;}
 public Double getResumeScore(){return resumeScore;} public void setResumeScore(Double v){resumeScore=v;}
 public Double getInterviewScore(){return interviewScore;} public void setInterviewScore(Double v){interviewScore=v;}
 public Double getReadinessScore(){return readinessScore;} public void setReadinessScore(Double v){readinessScore=v;}
 public String getPlacementStatus(){return placementStatus;} public void setPlacementStatus(String v){placementStatus=v;}
}
