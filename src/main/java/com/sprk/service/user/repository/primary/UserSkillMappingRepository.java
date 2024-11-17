package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.mapping.UserSkillMapping;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface UserSkillMappingRepository extends JpaRepository<UserSkillMapping, Long> {
    List<UserSkillMapping> findByCourseCourseName(String courseName);
}
