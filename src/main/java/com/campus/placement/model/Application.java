package com.campus.placement.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name="applications", uniqueConstraints=@UniqueConstraint(columnNames={"studentId","companyName"}))
public class Application {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String studentId,companyName,stage,status,appliedAt;
 private String name,email,phone,dob,gender,address,college,branch,skills,resumeFileName;
 private Double cgpa,tenthPercentage,intermediatePercentage;
 private boolean shortlisted;
 @JsonIgnore @Lob @Basic(fetch=FetchType.LAZY) private byte[] resumeData;
 public Application(){}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getStudentId(){return studentId;} public void setStudentId(String v){studentId=v;}
 public String getCompanyName(){return companyName;} public void setCompanyName(String v){companyName=v;}
 public String getStage(){return stage;} public void setStage(String v){stage=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public String getAppliedAt(){return appliedAt;} public void setAppliedAt(String v){appliedAt=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
 public String getDob(){return dob;} public void setDob(String v){dob=v;}
 public String getGender(){return gender;} public void setGender(String v){gender=v;}
 public String getAddress(){return address;} public void setAddress(String v){address=v;}
 public String getCollege(){return college;} public void setCollege(String v){college=v;}
 public String getBranch(){return branch;} public void setBranch(String v){branch=v;}
 public String getSkills(){return skills;} public void setSkills(String v){skills=v;}
 public String getResumeFileName(){return resumeFileName;} public void setResumeFileName(String v){resumeFileName=v;}
 public Double getCgpa(){return cgpa;} public void setCgpa(Double v){cgpa=v;}
 public Double getTenthPercentage(){return tenthPercentage;} public void setTenthPercentage(Double v){tenthPercentage=v;}
 public Double getIntermediatePercentage(){return intermediatePercentage;} public void setIntermediatePercentage(Double v){intermediatePercentage=v;}
 public boolean isShortlisted(){return shortlisted;} public void setShortlisted(boolean v){shortlisted=v;}
 public byte[] getResumeData(){return resumeData;} public void setResumeData(byte[] v){resumeData=v;}
}
