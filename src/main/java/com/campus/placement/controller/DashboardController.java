package com.campus.placement.controller;
import com.campus.placement.service.PlacementService; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/dashboard") @CrossOrigin(origins="http://localhost:5173")
public class DashboardController{private final PlacementService s;public DashboardController(PlacementService s){this.s=s;}@GetMapping("/admin") public Object admin(){return s.adminDashboard();}@GetMapping("/student/{id}") public Object student(@PathVariable String id){return s.personalizedDashboard(id);}}
