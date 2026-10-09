package com.campus.placement.service;
import com.campus.placement.model.*; import com.campus.placement.repository.*; import org.springframework.stereotype.Service; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.transaction.annotation.Transactional; import org.w3c.dom.*; import javax.xml.parsers.DocumentBuilderFactory; import java.io.ByteArrayInputStream; import java.net.URI; import java.net.http.*; import java.nio.charset.StandardCharsets; import java.time.*; import java.util.*; import java.util.stream.Collectors;
@Service
public class PlacementService {
 private final StudentRepository students; private final CompanyRepository companies; private final PlacementDriveRepository drives; private final ApplicationRepository applications; private final LearningResourceRepository resources; private final LiveOpportunityRepository liveOpportunities;
 public PlacementService(StudentRepository s,CompanyRepository c,PlacementDriveRepository d,ApplicationRepository a,LearningResourceRepository r,LiveOpportunityRepository lo){students=s;companies=c;drives=d;applications=a;resources=r;liveOpportunities=lo;}
 public Student getStudent(String id){return students.findByStudentId(id).orElseThrow();}
 public Student registerStudent(String name,String email,String password){
  String e=email==null?"":email.trim().toLowerCase();
  if(e.isBlank()||password==null||password.length()<6) throw new IllegalArgumentException("Enter a valid email and a password of at least 6 characters");
  if(students.findByEmailIgnoreCase(e).isPresent()) throw new IllegalArgumentException("An account already exists with this email");
  Student s=new Student(); s.setStudentId("STU"+System.currentTimeMillis()); s.setName(name==null||name.isBlank()?"New Student":name.trim()); s.setEmail(e); s.setPassword(password); s.setBranch("AI"); s.setSkills(""); s.setCgpa(0.0); s.setTenthPercentage(0.0); s.setIntermediatePercentage(0.0); s.setBacklogs(0); s.setEducationGap(0); s.setProjects(0); s.setInternships(0); s.setCodingScore(0.0); s.setAptitudeScore(0.0); s.setCommunicationScore(0.0); s.setResumeScore(0.0); s.setInterviewScore(0.0); s.setReadinessScore(0.0); s.setPlacementStatus("Not Placed"); return students.save(s);
 }
 public Optional<Student> findStudentByEmail(String email){return students.findByEmailIgnoreCase(email);}
 public Student updateStudent(String id, Student incoming){Student s=getStudent(id);s.setName(incoming.getName());s.setBranch(incoming.getBranch());s.setSkills(incoming.getSkills());s.setCgpa(normalizeCgpa(incoming.getCgpa()));s.setTenthPercentage(clamp(incoming.getTenthPercentage()));s.setIntermediatePercentage(clamp(incoming.getIntermediatePercentage()));s.setBacklogs(Math.max(0,incoming.getBacklogs()==null?0:incoming.getBacklogs()));s.setEducationGap(Math.max(0,incoming.getEducationGap()==null?0:incoming.getEducationGap()));s.setProjects(Math.max(0,incoming.getProjects()==null?0:incoming.getProjects()));s.setInternships(Math.max(0,incoming.getInternships()==null?0:incoming.getInternships()));s.setCodingScore(clamp(incoming.getCodingScore()));s.setAptitudeScore(clamp(incoming.getAptitudeScore()));s.setCommunicationScore(clamp(incoming.getCommunicationScore()));s.setResumeScore(clamp(incoming.getResumeScore()));s.setInterviewScore(clamp(incoming.getInterviewScore()));double readiness=(s.getCodingScore()+s.getAptitudeScore()+s.getCommunicationScore()+s.getResumeScore()+s.getInterviewScore())/5.0;s.setReadinessScore(Math.round(readiness*10)/10.0);return students.save(s);}
 private double clamp(Double v){return Math.max(0,Math.min(100,v==null?0:v));}
 private double normalizeCgpa(Double v){double x=v==null?0:v;return x>10?Math.min(10,x/10.0):Math.max(0,x);}
 private boolean containsIgnore(String list,String value){if(list==null||value==null)return false;for(String x:list.split("\\|"))if(x.trim().equalsIgnoreCase(value.trim()))return true;return false;}
 private Set<String> skillSet(String skills){return skills==null?new HashSet<>():Arrays.stream(skills.split("\\|")).map(String::trim).map(String::toLowerCase).collect(Collectors.toSet());}
 public boolean isEligible(Student s,Company c){
  if(!containsIgnore(c.getBranches(),s.getBranch())) return false;
  if(s.getCgpa()==null || s.getTenthPercentage()==null || s.getIntermediatePercentage()==null) return false;
  if(s.getCgpa()<c.getMinCgpa() || s.getTenthPercentage()<c.getMinTenth() || s.getIntermediatePercentage()<c.getMinIntermediate()) return false;
  if((s.getBacklogs()==null?0:s.getBacklogs())>c.getMaxBacklogs() || (s.getEducationGap()==null?0:s.getEducationGap())>c.getMaxEducationGap()) return false;

  // Eligibility is based on the student's current overall placement readiness and
  // personalized fit. Required skills improve the fit score, but they are not a
  // hard gate by themselves; this lets a high-readiness student see companies
  // they can realistically apply to while still seeing the skills they should learn.
  double readiness=s.getReadinessScore()==null?0:s.getReadinessScore();
  double fit=personalizedFit(s,c);
  return readiness>=minimumReadiness(c) && fit>=minimumFit(c);
 }
 private double minimumReadiness(Company c){
  String n=c.getName()==null?"":c.getName().toLowerCase();
  if(n.contains("nexora")) return 85;
  if(n.contains("finedge")) return 80;
  if(n.contains("datasphere")) return 72;
  if(n.contains("technova")) return 68;
  if(n.contains("innosoft")) return 68;
  return 65;
 }
 private double minimumFit(Company c){
  String n=c.getName()==null?"":c.getName().toLowerCase();
  if(n.contains("nexora")) return 68;
  if(n.contains("finedge")) return 62;
  if(n.contains("datasphere")) return 55;
  if(n.contains("innosoft")) return 52;
  if(n.contains("technova")) return 50;
  if(n.contains("cloudworks")) return 48;
  return 50;
 }
 private double minimumSkillCoverage(Company c){
  String n=c.getName()==null?"":c.getName().toLowerCase();
  if(n.contains("nexora")) return 60;
  if(n.contains("finedge")) return 50;
  return 40;
 }
 private List<String> missingSkills(Student s,Company c){Set<String> have=skillSet(s.getSkills()); List<String> missing=new ArrayList<>(); if(c.getRequiredSkills()!=null)for(String x:c.getRequiredSkills().split("\\|"))if(!have.contains(x.trim().toLowerCase()))missing.add(x.trim()); return missing;}
 private double skillScore(Student s,Company c){int total=c.getRequiredSkills()==null?0:c.getRequiredSkills().split("\\|").length;return total==0?100:(total-missingSkills(s,c).size())*100.0/total;}
 private double academicScore(Student s){
  double cgpa=Math.min(100,(s.getCgpa()==null?0:s.getCgpa()/10.0*100));
  double tenth=Math.min(100,s.getTenthPercentage()==null?0:s.getTenthPercentage());
  double inter=Math.min(100,s.getIntermediatePercentage()==null?0:s.getIntermediatePercentage());
  return Math.round((cgpa*0.50+tenth*0.25+inter*0.25)*10)/10.0;
 }
 private double personalizedFit(Student s,Company c){
  double skill=skillScore(s,c);
  double readiness=s.getReadinessScore()==null?0:s.getReadinessScore();
  double academic=academicScore(s);
  double projects=Math.min(100,(s.getProjects()==null?0:s.getProjects())*20.0);
  double internships=Math.min(100,(s.getInternships()==null?0:s.getInternships())*25.0);
  return Math.round((skill*0.35+readiness*0.35+academic*0.20+projects*0.05+internships*0.05)*10)/10.0;
 }
 public List<Map<String,Object>> companyMatches(String id){
  Student s=getStudent(id); List<Map<String,Object>> out=new ArrayList<>();
  for(Company c:companies.findAll()){
   boolean eligible=isEligible(s,c); List<String> missing=missingSkills(s,c);
   Map<String,Object> m=new LinkedHashMap<>();
   m.put("id",c.getId()); m.put("company",c.getName()); m.put("role",c.getRole()); m.put("eligible",eligible);
   m.put("fitScore",personalizedFit(s,c)); m.put("missingSkills",missing); m.put("requiredSkills",c.getRequiredSkills());
   m.put("applicationDeadline",c.getApplicationDeadline()); m.put("driveDate",c.getDriveDate()); m.put("newCompany",isNew(c));
   m.put("minimumReadiness",minimumReadiness(c)); m.put("minimumFit",minimumFit(c)); m.put("minimumSkillCoverage",minimumSkillCoverage(c));
   m.put("reason",eligible?"You currently meet this company’s academic, knowledge and skill requirements.":"Not eligible yet — improve the highlighted gaps to unlock this company.");
   out.add(m);
  }
  out.sort((a,b)->{int e=Boolean.compare((Boolean)b.get("eligible"),(Boolean)a.get("eligible")); return e!=0?e:Double.compare((Double)b.get("fitScore"),(Double)a.get("fitScore"));});
  return out;
 }
 private boolean isNew(Company c){try{return LocalDate.parse(c.getLaunchDate()).isAfter(LocalDate.now().minusDays(45));}catch(Exception e){return false;}}
 public Map<String,Object> personalizedDashboard(String id){
  Student s=getStudent(id); List<Map<String,Object>> matches=companyMatches(id);
  List<Map<String,Object>> eligible=matches.stream().filter(x->(Boolean)x.get("eligible")).collect(Collectors.toList());
  List<Map<String,Object>> targets=matches.stream().filter(x->!(Boolean)x.get("eligible") && (Double)x.get("fitScore")>=45.0).sorted((a,b)->Double.compare((Double)b.get("fitScore"),(Double)a.get("fitScore"))).limit(4).collect(Collectors.toList());
  List<Map<String,Object>> newCompanies=matches.stream().filter(x->(Boolean)x.get("newCompany")).sorted((a,b)->Double.compare((Double)b.get("fitScore"),(Double)a.get("fitScore"))).limit(4).collect(Collectors.toList());
  List<String> gaps=personalizedGaps(s,matches); List<Map<String,Object>> learning=learningPlan(s,gaps);
  Map<String,Object> m=new LinkedHashMap<>(); m.put("student",s); m.put("eligibleCompanies",eligible); m.put("targetCompanies",targets); m.put("newCompanies",newCompanies); m.put("allMatches",matches); m.put("skillGaps",gaps); m.put("learningPlan",learning); m.put("readinessMessage",readinessMessage(s)); m.put("applications",applications.findByStudentId(id)); return m;
 }
 private List<String> personalizedGaps(Student s,List<Map<String,Object>> matches){
  Set<String> owned=skillSet(s.getSkills());
  Map<String,Double> priority=new HashMap<>();

  // 1) Company-specific technical gaps: only skills the student does not already have.
  for(Map<String,Object> m:matches){
    boolean eligible=(Boolean)m.get("eligible");
    double fit=(Double)m.get("fitScore");
    if(eligible || fit<45) continue;
    List<String> missing=(List<String>)m.get("missingSkills");
    double companyWeight=Math.max(0.5,fit/100.0);
    for(String x:missing){
      String key=x.trim();
      if(!key.isBlank() && !owned.contains(key.toLowerCase())) priority.put(key,priority.getOrDefault(key,0.0)+companyWeight);
    }
  }

  // 2) Personal knowledge gaps. These are driven by this student's own scores,
  // so two students with different scores receive different learning priorities.
  double coding=s.getCodingScore()==null?0:s.getCodingScore();
  double aptitude=s.getAptitudeScore()==null?0:s.getAptitudeScore();
  double communication=s.getCommunicationScore()==null?0:s.getCommunicationScore();
  double resume=s.getResumeScore()==null?0:s.getResumeScore();
  double interview=s.getInterviewScore()==null?0:s.getInterviewScore();

  if(coding<60) priority.put("DSA",Math.max(priority.getOrDefault("DSA",0.0),9.0));
  else if(coding<75) priority.put("DSA",Math.max(priority.getOrDefault("DSA",0.0),6.0));
  else if(coding<85) priority.put("DSA",Math.max(priority.getOrDefault("DSA",0.0),3.0));

  if(aptitude<60) priority.put("Aptitude",9.0);
  else if(aptitude<75) priority.put("Aptitude",6.0);
  else if(aptitude<85) priority.put("Aptitude",3.0);

  if(communication<60) priority.put("Communication",9.0);
  else if(communication<75) priority.put("Communication",6.0);
  else if(communication<85) priority.put("Communication",3.0);

  if(resume<60) priority.put("Resume Building",9.0);
  else if(resume<75) priority.put("Resume Building",6.0);
  else if(resume<85) priority.put("Resume Building",3.0);

  if(interview<60) priority.put("Interview",9.0);
  else if(interview<75) priority.put("Interview",6.0);
  else if(interview<85) priority.put("Interview",3.0);

  // 3) If the student has no skills entered, give foundational topics; otherwise
  // use the student's actual missing company skills. Never show a fixed curriculum.
  if(owned.isEmpty()){
    priority.putIfAbsent("Python",7.0);
    priority.putIfAbsent("SQL",6.0);
  }

  List<String> result=new ArrayList<>(priority.keySet());
  result.sort((a,b)->{
    int c=Double.compare(priority.get(b),priority.get(a));
    return c!=0?c:a.compareToIgnoreCase(b);
  });
  return result.stream().limit(6).collect(Collectors.toList());
 }
 private List<Map<String,Object>> learningPlan(Student s,List<String> gaps){
  List<Map<String,Object>> out=new ArrayList<>();
  for(String g:gaps){
   LearningResource r=resources.findBySkillIgnoreCase(g).stream().findFirst().orElse(null); Map<String,Object> x=new LinkedHashMap<>();
   x.put("skill",g); x.put("currentLevel",levelFor(g,s)); x.put("priority",priorityFor(g,s)); x.put("reason",reasonFor(g,s));
   if(r!=null){x.put("title",r.getTitle());x.put("type",r.getType());x.put("url",r.getUrl());x.put("estimatedHours",personalizedHours(g,s,r.getEstimatedHours()));x.put("description",r.getDescription());}
   else{x.put("title",resourceTitle(g));x.put("type","Practice");x.put("url",resourceUrl(g));x.put("estimatedHours",personalizedHours(g,s,8));x.put("description",reasonFor(g,s));}
   out.add(x);
  }
  return out;
 }
 private int personalizedHours(String skill,Student s,int base){
  double score=scoreForSkill(skill,s);
  if(score<50) return Math.max(base,base+4);
  if(score<70) return base+2;
  if(score<85) return Math.max(3,base-1);
  return Math.max(2,base-2);
 }
 private double scoreForSkill(String skill,Student s){
  String x=skill.toLowerCase();
  if(x.equals("dsa")||x.equals("python")||x.equals("java")||x.equals("sql")) return s.getCodingScore()==null?0:s.getCodingScore();
  if(x.equals("aptitude")) return s.getAptitudeScore()==null?0:s.getAptitudeScore();
  if(x.equals("communication")) return s.getCommunicationScore()==null?0:s.getCommunicationScore();
  if(x.equals("resume building")) return s.getResumeScore()==null?0:s.getResumeScore();
  if(x.equals("interview")) return s.getInterviewScore()==null?0:s.getInterviewScore();
  return skillSet(s.getSkills()).contains(x)?80:0;
 }
 private String levelFor(String skill,Student s){
  double v=scoreForSkill(skill,s); String x=skill.toLowerCase();
  if((x.equals("python")||x.equals("java")||x.equals("sql")||x.equals("dsa")) && !skillSet(s.getSkills()).contains(x) && !x.equals("dsa")) return "Not demonstrated";
  if(v>=85)return "Strong"; if(v>=70)return "Developing"; if(v>=50)return "Needs focus"; return "Beginner";
 }
 private String priorityFor(String skill,Student s){double v=scoreForSkill(skill,s); if(v<60)return "High"; if(v<80)return "Medium"; return "Low";} private String readinessMessage(Student s){double r=s.getReadinessScore()==null?0:s.getReadinessScore(); if(r>=90)return "You are highly placement-ready. Focus on target-company-specific skills and interview excellence."; if(r>=75)return "You have a strong base. Close the highest-priority gaps to unlock more companies."; if(r>=60)return "You are building a good foundation. Focus on the high-priority areas below to become placement-ready."; return "Start with the highest-priority knowledge gaps below and build your placement readiness step by step.";}

