package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.course.CourseModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;



public interface CourseRepository extends JpaRepository<CourseModel, Long> {

    @Query("SELECT c FROM CourseModel c WHERE LOWER(c.courseName) IN :skillSet")
    List<CourseModel> findBySkillSetContaining(@Param("skillSet") Collection<String> skillSetLowerCase);

    @Query("SELECT DISTINCT c.courseName FROM CourseModel c WHERE LOWER(c.courseName) NOT IN :courseNamesToCheck")
    List<String> findCourseNamesNotEqualToCollection(Collection<String> courseNamesToCheck);

    @Query("SELECT DISTINCT c.courseName FROM CourseModel c")
    List<String> findAllCourseNames();

}
