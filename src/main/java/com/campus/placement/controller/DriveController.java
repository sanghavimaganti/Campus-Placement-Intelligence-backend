package com.campus.placement.controller;
import com.campus.placement.model.PlacementDrive;
import com.campus.placement.service.PlacementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/drives") @CrossOrigin(origins="http://localhost:5173")
public class DriveController {
 private final PlacementService service;
 public DriveController(PlacementService service){this.service=service;}
 @GetMapping public Object all(){return service.allDrives();}
 @PostMapping public ResponseEntity<?> create(@RequestBody PlacementDrive drive){try{return ResponseEntity.ok(service.createDrive(drive));}catch(Exception e){return ResponseEntity.badRequest().body(java.util.Map.of("message",e.getMessage()==null?"Could not create drive":e.getMessage()));}}
}
