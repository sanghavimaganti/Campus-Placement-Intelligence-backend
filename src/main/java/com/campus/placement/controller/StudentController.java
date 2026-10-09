package com.campus.placement.controller;
import com.campus.placement.model.Student; import com.campus.placement.service.PlacementService; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/students") @CrossOrigin(origins="http://localhost:5173")
public class StudentController{private final PlacementService s;public StudentController(PlacementService s){this.s=s;}@GetMapping public Object all(){return s.allStudents();}@GetMapping("/{id}") public Student one(@PathVariable String id){return s.getStudent(id);}@PutMapping("/{id}") public Student update(@PathVariable String id,@RequestBody Student incoming){return s.updateStudent(id,incoming);}}
