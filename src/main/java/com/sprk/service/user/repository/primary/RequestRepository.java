package com.sprk.service.user.repository.primary;

import com.sprk.commons.entity.primary.user.RequestModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface RequestRepository extends JpaRepository<RequestModel, Long> {
    List<RequestModel> findByEmail(String email);
    List<RequestModel> findByPhone(String phone);
    RequestModel findByRequestPid(Long requestPid);
    RequestModel findByRequestUid(String requestUid);
    Boolean existsByRequestUid(String requestUid);
    Long countByRequestUid(String requestUid);

    Boolean existsByIdentityDocumentIsNotNullAndRequestPid(Long requestPid);
    Boolean existsByAddressDocumentIsNotNullAndRequestPid(Long requestPid);
    Boolean existsByOfferLetterDocumentIsNotNullAndRequestPid(Long requestPid);
    Boolean existsByEducationDocumentIsNotNullAndRequestPid(Long requestPid);
    Boolean existsByExperienceDocumentIsNotNullAndRequestPid(Long requestPid);
    Boolean existsBySalarySlipDocumentIsNotNullAndRequestPid(Long requestPid);

}
