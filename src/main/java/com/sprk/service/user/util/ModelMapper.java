package com.sprk.service.user.util;

import com.sprk.commons.document.EntitlementModel;
import com.sprk.commons.entity.primary.common.ActivityModel;
import com.sprk.commons.entity.primary.user.EmployeeModel;
import com.sprk.commons.entity.primary.user.LeaveRequestModel;
import com.sprk.commons.entity.primary.user.RequestModel;
import com.sprk.commons.entity.primary.user.UserModel;
import com.sprk.commons.entity.primary.user.mapping.NotificationUserMapping;
import com.sprk.service.user.dto.response.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;


public interface ModelMapper {
    <T> T getDataById(String id, Supplier<T> dataRetrievalFunction);
    <T> T getOptionalDataById(String Id, Supplier<Optional<T>> dataRetrievalFunction);

    <T, R> List<R> getListOfResponses(List<T> list, Function<T, R> mappingFunction);

    UserDetailsResponse mapToUserDetailsResponse(UserModel userModel, EntitlementModel entitlementModel);
    UserDetailsResponse mapToUserDetailsResponse(UserModel userModel, EntitlementModel entitlementModel, boolean isAuthoritiesRequired);


    EmployeeResponse mapToEmployeeResponse(UserModel userModel);
    EmployeeResponse mapToEmployeeResponse(EmployeeModel employeeModel);


    TEmployeeResponse mapToTEmployeeResponse(UserModel userModel);
    TEmployeeResponse mapToTEmployeeResponse(EmployeeModel employeeModel);


    EEmployeeRequestResponse mapToEEmployeeRequestResponse(RequestModel requestModel);
    EEmployeeRequestResponse mapToEEmployeeRequestResponse(UserModel userModel);
    EEmployeeRequestResponse mapToEEmployeeRequestResponse(RequestModel requestModel, EntitlementModel entitlementModel);
    EEmployeeRequestResponse mapToEEmployeeRequestResponse(UserModel userModel, EntitlementModel entitlementModel);


    TEmployeeRequestResponse mapToTEmployeeRequestResponse(RequestModel requestModel);


    TEmployeeMiniResponse mapToTEmployeeMiniResponse(UserModel userModel);
    TEmployeeMiniResponse mapToTEmployeeMiniResponse(EntitlementModel entitlementModel);


    TEmployeeActivityResponse mapToTEmployeeActivityResponse(ActivityModel activityModel);


    TNotificationResponse mapToTNotificationResponse(NotificationUserMapping mapping);

    LeaveResponse mapToLeaveResponse(LeaveRequestModel leaveModel, boolean isWithdrewIncluded);
}

