package com.campus.placement.controller;
import com.campus.placement.model.Company;
import com.campus.placement.service.PlacementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/companies") @CrossOrigin(origins="http://localhost:5173")
public class CompanyController {
 private final PlacementService service;
 public CompanyController(PlacementService service){this.service=service;}
 @GetMapping public Object all(){return service.allCompanies();}
 @PostMapping public ResponseEntity<?> create(@RequestBody Company company){try{return ResponseEntity.ok(service.createCompany(company));}catch(Exception e){return ResponseEntity.badRequest().body(java.util.Map.of("message",e.getMessage()==null?"Could not create company":e.getMessage()));}}
 @DeleteMapping("/{id}") public ResponseEntity<?> delete(@PathVariable Long id){try{service.deleteCompany(id);return ResponseEntity.ok(java.util.Map.of("success",true));}catch(Exception e){return ResponseEntity.badRequest().body(java.util.Map.of("message",e.getMessage()==null?"Could not delete company":e.getMessage()));}}
}
