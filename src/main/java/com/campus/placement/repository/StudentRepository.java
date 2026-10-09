package com.campus.placement.repository;
import com.campus.placement.model.Student; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface StudentRepository extends JpaRepository<Student,Long>{ Optional<Student> findByStudentId(String studentId); Optional<Student> findByEmailIgnoreCase(String email); }
