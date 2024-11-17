package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.EmployeeModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface EmployeeRepository extends JpaRepository<EmployeeModel, Long> {
    Boolean existsByIdentityDocumentIsNotNullAndUserUserPid(Long userPid);
    Boolean existsByAddressDocumentIsNotNullAndUserUserPid(Long userPid);
    Boolean existsByOfferLetterDocumentIsNotNullAndUserUserPid(Long userPid);
    Boolean existsByEducationDocumentIsNotNullAndUserUserPid(Long userPid);
    Boolean existsByExperienceDocumentIsNotNullAndUserUserPid(Long userPid);
    Boolean existsBySalarySlipDocumentIsNotNullAndUserUserPid(Long userPid);

    @Query("SELECT e FROM EmployeeModel e WHERE e.skillSet LIKE %:skill%")
    List<EmployeeModel> findBySkillSetContaining(@Param("skill") String skill);

    @Query(
            "SELECT e.skillSet FROM EmployeeModel e " +
            "JOIN e.user u " +
            "WHERE u.userPid = :userPid"
    )
    String findSkillSetByPid(@Param("userPid") Long userPid);
}
