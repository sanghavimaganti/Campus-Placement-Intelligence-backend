package com.campus.placement.model;
import jakarta.persistence.*;
@Entity @Table(name="learning_resources")
public class LearningResource {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String skill,title,type,level,url,description;
 private Integer estimatedHours;
 public LearningResource(){}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getSkill(){return skill;} public void setSkill(String v){skill=v;}
 public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getType(){return type;} public void setType(String v){type=v;}
 public String getLevel(){return level;} public void setLevel(String v){level=v;}
 public String getUrl(){return url;} public void setUrl(String v){url=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public Integer getEstimatedHours(){return estimatedHours;} public void setEstimatedHours(Integer v){estimatedHours=v;}
}
