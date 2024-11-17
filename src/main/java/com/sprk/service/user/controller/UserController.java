package com.sprk.service.user.controller;


//@RestController
//@RequestMapping(path = "/api/auth")
public class UserController {
/*
	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtils jwtUtils;



//	GET
	@GetMapping(path = "/users")
	@Operation(
			summary = "GET-ALL-USERS",
			description = "Retrieves a list of all users registered in the system. This endpoint is accessible only to administrators with 'ROLE_ADMIN' authority.\n\n" +
					"Access Control:\n" +
					"Only administrators with 'ROLE_ADMIN' authority have permission to use this endpoint.\n\n" +
					"Endpoint Workflow:\n" +
					"1. When an authorized request is made to this endpoint, the server initiates the retrieval process.\n" +
					"2. The service layer calls the 'getAllUsers()' method to fetch a list of UserModel objects from the repository.\n" +
					"3. The fetched UserModel objects are then transformed into a list of UserResponse objects using the 'getUserResponseFromUserModel()' method.\n" +
					"4. Each UserResponse object contains user attributes such as creation timestamp, user ID, names, username, email, and account status.\n" +
					"5. If the 'isAuthoritiesRequired' parameter is set to 'true', the authorities associated with each user are also included in the response.\n" +
					"6. The list of UserResponse objects is packaged into an HTTP response with an HTTP status code of 200 (OK).",
			tags = { "GET" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = UserResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN})
	public ResponseEntity<?> getAllUsers() {
		// Call the getAllUsers() method from the userService to retrieve a list of UserResponse objects.
		List<UserResponse> userList = userService.getAllUsers();

		// Wrap the list of UserResponse objects in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				userList,
				HttpStatus.OK
		);
	}





	@GetMapping(path = "/users/{role}")
	@Operation(
			summary = "GET-ALL-USERS-BY-AUTHORITY",
			description = "Retrieves a list of users filtered by the specified authority role. This endpoint is accessible to administrators with 'ROLE_ADMIN' and salespersons with 'ROLE_SALES' authority.\n\n" +
					"Access Control:\n" +
					"Administrators with 'ROLE_ADMIN' and salespersons with 'ROLE_SALES' authority can access this endpoint.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An authorized request is made with a specific authority role ('ROLE_ADMIN' or 'ROLE_SALES') and a token validation response.\n" +
					"2. The server determines the appropriate authority based on the validation response and roleName.\n" +
					"3. The service layer retrieves a list of UserModel objects with the specified authority from the repository.\n" +
					"4. The fetched UserModel list is transformed into a list of UserResponse objects using the 'getUserResponseFromUserModel()' method.\n" +
					"5. Each UserResponse object contains user attributes such as creation timestamp, user ID, names, username, email, and account status.\n" +
					"6. If required, the authorities associated with each user are also included in the response.\n" +
					"7. The list of UserResponse objects is wrapped in an HTTP response with an HTTP status code of 200 (OK).\n" +
					"8. This endpoint facilitates retrieving user data based on specific authority roles, aiding administrators and salespersons in managing users.",
			tags = { "GET" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = UserResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN, Authority.ROLE_SALES})
	public ResponseEntity<?> getAllUsersByAuthority(@PathVariable("role") String roleName,
													TokenValidationResponse validationResponse) {
		// Call the service method to retrieve a list of UserResponse objects.
		List<UserResponse> userList = userService.getAllUsersByAuthority(
				validationResponse,
				roleName
		);

		// Wrap the list of UserResponse objects in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				userList,
				HttpStatus.OK
		);
	}





	@GetMapping(path = "/users/transfer")
	@Operation(
			summary = "GET-ALL-ENQUIRY-TRANSFERABLE-USERS",
			description = "Retrieves a list of users suitable for transfer enquiries. This endpoint is accessible to administrators with 'ROLE_ADMIN' and salespersons with 'ROLE_SALES' authority.\n\n" +
					"Access Control:\n" +
					"Administrators with 'ROLE_ADMIN' and salespersons with 'ROLE_SALES' authority can access this endpoint.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An authorized request is made with an authorization header containing the token and an optional flag 'include-logged-user' indicating whether the logged-in user should be included in the response.\n" +
					"2. The server validates the token and the user's authority.\n" +
					"3. The service layer processes the request and retrieves a list of UserResponse objects suitable for transfer enquiries.\n" +
					"4. If the user has administrator or sales authority, a combination of users with 'ROLE_ADMIN' and 'ROLE_SALES' authority are included.\n" +
					"5. If the 'include-logged-user' flag is provided and set to 'false', the logged-in user is removed from the list.\n" +
					"6. The list of UserResponse objects is wrapped in an HTTP response with an HTTP status code of 200 (OK).\n" +
					"7. This endpoint facilitates retrieving user data for transfer enquiries, aiding administrators and salespersons in managing transfer requests.",
			tags = { "GET" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = UserResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN, Authority.ROLE_SALES})
	public ResponseEntity<?> getAllUsersForTransferEnquiry(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
														   @RequestHeader(value = "include-logged-user", required = false) Boolean isLoggedUserIncluded,
														   TokenValidationResponse validationResponse) {
		// Call the service method to retrieve a list of UserResponse objects for transfer enquiries.
		List<UserResponse> userList = userService.getAllUsersForTransferEnquiry(
				authorizationHeader,
				(isLoggedUserIncluded != null && isLoggedUserIncluded),
				validationResponse
		);

		// Wrap the list of UserResponse objects in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				userList,
				HttpStatus.OK
		);
	}





	@GetMapping(path = "/user/{userId}")
	@Operation(
			summary = "GET-USER-BY-UID",
			description = "Retrieves detailed information about a specific user identified by the provided user ID. This endpoint is accessible to administrators with 'ROLE_ADMIN' and users with 'ROLE_DEFAULT' authority.\n\n" +
					"Access Control:\n" +
					"Administrators with 'ROLE_ADMIN' and users with 'ROLE_DEFAULT' authority can access this endpoint.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An authorized request is made with an authorization header containing the token, a valid user ID, and validation response.\n" +
					"2. The server validates the token and the user's authority.\n" +
					"3. The service layer processes the request and retrieves the UserResponse object for the specified user.\n" +
					"4. If the user is not authorized to access the requested user's data, an UnauthorizedException is thrown.\n" +
					"5. If the user is authorized, the service generates a detailed UserResponse object from the UserModel, including authority details.\n" +
					"6. The UserResponse object is wrapped in an HTTP response with an HTTP status code of 200 (OK).\n" +
					"7. This endpoint facilitates retrieving detailed user information for administrators and users.",
			tags = { "GET" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = UserResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN, Authority.ROLE_DEFAULT})
	public ResponseEntity<?> getUserById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
										 @PathVariable("userId") Long userId,
										 TokenValidationResponse validationResponse) {
		// Call the service method to retrieve the detailed UserResponse object for the specified user.
		UserResponse userResponse = userService.getUserById(
				authorizationHeader,
				validationResponse,
				userId
		);

		// Wrap the UserResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				userResponse,
				HttpStatus.OK
		);
	}





	@GetMapping(path = "/roles")
	@Operation(
			summary = "GET-ALL-AUTHORITIES",
			description = "Retrieves a list of all available authorities (roles) in the system. This endpoint is accessible to administrators with 'ROLE_ADMIN' authority.\n\n" +
					"Access Control:\n" +
					"Administrators with 'ROLE_ADMIN' authority can access this endpoint.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An authorized request is made with an authorization header containing the token.\n" +
					"2. The server validates the token and checks if the user has the required 'ROLE_ADMIN' authority.\n" +
					"3. The service layer processes the request and retrieves a list of authority names.\n" +
					"4. The list of authority names is wrapped in an HTTP response with an HTTP status code of 200 (OK).\n" +
					"5. This endpoint facilitates retrieving a list of all available authorities for administrators.",
			tags = { "GET" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = ArrayList.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN})
	public ResponseEntity<?> getAllAuthorities() {
		// Call the service method to retrieve a list of all available authority names.
		List<String> authorityList = userService.getAllAuthorities();

		// Wrap the list of authority names in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				authorityList,
				HttpStatus.OK
		);
	}





	@GetMapping(path = "/qr")
	@Operation(
			summary = "GENERATE-TOKEN-FOR-QR (OPEN)",
			description = "Generates a JWT token for QR code generation. This endpoint is used to generate a token for QR code-based authentication, and it can be accessed by users with appropriate authority.\n\n" +
					"Access Control:\n" +
					"Users with the required authority can access this endpoint. The token generated here can be used for QR code authentication.\n\n" +
					"Endpoint Workflow:\n" +
					"1. A request is made to this endpoint.\n" +
					"2. The server processes the request and generates a JWT token specifically for QR code-based.\n" +
					"3. The generated token is wrapped in an HTTP response with an HTTP status code of 200 (OK).\n" +
					"4. The token can be used for QR code-based authentication by authorized users.",
			tags = { "GET" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = UserResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(auditToken = false)
	public ResponseEntity<?> generateTokenForQR() {
		// Call the service method to generate a JWT token for QR code authentication.
		JwtTokenResponse tokenResponse = userService.generateTokenForQR();

		// Wrap the generated token in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				tokenResponse,
				HttpStatus.OK
		);
	}





	@GetMapping(path = "/username/{username}")
	@Operation(
			summary = "USERNAME-AVAILABILITY (OPEN)",
			description = "Checks the availability of a username. This endpoint allows users to check if a username is available for registration. The endpoint does not require token-based authentication.\n\n" +
					"Access Control:\n" +
					"This endpoint can be accessed by anyone to check the availability of a specific username for registration.\n\n" +
					"Endpoint Workflow:\n" +
					"1. A request is made to this endpoint with the desired username.\n" +
					"2. The server processes the request and checks if the provided username is available for registration.\n" +
					"3. An HTTP response is generated, containing the availability status of the username.\n" +
					"4. The response includes a JSON object indicating whether the username is available or not.",
			tags = { "GET" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = AvailabilityResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(auditToken = false)
	public ResponseEntity<?> isUsernameAvailable(@PathVariable("username") String username) {
		// Call the service method to check the availability of the provided username.
		boolean isAvailable = userService.isUsernameAvailable(username);

		// Create an AvailabilityResponse object based on the availability status.
		AvailabilityResponse availabilityResponse = new AvailabilityResponse(isAvailable);

		// Wrap the AvailabilityResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				availabilityResponse,
				HttpStatus.OK
		);
	}





	@GetMapping(path = "/email/{email}")
	@Operation(
			summary = "EMAIL-AVAILABILITY (OPEN)",
			description = "Checks the availability of an email address. This endpoint allows users to check if an email address is available for registration. The endpoint does not require token-based authentication.\n\n" +
					"Access Control:\n" +
					"This endpoint can be accessed by anyone to check the availability of a specific email address for registration.\n\n" +
					"Endpoint Workflow:\n" +
					"1. A request is made to this endpoint with the desired email address.\n" +
					"2. The server processes the request and checks if the provided email address is available for registration.\n" +
					"3. An HTTP response is generated, containing the availability status of the email address.\n" +
					"4. The response includes a JSON object indicating whether the email address is available or not.",
			tags = { "GET" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = AvailabilityResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(auditToken = false)
	public ResponseEntity<?> isEmailAddressAvailable(@PathVariable("email") String emailAddress) {
		// Call the service method to check the availability of the provided email address.
		boolean isAvailable = userService.isEmailAddressAvailable(emailAddress);

		// Create an AvailabilityResponse object based on the availability status.
		AvailabilityResponse availabilityResponse = new AvailabilityResponse(isAvailable);

		// Wrap the AvailabilityResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				availabilityResponse,
				HttpStatus.OK
		);
	}


	@GetMapping(path = "/profile")
	@Auditor(allowedRoles = {Authority.ROLE_DEFAULT})
	public ResponseEntity<?> getProfilePhoto(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
		// Call the service method to get profile picture of the user's account.
		byte[] profilePicture = userService.getProfileByUserId(authorizationHeader);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.IMAGE_JPEG)
				.body(profilePicture);
	}



//	POST
	@PostMapping(path = "/")
	@Operation(
			summary = "VALIDATE-TOKEN (OPEN)",
			description = "Validates a token's authenticity and permissions. This endpoint allows clients to validate a JWT token's authenticity and permissions. The token's validity is determined based on the provided authorization header and authorities.\n\n" +
					"Access Control:\n" +
					"This endpoint can be accessed by any client to validate a JWT token's authenticity and permissions.\n\n" +
					"Endpoint Workflow:\n" +
					"1. A request is made to this endpoint with the JWT token included in the authorization header.\n" +
					"2. The server processes the request and checks if the token is valid and belongs to an existing user.\n" +
					"3. The server validates whether the user has the required authorities specified in the request body.\n" +
					"4. An HTTP response is generated, containing the result of token validation and the user's authorities.\n" +
					"5. The response includes a JSON object indicating the token's validity and user's authorities.",
			tags = { "POST" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = TokenValidationResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(auditToken = false)
	public ResponseEntity<?> isTokenValid(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
										  @RequestBody @Valid TokenValidationRequest validationRequest) {
		// Convert the list of authority strings from the request to an EnumSet of Authority.
		EnumSet<Authority> authorizedRoles = validationRequest.getAuthorities()
				.stream()
				.map(authority -> {
					try {
						return Authority.valueOf(authority);
					} catch (IllegalArgumentException e) {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(Authority.class)));

		// Call the service method to validate the token and authorities.
		TokenValidationResponse validationResponse = userService.isTokenValid(authorizationHeader, authorizedRoles);

		// Wrap the TokenValidationResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				validationResponse,
				HttpStatus.OK
		);
	}





	@PostMapping(path = "/register")
	@Operation(
			summary = "REGISTER-USER (OPEN)",
			description = "Registers a new user in the system. This endpoint allows clients to register a new user with the provided details.\n\n" +
					"Access Control:\n" +
					"This endpoint can be accessed by any client to register a new user.\n\n" +
					"Endpoint Workflow:\n" +
					"1. A request is made to this endpoint with the user's registration details in the request body.\n" +
					"2. The server processes the request and sanitizes the provided user details.\n" +
					"3. The server checks if the chosen username and email address are available.\n" +
					"4. The server assigns the default user role if necessary and creates a new UserModel object.\n" +
					"5. The user's details are stored in the database, and a response indicating successful registration is generated.\n" +
					"6. The response includes a message indicating the success of the registration process.",
			tags = { "POST" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(example = "N/A"),
											mediaType = "application/json"
									)
							}
					),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "201",
							content = {
									@Content(
											schema = @Schema(implementation = ApiResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(auditToken = false)
	public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
		// Call the service method to register a new user with the provided registration details.
		ApiResponse response = userService.registerUser(userRegisterRequest);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status Created (201).
		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED
		);
	}



	@PostMapping(path = "/org/register")
	@Operation(
			summary = "REGISTER-USER (OPEN)",
			description = "Registers a new user in the system. This endpoint allows clients to register a new user with the provided details.\n\n" +
					"Access Control:\n" +
					"This endpoint can be accessed by any client to register a new user.\n\n" +
					"Endpoint Workflow:\n" +
					"1. A request is made to this endpoint with the user's registration details in the request body.\n" +
					"2. The server processes the request and sanitizes the provided user details.\n" +
					"3. The server checks if the chosen username and email address are available.\n" +
					"4. The server assigns the default user role if necessary and creates a new UserModel object.\n" +
					"5. The user's details are stored in the database, and a response indicating successful registration is generated.\n" +
					"6. The response includes a message indicating the success of the registration process.",
			tags = { "POST" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(example = "N/A"),
											mediaType = "application/json"
									)
							}
					),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "201",
							content = {
									@Content(
											schema = @Schema(implementation = ApiResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(auditToken = false)
	public ResponseEntity<?> registerOrganization(@RequestBody @Valid OrganizationRegisterRequest organizationRegisterRequest) {
		// Call the service method to register a new user with the provided registration details.
		ApiResponse response = userService.registerOrganization(organizationRegisterRequest);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status Created (201).
		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED
		);
	}





	@PostMapping(path = "/login")
	@Operation(
			summary = "LOGIN (OPEN)",
			description = "Allows a user to log in to the system. This endpoint enables users to authenticate and receive an access token for further API interactions.\n\n" +
					"Access Control:\n" +
					"This endpoint is accessible to registered users for logging into the system.\n\n" +
					"Endpoint Workflow:\n" +
					"1. A user provides their username or email and password in the request body.\n" +
					"2. The server processes the request and sanitizes the provided login credentials.\n" +
					"3. The server retrieves the corresponding user details from the database based on the provided credentials.\n" +
					"4. If the user is found and the provided password matches the stored password, a JWT access token is generated.\n" +
					"5. If the user account is disabled, an exception is thrown.\n" +
					"6. The server responds with the generated JWT access token.\n" +
					"7. The response includes the generated JWT token.",
			tags = { "POST" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = JwtTokenResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor(auditToken = false)
    public ResponseEntity<?> signInUser(@RequestBody @Valid LoginRequest loginRequest) {
		// Call the service method to authenticate the user and generate a JWT token.
		JwtTokenResponse tokenResponse = userService.signInUser(loginRequest);

		// Wrap the JwtTokenResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				tokenResponse,
				HttpStatus.OK
		);
    }





	@PostMapping(path = "/logout")
	@Operation(
			summary = "LOGOUT (ALL-USERS)",
			description = "Allows a user to log out from the system by invalidating their access token. By logging out, the user's current access token becomes invalid, which ensures the user's session is terminated and their access is revoked.\n\n" +
					"Access:\n" +
					"This endpoint is accessible to authenticated users for logging out from the system.\n\n" +
					"Endpoint Workflow:\n" +
					"1. A user makes a request to log out by providing their access token in the request header.\n" +
					"2. The server processes the request and extracts the access token from the header.\n" +
					"3. If the user chooses to sign out from other devices, the server updates the user's account information to invalidate the current access token.\n" +
					"4. The server generates a new access token to replace the old one.\n" +
					"5. The new access token is sent as a response to the user.\n" +
					"6. The user's session on other devices (if chosen) is terminated, and the user is logged out.\n",
			tags = { "POST" }
	)
	@DefaultResponses
	@ApiResponses(
			{
					@io.swagger.v3.oas.annotations.responses.ApiResponse(
							responseCode = "200",
							content = {
									@Content(
											schema = @Schema(implementation = JwtTokenResponse.class),
											mediaType = "application/json"
									)
							}
					)
			}
	)
	@Auditor()
	public ResponseEntity<?> refreshToken(@RequestHeader(value = "logout-from-other-devices", required = false) Boolean signOutFromOtherDevices,
										  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
		// Call the service method to refresh the user's access token.
		JwtTokenResponse tokenResponse = userService.refreshToken(
				authorizationHeader,
				(signOutFromOtherDevices != null && signOutFromOtherDevices)
		);

		// Wrap the JwtTokenResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				tokenResponse,
				HttpStatus.OK
		);
	}

	@PostMapping(path = "/upload")
	@Auditor(allowedRoles = {Authority.ROLE_DEFAULT})
	public ResponseEntity<?> uploadProfilePhoto(@RequestParam("image") MultipartFile file,
												@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
		// Call the service method to set profile picture of the user's account.
		ApiResponse response = userService.uploadUserProfile(authorizationHeader, file);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				response,
				HttpStatus.OK
		);
	}



	
	
//	PATCH
	@PatchMapping(path = "/enable/{userId}")
	@Operation(
			summary = "ENABLE-USER-ACCOUNT",
			description = "Allows an administrator to enable a user account in the system. Enabling a user account grants the user access to the system and its functionalities.\n\n" +
					"Access Control:\n" +
					"This endpoint is accessible to administrators only.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An administrator makes a request to enable a user account by providing the user's ID in the URL.\n" +
					"2. The server processes the request and extracts the user's ID.\n" +
					"3. The server calls the service method to enable the user's account.\n" +
					"4. If successful, the user's account is enabled, and a success response is sent.\n" +
					"5. If the user's ID is not found, a ResourceNotFoundException is thrown.\n" +
					"6. If an exception occurs during the process, an ApplicationException is thrown.",
			tags = { "PATCH" }
	)
	@DefaultResponses
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN})
	public ResponseEntity<?> enableUser(@PathVariable("userId") Long userId) {
		// Call the service method to enable the user's account.
		ApiResponse response = userService.setEnabledUser(userId, true);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				response,
				HttpStatus.OK
		);
	}





	@PatchMapping(path = "/disable/{userId}")
	@Operation(
			summary = "DISABLE-USER",
			description = "Allows an administrator to disable a user account in the system. Disabling a user account restricts the user's access to the system and its functionalities.\n\n" +
					"Access Control:\n" +
					"This endpoint is accessible to administrators only.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An administrator makes a request to disable a user account by providing the user's ID in the URL.\n" +
					"2. The server processes the request and extracts the user's ID.\n" +
					"3. The server calls the service method to disable the user's account.\n" +
					"4. If successful, the user's account is disabled, and a success response is sent.\n" +
					"5. If the user's ID is not found, a ResourceNotFoundException is thrown.\n" +
					"6. If an exception occurs during the process, an ApplicationException is thrown.",
			tags = { "PATCH" }
	)
	@DefaultResponses
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN})
	public ResponseEntity<?> disableUser(@PathVariable("userId") Long userId) {
		// Call the service method to disable the user's account.
		ApiResponse response = userService.setEnabledUser(userId, false);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				response,
				HttpStatus.OK
		);
	}





	@PatchMapping(path = "/details/{userId}")
	@Operation(
			summary = "UPDATE-USER-DETAILS",
			description = "Allows an administrator to update the details of a user's account, such as their first name, last name, username, and email address.\n\n" +
					"Access Control:\n" +
					"This endpoint is accessible to administrators only.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An administrator makes a request to update the user details by providing the user's ID in the URL and the updated details in the request body.\n" +
					"2. The server processes the request and extracts the user's ID and the updated details.\n" +
					"3. The server calls the service method to update the user's details.\n" +
					"4. If successful, the user's details are updated, and a success response is sent.\n" +
					"5. If the user's ID is not found, a ResourceNotFoundException is thrown.\n" +
					"6. If the provided details are invalid or an exception occurs during the process, appropriate exceptions are thrown.",
			tags = { "PATCH" }
	)
	@DefaultResponses
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN})
	public ResponseEntity<?> setUserDetails(@PathVariable("userId") Long userId,
											@RequestBody @Valid UpdateUserDetailsRequest userDetailsRequest) {
		// Call the service method to update the user's details.
		ApiResponse response = userService.setUserDetails(userId, userDetailsRequest);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				response,
				HttpStatus.OK
		);
	}





	@PatchMapping(path = "/password/{userId}")
	@Operation(
			summary = "NEW-USER-PASSWORD",
			description = "Allows a user to change their own password. This action can be performed by the user themselves.\n\n" +
					"Access Control:\n" +
					"This endpoint is accessible to users themselves (self-service).\n\n" +
					"Endpoint Workflow:\n" +
					"1. A user makes a request to change their password by providing their user ID in the URL and the new password in the request body.\n" +
					"2. The server processes the request and extracts the user's ID and the new password.\n" +
					"3. The server validates the request and updates the user's password.\n" +
					"4. If successful, the user's password is updated, and a success response is sent.\n" +
					"5. If the user's ID is not found, a ResourceNotFoundException is thrown.\n" +
					"6. If the provided password is invalid or an exception occurs during the process, appropriate exceptions are thrown.",
			tags = { "PATCH" }
	)
	@DefaultResponses
	@Auditor(allowedRoles = {Authority.ROLE_DEFAULT})
	public ResponseEntity<?> setUserPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
											 @PathVariable("userId") Long userId,
											 @RequestBody @Valid UpdatePasswordRequest passwordRequest,
											 TokenValidationResponse validationResponse) {
		// Call the service method to update the user's own password.
		ApiResponse response = userService.setUserPassword(
				authorizationHeader,
				validationResponse,
				userId,
				passwordRequest
		);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				response,
				HttpStatus.OK
		);
	}




	@PatchMapping(path = "/role/{userId}")
	@Operation(
			summary = "SET-USER-AUTHORITIES",
			description = "Allows an admin user to assign or update authorities/roles for a specific user.\n\n" +
					"Access Control:\n" +
					"Only admin users have access to this endpoint.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An admin user makes a request to assign or update authorities for a specific user by providing the user's ID in the URL and the list of new authorities in the request body.\n" +
					"2. The server processes the request and extracts the user's ID and the list of new authorities.\n" +
					"3. The server validates the request and updates the user's authorities.\n" +
					"4. If successful, the user's authorities are updated, and a success response is sent.\n" +
					"5. If the user's ID is not found or the provided authorities are invalid, appropriate exceptions are thrown.",
			tags = { "PATCH" }
	)
	@DefaultResponses
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN})
	public ResponseEntity<?> setUserAuthorities(@PathVariable("userId") Long userId,
												@RequestBody @Valid UpdateAuthoritiesRequest authoritiesRequest) {
		// Call the service method to set user authorities.
		ApiResponse response = userService.setUserAuthorities(userId, authoritiesRequest);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				response,
				HttpStatus.OK
		);
	}





	@PatchMapping(path = "/user/{userId}")
	@Operation(
			summary = "UPDATE-USER-INFO",
			description = "Allows an admin user to update various information about a specific user, including their personal details, " +
					"username, email, password, enabled status, and authorities/roles.\n\n" +
					"Access Control:\n" +
					"Only admin users have access to this endpoint.\n\n" +
					"Endpoint Workflow:\n" +
					"1. An admin user makes a request to update user information for a specific user by providing the user's ID in the URL and the updated user information in the request body.\n" +
					"2. The server processes the request and extracts the user's ID and the updated user information.\n" +
					"3. The server validates the request and updates the user's information accordingly.\n" +
					"4. If successful, the user's information is updated, and a success response is sent.\n" +
					"5. If the user's ID is not found, the provided information is invalid, or an error occurs during the update, appropriate exceptions are thrown.",
			tags = { "PATCH" }
	)
	@DefaultResponses
	@Auditor(allowedRoles = {Authority.ROLE_ADMIN})
	public ResponseEntity<?> setUser(@PathVariable("userId") Long userId,
									 @RequestBody @Valid UserRequest userRequest) {
		// Call the service method to update user information.
		ApiResponse response = userService.setUser(userId, userRequest);

		// Wrap the ApiResponse object in a ResponseEntity and send it as a response with HTTP status OK (200).
		return new ResponseEntity<>(
				response,
				HttpStatus.OK
		);
	}

 */

}
