package com.campus.placement.controller;

import com.campus.placement.model.Student;
import com.campus.placement.service.PlacementService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="http://localhost:5173")
public class AuthController {
 private final PlacementService service;
 public AuthController(PlacementService service){this.service=service;}

 @PostMapping("/login")
 public Map<String,Object> login(@RequestBody Map<String,String> b){
  String email=b.getOrDefault("email","").trim().toLowerCase();
  String pass=b.getOrDefault("password","");
  Map<String,Object> r=new LinkedHashMap<>();
  if("admin@campus.local".equals(email)&&"admin123".equals(pass)){
   r.put("success",true); r.put("role","ADMIN"); r.put("name","Administrator"); r.put("message","Login successful"); return r;
  }
  Optional<Student> found=service.findStudentByEmail(email);
  if(found.isPresent() && pass.equals(found.get().getPassword())){
   Student s=found.get(); r.put("success",true); r.put("role","STUDENT"); r.put("studentId",s.getStudentId()); r.put("name",s.getName()); r.put("email",s.getEmail()); r.put("message","Login successful"); return r;
  }
  r.put("success",false); r.put("message","Incorrect email or password"); return r;
 }

 @PostMapping("/register")
 public Map<String,Object> register(@RequestBody Map<String,String> b){
  Map<String,Object> r=new LinkedHashMap<>();
  try{
   Student s=service.registerStudent(b.get("name"),b.get("email"),b.get("password"));
   r.put("success",true); r.put("role","STUDENT"); r.put("studentId",s.getStudentId()); r.put("name",s.getName()); r.put("email",s.getEmail()); r.put("message","Account created successfully. Complete your profile to get personalized company recommendations.");
  }catch(IllegalArgumentException ex){r.put("success",false);r.put("message",ex.getMessage());}
  return r;
 }
}
