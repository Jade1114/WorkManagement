package org.example.backend.repository;

import org.example.backend.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CoursesRepository extends JpaRepository<Courses, Long> {

    Courses findByTitle(String title);

    long countByDeletedFalse();

    List<Courses> findByDeletedFalse();

    List<Courses> findByIdInAndDeletedFalse(Iterable<Long> ids);
}
