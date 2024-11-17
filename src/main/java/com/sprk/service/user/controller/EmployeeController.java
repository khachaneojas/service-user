package com.sprk.service.user.controller;

import com.sprk.commons.annotation.Auditor;
import com.sprk.commons.lang.JwtWizard;
import com.sprk.commons.tag.Action;
import com.sprk.commons.tag.View;
import com.sprk.commons.entity.primary.user.tag.LeaveStatus;
import com.sprk.commons.entity.primary.user.tag.ToDoStatus;
import com.sprk.service.user.annotation.DefaultResponses;
import com.sprk.service.user.dto.payload.*;
import com.sprk.service.user.dto.response.TSkillSetResponse;
import com.sprk.commons.dto.APIResponse;
import com.sprk.service.user.dto.response.common.FileDataResponse;
import com.sprk.service.user.dto.response.common.JwtTokenResponse;
import com.sprk.commons.dto.TokenValidationResponse;
import com.sprk.service.user.enums.*;
import com.sprk.service.user.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.*;


@RestController
@RequestMapping(path = "/api/auth")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final JwtWizard jwtWizard;

    @Autowired
    public EmployeeController(
            EmployeeService employeeService,
            JwtWizard jwtWizard
    ) {
        this.employeeService = employeeService;
        this.jwtWizard = jwtWizard;
    }








