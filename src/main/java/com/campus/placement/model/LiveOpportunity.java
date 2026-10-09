package com.campus.placement.model;

import jakarta.persistence.*;

@Entity
@Table(name="live_opportunities", uniqueConstraints=@UniqueConstraint(columnNames={"company","role","postedDate"}))
public class LiveOpportunity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String company,role,location,postedDate,deadline,source,sourceUrl,applyUrl,requiredSkills,batch,eligibility,description;
 private Double minCgpa;
 private Boolean verified = false;
 public LiveOpportunity(){}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getCompany(){return company;} public void setCompany(String v){company=v;}
 public String getRole(){return role;} public void setRole(String v){role=v;}
 public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public String getPostedDate(){return postedDate;} public void setPostedDate(String v){postedDate=v;}
 public String getDeadline(){return deadline;} public void setDeadline(String v){deadline=v;}
 public String getSource(){return source;} public void setSource(String v){source=v;}
 public String getSourceUrl(){return sourceUrl;} public void setSourceUrl(String v){sourceUrl=v;}
 public String getApplyUrl(){return applyUrl;} public void setApplyUrl(String v){applyUrl=v;}
 public String getRequiredSkills(){return requiredSkills;} public void setRequiredSkills(String v){requiredSkills=v;}
 public String getBatch(){return batch;} public void setBatch(String v){batch=v;}
 public String getEligibility(){return eligibility;} public void setEligibility(String v){eligibility=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public Double getMinCgpa(){return minCgpa;} public void setMinCgpa(Double v){minCgpa=v;}
 public Boolean getVerified(){return verified;} public void setVerified(Boolean v){verified=v;}
}