 private String reasonFor(String skill,Student s){double v=scoreForSkill(skill,s); String x=skill.toLowerCase();
  if(x.equals("aptitude")) return "Your aptitude score is "+Math.round(v)+"/100. Improve speed, quantitative reasoning and logical problem solving for placement tests.";
  if(x.equals("communication")) return "Your communication score is "+Math.round(v)+"/100. Practice introductions, behavioral answers and clear technical explanations.";
  if(x.equals("resume building")) return "Your resume score is "+Math.round(v)+"/100. Strengthen project impact, skills evidence and ATS-friendly presentation.";
  if(x.equals("interview")) return "Your interview score is "+Math.round(v)+"/100. Practice technical and HR questions for the companies you are targeting.";
  if(v<70) return "Your current coding/technical score is "+Math.round(v)+"/100. This topic is prioritized to raise your readiness for your target companies.";
  return "This topic is required by companies close to your current fit. Strengthening it can unlock more roles.";
 }
 private String resourceTitle(String g){
  String x=g.toLowerCase(); if(x.equals("aptitude"))return "Placement Aptitude Practice"; if(x.equals("communication"))return "Communication & HR Practice"; if(x.equals("resume building"))return "ATS Resume Building Practice"; if(x.equals("interview"))return "Technical & HR Interview Practice"; if(x.equals("dsa"))return "DSA Placement Problem Set"; return "Build "+g+" for placements";
 }
 private String resourceUrl(String g){
  String x=g.toLowerCase();
  if(x.equals("aptitude"))return "https://www.indiabix.com/aptitude/questions-and-answers/";
  if(x.equals("communication"))return "https://www.toastmasters.org/resources/public-speaking-tips";
  if(x.equals("resume building"))return "https://www.canva.com/resumes/";
  if(x.equals("interview"))return "https://www.indeed.com/career-advice/interviewing";
  if(x.equals("dsa"))return "https://www.hackerrank.com/domains/data-structures";
  return "https://www.google.com/search?q="+g.replace(" ","+")+"+placement+practice";
 }
 public Map<String,Object> readiness(String id){Student s=getStudent(id);Map<String,Object> m=new LinkedHashMap<>();Map<String,Double> a=new LinkedHashMap<>();a.put("Coding",s.getCodingScore());a.put("Aptitude",s.getAptitudeScore());a.put("Communication",s.getCommunicationScore());a.put("Resume",s.getResumeScore());a.put("Interview",s.getInterviewScore());m.put("areas",a);m.put("overall",s.getReadinessScore());m.put("message",readinessMessage(s));m.put("topGaps",personalizedGaps(s,companyMatches(id)));m.put("recommendations",learningPlan(s,personalizedGaps(s,companyMatches(id))));return m;}
 public Application apply(String studentId,Long companyId,Map<String,String> details,org.springframework.web.multipart.MultipartFile resume){
  Student s=getStudent(studentId); Company c=companies.findById(companyId).orElseThrow();
  if(!isEligible(s,c)) throw new IllegalArgumentException("Student is not eligible for this company yet");
  if(applications.findByStudentIdAndCompanyName(studentId,c.getName()).isPresent()) throw new IllegalArgumentException("You already applied to this company");
  if(details==null || details.getOrDefault("phone","").isBlank() || details.getOrDefault("address","").isBlank()) throw new IllegalArgumentException("Please complete your personal details before applying");
  if(resume==null || resume.isEmpty()) throw new IllegalArgumentException("Please upload your resume before applying");
  Application a=new Application(); a.setStudentId(studentId); a.setCompanyName(c.getName()); a.setStage("Application Submitted"); a.setStatus("Applied"); a.setAppliedAt(LocalDate.now().toString());
  a.setName(details.getOrDefault("name",s.getName())); a.setEmail(details.getOrDefault("email",s.getEmail())); a.setPhone(details.get("phone")); a.setDob(details.get("dob")); a.setGender(details.get("gender")); a.setAddress(details.get("address")); a.setCollege(details.get("college")); a.setBranch(details.getOrDefault("branch",s.getBranch())); a.setSkills(details.getOrDefault("skills",s.getSkills()));
  a.setCgpa(parseDouble(details.get("cgpa"),s.getCgpa())); a.setTenthPercentage(parseDouble(details.get("tenthPercentage"),s.getTenthPercentage())); a.setIntermediatePercentage(parseDouble(details.get("intermediatePercentage"),s.getIntermediatePercentage()));
  a.setResumeFileName(resume.getOriginalFilename()==null?"resume":resume.getOriginalFilename());
  try{a.setResumeData(resume.getBytes());}catch(Exception ex){throw new IllegalArgumentException("Unable to read the uploaded resume");}
  a.setShortlisted(false); return applications.save(a);
 }
 private Double parseDouble(String v,Double fallback){try{return v==null||v.isBlank()?fallback:Double.valueOf(v);}catch(Exception e){return fallback;}}
 public Application setShortlisted(Long id,boolean value){Application a=applications.findById(id).orElseThrow();a.setShortlisted(value);a.setStatus(value?"Shortlisted":"Under Review");a.setStage(value?"Shortlisted":"Application Submitted");return applications.save(a);}
 public String offerLetterText(Application a){return "CAMPUS PLACEMENT INTELLIGENCE\n\nOFFER LETTER\n\nDear Student,\n\nCongratulations! You have been shortlisted for the " + a.getCompanyName() + " placement opportunity.\n\nCandidate: " + a.getStudentId() + "\nRole: Placement-selected role\nApplication Date: " + a.getAppliedAt() + "\n\nPlease retain this letter for your placement records. The final joining terms are subject to the company's official communication.\n\nCongratulations on your achievement!\n\nCampus Placement Intelligence";}
 public Map<String,Object> adminDashboard(){List<Student> all=students.findAll();long ready=all.stream().filter(s->s.getReadinessScore()!=null&&s.getReadinessScore()>=75).count();long placed=all.stream().filter(s->"Placed".equalsIgnoreCase(s.getPlacementStatus())).count();Map<String,Object> m=new LinkedHashMap<>();m.put("totalStudents",all.size());m.put("placementReady",ready);m.put("eligibleStudents",eligibleCount());m.put("companies",companies.count());m.put("placed",placed);m.put("students",all);return m;}
 public long eligibleCount(){return students.findAll().stream().filter(s->companies.findAll().stream().anyMatch(c->isEligible(s,c))).count();}

