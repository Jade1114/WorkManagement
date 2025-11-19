package org.example.backend.repository;

import org.example.backend.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CoursesRepository extends JpaRepository<Courses, Long> {

}
