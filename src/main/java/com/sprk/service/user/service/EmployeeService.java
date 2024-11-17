package com.sprk.service.user.service;

import com.sprk.commons.tag.Action;
import com.sprk.commons.tag.View;
import com.sprk.commons.entity.primary.user.tag.LeaveStatus;
import com.sprk.commons.entity.primary.user.tag.ToDoStatus;
import com.sprk.service.user.dto.payload.*;
import com.sprk.service.user.dto.response.TSkillSetResponse;
import com.sprk.commons.dto.APIResponse;
import com.sprk.service.user.dto.response.common.FileDataResponse;
import com.sprk.service.user.dto.response.common.JwtTokenResponse;
import com.sprk.commons.dto.TokenValidationResponse;
import com.sprk.service.user.enums.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;





public interface EmployeeService {

//  GET
    APIResponse<?> getUserDetailsByToken(TokenValidationResponse validationResponse);
    APIResponse<?> getUserDetailsByUid(String userUid);
    APIResponse<?> getEnquiryTransferableEmployees();
    APIResponse<?> getAllFaculties();
    APIResponse<?> getAllFacultiesByCourse(String courseName);
    APIResponse<?> getTableOfEmployees();
    APIResponse<?> getTableOfEmployeeRequests();
    APIResponse<?> getNotificationsByToken(TokenValidationResponse validationResponse);
    APIResponse<?> getTableOfActivities();
    APIResponse<?> getEmployeeDetailsByUid(String userUid);
    APIResponse<?> getEmployeeRequestDetailsByUserUid(String userUid);
    APIResponse<?> getEmployeeRequestDetailsByRequestUid(String requestUid);
    APIResponse<?> getEmployeeRequestDetailsByToken(String authorizationHeader);
    APIResponse<?> getActivitiesByUid(String userUid);
    FileDataResponse getProfilePhotoByToken(TokenValidationResponse validationResponse);
    FileDataResponse getProfilePhotoByUid(TokenValidationResponse validationResponse, String userUid);
    FileDataResponse getEmployeeDocumentByUid(TokenValidationResponse validationResponse, String documentType, String userUid);
    FileDataResponse getEmployeeRequestDocumentByUid(TokenValidationResponse validationResponse, String documentType, String requestUid);
    APIResponse<?> getAllEmployeesLeaves(TokenValidationResponse validationResponse);
    APIResponse<?> getEmployeeLeave(TokenValidationResponse validationResponse);
    APIResponse<?> getPublicHolidaysByYear(TokenValidationResponse validationResponse, String yearInString);
    APIResponse<?> getEmployeeSchedule(TokenValidationResponse validationResponse, String userId);
    APIResponse<?> getToDoList(TokenValidationResponse validationResponse);


//  POST
    TokenValidationResponse isTokenValid(String authorizationHeader, TokenValidationRequest validationRequest);
    TokenValidationResponse isTokenValid(String authorizationHeader, EnumSet<View> authorizedViews, EnumSet<Action> authorizedActions);
    JwtTokenResponse signInUser(LoginRequest loginRequest);
    JwtTokenResponse refreshToken(TokenValidationResponse validationResponse, boolean signOutFromOtherDevices);
    APIResponse<?> createEmployeeRequest(TokenValidationResponse validationResponse, MEmployeeCreationRequest employeeCreationRequest);
    APIResponse<?> createLeaveRequest(TokenValidationResponse validationResponse, ApplyLeaveRequest request);
    APIResponse<?> getRectificationEmployeeRequestToken(TokenValidationResponse validationResponse, RectificationLinkRequest request);
    APIResponse<?> getRectificationEmployeeRequestLink(TokenValidationResponse validationResponse, RectificationLinkRequest request);
    APIResponse<?> uploadDocumentsById(TokenValidationResponse validationResponse, String uid, boolean isExternal, MultipartFile profileDocument, MultipartFile identityDocument, MultipartFile addressDocument, MultipartFile offerLetterDocument, MultipartFile educationDocument, MultipartFile experienceDocument, MultipartFile salarySlipDocument);
    void activityLogger(TokenValidationResponse validationResponse, View view, Action action, Set<String> referenceIds);
    void notificationLogger(List<String> notifyToUserModelIds, String referenceId, View view, String message);
    void markNotificationAsRead(TokenValidationResponse validationResponse, List<Long> notificationIds);
    APIResponse<?> addToDoList(TokenValidationResponse validationResponse, AddToDoRequest addToDoRequest);


//  PATCH
    APIResponse<?> updateExternalEmployeeRequest(String authorizationHeader, UpdateExternalEmployeeRequest updateExternalEmployeeRequest, boolean isSaveAsDraft);
    APIResponse<?> updateEmployee(TokenValidationResponse validationResponse, String userUid, UpdateEmployeeRequest updateEmployeeRequest);
    APIResponse<?> updateEmployeeRequest(TokenValidationResponse validationResponse, String requestUid, UpdateEmployeeRequest updateEmployeeRequest);
    APIResponse<?> updateEmployeeEntitlements(String userUid, UpdateEntitlementsRequest updateEntitlementsRequest);
    APIResponse<?> declineEmployeeRequestStatus(TokenValidationResponse validationResponse, String requestUid);
    APIResponse<?> approveEmployeeRequestStatus(TokenValidationResponse validationResponse, ApproveEmployeeRequest request);
    APIResponse<?> updateEmployeeSchedule(TokenValidationResponse validationResponse, ApproveEmployeeRequest request);

    APIResponse<?> updateEmployeeAccess(TokenValidationResponse validationResponse, String userUid, boolean isEnabled);
    APIResponse<?> updateLeaveStatus(TokenValidationResponse validationResponse, String leaveUid, LeaveStatus leaveStatus);
    APIResponse<?> updateEmployeePassword(TokenValidationResponse validationResponse, UpdatePasswordRequest updatePasswordRequest);



//  DELETE
    APIResponse<?> deleteEmployeeRequestDocumentById(boolean isAdmin, String uid, String requestUid, DocumentType documentType);


    APIResponse<List<TSkillSetResponse>> getSkillSetByToken(TokenValidationResponse validationResponse);

    APIResponse<?> changeStatusOfToDo(TokenValidationResponse validationResponse, String toDoUid, ToDoStatus status);

    APIResponse<?> deleteToDoList(TokenValidationResponse validationResponse, String toDoUid);

    APIResponse<?> getAllNotificationsByToken(TokenValidationResponse validationResponse, boolean seen, Pageable pageable);

}
