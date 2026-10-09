package com.campus.placement.controller;
import com.campus.placement.model.Application; import com.campus.placement.repository.ApplicationRepository; import com.campus.placement.service.PlacementService; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile; import java.nio.charset.StandardCharsets; import java.util.*;
@RestController @RequestMapping("/api/applications") @CrossOrigin(origins="http://localhost:5173")
public class ApplicationController{
 private final ApplicationRepository repo; private final PlacementService service;
 public ApplicationController(ApplicationRepository r,PlacementService s){repo=r;service=s;}
 @GetMapping("/{id}") public Object all(@PathVariable String id){return repo.findByStudentId(id);}
 @GetMapping public Object allApplications(){return repo.findAll();}
 @PostMapping(value="/{studentId}/{companyId}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<?> apply(@PathVariable String studentId,@PathVariable Long companyId,@RequestParam Map<String,String> details,@RequestPart(value="resume",required=false) MultipartFile resume){try{return ResponseEntity.ok(service.apply(studentId,companyId,details,resume));}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()==null?"Could not submit application":e.getMessage()));}}
 @PutMapping("/{id}/shortlist") public ResponseEntity<?> shortlist(@PathVariable Long id,@RequestParam boolean value){try{return ResponseEntity.ok(service.setShortlisted(id,value));}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
 @PutMapping("/{id}/status") public ResponseEntity<?> status(@PathVariable Long id,@RequestParam String value){try{return ResponseEntity.ok(service.setApplicationStatus(id,value));}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()==null?"Unable to update status":e.getMessage()));}}
 @GetMapping("/{id}/offer-letter") public ResponseEntity<byte[]> offerLetter(@PathVariable Long id){Application a=repo.findById(id).orElseThrow();if(!a.isShortlisted())return ResponseEntity.status(HttpStatus.FORBIDDEN).build();String text=service.offerLetterText(a);return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=Offer_Letter_"+a.getCompanyName().replaceAll("[^A-Za-z0-9]","_")+".txt").contentType(MediaType.TEXT_PLAIN).body(text.getBytes(StandardCharsets.UTF_8));}
}
