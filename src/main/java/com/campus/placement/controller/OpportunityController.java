package com.campus.placement.controller;
import com.campus.placement.service.PlacementService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/opportunities") @CrossOrigin(origins="http://localhost:5173")
public class OpportunityController{
 private final PlacementService service;
 public OpportunityController(PlacementService service){this.service=service;}
 @GetMapping("/latest") public Object latest(){return service.latestOpportunities();}
 @PostMapping("/refresh") public Object refresh(){return service.refreshLiveOpportunities();}
}
