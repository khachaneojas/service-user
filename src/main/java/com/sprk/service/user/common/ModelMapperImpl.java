package com.sprk.service.user.common;

import com.sprk.commons.document.EntitlementModel;
import com.sprk.commons.entity.primary.common.ActivityModel;
import com.sprk.commons.entity.primary.user.mapping.UserSkillMapping;
import com.sprk.commons.entity.primary.user.tag.SkillClearanceStatus;
import com.sprk.commons.tag.Action;
import com.sprk.commons.tag.View;
import com.sprk.commons.entity.primary.course.CourseModel;
import com.sprk.commons.entity.primary.user.*;
import com.sprk.commons.entity.primary.user.mapping.NotificationUserMapping;
import com.sprk.commons.entity.primary.user.tag.EmployeeRequestStatus;
import com.sprk.commons.entity.primary.user.tag.LeaveStatus;
import com.sprk.service.user.dto.SkillSetDTO;
import com.sprk.service.user.dto.response.*;
import com.sprk.service.user.enums.*;
import com.sprk.commons.exception.InvalidDataException;
import com.sprk.commons.exception.ResourceNotFoundException;
import com.sprk.service.user.repository.primary.EmployeeRepository;
import com.sprk.service.user.repository.mongo.EntitlementRepository;
import com.sprk.service.user.repository.primary.RequestRepository;
import com.sprk.service.user.repository.primary.UserRepository;
import com.sprk.service.user.util.JsonConverter;
import com.sprk.service.user.util.ModelMapper;
import com.sprk.service.user.util.TextHelper;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;





@Component
public class ModelMapperImpl implements ModelMapper {

    private static final int LOOP_THRESHOLD = 100;
    private final TextHelper textHelper;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final EntitlementRepository entitlementRepository;
    private final JsonConverter jsonConverter;

    @Autowired
    public ModelMapperImpl(
            TextHelper textHelper,
            RequestRepository requestRepository,
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            EntitlementRepository entitlementRepository,
            JsonConverter jsonConverter
    ) {
        this.textHelper = textHelper;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.entitlementRepository = entitlementRepository;
        this.jsonConverter = jsonConverter;
    }


//  USER
    @Override
    public UserDetailsResponse mapToUserDetailsResponse(UserModel userModel, EntitlementModel entitlementModel) {
        return getUserDetailsResponseByModel(userModel, entitlementModel, false);
    }

    @Override
    public UserDetailsResponse mapToUserDetailsResponse(UserModel userModel, EntitlementModel entitlementModel, boolean isAuthoritiesRequired) {
        return getUserDetailsResponseByModel(userModel, entitlementModel, isAuthoritiesRequired);
    }






//  EMPLOYEE
    @Override
    public EmployeeResponse mapToEmployeeResponse(UserModel userModel) {
        return getEmployeeResponseByModel(userModel, null);
    }

    @Override
    public EmployeeResponse mapToEmployeeResponse(EmployeeModel employeeModel) {
        return getEmployeeResponseByModel(null, employeeModel);
    }






    @Override
    public TEmployeeResponse mapToTEmployeeResponse(UserModel userModel) {
        return getTEmployeeResponseByModel(userModel, null);
    }

    @Override
    public TEmployeeResponse mapToTEmployeeResponse(EmployeeModel employeeModel) {
        return getTEmployeeResponseByModel(null, employeeModel);
    }






    @Override
    public EEmployeeRequestResponse mapToEEmployeeRequestResponse(RequestModel requestModel) {
        return getEEmployeeRequestResponseByModel(requestModel, null, null);
    }

    @Override
    public EEmployeeRequestResponse mapToEEmployeeRequestResponse(UserModel userModel) {
        return getEEmployeeRequestResponseByModel(null, userModel, null);
    }

    @Override
    public EEmployeeRequestResponse mapToEEmployeeRequestResponse(RequestModel requestModel, EntitlementModel entitlementModel) {
        return getEEmployeeRequestResponseByModel(requestModel, null, entitlementModel);
    }