 public List<Map<String,Object>> latestOpportunities(){
  LocalDate today=LocalDate.now();
  List<Map<String,Object>> out=new ArrayList<>();
  for(LiveOpportunity o:liveOpportunities.findTop30ByOrderByPostedDateDescIdDesc()){
   Map<String,Object> m=new LinkedHashMap<>();
   m.put("id",o.getId()); m.put("company",o.getCompany()); m.put("role",o.getRole()); m.put("location",o.getLocation());
   m.put("postedDate",o.getPostedDate()); m.put("deadline",o.getDeadline()); m.put("source",o.getSource()); m.put("sourceUrl",o.getSourceUrl()); m.put("applyUrl",o.getApplyUrl());
   m.put("requiredSkills",o.getRequiredSkills()); m.put("batch",o.getBatch()); m.put("eligibility",o.getEligibility()); m.put("description",o.getDescription()); m.put("verified",Boolean.TRUE.equals(o.getVerified()));
   boolean fresh=false; try{fresh=!LocalDate.parse(o.getPostedDate()).isBefore(today.minusDays(2));}catch(Exception ignored){}
   m.put("newToday",fresh);
   out.add(m);
  }
  Map<String,Object> result=new LinkedHashMap<>(); result.put("updatedAt",LocalDateTime.now().toString()); result.put("count",out.size()); result.put("opportunities",out); result.put("message","Feed refreshed from configured sources. New listings are checked daily."); return List.of(result);
 }
 @Transactional
 public Map<String,Object> refreshLiveOpportunities(){
  int added=0; int seen=0;
  String feed="https://freshershunt.in/off-campus-drive-jobs/off-campus-drive/feed/";
  try{
   HttpRequest req=HttpRequest.newBuilder(URI.create(feed)).timeout(java.time.Duration.ofSeconds(12)).header("User-Agent","CampusPlacementIntelligence/1.0").GET().build();
   String xml=HttpClient.newHttpClient().send(req,HttpResponse.BodyHandlers.ofString()).body();
   Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
   NodeList items=doc.getElementsByTagName("item");
   for(int i=0;i<Math.min(items.getLength(),20);i++){
    Element e=(Element)items.item(i); String title=text(e,"title"); String link=text(e,"link"); String pub=text(e,"pubDate");
    if(title.isBlank()) continue; seen++;
    String company=extractCompany(title); String role=extractRole(title); String posted=LocalDate.now().toString();
    if(pub!=null&&!pub.isBlank()){try{posted=java.time.ZonedDateTime.parse(pub,java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDate().toString();}catch(Exception ignored){}}
    if(liveOpportunities.findByCompanyIgnoreCaseAndRoleIgnoreCaseAndPostedDate(company,role,posted).isEmpty()){
      LiveOpportunity o=new LiveOpportunity(); o.setCompany(company);o.setRole(role);o.setLocation(extractLocation(title));o.setPostedDate(posted);o.setSource("Freshershunt");o.setSourceUrl(feed);o.setApplyUrl(link);o.setRequiredSkills(inferSkills(title));o.setBatch("2026 / 2027 / Check listing");o.setEligibility("Open the source listing for the exact eligibility criteria.");o.setDescription(title);o.setVerified(false);liveOpportunities.save(o);added++;
    }
   }
  }catch(Exception ignored){ /* keep existing verified/seeded feed when a public source is temporarily unavailable */ }
  Map<String,Object> r=new LinkedHashMap<>();r.put("added",added);r.put("seen",seen);r.put("checkedAt",LocalDateTime.now().toString());r.put("source","Freshershunt RSS");return r;
 }
 @Scheduled(cron="0 0 8 * * *", zone="Asia/Kolkata") public void dailyOpportunityRefresh(){refreshLiveOpportunities();}
 private String text(Element e,String tag){NodeList n=e.getElementsByTagName(tag);return n.getLength()==0?"":n.item(0).getTextContent().trim();}
 private String extractCompany(String title){String t=title.replaceAll("\\s+"," ").trim(); String[] known={"IBM","Jio","Virtusa","Tech Mahindra","NTT DATA","NTT","Accenture","Avanade","Flex","DTCC","Johnson & Johnson Technology","ZoomInfo","Hewlett Packard Enterprise","GE HealthCare","Tata Motors","Citi","LSEG","Amazon","Wipro","Infosys","HCLTech","Mphasis","Unisys"};for(String k:known)if(t.toLowerCase().contains(k.toLowerCase()))return k; String x=t.replaceAll("(?i)\\b(off campus drive|walk-in drive|2026|2027|for freshers|freshers)\\b.*$","").trim();return x.isBlank()?"New Opportunity":x;}
 private String extractRole(String title){String t=title.replaceAll("(?i)\\s*[-|:]\\s*apply now.*$","");String[] markers={"Off Campus Drive 2026 |","Off Campus Drive 2026 –","Off Campus Drive 2026 -","2026 |","2026 –"};for(String m:markers){int i=t.indexOf(m);if(i>=0){String r=t.substring(i+m.length()).trim();if(!r.isBlank())return r;}}return "Graduate / Fresher Opportunity";}
 private String extractLocation(String title){String[] cities={"Hyderabad","Bengaluru","Bangalore","Kolkata","Chennai","Mumbai","Pune","Gurugram","Gurgaon","Navi Mumbai","Remote","Pan India"};for(String c:cities)if(title.toLowerCase().contains(c.toLowerCase()))return c;return "India";}
 private String inferSkills(String title){String t=title.toLowerCase();List<String>x=new ArrayList<>();for(String k:new String[]{"python","java","sql","javascript","react","aws","docker","gen ai","machine learning","data science","testing","selenium","c#","devops","communication"})if(t.contains(k))x.add(k);return String.join("|",x);}
 public Company createCompany(Company c){
  if(c.getName()==null||c.getName().isBlank()) throw new IllegalArgumentException("Company name is required");
  if(c.getRole()==null||c.getRole().isBlank()) c.setRole("Graduate / Software Role");
  if(c.getBranches()==null||c.getBranches().isBlank()) c.setBranches("CSE|AI|IT");
  if(c.getRequiredSkills()==null) c.setRequiredSkills("");
  if(c.getDescription()==null||c.getDescription().isBlank()) c.setDescription("Placement opportunity added by the placement administrator.");
  if(c.getLaunchDate()==null||c.getLaunchDate().isBlank()) c.setLaunchDate(LocalDate.now().toString());
  if(c.getMinCgpa()==null) c.setMinCgpa(0.0); if(c.getMinTenth()==null)c.setMinTenth(0.0); if(c.getMinIntermediate()==null)c.setMinIntermediate(0.0);
  if(c.getMaxBacklogs()==null)c.setMaxBacklogs(0); if(c.getMaxEducationGap()==null)c.setMaxEducationGap(0);
  return companies.save(c);
 }
 public void deleteCompany(Long id){companies.deleteById(id);}
 public PlacementDrive createDrive(PlacementDrive d){
  if(d.getCompanyName()==null||d.getCompanyName().isBlank()) throw new IllegalArgumentException("Company name is required");
  if(d.getDriveDate()==null||d.getDriveDate().isBlank()) throw new IllegalArgumentException("Drive date is required");
  return drives.save(d);
 }
 public Application setApplicationStatus(Long id,String value){
  Application a=applications.findById(id).orElseThrow();
  String v=value==null?"":value.trim();
  Set<String> allowed=Set.of("Applied","Under Review","Shortlisted","Interview Scheduled","Selected","Offer Released","Rejected");
  if(!allowed.contains(v)) throw new IllegalArgumentException("Invalid application status");
  a.setStatus(v); a.setStage(v);
  if(v.equals("Shortlisted")||v.equals("Interview Scheduled")||v.equals("Selected")||v.equals("Offer Released")) a.setShortlisted(true);
  if(v.equals("Selected")||v.equals("Offer Released")){students.findByStudentId(a.getStudentId()).ifPresent(st->{st.setPlacementStatus("Placed");students.save(st);});}
  return applications.save(a);
 }
 public List<Company> allCompanies(){return companies.findAll();}
 public List<PlacementDrive> allDrives(){return drives.findAll();}
 public List<Student> allStudents(){return students.findAll();}
}