//  GET
    /**
     * Retrieves user details based on the information provided in the TokenValidationResponse.
     *
     * @param validationResponse The TokenValidationResponse containing information for validating and retrieving user details.
     * @return ResponseEntity with ApiResponse containing user details retrieved using the provided token validation response.
     */
    @GetMapping(path = "/")
    @Operation(
            summary = "GET USER DETAILS BY TOKEN",
            description = "Retrieve user details based on the information provided in the TokenValidationResponse.\n\n" +
                    "Access Control:\n" +
                    "This endpoint requires the user to have the 'Auditor' role.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a TokenValidationResponse, the server calls the 'getUserDetails' method from the 'employeeService'.\n" +
                    "2. The 'getUserDetails' method retrieves user details based on the UID from the token validation response.\n" +
                    "3. The user details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User details retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor
    public ResponseEntity<?> getUserDetailsByToken(
            TokenValidationResponse validationResponse
    ) {
        APIResponse<?> response = employeeService.getUserDetailsByToken(validationResponse);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieves employees eligible for transferring enquiries.
     *
     * @return ResponseEntity with ApiResponse containing transferable employees' details.
     */
    @GetMapping(path = "/users/transfer")
    @Operation(
            summary = "GET ENQUIRY TRANSFERABLE EMPLOYEES",
            description = "Retrieve employees eligible for transferring enquiries.\n\n" +
                    "Access Control:\n" +
                    "This endpoint requires the user to have the 'Auditor' role with permission to view and create leads.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint, the server calls the 'getEnquiryTransferableEmployees' method from the 'employeeService'.\n" +
                    "2. The 'getEnquiryTransferableEmployees' method retrieves employees with 'ROLE_ADMIN' or 'ROLE_SALES' authorities.\n" +
                    "3. The employee details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Transferable employees retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.LEADS, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getEnquiryTransferableEmployees() {
        APIResponse<?> response = employeeService.getEnquiryTransferableEmployees();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }





    /**
     * Retrieves all faculties with the 'ROLE_FACULTY' authority.
     *
     * @return ResponseEntity with ApiResponse containing faculty details.
     */
    @GetMapping(path = "/users/skill")
    @Operation(
            summary = "GET ALL FACULTIES",
            description = "Retrieve all faculties with the 'ROLE_FACULTY' authority.\n\n" +
                    "Access Control:\n" +
                    "This endpoint requires the user to have the 'Auditor' role with permission to view and create batches, faculty schedules, and student schedules.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint, the server calls the 'getAllFaculties' method from the 'employeeService'.\n" +
                    "2. The 'getAllFaculties' method retrieves all users with 'ROLE_FACULTY' authority.\n" +
                    "3. The faculty details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "All faculties retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(
            allowedViews = {View.BATCHES, View.FACULTY_SCHEDULE, View.STUDENT_SCHEDULE, View.MY_BATCHES, View.DASHBOARD},
            allowedActions = {Action.VIEW, Action.CREATE}
    )
    public ResponseEntity<?> getAllFaculties() {
        // Retrieve all faculties using the employeeService.
        APIResponse<?> response = employeeService.getAllFaculties();
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }





    /**
     * Retrieves all faculties with expertise in a specific skill (course).
     *
     * @param request The name of the skill (course) to filter faculties.
     * @return ResponseEntity with ApiResponse containing faculty details.
     */
    @PostMapping(path = "/users/skill")
    @Operation(
            summary = "GET ALL FACULTIES BY COURSE",
            description = "Retrieve all faculties with expertise in a specific skill (course).\n\n" +
                    "Access Control:\n" +
                    "This endpoint requires the user to have the 'Auditor' role with permission to view and create batches, faculty schedules, and student schedules.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a specific skill name, the server calls the 'getAllFacultiesByCourse' method from the 'employeeService'.\n" +
                    "2. The 'getAllFacultiesByCourse' method retrieves all users with expertise in the specified skill (course).\n" +
                    "3. The faculty details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "All faculties for the specified skill retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(
            allowedViews = {View.BATCHES, View.FACULTY_SCHEDULE, View.STUDENT_SCHEDULE, View.MY_BATCHES, View.DASHBOARD},
            allowedActions = {Action.VIEW, Action.CREATE}
    )
    public ResponseEntity<?> getAllFacultiesByCourse(
            @Parameter(
                    name = "skillName",
                    description = "The name of the skill (course) to filter faculties.",
                    required = true,
                    example = "Java Programming"
            )
            @RequestBody(required = false) SkillRequest request
    ) {
        if (null == request) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        // Retrieve all faculties with expertise in the specified skill using the employeeService.
        APIResponse<?> response = employeeService.getAllFacultiesByCourse(request.getCourse());
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    // NEW


    /**
     * Endpoint to get skill set information by token validation response.
     *
     * @param validationResponse Token validation response containing user information.
     * @return ResponseEntity containing the APIResponse<List<TSkillSetResponse>> with skill set information or an error message.
     */
    @GetMapping(path = "/skill-set")
    @Operation(
            summary = "GET SKILL SET BY TOKEN",
            description = "Endpoint to get skill set information by token validation response.\n\n" +
                    "Path:\n" +
                    "- GET /skill-set\n\n" +
                    "Request Parameters:\n" +
                    "- validationResponse: Token validation response containing user information.\n\n" +
                    "Response Body:\n" +
                    "- APIResponse<List<TSkillSetResponse>>: Response containing a list of TSkillSetResponse objects or an error message.\n",
            tags = {"SKILL SET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Skill set information retrieved successfully."
            )
    })
    @Auditor
    public ResponseEntity<APIResponse<List<TSkillSetResponse>>> getSkillSetByToken(
            TokenValidationResponse validationResponse
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeService.getSkillSetByToken(validationResponse));
    }







    /**
     * Retrieve details of a user based on the provided user UID.
     *
     * @param userUid The unique identifier of the user to retrieve details.
     * @return ResponseEntity with ApiResponse containing user details.
     */
    @GetMapping(path = "/{userUid}")
    @Operation(
            summary = "GET USER DETAILS BY UID",
            description = "Retrieve details of a user based on the provided user UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create employees.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a specific user UID, the server calls the 'getUserDetailsByUid' method from the 'employeeService'.\n" +
                    "2. The 'getUserDetailsByUid' method retrieves details of the user using the user UID.\n" +
                    "3. The user details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User details retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getUserDetailsByUid(
            @Parameter(
                    name = "userUid",
                    description = "The unique identifier of the user to retrieve details.",
                    required = true,
                    example = "123456"
            )
            @PathVariable("userUid") String userUid
    ) {
        // Retrieve details of the user based on the user UID using the employeeService.
        APIResponse<?> response = employeeService.getUserDetailsByUid(userUid);
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }





    /**
     * Retrieve a table of all employees.
     *
     * @return ResponseEntity with ApiResponse containing a table of all employees.
     */
    @GetMapping(path = "/emp")
    @Operation(
            summary = "GET TABLE OF EMPLOYEES",
            description = "Retrieve a table of all employees.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create employees.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint, the server calls the 'getTableOfEmployees' method from the 'employeeService'.\n" +
                    "2. The 'getTableOfEmployees' method retrieves a table of all employees.\n" +
                    "3. The employee table is wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Table of employees retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getTableOfEmployees() {
        // Retrieve a table of all employees using the employeeService.
        APIResponse<?> response = employeeService.getTableOfEmployees();
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieve a table of all employee requests.
     *
     * @return ResponseEntity with ApiResponse containing a table of all employee requests.
     */
    @GetMapping(path = "/req")
    @Operation(
            summary = "GET TABLE OF EMPLOYEE REQUESTS",
            description = "Retrieve a table of all employee requests.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create employee requests.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint, the server calls the 'getTableOfEmployeeRequests' method from the 'employeeService'.\n" +
                    "2. The 'getTableOfEmployeeRequests' method retrieves a table of all employee requests.\n" +
                    "3. The employee request table is wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Table of employee requests retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.PORTAL_ACCESS_REQUEST, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getTableOfEmployeeRequests() {
        // Retrieve a table of all employee requests using the employeeService.
        APIResponse<?> response = employeeService.getTableOfEmployeeRequests();
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieve notifications for a user based on the provided token validation response.
     *
     * @param validationResponse The token validation response containing the user PID.
     * @return ResponseEntity with ApiResponse containing notifications for the user.
     */
    @GetMapping(path = "/notify")
    @Operation(
            summary = "GET NOTIFICATIONS BY TOKEN",
            description = "Retrieve notifications for a user based on the provided token validation response.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must have appropriate permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response, the server calls the 'getNotificationsByToken' method from the 'employeeService'.\n" +
                    "2. The 'getNotificationsByToken' method retrieves notifications for the user using the provided token validation response.\n" +
                    "3. The notifications are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notifications retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor
    public ResponseEntity<?> getNotificationsByToken(
            @Parameter(
                    description = "The token validation response containing the user PID.",
                    required = true
            )
            TokenValidationResponse validationResponse
    ) {
        // Retrieve notifications for the user using the token validation response.
        APIResponse<?> response = employeeService.getNotificationsByToken(validationResponse);
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping(path = "/notifications")
    @Auditor
    public ResponseEntity<?> getAllNotificationsByToken(
            @Parameter(description = "The token validation response containing the user PID.", required = true)
            TokenValidationResponse validationResponse,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam (value = "seen", defaultValue = "true") boolean seen
    ) {
        // Sort by "createdAt" in descending order (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));


        APIResponse<?> response = employeeService.getAllNotificationsByToken(validationResponse, seen, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }






    /**
     * Retrieve a table of activities.
     *
     * @return ResponseEntity with ApiResponse containing a table of activities.
     */
    @GetMapping(path = "/act")
    @Operation(
            summary = "GET TABLE OF ACTIVITIES",
            description = "Retrieve a table of activities.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create activities.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint, the server calls the 'getTableOfActivities' method from the 'employeeService'.\n" +
                    "2. The 'getTableOfActivities' method retrieves a table of activities.\n" +
                    "3. The activity table is wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Table of activities retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.ACTIVITIES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getTableOfActivities() {
        // Retrieve a table of activities using the employeeService.
        APIResponse<?> response = employeeService.getTableOfActivities();
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieve details of an employee by their unique identifier.
     *
     * @param userUid The unique identifier of the employee to retrieve details.
     * @return ResponseEntity with ApiResponse containing details of the employee.
     */
    @GetMapping(path = "/emp/{userUid}")
    @Operation(
            summary = "GET EMPLOYEE DETAILS BY UID",
            description = "Retrieve details of an employee by their unique identifier.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create employees.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a specific user UID, the server calls the 'getEmployeeDetailsByUid' method from the 'employeeService'.\n" +
                    "2. The 'getEmployeeDetailsByUid' method retrieves details of the employee using the user UID.\n" +
                    "3. The employee details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee details retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getEmployeeDetailsByUid(
            @Parameter(
                    name = "userUid",
                    description = "The unique identifier of the employee to retrieve details.",
                    required = true,
                    example = "EMP123456"
            )
            @PathVariable("userUid") String userUid
    ) {
        // Retrieve details of the employee based on the user UID using the employeeService.
        APIResponse<?> response = employeeService.getEmployeeDetailsByUid(userUid);
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieve details of an employee's requests by their user unique identifier.
     *
     * @param userUid The unique identifier of the employee to retrieve request details.
     * @return ResponseEntity with ApiResponse containing details of the employee's requests.
     */
    @GetMapping(path = "/req/e/{userUid}")
    @Operation(
            summary = "GET EMPLOYEE REQUEST DETAILS BY USER UID",
            description = "Retrieve details of an employee's requests by their user unique identifier.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create employee requests.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a specific user UID, the server calls the 'getEmployeeRequestDetailsByUserUid' method from the 'employeeService'.\n" +
                    "2. The 'getEmployeeRequestDetailsByUserUid' method retrieves details of the employee's requests using the user UID.\n" +
                    "3. The request details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee request details retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getEmployeeRequestDetailsByUserUid(
            @Parameter(
                    name = "userUid",
                    description = "The unique identifier of the employee to retrieve request details.",
                    required = true,
                    example = "EMP123456"
            )
            @PathVariable("userUid") String userUid
    ) {
        // Retrieve details of the employee's requests based on the user UID using the employeeService.
        APIResponse<?> response = employeeService.getEmployeeRequestDetailsByUserUid(userUid);
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieve details of an employee's request by its unique identifier.
     *
     * @param requestUid The unique identifier of the request to retrieve details.
     * @return ResponseEntity with ApiResponse containing details of the employee's request.
     */
    @GetMapping(path = "/req/{requestUid}")
    @Operation(
            summary = "GET EMPLOYEE REQUEST DETAILS BY REQUEST UID",
            description = "Retrieve details of an employee's request by its unique identifier.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create employee requests.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a specific request UID, the server calls the 'getEmployeeRequestDetailsByRequestUid' method from the 'employeeService'.\n" +
                    "2. The 'getEmployeeRequestDetailsByRequestUid' method retrieves details of the employee's request using the request UID.\n" +
                    "3. The request details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee request details retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.PORTAL_ACCESS_REQUEST, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getEmployeeRequestDetailsByRequestUid(
            @Parameter(
                    name = "requestUid",
                    description = "The unique identifier of the request to retrieve details.",
                    required = true,
                    example = "REQ123456"
            )
            @PathVariable("requestUid") String requestUid
    ) {
        // Retrieve details of the employee's request based on the request UID using the employeeService.
        APIResponse<?> response = employeeService.getEmployeeRequestDetailsByRequestUid(requestUid);
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieve details of an employee's request based on the provided token validation response.
     *
     * @param authorizationHeader The validation response containing the necessary token information.
     * @return ResponseEntity with ApiResponse containing details of the employee's request.
     */
    @GetMapping(path = "/opn/emp")
    @Operation(
            summary = "GET EMPLOYEE REQUEST DETAILS BY TOKEN",
            description = "Retrieve details of an employee's request based on the provided token validation response.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint, the server calls the 'getEmployeeRequestDetailsByToken' method from the 'employeeService' with the provided validation response.\n" +
                    "2. The 'getEmployeeRequestDetailsByToken' method retrieves details of the employee's request using the user UID obtained from the validation response.\n" +
                    "3. The request details are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee request details retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getEmployeeRequestDetailsByToken(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        // Retrieve details of the employee's request based on the token validation response using the employeeService.
        APIResponse<?> response = employeeService.getEmployeeRequestDetailsByToken(authorizationHeader);
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieve activities associated with a user based on the provided user UID.
     *
     * @param userUid The unique identifier of the user whose activities are to be retrieved.
     * @return ResponseEntity with ApiResponse containing activities associated with the user.
     */
    @GetMapping(path = "/act/{userUid}")
    @Operation(
            summary = "GET ACTIVITIES BY USER UID",
            description = "Retrieve activities associated with a user based on the provided user UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create activities.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a specific user UID, the server calls the 'getActivitiesByUid' method from the 'employeeService'.\n" +
                    "2. The 'getActivitiesByUid' method retrieves activities associated with the user using the user UID.\n" +
                    "3. The activities are wrapped in an ApiResponse.\n" +
                    "4. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Activities retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.ACTIVITIES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getActivitiesByUid(
            @Parameter(
                    name = "userUid",
                    description = "The unique identifier of the user whose activities are to be retrieved.",
                    required = true,
                    example = "USER123456"
            )
            @PathVariable("userUid") String userUid
    ) {
        // Retrieve activities associated with the user based on the user UID using the employeeService.
        APIResponse<?> response = employeeService.getActivitiesByUid(userUid);
        // Return the ApiResponse in a ResponseEntity with HttpStatus OK.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Retrieve the profile photo associated with the user identified by the token validation response.
     *
     * @param validationResponse The token validation response containing user information and permissions.
     * @return ResponseEntity containing the profile photo data.
     */
    @GetMapping(path = "/profile")
    @Operation(
            summary = "GET PROFILE PHOTO BY TOKEN",
            description = "Retrieve the profile photo associated with the user identified by the token validation response.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response, the server calls the 'getProfilePhotoByToken' method from the 'employeeService'.\n" +
                    "2. The 'getProfilePhotoByToken' method retrieves the profile photo associated with the user identified by the token.\n" +
                    "3. The profile photo data is wrapped in a FileDataResponse.\n" +
                    "4. The FileDataResponse is returned in a ResponseEntity with HTTP status OK (200) and appropriate content type header.\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile photo retrieved successfully.",
                    content = @Content(
                            mediaType = "image/jpeg",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    })
    @Auditor
    public ResponseEntity<?> getProfilePhotoByToken(
            @Parameter(
                    name = "validationResponse",
                    description = "The token validation response containing user information and permissions.",
                    required = true
            )
            TokenValidationResponse validationResponse
    ) {
        // Retrieve the profile photo associated with the user identified by the token validation response.
        FileDataResponse response = employeeService.getProfilePhotoByToken(validationResponse);
        // Return the profile photo data in a ResponseEntity with appropriate content type header.
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(response.getContentType()))
                .body(response.getData());
    }






    /**
     * Retrieve the profile photo associated with the user identified by the provided user UID.
     *
     * @param validationResponse The token validation response containing user information and permissions.
     * @param userUid            The unique identifier of the user whose profile photo is to be retrieved.
     * @return ResponseEntity containing the profile photo data.
     */
    @GetMapping(path = "/profile/{userUid}")
    @Operation(
            summary = "GET PROFILE PHOTO BY USER UID",
            description = "Retrieve the profile photo associated with the user identified by the provided user UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view employees and create new actions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response and a user UID, the server calls the 'getProfilePhotoByUid' method from the 'employeeService'.\n" +
                    "2. The 'getProfilePhotoByUid' method retrieves the profile photo associated with the specified user UID.\n" +
                    "3. The profile photo data is wrapped in a FileDataResponse.\n" +
                    "4. The FileDataResponse is returned in a ResponseEntity with HTTP status OK (200) and appropriate content type header.\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile photo retrieved successfully.",
                    content = @Content(
                            mediaType = "image/jpeg",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getProfilePhotoByUid(
            @Parameter(
                    name = "validationResponse",
                    description = "The token validation response containing user information and permissions.",
                    required = true
            )
            TokenValidationResponse validationResponse,
            @Parameter(
                    name = "userUid",
                    description = "The unique identifier of the user whose profile photo is to be retrieved.",
                    required = true
            )
            @PathVariable("userUid") String userUid
    ) {
        // Retrieve the profile photo associated with the user identified by the provided user UID.
        FileDataResponse response = employeeService.getProfilePhotoByUid(
                validationResponse,
                userUid
        );
        // Return the profile photo data in a ResponseEntity with appropriate content type header.

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(response.getContentType()))
                .body(response.getData());
    }








    /**
     * Retrieve the employee document of the specified type associated with the user identified by the provided user UID.
     *
     * @param validationResponse The token validation response containing user information and permissions.
     * @param documentType       The type of document to retrieve (e.g., identity proof, address proof, offer letter, etc.).
     * @param userUid            The unique identifier of the user whose document is to be retrieved.
     * @return ResponseEntity containing the requested document data.
     */
    @GetMapping("/doc/{documentType}/{userUid}")
    @Operation(
            summary = "GET EMPLOYEE DOCUMENT BY USER UID",
            description = "Retrieve the employee document of the specified type associated with the user identified by the provided user UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view employees and create new actions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response, document type, and user UID, the server calls the 'getEmployeeDocumentByUid' method from the 'employeeService'.\n" +
                    "2. The 'getEmployeeDocumentByUid' method retrieves the specified employee document associated with the specified user UID.\n" +
                    "3. The document data is wrapped in a FileDataResponse.\n" +
                    "4. The FileDataResponse is returned in a ResponseEntity with HTTP status OK (200) and appropriate content type header.\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee document retrieved successfully.",
                    content = @Content(
                            mediaType = "image/jpeg",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getEmployeeDocumentByUid(
            @Parameter(
                    name = "validationResponse",
                    description = "The token validation response containing user information and permissions.",
                    required = true
            )
            TokenValidationResponse validationResponse,
            @Parameter(
                    name = "documentType",
                    description = "The type of document to retrieve (e.g., identity proof, address proof, offer letter, etc.).",
                    required = true
            )
            @PathVariable("documentType") String documentType,
            @Parameter(
                    name = "userUid",
                    description = "The unique identifier of the user whose document is to be retrieved.",
                    required = true
            )
            @PathVariable("userUid") String userUid
    ) {
        // Retrieve the employee document of the specified type associated with the user identified by the provided user UID.
        FileDataResponse response = employeeService.getEmployeeDocumentByUid(
                validationResponse,
                documentType,
                userUid
        );
        // Return the document data in a ResponseEntity with appropriate content type header.
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(response.getContentType()))
                .body(response.getData());
    }






    /**
     * Retrieve the employee request document of the specified type associated with the request identified by the provided request UID.
     *
     * @param validationResponse The token validation response containing user information and permissions.
     * @param documentType       The type of document to retrieve (e.g., identity proof, address proof, offer letter, etc.).
     * @param requestUid         The unique identifier of the request whose document is to be retrieved.
     * @return ResponseEntity containing the requested document data.
     */
    @GetMapping("/req/doc/{documentType}/{requestUid}")
    @Operation(
            summary = "GET EMPLOYEE REQUEST DOCUMENT BY REQUEST UID",
            description = "Retrieve the employee request document of the specified type associated with the request identified by the provided request UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view requests and create new actions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response, document type, and request UID, the server calls the 'getEmployeeRequestDocumentByUid' method from the 'employeeService'.\n" +
                    "2. The 'getEmployeeRequestDocumentByUid' method retrieves the specified employee request document associated with the specified request UID.\n" +
                    "3. The document data is wrapped in a FileDataResponse.\n" +
                    "4. The FileDataResponse is returned in a ResponseEntity with HTTP status OK (200) and appropriate content type header.\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee request document retrieved successfully.",
                    content = @Content(
                            mediaType = "image/jpeg",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    })
    @Auditor(allowedViews = View.PORTAL_ACCESS_REQUEST, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getDocumentByRID(
            @Parameter(
                    name = "validationResponse",
                    description = "The token validation response containing user information and permissions.",
                    required = true
            )
            TokenValidationResponse validationResponse,
            @Parameter(
                    name = "documentType",
                    description = "The type of document to retrieve (e.g., identity proof, address proof, offer letter, etc.).",
                    required = true
            )
            @PathVariable("documentType") String documentType,
            @Parameter(
                    name = "requestUid",
                    description = "The unique identifier of the request whose document is to be retrieved.",
                    required = true
            )
            @PathVariable("requestUid") String requestUid
    ) {
        // Retrieve the employee request document of the specified type associated with the request identified by the provided request UID.
        FileDataResponse response = employeeService.getEmployeeRequestDocumentByUid(
                validationResponse,
                documentType,
                requestUid
        );
        // Return the document data in a ResponseEntity with appropriate content type header.
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(response.getContentType()))
                .body(response.getData());
    }



    /**
     * Retrieve the employee request document of the specified type associated with the request identified by the provided request UID.
     *
     * @param authorizationHeader The token validation response containing user information and permissions.
     * @param documentType       The type of document to retrieve (e.g., identity proof, address proof, offer letter, etc.).
     * @param requestUid         The unique identifier of the request whose document is to be retrieved.
     * @return ResponseEntity containing the requested document data.
     */
    @GetMapping("/opn/req/doc/{documentType}/{requestUid}")
    @Operation(
            summary = "GET EMPLOYEE REQUEST DOCUMENT BY REQUEST UID",
            description = "Retrieve the employee request document of the specified type associated with the request identified by the provided request UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view requests and create new actions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response, document type, and request UID, the server calls the 'getEmployeeRequestDocumentByUid' method from the 'employeeService'.\n" +
                    "2. The 'getEmployeeRequestDocumentByUid' method retrieves the specified employee request document associated with the specified request UID.\n" +
                    "3. The document data is wrapped in a FileDataResponse.\n" +
                    "4. The FileDataResponse is returned in a ResponseEntity with HTTP status OK (200) and appropriate content type header.\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee request document retrieved successfully.",
                    content = @Content(
                            mediaType = "image/jpeg",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    })
    public ResponseEntity<?> getDocumentByERID(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @Parameter(
                    name = "documentType",
                    description = "The type of document to retrieve (e.g., identity proof, address proof, offer letter, etc.).",
                    required = true
            )
            @PathVariable("documentType") String documentType,
            @Parameter(
                    name = "requestUid",
                    description = "The unique identifier of the request whose document is to be retrieved.",
                    required = true
            )
            @PathVariable("requestUid") String requestUid
    ) {
        // Retrieve the employee request document of the specified type associated with the request identified by the provided request UID.
        FileDataResponse response = employeeService.getEmployeeRequestDocumentByUid(
                TokenValidationResponse.builder().uid(jwtWizard.getSubject(authorizationHeader)).adminRole(false).build(),
                documentType,
                requestUid
        );
        // Return the document data in a ResponseEntity with appropriate content type header.
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(response.getContentType()))
                .body(response.getData());
    }






    /**
     * Retrieve information about employee leave requests.
     *
     * @param validationResponse The token validation response containing user information and permissions.
     * @return ResponseEntity containing the leave information wrapped in an ApiResponse.
     */
    @GetMapping(path = "/leave")
    @Operation(
            summary = "GET EMPLOYEE LEAVE",
            description = "Retrieve information about employee leave requests.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response, the server calls the 'getEmployeeLeave' method from the 'employeeService'.\n" +
                    "2. The 'getEmployeeLeave' method retrieves all leave requests associated with the user PID from the repository.\n" +
                    "3. The retrieved leave requests are mapped to corresponding LeaveResponse objects.\n" +
                    "4. The leave information is wrapped in an ApiResponse.\n" +
                    "5. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee leave information retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor
    public ResponseEntity<?> getEmployeeLeave(
            @Parameter(
                    name = "validationResponse",
                    description = "The token validation response containing user information and permissions.",
                    required = true
            )
            TokenValidationResponse validationResponse
    ) {
        // Retrieve information about employee leave requests.
        APIResponse<?> response = employeeService.getEmployeeLeave(
                validationResponse
        );
        // Return the leave information wrapped in an ApiResponse in a ResponseEntity.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



    /**
     * Retrieve information about all employees' leave requests.
     *
     * @param validationResponse The token validation response containing user information and permissions.
     * @return ResponseEntity containing the leave information of all employees wrapped in an ApiResponse.
     */
    @GetMapping(path = "/emp/leave")
    @Operation(
            summary = "GET ALL EMPLOYEES' LEAVE",
            description = "Retrieve information about all employees' leave requests.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to view and create leave requests.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response, the server calls the 'getAllEmployeesLeaves' method from the 'employeeService'.\n" +
                    "2. The 'getAllEmployeesLeaves' method retrieves all leave requests from the repository.\n" +
                    "3. The retrieved leave requests are mapped to corresponding LeaveResponse objects.\n" +
                    "4. The leave information is wrapped in an ApiResponse.\n" +
                    "5. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "All employees' leave information retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.LEAVE_REQUESTS, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getAllEmployeesLeaves(
            @Parameter(
                    name = "validationResponse",
                    description = "The token validation response containing user information and permissions.",
                    required = true
            )
            TokenValidationResponse validationResponse
    ) {
        // Retrieve information about all employees' leave requests.
        APIResponse<?> response = employeeService.getAllEmployeesLeaves(
                validationResponse
        );
        // Return the leave information of all employees wrapped in an ApiResponse in a ResponseEntity.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }





    /**
     * Retrieve public holidays for a specified year.
     *
     * @param year The year for which public holidays are requested.
     * @return ResponseEntity containing the public holiday information for the specified year wrapped in an ApiResponse.
     */
    @GetMapping("/holidays")
    @Operation(
            summary = "GET PUBLIC HOLIDAYS BY YEAR",
            description = "Retrieve public holidays for a specified year.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with an optional 'year' parameter, the server calls the 'getPublicHolidaysByYear' method from the 'employeeService'.\n" +
                    "2. The 'getPublicHolidaysByYear' method retrieves public holidays for the specified year from the repository.\n" +
                    "3. The retrieved holiday information is formatted into HolidaysResponse objects.\n" +
                    "4. The holiday information is wrapped in an ApiResponse.\n" +
                    "5. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Public holidays for the specified year retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor
    public ResponseEntity<?> getPublicHolidaysByYear(
            TokenValidationResponse validationResponse,
            @RequestParam(name = "year", required = false) String year
    ) {
        APIResponse<?> response = employeeService.getPublicHolidaysByYear(validationResponse, year);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }




    @GetMapping(path = "/todo")
    @Auditor
    public ResponseEntity<?> getToDoList(
            @Parameter(
                    name = "validationResponse",
                    description = "The token validation response containing user information and permissions.",
                    required = true
            )
            TokenValidationResponse validationResponse
    ) {
        // Retrieve information about an employee's schedule.
        APIResponse<?> response = employeeService.getToDoList(validationResponse);
        // Return the employee's schedule information wrapped in an ApiResponse in a ResponseEntity.
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }



    /**
     * Retrieve information about an employee's schedule.
     *
     * @param validationResponse The token validation response containing user information and permissions.
     * @param userId             The unique identifier of the employee whose schedule information is to be retrieved.
     * @return ResponseEntity containing the employee's schedule information wrapped in an ApiResponse.
     */
    @GetMapping(path = "/emp/schedule/{userId}")
    @Operation(
            summary = "GET EMPLOYEE SCHEDULE",
            description = "Retrieve information about an employee's schedule.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with the specified views and actions permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a request is made to this endpoint with a token validation response and the user ID, the server calls the 'getEmployeeSchedule' method from the 'employeeService'.\n" +
                    "2. The 'getEmployeeSchedule' method retrieves the schedule information for the specified employee from the repository.\n" +
                    "3. The retrieved schedule information is mapped to corresponding EmployeeScheduleResponse objects.\n" +
                    "4. The employee's schedule information is wrapped in an ApiResponse.\n" +
                    "5. The ApiResponse is returned in a ResponseEntity with HTTP status OK (200).\n",
            tags = {"GET"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee schedule information retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = {Action.VIEW, Action.CREATE})
    public ResponseEntity<?> getEmployeeSchedule(
            @Parameter(
                    name = "validationResponse",
                    description = "The token validation response containing user information and permissions.",
                    required = true
            )
            TokenValidationResponse validationResponse,
            @Parameter(
                    name = "userId",
                    description = "The unique identifier of the employee whose schedule information is to be retrieved.",
                    required = true
            )
            @PathVariable("userId") String userId
    ) {
        // Retrieve information about an employee's schedule.
        APIResponse<?> response = employeeService.getEmployeeSchedule(
                validationResponse,
                userId
        );
        // Return the employee's schedule information wrapped in an ApiResponse in a ResponseEntity.
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }








//  POST
    /**
     * Validate the authorization token and check if it is associated with the provided views and actions.
     *
     * @param authorizationHeader The HTTP Authorization header containing the token to be validated.
     * @param validationRequest   The request containing the views and actions to be validated against the token.
     * @return ResponseEntity containing the validation response indicating the validity of the token and authorization for specified views and actions.
     */
    @PostMapping(path = "/")
    @Operation(
            summary = "VALIDATE TOKEN AND AUTHORIZATION",
            description = "Validate the authorization token and check if it is associated with the provided views and actions.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors. The 'auditToken' property in the @Auditor annotation is set to 'false'.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a POST request is made to this endpoint, the server calls the 'isTokenValid' method from the 'employeeService'.\n" +
                    "2. The 'isTokenValid' method validates the authorization token and checks if it is associated with the provided views and actions.\n" +
                    "3. The validation response indicating the validity of the token and authorization for specified views and actions is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token and authorization validation successful.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenValidationResponse.class)
                    )
            )
    })
    @Auditor(auditJwt = false)
    public ResponseEntity<?> isTokenValid(
            @Parameter(
                    name = HttpHeaders.AUTHORIZATION,
                    description = "The HTTP Authorization header containing the token to be validated.",
                    required = true
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @Parameter(
                    description = "The request containing the views and actions to be validated against the token.",
                    required = true
            )
            @Valid @RequestBody TokenValidationRequest validationRequest
    ) {
        // Validate the authorization token and check if it is associated with the provided views and actions.
        TokenValidationResponse response = employeeService.isTokenValid(
                authorizationHeader,
                validationRequest
        );
        // Return the validation response indicating the validity of the token and authorization for specified views and actions.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }








    /**
     * Sign in a user by validating the provided login credentials and generating a JWT token for authentication.
     *
     * @param loginRequest The request containing the user's login credentials.
     * @return ResponseEntity containing the JWT token response upon successful user authentication.
     */
    @PostMapping(path = "/login")
    @Operation(
            summary = "USER SIGN-IN",
            description = "Sign in a user by validating the provided login credentials and generating a JWT token for authentication.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be authenticated. The 'auditToken' property in the @Auditor annotation is set to 'false'.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a POST request is made to this endpoint, the server calls the 'signInUser' method from the 'employeeService'.\n" +
                    "2. The 'signInUser' method validates the provided login credentials and generates a JWT token for authentication.\n" +
                    "3. Upon successful authentication, the JWT token response is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User sign-in successful.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenResponse.class)
                    )
            )
    })
    @Auditor(auditJwt = false)
    public ResponseEntity<?> signInUser(
            @Parameter(
                    description = "The request containing the user's login credentials.",
                    required = true
            )
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        // Sign in the user by validating the provided login credentials and generating a JWT token.
        JwtTokenResponse response = employeeService.signInUser(loginRequest);
        // Return the JWT token response upon successful user authentication.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }








    /**
     * Refreshes the user's access token, optionally signing out from other devices.
     *
     * @param validationResponse The token validation response containing the user's authentication details.
     * @param signOutFromOtherDevices A boolean flag indicating whether to sign out the user from other devices.
     * @return ResponseEntity containing the updated JWT token response upon successful token refresh.
     */
    @PostMapping(path = "/logout")
    @Operation(
            summary = "USER LOGOUT (TOKEN REFRESH)",
            description = "Refreshes the user's access token, optionally signing out from other devices.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be authenticated. No token auditing is performed.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a POST request is made to this endpoint, the server calls the 'refreshToken' method from the 'employeeService'.\n" +
                    "2. The 'refreshToken' method refreshes the user's access token, updating the token's validity.\n" +
                    "3. If requested, the user can be signed out from other devices by updating the user's 'updatedAt' timestamp.\n" +
                    "4. The updated JWT token response is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refresh successful.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenResponse.class)
                    )
            )
    })
    @Auditor
    public ResponseEntity<?> refreshToken(
            @Parameter(
                    description = "The token validation response containing the user's authentication details.",
                    required = true
            )
            TokenValidationResponse validationResponse,
            @Parameter(
                    description = "A boolean flag indicating whether to sign out the user from other devices.",
                    required = false
            )
            @RequestHeader(value = "logout-from-other-devices", required = false) Boolean signOutFromOtherDevices)
    {
        // Call the service method to refresh the user's access token.
        JwtTokenResponse tokenResponse = employeeService.refreshToken(
                validationResponse,
                Boolean.TRUE.equals(signOutFromOtherDevices)
        );

        // Wrap the JwtTokenResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
        return new ResponseEntity<>(
                tokenResponse,
                HttpStatus.OK
        );
    }








    /**
     * Creates a new employee request based on the provided employee creation request data.
     *
     * @param request The employee creation request containing the details of the new employee.
     * @param validationResponse The token validation response containing the user's authentication details.
     * @return ResponseEntity containing the ApiResponse confirming the successful creation of the employee request.
     */
    @PostMapping(path = "/emp/add")
    @Operation(
            summary = "CREATE EMPLOYEE REQUEST",
            description = "Creates a new employee request based on the provided employee creation request data.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be authenticated and have the 'CREATE' action permission.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a POST request is made to this endpoint with the employee creation request data, the server calls the 'createEmployeeRequest' method from the 'employeeService'.\n" +
                    "2. The 'createEmployeeRequest' method processes the request, validates the input data, and creates a new employee request.\n" +
                    "3. An email is sent to the new employee containing a link for further action.\n" +
                    "4. An ApiResponse confirming the successful creation of the employee request is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Employee request created successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> createEmployeeRequest(
            @Parameter(
                    description = "The employee creation request containing the details of the new employee.",
                    required = true
            )
            @Valid @RequestBody MEmployeeCreationRequest request,
            @Parameter(
                    description = "The token validation response containing the user's authentication details.",
                    required = true
            )
            TokenValidationResponse validationResponse
    ) {
        // Call the service method to create the employee request.
        APIResponse<?> response = employeeService.createEmployeeRequest(
                validationResponse,
                request
        );

        // Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status CREATED (201).
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }








    /**
     * Creates a leave request based on the provided leave application request data.
     *
     * @param request           The apply leave request containing the details of the leave application.
     * @param validationResponse The token validation response containing the user's authentication details.
     * @return ResponseEntity containing the ApiResponse confirming the successful submission of the leave request.
     */
    @PostMapping(path = "/leave")
    @Operation(
            summary = "APPLY FOR LEAVE",
            description = "Creates a leave request based on the provided leave application request data.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be authenticated.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a POST request is made to this endpoint with the leave application request data, the server calls the 'createLeaveRequest' method from the 'employeeService'.\n" +
                    "2. The 'createLeaveRequest' method processes the request, validates the input data, and creates a new leave request.\n" +
                    "3. An ApiResponse confirming the successful submission of the leave request is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Leave request created successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor
    public ResponseEntity<?> applyForLeave(
            @Valid @RequestBody ApplyLeaveRequest request,
            TokenValidationResponse validationResponse
    ) {
        APIResponse<?> response = employeeService.createLeaveRequest(
                validationResponse,
                request
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }








    /**
     * Resends the rectification link for an employee request.
     *
     * @param request           The rectification link request containing the details for sending the link.
     * @param validationResponse The token validation response containing the user's authentication details.
     * @return ResponseEntity containing the ApiResponse indicating the success of the operation.
     */
    @PostMapping(path = "/req/link")
    @Operation(
            summary = "RESEND RECTIFICATION LINK",
            description = "Resends the rectification link for an employee request.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be authenticated and have permission to create requests.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a POST request is made to this endpoint with the rectification link request data, the server calls the 'getRectificationEmployeeRequestLink' method from the 'employeeService'.\n" +
                    "2. The 'getRectificationEmployeeRequestLink' method processes the request, validates the input data, and either generates a new token or sends a rectification email to the recipient.\n" +
                    "3. An ApiResponse indicating the success of the operation is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Rectification link sent successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.PORTAL_ACCESS_REQUEST, allowedActions = Action.CREATE)
    public ResponseEntity<?> resendLinkRequest(
            @Parameter(
                    description = "The rectification link request containing the details for sending the link.",
                    required = true
            )
            @Valid @RequestBody RectificationLinkRequest request,
            @Parameter(
                    description = "The token validation response containing the user's authentication details.",
                    required = true
            )
            TokenValidationResponse validationResponse
    ) {
        // Call the service method to resend the rectification link.
        APIResponse<?> response = employeeService.getRectificationEmployeeRequestLink(
                validationResponse,
                request
        );
        // Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



    /**
     * Resends the rectification token for an employee request.
     *
     * @param request           The rectification link request containing the details for sending the token.
     * @param validationResponse The token validation response containing the user's authentication details.
     * @return ResponseEntity containing the ApiResponse indicating the success of the operation.
     */
    @PostMapping(path = "/req/t/link")
    @Operation(
            summary = "RESEND RECTIFICATION TOKEN",
            description = "Resends the rectification token for an employee request.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be authenticated and have permission to create requests.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a POST request is made, the method 'getRectificationEmployeeRequestToken' from the service class is called.\n" +
                    "2. The service method processes the request, retrieves the necessary data, generates a new token, and sends it to the recipient via email if required.\n" +
                    "3. The service method returns an ApiResponse containing a success message if the token is generated successfully.\n\n" +
                    "Request Body Example:\n" +
                    "```\n" +
                    "{\n" +
                    "  \"req_id\": \"string\",\n" +
                    "  \"reason\": \"string\"\n" +
                    "}\n" +
                    "```\n" +
                    "The 'req_id' field contains the ID of the request, and the 'reason' field provides the reason for the rectification.\n\n" +
                    "Response Example:\n" +
                    "```\n" +
                    "{\n" +
                    "  \"message\": \"Rectification email sent to [recipient_email] successfully.\",\n" +
                    "  \"data\": null\n" +
                    "}\n" +
                    "```\n" +
                    "The response contains a success message indicating that the rectification email was sent successfully.\n"
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Rectification link resent successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.PORTAL_ACCESS_REQUEST, allowedActions = Action.CREATE)
    public ResponseEntity<?> resendTokenRequest(
            @Valid @RequestBody RectificationLinkRequest request,
            TokenValidationResponse validationResponse
    ) {
        APIResponse<?> response = employeeService.getRectificationEmployeeRequestToken(
                validationResponse,
                request
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }








    /**
     * Uploads a profile photo for the authenticated user.
     *
     * @param profileDocument    The profile photo file to be uploaded.
     * @param validationResponse The token validation response containing the user's authentication details.
     * @return ResponseEntity containing the ApiResponse indicating the success of the operation.
     */
    @PostMapping(path = "/upload/profile")
    @Operation(
            summary = "UPLOAD PROFILE PHOTO",
            description = "Uploads a profile photo for the authenticated user.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be authenticated.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. When a POST request is made, the method 'uploadDocumentsById' from the service class is called.\n" +
                    "2. The service method processes the request, validates the user's role, and uploads the profile photo.\n" +
                    "3. The service method returns an ApiResponse containing a success message if the profile photo is uploaded successfully.\n\n" +
                    "Request Parameter:\n" +
                    "`profile`: The profile photo file to be uploaded.\n\n" +
                    "Response Example:\n" +
                    "```\n" +
                    "{\n" +
                    "  \"message\": \"Documents uploaded successfully.\",\n" +
                    "  \"data\": null\n" +
                    "}\n" +
                    "```\n" +
                    "The response contains a success message indicating that the profile photo was uploaded successfully.\n"
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Document uploaded successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("profile") MultipartFile profileDocument,
            TokenValidationResponse validationResponse
    ) {
        APIResponse<?> response = employeeService.uploadDocumentsById(
                validationResponse,
                validationResponse.getUid(),
                false,
                profileDocument,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Upload documents for a specific user identified by user UID.
     *
     * @param userUid             The unique identifier of the user to upload documents for.
     * @param identityDocument    The identity document file to upload (optional).
     * @param addressDocument     The address document file to upload (optional).
     * @param offerLetterDocument The offer letter document file to upload (optional).
     * @param educationDocument   The education document file to upload (optional).
     * @param experienceDocument  The experience document file to upload (optional).
     * @param salarySlipDocument  The salary slip document file to upload (optional).
     * @param validationResponse  Token validation response to ensure authorization.
     * @return ResponseEntity with ApiResponse indicating the success of document upload.
     */
    @PostMapping("/upload/doc/{userUid}")
    @Operation(
            summary = "UPLOAD DOCUMENTS FOR USER",
            description = "Upload documents for a specific user identified by user UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors with permission to create employees.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. If the user is not authorized, an UnauthorizedException is thrown.\n" +
                    "2. Uploaded documents are processed and saved based on the user type.\n" +
                    "3. ApiResponse indicating the success of document upload is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Documents uploaded successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> uploadDocumentsById(
            @PathVariable("userUid") String userUid,
            @RequestParam(name = "ide", required = false) MultipartFile identityDocument,
            @RequestParam(name = "add", required = false) MultipartFile addressDocument,
            @RequestParam(name = "off", required = false) MultipartFile offerLetterDocument,
            @RequestParam(name = "edu", required = false) MultipartFile educationDocument,
            @RequestParam(name = "exp", required = false) MultipartFile experienceDocument,
            @RequestParam(name = "sal", required = false) MultipartFile salarySlipDocument,
            TokenValidationResponse validationResponse
    ) {
        APIResponse<?> response = employeeService.uploadDocumentsById(
                validationResponse,
                userUid,
                false,
                null,
                identityDocument,
                addressDocument,
                offerLetterDocument,
                educationDocument,
                experienceDocument,
                salarySlipDocument
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Upload documents for an external employee request.
     *
     * @param identityDocument    The identity document file to upload (optional).
     * @param addressDocument     The address document file to upload (optional).
     * @param offerLetterDocument The offer letter document file to upload (optional).
     * @param educationDocument   The education document file to upload (optional).
     * @param experienceDocument  The experience document file to upload (optional).
     * @param salarySlipDocument  The salary slip document file to upload (optional).
     * @param authorizationHeader  Token validation response to ensure authorization.
     * @return ResponseEntity with ApiResponse indicating the success of document upload.
     */
    @PostMapping("/opn/emp")
    @Operation(
            summary = "UPLOAD DOCUMENTS FOR EXTERNAL EMPLOYEE",
            description = "Upload documents for an external employee request.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Uploaded documents are processed and saved for external employee requests.\n" +
                    "2. ApiResponse indicating the success of document upload is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Documents uploaded successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    public ResponseEntity<?> uploadDocumentsByExternalRequest(
            @RequestParam(name = "ide", required = false) MultipartFile identityDocument,
            @RequestParam(name = "add", required = false) MultipartFile addressDocument,
            @RequestParam(name = "off", required = false) MultipartFile offerLetterDocument,
            @RequestParam(name = "edu", required = false) MultipartFile educationDocument,
            @RequestParam(name = "exp", required = false) MultipartFile experienceDocument,
            @RequestParam(name = "sal", required = false) MultipartFile salarySlipDocument,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        APIResponse<?> uploadIdentity = employeeService.uploadDocumentsById(
                null,
                jwtWizard.getSubject(authorizationHeader),
                true,
                null,
                identityDocument,
                addressDocument,
                offerLetterDocument,
                educationDocument,
                experienceDocument,
                salarySlipDocument
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(uploadIdentity);
    }




    @PostMapping(path = "/todo/add")
    @Auditor
    public ResponseEntity<?> addToDoList(
            TokenValidationResponse validationResponse,
            @RequestBody AddToDoRequest addToDoRequest
    ) {
        APIResponse<?> response = employeeService.addToDoList(validationResponse, addToDoRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }





    /**
     * Export data based on specified content type and reference IDs.
     *
     * @param contentType       The type of content to export (e.g., "pdf" or "excel").
     * @param referenceIds      The set of reference IDs for the data to be exported.
     * @param validationResponse  Token validation response to ensure authorization.
     * @return ResponseEntity indicating the success of the export request.
     */
    @PostMapping(path = "/exp/{tabName}/{contentType}")
    @Operation(
            summary = "EXPORT REQUEST",
            description = "Export data based on specified content type and reference IDs.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. The export request is logged as an activity.\n" +
                    "2. ApiResponse indicating the success of the export request is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Export request processed successfully."
            )
    })
    @Auditor
    public ResponseEntity<?> exportRequest(
            @PathVariable("contentType") String contentType,
            @PathVariable("tabName") View tabName,
            @Valid @RequestBody Set<String> referenceIds,
            TokenValidationResponse validationResponse
    ) {
        employeeService.activityLogger(
                validationResponse,
                tabName,
                (
                        "pdf".equalsIgnoreCase(contentType)
                                ? Action.EXP_PDF :
                        "excel".equalsIgnoreCase(contentType)
                                ? Action.EXP_EXCEL
                                : null
                ),
                referenceIds
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }






    /**
     * Mark notifications as read.
     *
     * @param notificationIds    The list of notification IDs to mark as read.
     * @param validationResponse Token validation response to ensure authorization.
     * @return ResponseEntity indicating the success of marking notifications as read.
     */
    @PostMapping(path = "/notify")
    @Operation(
            summary = "MARK NOTIFICATIONS AS READ",
            description = "Mark notifications as read.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. The notifications specified by their IDs are marked as read.\n" +
                    "2. ApiResponse indicating the success of marking notifications as read is returned.\n",
            tags = {"POST"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notifications marked as read successfully."
            )
    })
    @Auditor
    public ResponseEntity<?> markNotificationAsRead(
            @Valid @RequestBody List<Long> notificationIds,
            TokenValidationResponse validationResponse
    ) {
        employeeService.markNotificationAsRead(
                validationResponse,
                notificationIds
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }










//  PATCH
    /**
     * Update an external employee request as a draft.
     *
     * @param request            The update request containing the new employee details.
     * @param authorizationHeader Token validation response to ensure authorization.
     * @return ResponseEntity indicating the success of updating the employee registration form as a draft.
     */
    @PatchMapping(path = "/opn/emp/draft")
    @Operation(
            summary = "UPDATE EXTERNAL EMPLOYEE REQUEST AS DRAFT",
            description = "Update an external employee request as a draft.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. The employee registration form is updated with the provided details.\n" +
                    "2. ApiResponse indicating the success of updating the form as a draft is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee registration form updated successfully as a draft."
            )
    })
    public ResponseEntity<?> updateRequestDraft(
            @Parameter(
                    name = "request",
                    description = "The update request containing the new employee details.",
                    required = true
            )
            @Valid @RequestBody UpdateExternalEmployeeRequest request,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        APIResponse<?> response = employeeService.updateExternalEmployeeRequest(
                authorizationHeader,
                request,
                true
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



    @PatchMapping(path = "/todo/{toDoUid}")
    @Auditor
    public ResponseEntity<?> changeStatusOfToDO(
            @PathVariable("toDoUid") String toDoUid,
            @RequestParam("status") ToDoStatus status,
            TokenValidationResponse validationResponse
    ) {
        APIResponse<?> response = employeeService.changeStatusOfToDo(validationResponse, toDoUid, status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Update an external employee request.
     *
     * @param request            The update request containing the new employee details.
     * @param authorizationHeader Token validation response to ensure authorization.
     * @return ResponseEntity indicating the success of updating the employee registration form.
     */
    @PatchMapping(path = "/opn/emp")
    @Operation(
            summary = "UPDATE EXTERNAL EMPLOYEE REQUEST",
            description = "Update an external employee request.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. The employee registration form is updated with the provided details.\n" +
                    "2. ApiResponse indicating the success of updating the form is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee registration form updated successfully."
            )
    })
    public ResponseEntity<?> updateRequestExternal(
            @Valid @RequestBody UpdateExternalEmployeeRequest request,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        APIResponse<?> response = employeeService.updateExternalEmployeeRequest(
                authorizationHeader,
                request,
                false
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



    /**
     * Update an employee's details.
     *
     * @param validationResponse Token validation response to ensure authorization.
     * @param userUid            The unique identifier of the user whose details are to be updated.
     * @param request            The update request containing the new employee details.
     * @return ResponseEntity indicating the success of updating the employee's details.
     */
    @PatchMapping(path = "/emp/{userUid}")
    @Operation(
            summary = "UPDATE EMPLOYEE DETAILS",
            description = "Update an employee's details.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors and have the required permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. The employee's details are updated based on the provided request.\n" +
                    "2. ApiResponse indicating the success of updating the employee's details is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee's details updated successfully."
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> updateEmployee(
            TokenValidationResponse validationResponse,
            @PathVariable("userUid") String userUid,
            @Valid @RequestBody UpdateEmployeeRequest request
    ) {
        APIResponse<?> response = employeeService.updateEmployee(
                validationResponse,
                userUid,
                request
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }





    /**
     * Update an employee request.
     *
     * @param validationResponse Token validation response to ensure authorization.
     * @param requestUid         The unique identifier of the employee request to be updated.
     * @param request            The update request containing the new employee details.
     * @return ResponseEntity indicating the success of updating the employee request.
     */
    @PatchMapping(path = "/req/{requestUid}")
    @Operation(
            summary = "UPDATE EMPLOYEE REQUEST",
            description = "Update an employee request.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors and have the required permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. The employee request is updated based on the provided request.\n" +
                    "2. ApiResponse indicating the success of updating the employee request is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee request updated successfully."
            )
    })
    @Auditor(allowedViews = View.PORTAL_ACCESS_REQUEST, allowedActions = Action.CREATE)
    public ResponseEntity<?> updateRequest(
            TokenValidationResponse validationResponse,
            @PathVariable("requestUid") String requestUid,
            @Valid @RequestBody UpdateEmployeeRequest request
    ) {
        APIResponse<?> response = employeeService.updateEmployeeRequest(
                validationResponse,
                requestUid,
                request
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }



    /**
     * Update entitlements of an employee.
     *
     * @param userUid                  The unique identifier of the user whose entitlements are to be updated.
     * @param request The request containing the updated entitlements.
     * @return ApiResponse indicating the success of updating the entitlements.
     */
    @PatchMapping(path = "/emp/entitle/{userUid}")
    @Operation(
            summary = "UPDATE EMPLOYEE ENTITLEMENTS",
            description = "Update entitlements of an employee.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors and have the required permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. The entitlements of the employee are updated based on the provided request.\n" +
                    "2. ApiResponse indicating the success of updating the entitlements is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Entitlements of the employee updated successfully."
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> updateEmployeeEntitlements(
            TokenValidationResponse validationResponse,
            @PathVariable("userUid") String userUid,
            @Valid @RequestBody UpdateEntitlementsRequest request
    ) {
        APIResponse<?> response = employeeService.updateEmployeeEntitlements(
                userUid,
                request
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }



    /**
     * Approve the status of an employee request.
     *
     * @param validationResponse      The validation response containing user information.
     * @param request  The request containing details to approve the employee request.
     * @return ApiResponse indicating the success of changing the employee request status.
     */
    @PatchMapping(path = "/req/approve")
    @Operation(
            summary = "APPROVE EMPLOYEE REQUEST STATUS",
            description = "Approve the status of an employee request.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors and have the required permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Validate and sanitize the request data.\n" +
                    "2. Check and process the request data to approve the employee request.\n" +
                    "3. Send an email with access approval details to the employee.\n" +
                    "4. ApiResponse indicating the success of changing the employee request status is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee request status changed successfully."
            )
    })
    @Auditor(allowedViews = View.PORTAL_ACCESS_REQUEST, allowedActions = Action.CREATE)
    public ResponseEntity<?> approveEmployeeRequestStatus(
            TokenValidationResponse validationResponse,
            @Valid @RequestBody ApproveEmployeeRequest request
    ) {
        APIResponse<?> response = employeeService.approveEmployeeRequestStatus(
                validationResponse,
                request
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }





    /**
     * Endpoint to update the schedule for an employee.
     *
     * @param validationResponse    The token validation response containing the user's authentication details.
     * @param request               The request object containing the updated schedule details for the employee.
     * @return ResponseEntity containing the response message upon successful update of the employee's schedule.
     */
    @PatchMapping(path = "/emp/update/schedule")
    @Operation(
            summary = "UPDATE EMPLOYEE SCHEDULE",
            description = "Updates the schedule for a specific employee.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be authenticated and have the necessary permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. A PATCH request to this endpoint updates the schedule for the specified employee.\n" +
                    "2. The updated schedule details, including clock-in and clock-out times, annual leaves, and working days, are provided in the request.\n" +
                    "3. Upon successful update, a confirmation message is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Schedule updated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> updateEmployeeSchedule(
            @Parameter(
                    description = "The token validation response containing the user's authentication details.",
                    required = true
            )
            TokenValidationResponse validationResponse,
            @Valid @RequestBody ApproveEmployeeRequest request
    ) {
        // Call the service method to update the employee's schedule
        APIResponse<?> response = employeeService.updateEmployeeSchedule(
                validationResponse,
                request
        );
        // Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }





    @PatchMapping(path = "/req/decline/{requestUid}")
    @Operation(
            summary = "DECLINE EMPLOYEE REQUEST STATUS",
            description = "Decline the status of an employee request.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors and have the required permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Fetch the employee request model by UID.\n" +
                    "2. Check and process the request data to decline the employee request.\n" +
                    "3. Send an email with access denial details to the employee.\n" +
                    "4. ApiResponse indicating the success of changing the employee request status is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee request status changed successfully."
            )
    })
    @Auditor(allowedViews = View.PORTAL_ACCESS_REQUEST, allowedActions = Action.CREATE)
    public ResponseEntity<?> declineEmployeeRequestStatus(
            TokenValidationResponse validationResponse,
            @PathVariable("requestUid") String requestUid
    ) {
        APIResponse<?> response = employeeService.declineEmployeeRequestStatus(
                validationResponse,
                requestUid
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }



    /**
     * Update the access status of an employee.
     *
     * @param validationResponse The validation response containing user information.
     * @param userUid             The UID of the employee whose access status to update.
     * @return ApiResponse indicating the success of updating the employee access status.
     */
    @PatchMapping(path = "/emp/enable/{userUid}")
    @Operation(
            summary = "UPDATE EMPLOYEE ACCESS STATUS",
            description = "Update the access status of an employee.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors and have the required permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Check if the user is authorized to perform this action.\n" +
                    "2. Fetch the user model by UID.\n" +
                    "3. Update the access status of the employee.\n" +
                    "4. ApiResponse indicating the success of updating the employee access status is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee access status updated successfully."
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> enableEmployeeAccess(
            TokenValidationResponse validationResponse,
            @PathVariable("userUid") String userUid
    ) {
        APIResponse<?> response = employeeService.updateEmployeeAccess(
                validationResponse,
                userUid,
                true
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }



    /**
     * Update the access status of an employee.
     *
     * @param validationResponse The validation response containing user information.
     * @param userUid             The UID of the employee whose access status to update.
     * @return ApiResponse indicating the success of updating the employee access status.
     */
    @PatchMapping(path = "/emp/disable/{userUid}")
    @Operation(
            summary = "UPDATE EMPLOYEE ACCESS STATUS",
            description = "Update the access status of an employee.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors and have the required permissions.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Check if the user is authorized to perform this action.\n" +
                    "2. Fetch the user model by UID.\n" +
                    "3. Update the access status of the employee.\n" +
                    "4. ApiResponse indicating the success of updating the employee access status is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee access status updated successfully."
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> disableEmployeeAccess(
            TokenValidationResponse validationResponse,
            @PathVariable("userUid") String userUid
    ) {
        APIResponse<?> response = employeeService.updateEmployeeAccess(
                validationResponse,
                userUid,
                false
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }




    /**
     * Update the status of a leave request.
     *
     * @param validationResponse The validation response containing user information.
     * @param leaveUid           The UID of the leave request to update.
     * @return ApiResponse indicating the success of updating the leave request status.
     */
    @PatchMapping(path = "/leave/withdraw/{leaveUid}")
    @Operation(
            summary = "WITHDRAW EMPLOYEE LEAVE REQUEST",
            description = "Update the status of a leave request to 'WITHDREW'.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Fetch the leave request model by UID.\n" +
                    "2. Update the status of the leave request to 'WITHDREW'.\n" +
                    "3. ApiResponse indicating the success of updating the leave request status is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Leave request status updated successfully."
            )
    })
    @Auditor
    public ResponseEntity<?> withdrawEmployeeLeaveRequest(
            TokenValidationResponse validationResponse,
            @PathVariable("leaveUid") String leaveUid
    ) {
        APIResponse<?> response = employeeService.updateLeaveStatus(
                validationResponse,
                leaveUid,
                LeaveStatus.WITHDREW
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }



    /**
     * Update the status of a leave request to 'APPROVED'.
     *
     * @param validationResponse The validation response containing user information.
     * @param leaveUid           The UID of the leave request to update.
     * @return ApiResponse indicating the success of updating the leave request status.
     */
    @PatchMapping(path = "/leave/approve/{leaveUid}")
    @Operation(
            summary = "APPROVE EMPLOYEE LEAVE REQUEST",
            description = "Update the status of a leave request to 'APPROVED'.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Fetch the leave request model by UID.\n" +
                    "2. Update the status of the leave request to 'APPROVED'.\n" +
                    "3. ApiResponse indicating the success of updating the leave request status is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Leave request status updated successfully."
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> approveEmployeeLeaveRequest(
            TokenValidationResponse validationResponse,
            @PathVariable("leaveUid") String leaveUid
    ) {
        APIResponse<?> response = employeeService.updateLeaveStatus(
                validationResponse,
                leaveUid,
                LeaveStatus.APPROVED
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }



    /**
     * Update the status of a leave request to 'DECLINED'.
     *
     * @param validationResponse The validation response containing user information.
     * @param leaveUid           The UID of the leave request to update.
     * @return ApiResponse indicating the success of updating the leave request status.
     */
    @PatchMapping(path = "/leave/deny/{leaveUid}")
    @Operation(
            summary = "DENY EMPLOYEE LEAVE REQUEST",
            description = "Update the status of a leave request to 'DECLINED'.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Fetch the leave request model by UID.\n" +
                    "2. Update the status of the leave request to 'DECLINED'.\n" +
                    "3. ApiResponse indicating the success of updating the leave request status is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Leave request status updated successfully."
            )
    })
    @Auditor(allowedViews = View.EMPLOYEES, allowedActions = Action.CREATE)
    public ResponseEntity<?> denyEmployeeLeaveRequest(
            TokenValidationResponse validationResponse,
            @PathVariable("leaveUid") String leaveUid
    ) {
        APIResponse<?> response = employeeService.updateLeaveStatus(
                validationResponse,
                leaveUid,
                LeaveStatus.DECLINED
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }






    /**
     * Update the password of the currently authenticated employee.
     *
     * @param validationResponse     The validation response containing user information.
     * @param updatePasswordRequest  The request containing the current and new passwords.
     * @return ApiResponse indicating the success of updating the employee password.
     */
    @PatchMapping(path = "/emp/pass")
    @Operation(
            summary = "UPDATE EMPLOYEE PASSWORD",
            description = "Update the password of the currently authenticated employee.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Retrieve the user ID from the validation response.\n" +
                    "2. Sanitize the update password request.\n" +
                    "3. Check if the new password is different from the current one.\n" +
                    "4. Retrieve the UserModel based on the user ID.\n" +
                    "5. Encode and update the new password.\n" +
                    "6. ApiResponse indicating the success of updating the password is returned.\n",
            tags = {"PATCH"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee password updated successfully."
            )
    })
    @Auditor
    public ResponseEntity<?> updateEmployeePassword(
            TokenValidationResponse validationResponse,
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        APIResponse<?> response = employeeService.updateEmployeePassword(
                validationResponse,
                updatePasswordRequest
        );
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }





//  DELETE

    @DeleteMapping("/todo/{toDoUid}")
    @Auditor
    public ResponseEntity<?> deleteToDoList(
            @PathVariable("toDoUid") String toDoUid,
            TokenValidationResponse validationResponse
    ) {
        APIResponse<?> response = employeeService.deleteToDoList(validationResponse, toDoUid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }






    /**
     * Delete a document associated with an employee request by its type and request UID.
     *
     * @param documentType        The type of the document to be deleted.
     * @param requestUid          The UID of the request associated with the document.
     * @param validationResponse  The validation response containing user information.
     * @return ApiResponse indicating the success of deleting the document.
     */
    @DeleteMapping("/req/doc/{documentType}/{requestUid}")
    @Operation(
            summary = "DELETE REQUEST DOCUMENT",
            description = "Delete a document associated with an employee request by its type and request UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Map the document type parameter to the corresponding DocumentType enum value.\n" +
                    "2. Call the service method to delete the document.\n" +
                    "3. Return the ApiResponse indicating the success of deleting the document.\n",
            tags = {"DELETE"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "The requested document has been successfully deleted."
            )
    })
    @Auditor
    public ResponseEntity<?> deleteDocumentByRID(
            @PathVariable("documentType") String documentType,
            @PathVariable("requestUid") String requestUid,
            TokenValidationResponse validationResponse
    ) {
        APIResponse<?> response = employeeService.deleteEmployeeRequestDocumentById(
                validationResponse.isAdminRole(),
                validationResponse.getUid(),
                requestUid,
                (
                        "ide".equalsIgnoreCase(documentType)
                                ? DocumentType.DOCUMENT_IDENTITY_PROOF :
                        "add".equalsIgnoreCase(documentType)
                                ? DocumentType.DOCUMENT_ADDRESS_PROOF :
                        "off".equalsIgnoreCase(documentType)
                                ? DocumentType.DOCUMENT_OFFER_LETTER :
                        "edu".equalsIgnoreCase(documentType)
                                ? DocumentType.DOCUMENT_EDUCATION :
                        "exp".equalsIgnoreCase(documentType)
                                ? DocumentType.DOCUMENT_EXPERIENCE_LETTER :
                        "sal".equalsIgnoreCase(documentType)
                                ? DocumentType.DOCUMENT_SALARY_SLIP
                                : null
                )
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Delete a document associated with an employee request by its type and request UID.
     *
     * @param documentType        The type of the document to be deleted.
     * @param requestUid          The UID of the request associated with the document.
     * @param authorizationHeader  The validation response containing user information.
     * @return ApiResponse indicating the success of deleting the document.
     */
    @DeleteMapping("/opn/req/doc/{documentType}/{requestUid}")
    @Operation(
            summary = "DELETE REQUEST DOCUMENT",
            description = "Delete a document associated with an employee request by its type and request UID.\n\n" +
                    "Access Control:\n" +
                    "To access this endpoint, users must be auditors.\n\n" +
                    "Endpoint Workflow:\n" +
                    "1. Map the document type parameter to the corresponding DocumentType enum value.\n" +
                    "2. Call the service method to delete the document.\n" +
                    "3. Return the ApiResponse indicating the success of deleting the document.\n",
            tags = {"DELETE"}
    )
    @DefaultResponses
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "The requested document has been successfully deleted."
            )
    })
    public ResponseEntity<?> deleteExternalDocumentByERID(
            @PathVariable("documentType") String documentType,
            @PathVariable("requestUid") String requestUid,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        APIResponse<?> response = employeeService.deleteEmployeeRequestDocumentById(
                false,
                jwtWizard.getSubject(authorizationHeader),
                requestUid,
                (
                        "ide".equalsIgnoreCase(documentType)
                            ? DocumentType.DOCUMENT_IDENTITY_PROOF :
                                "add".equalsIgnoreCase(documentType)
                                        ? DocumentType.DOCUMENT_ADDRESS_PROOF :
                                        "off".equalsIgnoreCase(documentType)
                                                ? DocumentType.DOCUMENT_OFFER_LETTER :
                                                "edu".equalsIgnoreCase(documentType)
                                                        ? DocumentType.DOCUMENT_EDUCATION :
                                                        "exp".equalsIgnoreCase(documentType)
                                                                ? DocumentType.DOCUMENT_EXPERIENCE_LETTER :
                                                                "sal".equalsIgnoreCase(documentType)
                                                                        ? DocumentType.DOCUMENT_SALARY_SLIP
                                                                        : null
                )
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



}