    @Override
    public EEmployeeRequestResponse mapToEEmployeeRequestResponse(UserModel userModel, EntitlementModel entitlementModel) {
        return getEEmployeeRequestResponseByModel(null, userModel, entitlementModel);
    }






    @Override
    public TEmployeeRequestResponse mapToTEmployeeRequestResponse(RequestModel requestModel) {
        return getTEmployeeRequestResponseByModel(requestModel);
    }





    @Override
    public TEmployeeMiniResponse mapToTEmployeeMiniResponse(UserModel userModel) {
        return getTEmployeeMiniResponseByModel(userModel, null);
    }

    @Override
    public TEmployeeMiniResponse mapToTEmployeeMiniResponse(EntitlementModel entitlementModel) {
        return getTEmployeeMiniResponseByModel(null, entitlementModel);
    }






    @Override
    public TEmployeeActivityResponse mapToTEmployeeActivityResponse(ActivityModel activityModel) {
        return getTEmployeeActivityResponseByModel(activityModel);
    }






    @Override
    public TNotificationResponse mapToTNotificationResponse(NotificationUserMapping mapping) {
        return getTNotificationResponseByModel(mapping);
    }





    @Override
    public LeaveResponse mapToLeaveResponse(
            LeaveRequestModel leaveModel,
            boolean isWithdrewIncluded
    ) {
        return getLeaveResponseByModel(leaveModel, isWithdrewIncluded);
    }





    //  IMPL
    private UserDetailsResponse getUserDetailsResponseByModel(
            UserModel userModel,
            EntitlementModel entitlementModel,
            boolean isAuthoritiesRequired
    ) {
        if (null == entitlementModel || null == userModel)
            return null;

        return UserDetailsResponse.builder()
                .emp_id(userModel.getUserUid())
                .name(
                        textHelper.buildFullName(
                                userModel.getFirstname(),
                                userModel.getMiddlename(),
                                userModel.getLastname()
                        )
                )
                .email(userModel.getEmail())
                .enabled(userModel.isEnabled())
                .authorities(isAuthoritiesRequired ? entitlementModel.getAuthorities() : Set.of())
                .profile(userRepository.existsByProfileIsNotNullAndUserPid(userModel.getUserPid()))
                .entitlements(entitlementModel.getEntitlements())
                .build();
    }



