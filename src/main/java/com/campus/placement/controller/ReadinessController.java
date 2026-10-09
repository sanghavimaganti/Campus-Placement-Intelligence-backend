package com.campus.placement.controller;
import com.campus.placement.service.PlacementService; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/readiness") @CrossOrigin(origins="http://localhost:5173")
public class ReadinessController{private final PlacementService s;public ReadinessController(PlacementService s){this.s=s;}@GetMapping("/{id}") public Object get(@PathVariable String id){return s.readiness(id);}}