    private EmployeeResponse getEmployeeResponseByModel(
            UserModel userModel,
            EmployeeModel employeeModel
    ) {
        if (null == employeeModel && null == userModel)
            return null;

        if (null != userModel && null == employeeModel) {
            if (null != userModel.getEmployeeDetails())
                employeeModel = userModel.getEmployeeDetails();
        }

        if (null != employeeModel && null == userModel) {
            if (null != employeeModel.getUser())
                userModel = employeeModel.getUser();
        }

        if (null == employeeModel || null == userModel)
            return null;


        EmployeeStatus employeeStatus = EmployeeStatus.DEACTIVE;
        if (Boolean.TRUE.equals(userModel.isEnabled()))
            employeeStatus = EmployeeStatus.ACTIVE;

        String userUid = userModel.getUserUid();
        EntitlementModel entitlementModel = getDataById(
                userUid,
                () -> entitlementRepository.findByUserUid(userUid)
        );



        String profileDocumentOriginalFileName = null;
        String identityDocumentOriginalFileName = null;
        String addressDocumentOriginalFileName = null;
        String offerLetterDocumentOriginalFileName = null;
        String educationDocumentOriginalFileName = null;
        String experienceDocumentOriginalFileName = null;
        String salarySlipDocumentOriginalFileName = null;

        if (userRepository.existsByProfileIsNotNullAndUserPid(userModel.getUserPid()))
            profileDocumentOriginalFileName = userModel.getProfile().getFileOriginal();

        if (employeeRepository.existsByIdentityDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            identityDocumentOriginalFileName = employeeModel.getIdentityDocument().getFileOriginal();

        if (employeeRepository.existsByAddressDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            addressDocumentOriginalFileName = employeeModel.getAddressDocument().getFileOriginal();

        if (employeeRepository.existsByOfferLetterDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            offerLetterDocumentOriginalFileName = employeeModel.getOfferLetterDocument().getFileOriginal();

        if (employeeRepository.existsByEducationDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            educationDocumentOriginalFileName = employeeModel.getEducationDocument().getFileOriginal();

        if (employeeRepository.existsByExperienceDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            experienceDocumentOriginalFileName = employeeModel.getExperienceDocument().getFileOriginal();

        if (employeeRepository.existsBySalarySlipDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            salarySlipDocumentOriginalFileName = employeeModel.getSalarySlipDocument().getFileOriginal();

        String inTime = Optional
                .ofNullable(employeeModel.getInTime())
                .map(instant -> instant
                        .atZone(ZoneId.of("UTC"))
                        .toLocalTime()
                )
                .map(String::valueOf)
                .orElse(null);

        String outTime = Optional
                .ofNullable(employeeModel.getOutTime())
                .map(instant -> instant
                        .atZone(ZoneId.of("UTC"))
                        .toLocalTime()
                )
                .map(String::valueOf)
                .orElse(null);

        List<SkillSetDTO> techSkillSetMap = Optional
                .ofNullable(userModel.getSkills())
                .map(skills -> skills
                        .stream()
                        .filter(Objects::nonNull)
                        .map(mapping -> {
                            CourseModel model = mapping.getCourse();
                            return new SkillSetDTO(model.getCourseId(), model.getCourseName(), mapping.getStatus());
                        })
                        .collect(Collectors.toList())
                )
                .orElse(List.of());

        List<String> softSkillSetMap = Optional
                .ofNullable(employeeModel.getSoftSkill())
                .map(jsonStr -> {
                    try {
                        return jsonConverter.convertToList(jsonStr, String.class);
                    } catch (IOException exception) {
                        throw new IllegalArgumentException(exception);
                    }
                })
                .orElse(List.of());

        return EmployeeResponse.builder()
                .joined_at(userModel.getJoinedAt())
                .emp_id(userModel.getUserUid())
                .name(
                        textHelper.buildFullName(
                                userModel.getFirstname(),
                                userModel.getMiddlename(),
                                userModel.getLastname()
                        )
                )
                .email(userModel.getEmail())
                .alt_email(employeeModel.getAlternateEmail())
                .phone(userModel.getPhone())
                .alt_phone(employeeModel.getAlternatePhone())
                .employee_status(employeeStatus)
                .authorities(entitlementModel.getAuthorities())
                .birth_date(employeeModel.getBirthDate())
                .gender(employeeModel.getGender())
                .blood_group(employeeModel.getBloodGroup())
                .marital_status(employeeModel.getMaritalStatus())
                .nationality(employeeModel.getNationality())
                .current_address(
                        textHelper.parseJsonAddress(employeeModel.getCurrentAddress())
                )
                .permanent_address(
                        textHelper.parseJsonAddress(employeeModel.getPermanentAddress())
                )
                .university(employeeModel.getUniversity())
                .degree(employeeModel.getDegree())
                .stream(employeeModel.getStream())
                .college(employeeModel.getCollege())
                .cgpa(employeeModel.getCgpa())
                .passing_year(employeeModel.getPassingYear())
                .skill_set(
                        new ArrayList<>(
                                parseSkillSetToStringList(employeeModel.getSkillSet())
                        )
                )
                .tech_skill(techSkillSetMap)
                .soft_skill(softSkillSetMap)
                .experience(
                        textHelper.parseJsonExperience(employeeModel.getExperience())
                )
                .eme_name(employeeModel.getEmergencyName())
                .eme_phone(employeeModel.getEmergencyPhone())
                .eme_relation(employeeModel.getEmergencyRelation())
                .physician_name(employeeModel.getPhysicianName())
                .physician_phone(employeeModel.getPhysicianPhone())
                .medical_conditions(employeeModel.getMedicalConditions())
                .doc_profile(
                        profileDocumentOriginalFileName
                )
                .doc_identity(
                        identityDocumentOriginalFileName
                )
                .doc_address(
                        addressDocumentOriginalFileName
                )
                .doc_offer_letter(
                        offerLetterDocumentOriginalFileName
                )
                .doc_education(
                        educationDocumentOriginalFileName
                )
                .doc_experience(
                        experienceDocumentOriginalFileName
                )
                .doc_salary_slip(
                        salarySlipDocumentOriginalFileName
                )
                .annual_leaves(employeeModel.getAnnualLeaves())
                .in_time(inTime)
                .out_time(outTime)
                .is_mon(employeeModel.isMonday())
                .is_tue(employeeModel.isTuesday())
                .is_wed(employeeModel.isWednesday())
                .is_thu(employeeModel.isThursday())
                .is_fri(employeeModel.isFriday())
                .is_sat(employeeModel.isSaturday())
                .is_sun(employeeModel.isSunday())
                .build();
    }



    private TEmployeeResponse getTEmployeeResponseByModel(
            UserModel userModel,
            EmployeeModel employeeModel
    ) {
        if (null == employeeModel && null == userModel)
            return null;

        if (null != userModel && null == employeeModel) {
            if (null != userModel.getEmployeeDetails())
                employeeModel = userModel.getEmployeeDetails();
        }

        if (null != employeeModel && null == userModel) {
            if (null != employeeModel.getUser())
                userModel = employeeModel.getUser();
        }

        if (null == employeeModel || null == userModel)
            return null;

        EmployeeStatus employeeStatus = EmployeeStatus.DEACTIVE;
        if (Boolean.TRUE.equals(userModel.isEnabled()))
            employeeStatus = EmployeeStatus.ACTIVE;

        String userUid = userModel.getUserUid();
        EntitlementModel entitlementModel = getDataById(
                userUid,
                () -> entitlementRepository.findByUserUid(userUid)
        );

        return TEmployeeResponse.builder()
                .joined_at(userModel.getJoinedAt())
                .emp_id(userModel.getUserUid())
                .name(
                        textHelper.buildFullName(
                                userModel.getFirstname(),
                                userModel.getMiddlename(),
                                userModel.getLastname()
                        )
                )
                .email(userModel.getEmail())
                .phone(userModel.getPhone())
                .employee_status(employeeStatus)
                .enabled(Boolean.TRUE.equals(userModel.isEnabled()))
                .authorities(entitlementModel.getAuthorities())
                .skill_set(
                        new HashSet<>(
                                parseSkillSetToStringList(employeeModel.getSkillSet())
                        )
                )
                .skill_achieved(
                        userModel.getSkills()
                                .stream()
                                .filter(userSkillMapping -> SkillClearanceStatus.CLEARED.equals(userSkillMapping.getStatus()))
                                .map(UserSkillMapping::getCourse)
                                .map(CourseModel::getCourseName)
                                .collect(Collectors.toSet())
                )
                .build();
    }

    private Collection<String> parseSkillSetToStringList(String skillSetAsJsonString) {
       return Optional
                .ofNullable(skillSetAsJsonString)
                .map(jsonStr -> {
                    try {
                        return jsonConverter.convertToList(jsonStr, String.class);
                    } catch (IOException exception) {
                        throw new IllegalArgumentException(exception);
                    }
                })
                .orElse(List.of());
    }



    private EEmployeeRequestResponse getEEmployeeRequestResponseByModel(
            RequestModel requestModel,
            UserModel userModel,
            EntitlementModel entitlementModel
    ) {
        if (null == requestModel && null == userModel)
            return null;

        boolean isExternal = (null != requestModel && null == userModel);
        EmployeeModel employeeModel = null;
        String uid;
        if (!isExternal) {
            employeeModel = userModel.getEmployeeDetails();
            uid = userModel.getUserUid();
        } else
            uid = requestModel.getRequestUid();

        if (null == entitlementModel) {
            entitlementModel = getDataById(
                    uid,
                    () -> entitlementRepository.findByUserUid(uid)
            );
        }


        String currentAddressString = isExternal ? requestModel.getCurrentAddress() : employeeModel.getCurrentAddress();
        JSONObject jsonCurrentAddressObject = (
                textHelper.isBlank(currentAddressString)
                        ? null
                        : new JSONObject(currentAddressString)
        );

        String permanentAddressString = isExternal ? requestModel.getPermanentAddress() : employeeModel.getPermanentAddress();
        JSONObject jsonPermanentAddressObject = (
                textHelper.isBlank(permanentAddressString)
                        ? null
                        : new JSONObject(permanentAddressString)
        );


        String identityDocumentOriginalFileName = null;
        String addressDocumentOriginalFileName = null;
        String offerLetterDocumentOriginalFileName = null;
        String educationDocumentOriginalFileName = null;
        String experienceDocumentOriginalFileName = null;
        String salarySlipDocumentOriginalFileName = null;

        if (isExternal && requestRepository.existsByIdentityDocumentIsNotNullAndRequestPid(requestModel.getRequestPid()))
            identityDocumentOriginalFileName = requestModel.getIdentityDocument().getFileOriginal();

        if (!isExternal && employeeRepository.existsByIdentityDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            identityDocumentOriginalFileName = employeeModel.getIdentityDocument().getFileOriginal();

        if (isExternal && requestRepository.existsByAddressDocumentIsNotNullAndRequestPid(requestModel.getRequestPid()))
            addressDocumentOriginalFileName = requestModel.getAddressDocument().getFileOriginal();

        if (!isExternal && employeeRepository.existsByAddressDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            addressDocumentOriginalFileName = employeeModel.getAddressDocument().getFileOriginal();

        if (isExternal && requestRepository.existsByOfferLetterDocumentIsNotNullAndRequestPid(requestModel.getRequestPid()))
            offerLetterDocumentOriginalFileName = requestModel.getOfferLetterDocument().getFileOriginal();

        if (!isExternal && employeeRepository.existsByOfferLetterDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            offerLetterDocumentOriginalFileName = employeeModel.getOfferLetterDocument().getFileOriginal();

        if (isExternal && requestRepository.existsByEducationDocumentIsNotNullAndRequestPid(requestModel.getRequestPid()))
            educationDocumentOriginalFileName = requestModel.getEducationDocument().getFileOriginal();

        if (!isExternal && employeeRepository.existsByEducationDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            educationDocumentOriginalFileName = employeeModel.getEducationDocument().getFileOriginal();

        if (isExternal && requestRepository.existsByExperienceDocumentIsNotNullAndRequestPid(requestModel.getRequestPid()))
            experienceDocumentOriginalFileName = requestModel.getExperienceDocument().getFileOriginal();

        if (!isExternal && employeeRepository.existsByExperienceDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            experienceDocumentOriginalFileName = employeeModel.getExperienceDocument().getFileOriginal();

        if (isExternal && requestRepository.existsBySalarySlipDocumentIsNotNullAndRequestPid(requestModel.getRequestPid()))
            salarySlipDocumentOriginalFileName = requestModel.getSalarySlipDocument().getFileOriginal();

        if (!isExternal && employeeRepository.existsBySalarySlipDocumentIsNotNullAndUserUserPid(userModel.getUserPid()))
            salarySlipDocumentOriginalFileName = employeeModel.getSalarySlipDocument().getFileOriginal();

        return EEmployeeRequestResponse.builder()
                .joined_at(
                        isExternal
                                ? requestModel.getJoinedAt()
                                : userModel.getJoinedAt()
                )
                .req_id(
                        isExternal
                                ? requestModel.getRequestUid()
                                : null
                )
                .emp_id(
                        isExternal
                                ? null
                                : userModel.getUserUid()
                )
                .firstname(
                        isExternal
                                ? requestModel.getFirstname()
                                : userModel.getFirstname()
                )
                .middlename(
                        isExternal
                                ? requestModel.getMiddlename()
                                : userModel.getMiddlename()
                )
                .lastname(
                        isExternal
                                ? requestModel.getLastname()
                                : userModel.getLastname()
                )
                .birth_date(
                        isExternal
                                ? requestModel.getBirthDate()
                                : employeeModel.getBirthDate()
                )
                .phone(
                        isExternal
                                ? requestModel.getPhone()
                                : userModel.getPhone()
                )
                .alt_phone(
                        isExternal
                                ? requestModel.getAlternatePhone()
                                : employeeModel.getAlternatePhone()
                )
                .gender(
                        isExternal
                                ? requestModel.getGender()
                                : employeeModel.getGender()
                )
                .email(
                        isExternal
                                ? requestModel.getEmail()
                                : userModel.getEmail()
                )
                .alt_email(
                        isExternal
                                ? requestModel.getAlternateEmail()
                                : employeeModel.getAlternateEmail()
                )
                .request_status(
                        isExternal
                                ? requestModel.getRequestStatus()
                                : EmployeeRequestStatus.APPROVED
                )
                .blood_group(
                        isExternal
                                ? requestModel.getBloodGroup()
                                : employeeModel.getBloodGroup()
                )
                .marital_status(
                        isExternal
                                ? requestModel.getMaritalStatus()
                                : employeeModel.getMaritalStatus()
                )
                .nationality(
                        isExternal
                                ? requestModel.getNationality()
                                : employeeModel.getNationality()
                )
                .current_flat_house(
                        null == jsonCurrentAddressObject ? null : jsonCurrentAddressObject.optString(AddressField.flat.name(), null)
                )
                .current_building_apartment(
                        null == jsonCurrentAddressObject ? null : jsonCurrentAddressObject.optString(AddressField.build.name(), null)
                )
                .current_area_street_sector_village(
                        null == jsonCurrentAddressObject ? null : jsonCurrentAddressObject.optString(AddressField.area.name(), null)
                )
                .current_landmark(
                        null == jsonCurrentAddressObject ? null : jsonCurrentAddressObject.optString(AddressField.land.name(), null)
                )
                .current_pin_code(
                        null == jsonCurrentAddressObject ? null : jsonCurrentAddressObject.optString(AddressField.pin.name(), null)
                )
                .current_city_town(
                        null == jsonCurrentAddressObject ? null : jsonCurrentAddressObject.optString(AddressField.city.name(), null)
                )
                .current_state(
                        null == jsonCurrentAddressObject ? null : jsonCurrentAddressObject.optString(AddressField.state.name(), null)
                )
                .current_country(
                        null == jsonCurrentAddressObject ? null : jsonCurrentAddressObject.optString(AddressField.count.name(), null)
                )
                .permanent_flat_house(
                        null == jsonPermanentAddressObject ? null : jsonPermanentAddressObject.optString(AddressField.flat.name(), null)
                )
                .permanent_building_apartment(
                        null == jsonPermanentAddressObject ? null : jsonPermanentAddressObject.optString(AddressField.build.name(), null)
                )
                .permanent_area_street_sector_village(
                        null == jsonPermanentAddressObject ? null : jsonPermanentAddressObject.optString(AddressField.area.name(), null)
                )
                .permanent_landmark(
                        null == jsonPermanentAddressObject ? null : jsonPermanentAddressObject.optString(AddressField.land.name(), null)
                )
                .permanent_pin_code(
                        null == jsonPermanentAddressObject ? null : jsonPermanentAddressObject.optString(AddressField.pin.name(), null)
                )
                .permanent_city_town(
                        null == jsonPermanentAddressObject ? null : jsonPermanentAddressObject.optString(AddressField.city.name(), null)
                )
                .permanent_state(
                        null == jsonPermanentAddressObject ? null : jsonPermanentAddressObject.optString(AddressField.state.name(), null)
                )
                .permanent_country(
                        null == jsonPermanentAddressObject ? null : jsonPermanentAddressObject.optString(AddressField.count.name(), null)
                )
                .university(
                        isExternal
                                ? requestModel.getUniversity()
                                : employeeModel.getUniversity()
                )
                .degree(
                        isExternal
                                ? requestModel.getDegree()
                                : employeeModel.getDegree()
                )
                .stream(
                        isExternal
                                ? requestModel.getStream()
                                : employeeModel.getStream()
                )
                .college(
                        isExternal
                                ? requestModel.getCollege()
                                : employeeModel.getCollege()
                )
                .cgpa(
                        isExternal
                                ? requestModel.getCgpa()
                                : employeeModel.getCgpa()
                )
                .passing_year(
                        isExternal
                                ? requestModel.getPassingYear()
                                : employeeModel.getPassingYear()
                )
                .skill_set(
                        new HashSet<>(
                                parseSkillSetToStringList(
                                        isExternal
                                                ? requestModel.getSkillSet()
                                                : employeeModel.getSkillSet()
                                )
                        )
                )
                .experience(
                        textHelper.parseJsonExperience(
                                isExternal
                                        ? requestModel.getExperience()
                                        : employeeModel.getExperience()
                        )
                )
                .eme_name(
                        isExternal
                                ? requestModel.getEmergencyName()
                                : employeeModel.getEmergencyName()
                )
                .eme_phone(
                        isExternal
                                ? requestModel.getEmergencyPhone()
                                : employeeModel.getEmergencyPhone()
                )
                .eme_relation(
                        isExternal
                                ? requestModel.getEmergencyRelation()
                                : employeeModel.getEmergencyRelation()
                )
                .physician_name(
                        isExternal
                                ? requestModel.getPhysicianName()
                                : employeeModel.getPhysicianName()
                )
                .physician_phone(
                        isExternal
                                ? requestModel.getPhysicianPhone()
                                : employeeModel.getPhysicianPhone()
                )
                .medical_conditions(
                        isExternal
                                ? requestModel.getMedicalConditions()
                                : employeeModel.getMedicalConditions()
                )
                .doc_identity(
                        identityDocumentOriginalFileName
                )
                .doc_address(
                        addressDocumentOriginalFileName
                )
                .doc_offer_letter(
                        offerLetterDocumentOriginalFileName
                )
                .doc_education(
                        educationDocumentOriginalFileName
                )
                .doc_experience(
                        experienceDocumentOriginalFileName
                )
                .doc_salary_slip(
                        salarySlipDocumentOriginalFileName
                )
                .authorities(entitlementModel.getAuthorities())
                .entitlements(entitlementModel.getEntitlements())
                .build();
    }



    private TEmployeeRequestResponse getTEmployeeRequestResponseByModel(
            RequestModel requestModel
    ) {
        if (null == requestModel)
            return null;

        String requestUid = requestModel.getRequestUid();
        EntitlementModel entitlementModel = getDataById(
                requestUid,
                () -> entitlementRepository.findByRequestUid(requestUid)
        );

        return TEmployeeRequestResponse.builder()
                .requested_at(requestModel.getCreatedAt())
                .req_id(requestUid)
                .name(
                        textHelper.buildFullName(
                                requestModel.getFirstname(),
                                requestModel.getMiddlename(),
                                requestModel.getLastname()
                        )
                )
                .email(requestModel.getEmail())
                .phone(requestModel.getPhone())
                .request_status(requestModel.getRequestStatus())
                .authorities(entitlementModel.getAuthorities())
                .build();
    }



    private TEmployeeMiniResponse getTEmployeeMiniResponseByModel(
            UserModel userModel,
            EntitlementModel entitlementModel
    ) {
        if (null == userModel && null == entitlementModel)
            return null;

        if (null == userModel)
            userModel = userRepository.findByUserUid(entitlementModel.getUserUid());

        return TEmployeeMiniResponse.builder()
                .emp_id(userModel.getUserPid())
                .name(
                        textHelper.buildFullName(
                                userModel.getFirstname(),
                                userModel.getMiddlename(),
                                userModel.getLastname()
                        )
                )
                .build();
    }



    private TEmployeeActivityResponse getTEmployeeActivityResponseByModel(
            ActivityModel activityModel
    ) {
        if (null == activityModel)
            return null;

        UserModel userModel = activityModel.getUser();
        if (null == userModel)
            return null;

        String actionModel = activityModel.getAction();
        if (null == actionModel)
            return null;

        String viewModel = activityModel.getView();
        String userUid = userModel.getUserUid();
        String userName = textHelper.buildFullName(
                userModel.getFirstname(),
                userModel.getMiddlename(),
                userModel.getLastname()
        );

        String message = null;
        if (View.COLLECTIONS.name().equals(viewModel)) {
            message = "Exported Receipts from Collections as ";
        } else if (View.BOOKINGS.name().equals(viewModel)) {
            message = "Exported Bookings as ";
        }

        if (Action.EXP_PDF.name().equals(actionModel)) {
            message += "PDF (";
        } else if (Action.EXP_EXCEL.name().equals(actionModel)) {
            message += "EXCEL (";
        }

        message += textHelper.getElementsByStringList(activityModel.getReferenceId()) + ")";
        return TEmployeeActivityResponse.builder()
                .emp_id(userUid)
                .emp_name(userName)
                .emp_designation(entitlementRepository.findAuthoritiesByUserUid(userUid).getAuthorities())
                .emp_action(message)
                .emp_action_at(activityModel.getCreatedAt())
                .build();
    }



    private TNotificationResponse getTNotificationResponseByModel(
            NotificationUserMapping mapping
    ) {
        if (null == mapping)
            return null;

        NotificationModel notificationModel = mapping.getNotification();
        if (null == notificationModel)
            return null;

        String notificationMessage = notificationModel.getMessage();
        if (textHelper.isBlank(notificationMessage))
            return null;

        return TNotificationResponse.builder()
                .notify_id(notificationModel.getNotificationId())
                .notify_at(notificationModel.getCreatedAt())
                .seen(mapping.isSeen())
                .message(notificationModel.getMessage())
                .view(
                        null == notificationModel.getView() ? null :
                        notificationModel.getView()
                )
                .ref_id(notificationModel.getReferenceId())
                .build();
    }



    private LeaveResponse getLeaveResponseByModel(LeaveRequestModel leaveModel, boolean isWithdrewIncluded) {
        if (null == leaveModel || null == leaveModel.getAppliedBy() || !isWithdrewIncluded && LeaveStatus.WITHDREW.equals(leaveModel.getStatus()))
            return null;

        UserModel appliedByUserModel = leaveModel.getAppliedBy();
        UserModel handledByUserModel = leaveModel.getHandledBy();
        return LeaveResponse.builder()
                .leave_id(leaveModel.getLeaveRequestUid())
                .emp_id(appliedByUserModel.getUserUid())
                .name(
                        textHelper.buildFullName(
                                appliedByUserModel.getFirstname(),
                                appliedByUserModel.getMiddlename(),
                                appliedByUserModel.getLastname()
                        )
                )
                .start(leaveModel.getStartDate())
                .end(leaveModel.getEndDate())
                .type(leaveModel.getType())
                .status(leaveModel.getStatus())
                .reason(leaveModel.getReason())
                .handled_by(
                        null == handledByUserModel ? null :
                        textHelper.buildFullName(
                                handledByUserModel.getFirstname(),
                                handledByUserModel.getMiddlename(),
                                handledByUserModel.getLastname()
                        )
                )
                .build();
    }




    @Override
    public <T, R> List<R> getListOfResponses(
            List<T> list,
            Function<T, R> mappingFunction
    ) {
        if (null == list || list.isEmpty())
            return new ArrayList<>();

//        if (list.size() > LOOP_THRESHOLD)
//            return list.parallelStream()
//                    .map(mappingFunction)
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
        return list.stream()
                .map(mappingFunction)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



    @Override
    public <T> T getOptionalDataById(String Id, Supplier<Optional<T>> dataRetrievalFunction) {
        if (textHelper.isBlank(Id))
            throw new InvalidDataException("Identifier cannot be null.");

        return dataRetrievalFunction.get()
                .orElseThrow(() -> new ResourceNotFoundException("Sorry, we couldn't find the requested (" + Id + ")."));
    }

    @Override
    public <T> T getDataById(String id, Supplier<T> dataRetrievalFunction) {
        if (textHelper.isBlank(id))
            throw new InvalidDataException("Identifier cannot be null.");

        T data = dataRetrievalFunction.get();
        if (data == null)
            throw new ResourceNotFoundException("Sorry, we couldn't find the requested (" + id + ").");

        return data;
    }

}
