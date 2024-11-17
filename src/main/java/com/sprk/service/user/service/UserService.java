package com.sprk.service.user.service;

import org.springframework.stereotype.Service;


@Service
public class UserService {

/*

	private final UserRepository userRepository;
	private final AuthorityRepository authorityRepository;
	private final FileDataRepository fileDataRepository;
	private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

	@Value("${app.upload.dir.profile}")
	private String profileDirectory;

	@Autowired
	public UserService(
			UserRepository userRepository,
			AuthorityRepository authorityRepository,
			FileDataRepository fileDataRepository,
			OrganizationRepository organizationRepository,
			PasswordEncoder passwordEncoder,
			JwtUtils jwtUtils
	) {
		this.userRepository = userRepository;
		this.authorityRepository = authorityRepository;
		this.fileDataRepository = fileDataRepository;
		this.organizationRepository = organizationRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
	}






//	UTIL
	/**
	 * A generic method to retrieve user data of type T based on the user ID and a data retrieval function.
	 *
	 * @param userId              The ID of the user for which to retrieve the data.
	 * @param dataRetrievalFunction The function to retrieve the user data from a repository.
	 * @param <T>                 The type of user data to retrieve.
	 * @return The retrieved user data of type T.
	 * @throws ResourceNotFoundException If the requested user data is not found in the repository.
	 //
	private <T> T getUserDataById(Long userId, Supplier<Optional<T>> dataRetrievalFunction) {
		// Check if the userId is null. If it is, throw a ResourceNotFoundException with an informative error message.
		if (userId == null)
			throw new ResourceNotFoundException("UID cannot be null.");

		// Use the provided data retrieval function to attempt to retrieve the user data.
		// If the data is present, return it; otherwise, throw a ResourceNotFoundException.
		return dataRetrievalFunction
				.get()
				.orElseThrow(() -> new ResourceNotFoundException("Sorry, we couldn't find the requested user with UID (" + userId + ")."));
	}






	/**
	 * Retrieves a UserModel object from the user repository based on the specified user ID.
	 *
	 * @param userId The ID of the user for which to retrieve the UserModel.
	 * @return The retrieved UserModel.
	 * @throws ResourceNotFoundException If the requested user's data is not found in the repository.
	 //
	public UserModel getUserModelById(Long userId) {
		// Call the generic getUserDataById method and provide the user data retrieval function.
		return getUserDataById(userId, () -> userRepository.findByUserId(userId));
	}






	/**
	 * Retrieves a UserTokenModel object from the user repository based on the specified user ID.
	 *
	 * @param userId The ID of the user for which to retrieve the UserTokenModel.
	 * @return The retrieved UserTokenModel.
	 * @throws ResourceNotFoundException If the requested user's token data is not found in the repository.
	 //
//	public UserTokenDTO getUserTokenModelById(Long userId) {
//		// Call the generic getUserDataById method and provide the user data retrieval function.
//		return getUserDataById(userId, () -> userRepository.findUserTokenModelByUserId(userId));
//	}






	/**
	 * Retrieves an AuthorityModel object from the authority repository based on the specified Authority.
	 *
	 * @param authority The Authority for which to retrieve the AuthorityModel.
	 * @return The retrieved AuthorityModel.
	 * @throws ResourceNotFoundException If the requested authority is not found in the authority repository.
	 //
	private AuthorityModel getAuthorityModelByAuthority(Authority authority) {
		return authorityRepository
				.findByAuthority(authority) // Search the authority repository for a matching authority.
				.orElseThrow(() ->
						// If no matching authority is found, throw a ResourceNotFoundException with an informative error message.
						new ResourceNotFoundException("Sorry, we couldn't find the authority/role you requested with the role-name (" + authority.name() + ").")
				);
	}






	private OrganizationModel getOrganizationById(Long orgId) {
		return organizationRepository
				.findById(orgId) // Search the authority repository for a matching authority.
				.orElseThrow(() ->
						// If no matching authority is found, throw a ResourceNotFoundException with an informative error message.
						new ResourceNotFoundException("Sorry, we couldn't find the organization you requested with the organization-id (" + orgId + ").")
				);
	}






	/**
	 * Generates a UserResponse object from a UserModel along with optional authority details, based on the specified requirements.
	 *
	 * @param userModel The UserModel object from which to generate the UserResponse.
	 * @param isAuthoritiesRequired A flag indicating whether authority details are required in the UserResponse.
	 * @return The generated UserResponse object.
	 //
	private UserResponse getUserResponseFromUserModel(UserModel userModel, boolean isAuthoritiesRequired) {
		// If userModel is null, return null.
		if (null == userModel)
			return null;

		// If userModel is not null, create a new UserResponse object using values from the userModel.
		return new UserResponse(
				userModel.getCreatedAt(),
				userModel.getUserId(),
				userModel.getFirstname(),
				userModel.getMiddlename(),
				userModel.getLastname(),
				userModel.getUsername(),
				userModel.getEmail(),
				userModel.getEnabled(),
				isAuthoritiesRequired ? (
						// If isAuthoritiesRequired is true, collect authority details from the userModel's authorities.
						userModel.getAuthorities()
								.stream()
								.map(authorityModel ->
										// Map authorityModel to the corresponding authority name, or null if not available.
										Optional.ofNullable(authorityModel.getAuthority())
												.map(Authority::name)
												.orElse(null)
								)
								.filter(Objects::nonNull) // Filter out null authority names.
								.collect(Collectors.toSet()) // Collect the authority names into a set.
				) : null, // If isAuthoritiesRequired is false, set the authorities field to null.
				(null != userModel.getProfile())

		);
	}






	/**
	 * Converts a list of UserModel objects to a list of UserResponse objects.
	 *
	 * @param listOfUserModel The input list of UserModel objects to be converted.
	 * @param isAuthoritiesRequired A flag indicating whether authority details are required in UserResponse.
	 * @return The list of UserResponse objects corresponding to the input UserModel objects.
	 * @throws NoContentFoundException If the input list is null or empty.
	 //
	private List<UserResponse> getListOfUserResponses(List<UserModel> listOfUserModel, boolean isAuthoritiesRequired) {
		// Check if the input list is null or empty. If it is, throw a NoContentFoundException.
		if (listOfUserModel == null || listOfUserModel.isEmpty())
			throw new NoContentFoundException();

		// Transform the list of UserModel objects into a list of UserResponse objects.
		return listOfUserModel
				.stream()
				.map(userModel -> getUserResponseFromUserModel(userModel, isAuthoritiesRequired))
				.collect(Collectors.toList());
	}






	/**
	 * Sanitizes a given string to remove any potentially harmful HTML tags and returns the sanitized result.
	 *
	 * @param str The input string to be sanitized.
	 * @return The sanitized string with harmful HTML tags removed, or null if the input string is null.
	 //
	private String getSanitizedString(String str) {
		// Check if the input string is not null.
		// If it's not null, sanitize the string by trimming it and removing any potentially harmful HTML using Jsoup.
		// If it is null, return null.
		return str != null
				? Jsoup.clean(str.trim(), Safelist.simpleText())
				: null;
	}



	/**
	 * Sanitizes a set of strings to remove potentially harmful HTML tags from each string and returns the sanitized set.
	 *
	 * @param stringSet The input set of strings to be sanitized.
	 * @return The set of sanitized strings with harmful HTML tags removed, or an empty set if the input set is null.
	 //
	private Set<String> getSanitizedSet(Set<String> stringSet) {
		// Check if the input stringSet is null.
		if (stringSet == null) {
			// If the input set is null, return an empty set using Collections.emptySet().
			return Collections.emptySet();
		}

		// If not null then use Java stream to process each string in the set.
		// Filter out null elements, sanitize each string using Jsoup, and collect the sanitized strings into a new set.
		return stringSet.stream()
				.filter(Objects::nonNull) // Filter out null elements from the stream.
				.map(str -> Jsoup.clean(str.trim(), Safelist.simpleText())) // Sanitize each string using Jsoup.
				.collect(Collectors.toSet()); // Collect the sanitized strings into a new set.
	}






	private boolean isImageFormatSupported(String fileName) {
		if (null != fileName) {
			String[] allowedExtensions = {"jpg", "jpeg", "png"};
			String fileExtension = fileName
					.substring(fileName.lastIndexOf(".") + 1)
					.toLowerCase();

			for (String extension : allowedExtensions) {
				if (extension.equals(fileExtension))
					return true;
			}
		}

		return false;
	}






	@PostConstruct
	@Transactional
	public void init() {
		// Create a List of All Authorities
		List<Authority> allAuthorities = Arrays.asList(Authority.values());

		// Fetch existing authorities in a single query and build a map for lookup.
		Map<String, AuthorityModel> existingAuthoritiesMap = authorityRepository.findAll()
				.stream()
				.collect(Collectors.toMap(authority -> authority.getAuthority().name(), authority -> authority));

		// Collect authorities to create in a list.
		List<AuthorityModel> authoritiesToCreate = allAuthorities
				.stream()
				.filter(authority -> !existingAuthoritiesMap.containsKey(authority.name()))
				.map(AuthorityModel::new)
				.collect(Collectors.toList());

		// Save new authorities in a single batch.
		if (!authoritiesToCreate.isEmpty())
			authorityRepository.saveAll(authoritiesToCreate);
	}








//	GET
	/**
	 * Retrieves a list of all users and converts them into UserResponse objects.
	 *
	 * @return A list of UserResponse objects representing the retrieved users.
	 * @throws NoContentFoundException If no users are found in the database.
	 //
	public List<UserResponse> getAllUsers() {
		try {
			// Calls a method to convert the fetched UserModel list to UserResponse list.
			return getListOfUserResponses(userRepository.findAll(), true);
		} catch (NoContentFoundException exception) {
			// If there are no users in the database, it returns an empty list.
			throw new NoContentFoundException("No matching records found for the given request.");
		}
	}






	/**
	 * Retrieves a list of user responses filtered by authority.
	 *
	 * @param validationResponse The token validation response containing user authority information.
	 * @param roleName The name of the role/authority to filter users by.
	 * @return A list of UserResponse objects representing users with the specified authority.
	 * @throws ResourceNotFoundException If no users with the specified authority are found.
	 //
	public List<UserResponse> getAllUsersByAuthority(TokenValidationResponse validationResponse, String roleName) {
		try {
			// Determine the authority based on validation response and roleName.
			Authority authority = validationResponse.isSales()
					? Authority.ROLE_SALES
					: Enum.valueOf(Authority.class, roleName);

			// Retrieve a list of UserModel objects with the specified authority.
			List<UserModel> usersWithAuthority = userRepository.findByAuthoritiesAuthority(authority);

			// Convert the fetched UserModel list to UserResponse list.
			return getListOfUserResponses(usersWithAuthority, true);
		} catch (Exception exception) {
			// If an exception occurs, throw a ResourceNotFoundException with an informative error message.
			throw new ResourceNotFoundException("No matching records found for users with the authority (" + roleName + ").");
		}
	}






	/**
	 * Retrieves a list of user responses for transferring enquiry based on the given conditions.
	 *
	 * @param authorizationHeader The authorization header containing the JWT token.
	 * @param isLoggedUserIncluded Flag indicating whether to include the logged-in user in the result.
	 * @param validationResponse The token validation response containing user authority information.
	 * @return A list of UserResponse objects for transferring enquiry.
	 * @throws ResourceNotFoundException If no users match the specified conditions.
	 //
	public List<UserResponse> getAllUsersForTransferEnquiry(String authorizationHeader, boolean isLoggedUserIncluded, TokenValidationResponse validationResponse) {
		try {
			// Initialize an empty list to store the final result.
			List<UserResponse> listOfUsersForTransferEnquiry = new ArrayList<>();

			// If the validation response indicates admin or sales, fetch users with ROLE_ADMIN and ROLE_SALES authorities.
			if (validationResponse.isAdmin() || validationResponse.isSales()) {
				listOfUsersForTransferEnquiry.addAll(
						getListOfUserResponses(
								userRepository.findByAuthoritiesAuthority(Authority.ROLE_ADMIN),
								false
						)
				);
				listOfUsersForTransferEnquiry.addAll(
						getListOfUserResponses(
								userRepository.findByAuthoritiesAuthority(Authority.ROLE_SALES),
								false
						)
				);
			}

			// If logged user inclusion is not requested, remove the logged user from the list.
			if (!isLoggedUserIncluded) {
				Long tokenUserId = jwtUtils.getUserIdFromJWT(authorizationHeader);
				listOfUsersForTransferEnquiry.removeIf(user -> user.getUser_id().equals(tokenUserId));
			}

			// Return the final list of users for transfer enquiry.
			return listOfUsersForTransferEnquiry;
		} catch (Exception exception) {
			// If an exception occurs, throw a ResourceNotFoundException with an informative error message.
			throw new ResourceNotFoundException("No matching records found for such users.");
		}
	}






	/**
	 * Retrieves a UserResponse object for a specific user based on the given conditions.
	 *
	 * @param authorizationHeader The authorization header containing the JWT token.
	 * @param validationResponse The token validation response containing user authority information.
	 * @param userId The ID of the user to retrieve.
	 * @return A UserResponse object representing the requested user's data.
	 * @throws ResourceNotFoundException If the requested user is not found.
	 * @throws UnauthorizedException If the user is not authorized to access the requested user's data.
	 //
	public UserResponse getUserById(String authorizationHeader, TokenValidationResponse validationResponse, Long userId) {
		UserModel userModel;
		try {
			// Attempt to retrieve the UserModel using the provided userId.
			userModel = getUserModelById(userId);
		} catch (ResourceNotFoundException exception) {
			// If the user with the specified userId is not found, rethrow the exception with the same error message.
			throw new ResourceNotFoundException(exception.getMessage());
		}

		// Check if the user is authorized to access the requested user's data.
		// If the user is neither an admin nor the owner of the requested userId, throw an UnauthorizedException.
		if (!validationResponse.isAdmin() && !jwtUtils.getUserIdFromJWT(authorizationHeader).equals(userId))
			throw new UnauthorizedException();

		// Generate and return a UserResponse object from the retrieved UserModel, including authority details.
		return getUserResponseFromUserModel(userModel, true);
	}







	/**
	 * Retrieves a list of authority names.
	 *
	 * @return A list of authority names.
	 * @throws NoContentFoundException If no authority records are found.
	 //
	public List<String> getAllAuthorities() {
		// Retrieve a list of AuthorityModel objects from the authority repository.
		List<AuthorityModel> listOfAuthorities = authorityRepository.findAll();

		// Check if the list of authorities is empty. If it is, throw a NoContentFoundException.
		if (listOfAuthorities.isEmpty())
			throw new NoContentFoundException("No matching records found for the given request.");

		// Transform the list of AuthorityModel objects into a list of authority names.
		return listOfAuthorities
				.stream()
				.map(authorityModel -> {
					// Get the authority from the AuthorityModel and return its name.
					Authority authority = authorityModel.getAuthority();
					return (authority != null) ? authority.name() : null;
				})
				.filter(Objects::nonNull) // Filter out null authority names.
				.collect(Collectors.toList()); // Collect the authority names into a list.
	}






	/**
	 * Generates a JWT token for QR code authentication and returns it in a JwtTokenResponse.
	 *
	 * @return A JwtTokenResponse containing the generated QR token.
	 //
	public JwtTokenResponse generateTokenForQR() {
		// Generate a JWT token for QR code authentication using jwtTokenUtil.
		String qrToken = jwtUtils.generateTokenForQR();

		// Create a new JwtTokenResponse with the generated QR token and return it.
		return new JwtTokenResponse(qrToken);
	}






	/**
	 * Checks if a username is available for registration.
	 *
	 * @param username The username to check for availability.
	 * @return True if the username is available; otherwise, throws a DataAlreadyExistException.
	 * @throws DataAlreadyExistException If the username is already taken.
	 //
	public boolean isUsernameAvailable(String username) {
		// Sanitize the provided username using getSanitizedString method.
		String sanitizedUsername = getSanitizedString(username);

		// Check if the sanitized username already exists in the userRepository.
		if (userRepository.existsByUsername(sanitizedUsername)) {
			// If the username exists, throw a DataAlreadyExistException with an appropriate error message.
			throw new DataAlreadyExistException("The username '" + sanitizedUsername + "' is already taken. Please choose a different username.");
		}

		// Return true if the username is available (doesn't exist), otherwise, the exception would have been thrown.
		return true;
	}






	public boolean isOrganizationCodeAvailable(String organizationCode) {
		// Sanitize the provided username using getSanitizedString method.
		String sanitizedOrganizationCode = getSanitizedString(organizationCode);

		// Check if the sanitized username already exists in the userRepository.
		if (organizationRepository.existsByOrganizationCode(sanitizedOrganizationCode)) {
			// If the username exists, throw a DataAlreadyExistException with an appropriate error message.
			throw new DataAlreadyExistException("The organization code '" + sanitizedOrganizationCode + "' is already taken. Please choose a different organization code.");
		}

		// Return true if the username is available (doesn't exist), otherwise, the exception would have been thrown.
		return true;
	}






	/**
	 * Checks if an email address is available for registration.
	 *
	 * @param emailAddress The email address to check for availability.
	 * @return True if the email address is available; otherwise, throws a DataAlreadyExistException.
	 * @throws DataAlreadyExistException If the email address is already associated with an existing account.
	 //
	public boolean isEmailAddressAvailable(String emailAddress) {
		// Sanitize the provided email address using getSanitizedString method.
		String sanitizedEmailAddress = getSanitizedString(emailAddress);

		// Check if the sanitized email address already exists in the userRepository.
		if (userRepository.existsByEmail(sanitizedEmailAddress)) {
			// If the email address exists, throw a DataAlreadyExistException with an appropriate error message.
			throw new DataAlreadyExistException("The email address '" + sanitizedEmailAddress + "' is already associated with an existing account. Try logging in or use a different email address for a new account.");
		}

		// Return true if the email address is available (doesn't exist), otherwise, the exception would have been thrown.
		return true;
	}






	public byte[] getProfileByUserId(String authorizationHeader) {
		Long tokenUserId = jwtUtils.getUserIdFromJWT(authorizationHeader);

		// Get the user's existing profile picture, if any
		UserModel user = getUserModelById(tokenUserId);
		FileDataModel existingProfilePicture = user.getProfile();

		try {
			return Files.readAllBytes(new File(existingProfilePicture.getFilePath()).toPath());
		} catch (Exception e) {
			throw new ResourceNotFoundException("Requested document cannot be found.");
		}

	}








//	POST
	/**
	 * Registers a new user with sanitized user data and assigns the default user role.
	 *
	 * @param userRegisterRequest The registration request containing user data.
	 * @return An ApiResponse indicating the result of the user registration process.
	 * @throws DataAlreadyExistException If the provided username or email is already associated with an existing account.
	 //
	@Transactional
	public ApiResponse<?> registerUser(UserRegisterRequest userRegisterRequest) {
		// Sanitize the provided registerRequest data.
		UserRegisterRequest sanitizedUserRegisterRequest = UserRegisterRequest.builder()
				.firstname(getSanitizedString(userRegisterRequest.getFirstname()))
				.middlename(getSanitizedString(userRegisterRequest.getMiddlename()))
				.lastname(getSanitizedString(userRegisterRequest.getLastname()))
				.username(getSanitizedString(userRegisterRequest.getUsername()))
				.email(getSanitizedString(userRegisterRequest.getEmail()))
				.password(getSanitizedString(userRegisterRequest.getPassword()))
				.build();

		try {
			// Check if the sanitized username and email address are available for registration.
			isUsernameAvailable(sanitizedUserRegisterRequest.getUsername());
			isEmailAddressAvailable(sanitizedUserRegisterRequest.getEmail());
		} catch (DataAlreadyExistException exception) {
			// If username or email is not available, rethrow the exception with the same message.
			throw new DataAlreadyExistException(exception.getMessage());
		}

		OrganizationModel organizationModel = null;
		try {
			organizationModel = getOrganizationById(userRegisterRequest.getOrg_id());
		} catch (Exception exception) {
			throw new ResourceNotFoundException("Requested organization cannot be found.");
		}

		// Get the default user role authority from the authority repository.
		AuthorityModel defaultRole;
		try {
			defaultRole = getAuthorityModelByAuthority(Authority.ROLE_DEFAULT);
		} catch (ResourceNotFoundException exception) {
			throw new ResourceNotFoundException("An error occurred while attempting to assign the role (" + Authority.ROLE_DEFAULT + ") to the user.");
		}

		// Create a new UserModel with sanitized user data, encoded password, and default user role.
		UserModel userModel = UserModel.builder()
				.firstname(sanitizedUserRegisterRequest.getFirstname())
				.middlename(sanitizedUserRegisterRequest.getMiddlename())
				.lastname(sanitizedUserRegisterRequest.getLastname())
				.username(sanitizedUserRegisterRequest.getUsername())
				.email(sanitizedUserRegisterRequest.getEmail())
				.password(passwordEncoder.encode(sanitizedUserRegisterRequest.getPassword()))
				.enabled(false)
				.authorities(Collections.singleton(defaultRole))
				.organization(organizationModel)
				.build();

		// Save the created user model to the userRepository.
		UserModel addedUser = userRepository.save(userModel);

		// Return a response indicating successful user registration.
		return new ApiResponse<>(
				"New user registration completed. The user with UID (" + addedUser.getUserId() + ") has been successfully registered with the default user role.",
				addedUser
		);
	}





	@Transactional
	public ApiResponse<?> registerOrganization(OrganizationRegisterRequest organizationRegisterRequest) {
		// Sanitize the provided registerRequest data.
		OrganizationRegisterRequest sanitizedOrganizationRegisterRequest = OrganizationRegisterRequest.builder()
				.org_address(getSanitizedString(organizationRegisterRequest.getOrg_address()))
				.org_code(getSanitizedString(organizationRegisterRequest.getOrg_code()))
				.build();

		try {
			// Check if the sanitized username and email address are available for registration.
			isOrganizationCodeAvailable(sanitizedOrganizationRegisterRequest.getOrg_code());
		} catch (DataAlreadyExistException exception) {
			// If username or email is not available, rethrow the exception with the same message.
			throw new DataAlreadyExistException(exception.getMessage());
		}

		// Create a new UserModel with sanitized user data, encoded password, and default user role.
		OrganizationModel organizationModel = OrganizationModel.builder()
				.organizationCode(sanitizedOrganizationRegisterRequest.getOrg_code())
				.organizationAddress(sanitizedOrganizationRegisterRequest.getOrg_address())
				.build();

		// Save the created user model to the userRepository.
		OrganizationModel addedOrganizationModel = organizationRepository.save(organizationModel);

		// Return a response indicating successful user registration.
		return new ApiResponse<>(
				"New organization registration completed. The organization with OID (" + addedOrganizationModel.getOrganizationId() + ") has been successfully registered with the " + addedOrganizationModel.getOrganizationCode() + " code.",
				addedOrganizationModel
		);
	}






	/**
	 * Signs in a user using provided login credentials.
	 *
	 * @param loginRequest The login request containing user credentials.
	 * @return A JwtTokenResponse containing the generated JWT token upon successful login.
	 * @throws AccountDisabledException If the user account is disabled.
	 * @throws BadCredentialsException If the provided credentials are incorrect.
	 //
	public JwtTokenResponse signInUser(LoginRequest loginRequest) {
		// Sanitize the provided username or email and password using getSanitizedString method.
		String sanitizedUsernameOrEmail = getSanitizedString(loginRequest.getUsername_or_email());
		String sanitizedPassword = getSanitizedString(loginRequest.getPassword());

		// Attempt to find a UserModel with the sanitized username or email in the userRepository.
		Optional<UserModel> userModel = userRepository.findByUsernameOrEmail(sanitizedUsernameOrEmail, sanitizedUsernameOrEmail);

		// Check if a matching user model is found and the provided password matches the stored hashed password.
		if (userModel.isPresent() && passwordEncoder.matches(sanitizedPassword, userModel.get().getPassword())) {

			// If the user is not enabled, throw an AccountDisabledException.
			if (!userModel.get().getEnabled())
				throw new AccountDisabledException("Oops! Looks like your account has been disabled by the administrator.");

			// Generate a JWT token for the user model and return it in a JwtTokenResponse.
			return new JwtTokenResponse(jwtUtils.generateTokenWithUserModel(userModel.get()));
		}

		// If no matching user or wrong credentials are found, throw a BadCredentialsException.
		throw new BadCredentialsException("Bad credentials. No user found with the provided username or email (" + sanitizedUsernameOrEmail + ") and password (" + sanitizedPassword + "). Please check your credentials and try again.");
	}






	/**
	 * Refreshes the JWT token for the user and optionally signs them out from other devices.
	 *
	 * @param authorizationHeader The authorization header containing the JWT token.
	 * @param signOutFromOtherDevices A boolean indicating whether to sign out the user from other devices.
	 * @return A JwtTokenResponse containing the refreshed JWT token.
	 * @throws BadCredentialsException If the user is not found.
	 //
	@Transactional
	public JwtTokenResponse refreshToken(String authorizationHeader, boolean signOutFromOtherDevices) {
		Long tokenUserId = jwtUtils.getUserIdFromJWT(authorizationHeader);
		UserModel userModel;
		try {
			// Retrieve the UserModel based on the user ID extracted from the JWT token.
			userModel = getUserModelById(tokenUserId);
		} catch (ResourceNotFoundException exception) {
			throw new BadCredentialsException(exception.getMessage());
		}

		if (signOutFromOtherDevices) {
			// If signOutFromOtherDevices is requested, update the user's updatedAt timestamp.
			userModel.setUpdatedAt(new Date());
			userRepository.save(userModel);
		}

		// Ensure userModel changes are committed to the database before generating the token.
		userRepository.flush();

		// Generate a new JWT token for the user and return it in a JwtTokenResponse.
		return new JwtTokenResponse(jwtUtils.generateTokenWithUserModel(userModel));
	}






	/**
	 * Validates the provided JWT token and checks user authorization against authorized roles.
	 *
	 * @param authorizationHeader The authorization header containing the JWT token.
	 * @param authorizedRoles A set of authorized roles to check against.
	 * @return A TokenValidationResponse indicating user's authority roles.
	 * @throws BadCredentialsException If the user doesn't exist or the token is no longer valid.
	 * @throws AccountDisabledException If the user's account is disabled.
	 * @throws UnauthorizedException If the user is unauthorized to access the resource.
	 //
	public TokenValidationResponse isTokenValid(String authorizationHeader, EnumSet<Authority> authorizedRoles) {
		// Extract the user ID from the JWT token.
		Long tokenUserId = jwtUtils.getUserIdFromJWT(authorizationHeader);

		UserModel userModel;
		try {
			// Retrieve the UserTokenModel based on the extracted user ID.
			userModel = getUserModelById(tokenUserId);
		} catch (ResourceNotFoundException exception) {
			// If the user doesn't exist, throw a BadCredentialsException with an appropriate message.
			throw new BadCredentialsException("Oops! The user with the provided UID (" + tokenUserId + ") doesn't exist anymore. So, the token is no longer valid.");
		}

		// If the user is not enabled, throw an AccountDisabledException.
		if (!userModel.getEnabled())
			throw new AccountDisabledException("Oops! Looks like your account has been disabled by the administrator.");

		// Compare the token's updatedAt timestamp with the userModel's updatedAt timestamp.
		if (jwtUtils.getUptFromJWT(authorizationHeader) != userModel.getUpdatedAt().getTime())
			throw new UnauthorizedException("Your account may have been updated. To ensure you have access to the updated information, please log in again.");

		if (null == userModel.getOrganization())
			throw new AccountDisabledException("Oops, We couldn't find an organization linked to your account. Please reach out to our support team or administrator to get your organization assignment sorted out.");

		// Get the set of authority names associated with the userModel.
		Set<String> authoritiesFromDB = userModel.getAuthorities()
				.stream()
				.map(authorityModel -> authorityModel.getAuthority().name())
				.collect(Collectors.toSet());

		// If authorizedRoles is not empty and no matching roles are found in authoritiesFromDB, throw an UnauthorizedException.
		if (!authorizedRoles.isEmpty() && authoritiesFromDB.stream().noneMatch(str -> authorizedRoles.contains(Authority.valueOf(str))))
			throw new UnauthorizedException();

		// Return a TokenValidationResponse indicating whether the user has ROLE_ADMIN and ROLE_SALES authorities.
		return TokenValidationResponse.builder()
				.admin(authoritiesFromDB.contains(Authority.ROLE_ADMIN.name()))
				.sales(authoritiesFromDB.contains(Authority.ROLE_SALES.name()))
				.build();
	}



	@Transactional
	public ApiResponse<?> uploadUserProfile(String authorizationHeader, MultipartFile file) {
		try {

			// Validate the authorization header and file
			if (file == null || file.isEmpty())
				throw new InvalidDataException("Image is required, it cannot be null.");

			Long tokenUserId = jwtUtils.getUserIdFromJWT(authorizationHeader);

			// Check if the uploaded file has an allowed extension (PNG, JPG, JPEG)
			String originalFileName = file.getOriginalFilename();
			if (!isImageFormatSupported(originalFileName))
				throw new UnsupportedFileFormatException("Invalid file format. Supported formats: PNG, JPG, JPEG");

			// Generate a unique file name using UUID
			String uniqueFileName = UUID.randomUUID().toString() + ".jpeg";

			// Save the uploaded file as a compressed JPEG with the unique file name
			File uploadPath = new File(profileDirectory);
			if (!uploadPath.exists())
				uploadPath.mkdirs();

			File uploadedFile = new File(uploadPath, uniqueFileName);
			Thumbnails.of(file.getInputStream())
					.size(800, 800)
					.outputFormat("jpeg")
					.toFile(uploadedFile);

			// Get the user's existing profile picture, if any
			UserModel user = getUserModelById(tokenUserId);
			FileDataModel existingProfilePicture = user.getProfile();

			// Delete the existing profile picture file, if it exists
			if (existingProfilePicture != null) {
				File existingFile = new File(existingProfilePicture.getFilePath());
				if (existingFile.exists())
					existingFile.delete();
			}

			// Capture the original updatedAt value
			Date originalUpdatedAt = user.getUpdatedAt();

			// Create a FileDataModel and set the unique file name and file type
			FileDataModel fileDataModel = FileDataModel.builder()
					.fileName(uniqueFileName)
					.fileType("image/jpeg")
					.filePath(uploadedFile.getAbsolutePath())
					.build();

			// Save the FileDataModel to the repository first
			FileDataModel savedFileData = fileDataRepository.save(fileDataModel);

			// Set the new profile picture for the user
			user.setProfile(savedFileData);

			// Save the updated user entity with the profile picture reference and the original updatedAt value
			userRepository.save(user);

			// Restore the original updatedAt value
			userRepository.updateUpdatedAtByUserId(tokenUserId, originalUpdatedAt);

			// Return success response
			return new ApiResponse<>("File uploaded successfully.", null);

		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
	}



//	PATCH
	/**
	 * Enables or disables a user based on the provided user ID and isEnabled flag.
	 *
	 * @param userId The ID of the user to enable or disable.
	 * @param isEnabled A flag indicating whether to enable or disable the user.
	 * @return An ApiResponse indicating the result of the enable/disable operation.
	 * @throws ResourceNotFoundException If the user with the provided ID is not found.
	 //
	public ApiResponse<?> setEnabledUser(Long userId, boolean isEnabled) {
		try {
			// Attempt to update the user's enabled status using the updateUser method.
			if (updateUser(userId, new UserRequest(isEnabled))) {
				// Return an appropriate ApiResponse message based on whether the user was enabled or disabled.
				return new ApiResponse<>(
						isEnabled
								? "User enabled. The user with UID (" + userId + ") has been enabled successfully."
								: "User disabled. The user with UID (" + userId + ") has been disabled, and access to the account has been restricted.",
						null
				);
			}
		} catch (ResourceNotFoundException exception) {
			// If a ResourceNotFoundException occurs during updateUser, rethrow it.
			throw new ResourceNotFoundException(exception.getMessage());
		}

		// If the updateUser method didn't succeed, return a generic error message.
		return new ApiResponse<>("Something went wrong, cannot fulfill the request.", null);
	}



	/**
	 * Updates user details based on the provided user ID and UpdateUserDetailsRequest.
	 *
	 * @param userId The ID of the user whose details need to be updated.
	 * @param userDetailsRequest The request containing the updated user details.
	 * @return An ApiResponse indicating the result of the user details update operation.
	 * @throws ResourceNotFoundException If the user with the provided ID is not found.
	 * @throws DataAlreadyExistException If the provided username or email is already associated with another account.
	 * @throws ApplicationException If any other exception occurs during the update operation.
	 //
	public ApiResponse<?> setUserDetails(Long userId, UpdateUserDetailsRequest userDetailsRequest) {
		try {
			// Attempt to update user details using the updateUser method with sanitized input.
			if (updateUser(
					userId,
					new UserRequest(
							getSanitizedString(userDetailsRequest.getFirstname()),
							getSanitizedString(userDetailsRequest.getMiddlename()),
							getSanitizedString(userDetailsRequest.getLastname()),
							getSanitizedString(userDetailsRequest.getUsername()),
							getSanitizedString(userDetailsRequest.getEmail())
					)
				)
			) {
				// Return a success ApiResponse message.
				return new ApiResponse<>(
						"User details updated. The user's details for the UID (" + userId + ") have been successfully updated.",
						null
				);
			}
		} catch (ResourceNotFoundException e1) {
			// If a ResourceNotFoundException occurs, rethrow it.
			throw new ResourceNotFoundException(e1.getMessage());
		} catch (DataAlreadyExistException e2) {
			// If a DataAlreadyExistException occurs, rethrow it.
			throw new DataAlreadyExistException(e2.getMessage());
		} catch (Exception e3) {
			// If any other exception occurs, rethrow it as an ApplicationException.
			throw new ApplicationException(e3.getMessage());
		}

		// If the updateUser method didn't succeed, return a generic error message.
		return new ApiResponse<>("Something went wrong, cannot fulfill the request.", null);
	}



	/**
	 * Updates user password based on the provided user ID and UpdatePasswordRequest.
	 *
	 * @param authorizationHeader The authorization header containing the JWT token.
	 * @param validationResponse The token validation response containing user authority information.
	 * @param userId The ID of the user whose password needs to be updated.
	 * @param passwordRequest The request containing the current and new passwords.
	 * @return An ApiResponse indicating the result of the password update operation.
	 * @throws ResourceNotFoundException If the user with the provided ID is not found.
	 * @throws DataAlreadyExistException If the provided new password is the same as the current password.
	 * @throws UnauthorizedException If the user is not authorized to update the password for the requested user.
	 * @throws ApplicationException If any other exception occurs during the update operation.
	 //
	public ApiResponse<?> setUserPassword(String authorizationHeader, TokenValidationResponse validationResponse, Long userId, UpdatePasswordRequest passwordRequest) {
		try {
			// Check if the user is authorized to access the requested user's data.
			// If the user is neither an admin nor the owner of the requested userId, throw an UnauthorizedException.
			if (!validationResponse.isAdmin() && !jwtUtils.getUserIdFromJWT(authorizationHeader).equals(userId))
				throw new UnauthorizedException();

			// Attempt to update user password using the updateUser method with sanitized input.
			if (updateUser(
					userId,
					new UserRequest(
							getSanitizedString(passwordRequest.getCurrent_password()),
							getSanitizedString(passwordRequest.getNew_password())
					)
				)
			) {
				// Return a success ApiResponse message.
				return new ApiResponse<>(
						"Password updated. The user with UID (" + userId + ") has successfully updated their password.",
						null
				);
			}
		} catch (ResourceNotFoundException e1) {
			// If a ResourceNotFoundException occurs, rethrow it.
			throw new ResourceNotFoundException(e1.getMessage());
		} catch (DataAlreadyExistException e2) {
			// If a DataAlreadyExistException occurs, rethrow it.
			throw new DataAlreadyExistException(e2.getMessage());
		} catch (Exception e3) {
			// If any other exception occurs, rethrow it as an ApplicationException.
			throw new ApplicationException(e3.getMessage());
		}

		// If the updateUser method didn't succeed, return a generic error message.
		return new ApiResponse<>("Something went wrong, cannot fulfill the request.", null);
	}



	/**
	 * Updates user authorities based on the provided user ID and UpdateAuthoritiesRequest.
	 *
	 * @param userId The ID of the user whose authorities need to be updated.
	 * @param authoritiesRequest The request containing the updated authorities for the user.
	 * @return An ApiResponse indicating the result of the authorities update operation.
	 * @throws ResourceNotFoundException If the user with the provided ID is not found.
	 * @throws ApplicationException If any exception occurs during the update operation.
	 //
	public ApiResponse<?> setUserAuthorities(Long userId, UpdateAuthoritiesRequest authoritiesRequest) {
		try {
			// Attempt to update user authorities using the updateUser method with sanitized input.
			if (updateUser(
					userId,
					new UserRequest(getSanitizedSet(authoritiesRequest.getAuthorities()))
				)
			) {
				// Return a success ApiResponse message.
				return new ApiResponse<>(
						"User authorities updated. The user's authorities for the UID (" + userId + ") have been successfully updated.",
						null
				);
			}
		} catch (ResourceNotFoundException e1) {
			// If a ResourceNotFoundException occurs, rethrow it.
			throw new ResourceNotFoundException(e1.getMessage());
		} catch (Exception e2) {
			// If any other exception occurs, rethrow it as an ApplicationException.
			throw new ApplicationException(e2.getMessage());
		}

		// If the updateUser method didn't succeed, return a generic error message.
		return new ApiResponse<>("Something went wrong, cannot fulfill the request.", null);
	}



	/**
	 * Updates user details based on the provided user ID and UserRequest.
	 *
	 * @param userId The ID of the user whose details need to be updated.
	 * @param userRequest The request containing the updated user details.
	 * @return An ApiResponse indicating the result of the user update operation.
	 * @throws ResourceNotFoundException If the user with the provided ID is not found.
	 * @throws ApplicationException If any exception occurs during the update operation.
	 //
	public ApiResponse<?> setUser(Long userId, UserRequest userRequest) {
		try {
			// Attempt to update user using the updateUser method with sanitized input.
			if (updateUser(
					userId,
					new UserRequest(
							getSanitizedString(userRequest.getFirstname()),
							getSanitizedString(userRequest.getMiddlename()),
							getSanitizedString(userRequest.getLastname()),
							getSanitizedString(userRequest.getUsername()),
							getSanitizedString(userRequest.getEmail()),
							getSanitizedString(userRequest.getCurrent_password()),
							getSanitizedString(userRequest.getNew_password()),
							userRequest.getEnabled(),
							getSanitizedSet(userRequest.getAuthorities())
					)
				)
			) {
				// Return a success ApiResponse message.
				return new ApiResponse<>(
						"User update successful. The user with UID (" + userId + ") has been updated successfully.",
						null
				);
			}
		} catch (ResourceNotFoundException e1) {
			// If a ResourceNotFoundException occurs, rethrow it.
			throw new ResourceNotFoundException(e1.getMessage());
		} catch (DataAlreadyExistException e2) {
			// If a DataAlreadyExistException occurs, rethrow it.
			throw new DataAlreadyExistException(e2.getMessage());
		} catch (Exception e3) {
			// If any other exception occurs, rethrow it as an ApplicationException.
			throw new ApplicationException(e3.getMessage());
		}

		// If the updateUser method didn't succeed, return a generic error message.
		return new ApiResponse<>("Something went wrong, cannot fulfill the request.", null);
	}



	/**
	 * Updates a user's information based on the provided user request.
	 *
	 * @param userId The ID of the user to be updated.
	 * @param userRequest The user request containing the updated user information.
	 * @return `true` if the update was successful, `false` otherwise.
	 * @throws ResourceNotFoundException If the user with the provided ID is not found.
	 * @throws BadCredentialsException If the provided current password doesn't match the stored hashed password.
	 * @throws InvalidDataException If the provided new password is the same as the current password.
	 //
	@Transactional
	public boolean updateUser(Long userId, UserRequest userRequest) {
		// Retrieve the UserModel to be updated based on the provided user ID.
		UserModel userModel = getUserModelById(userId);

		// Check and update user's first name if provided.
		if (userRequest.getFirstname() != null)
			userModel.setFirstname(userRequest.getFirstname());

		// Check and update user's middle name if provided.
		if (userRequest.getMiddlename() != null)
			userModel.setMiddlename(userRequest.getMiddlename());

		// Check and update user's last name if provided.
		if (userRequest.getLastname() != null)
			userModel.setLastname(userRequest.getLastname());

		// Check and update user's username if provided and available.
		if (userRequest.getUsername() != null && isUsernameAvailable(userRequest.getUsername()))
			userModel.setUsername(userRequest.getUsername());

		// Check and update user's email if provided and available.
		if (userRequest.getEmail() != null && isEmailAddressAvailable(userRequest.getEmail()))
			userModel.setEmail(userRequest.getEmail());

		// Check and update user's password if current and new passwords are provided.
		if (userRequest.getCurrent_password() != null && userRequest.getNew_password() != null) {

			// Encode and match current password against the stored hashed password.
			String encodedCurrentPassword = passwordEncoder.encode(userRequest.getCurrent_password());
			if (!passwordEncoder.matches(encodedCurrentPassword, userModel.getPassword()))
				throw new BadCredentialsException("Oops! The user with the provided UID (" + userId + ") doesn't exist anymore. So, the token is no longer valid.");

			// Check if new password is different from the current password.
			if (userRequest.getCurrent_password().equals(userRequest.getNew_password()))
				throw new InvalidDataException("Oops! You can't use your old password as the new one. Try something different.");

			// Update the password with the new hashed password.
			userModel.setPassword(passwordEncoder.encode(userRequest.getNew_password()));
		}

		// Check and update user's enabled status if provided.
		if (userRequest.getEnabled() != null)
			userModel.setEnabled(userRequest.getEnabled());

		// Check and update user's authorities if provided.
		if (userRequest.getAuthorities() != null) {
			userModel.setAuthorities(
					userRequest
							.getAuthorities()	// Get the list of authority strings from the userRequest.
							.stream()	// Convert the list to a stream for processing.
							.map(authorityString -> {
								if (authorityString != null) {
									try {
										return Authority.valueOf(authorityString);	// Convert authority string to an Authority enum.
									} catch (IllegalArgumentException ex) {
										return null;	// If not a valid enum, return null.
									}
								}
								return null;	// If authorityString is null, return null.
							})	// Map each authority string to corresponding Authority enum or null.
							.filter(Objects::nonNull)	// Filter out null Authority enums.
							.map(authority -> {
								try {
									return getAuthorityModelByAuthority(authority);	// Convert Authority enum to corresponding AuthorityModel.
								} catch (ResourceNotFoundException ex) {
									return null;	// If corresponding AuthorityModel is not found, return null.
								}
							})	// Map each Authority enum to corresponding AuthorityModel or null.
							.filter(Objects::nonNull)	// Filter out null AuthorityModels.
							.collect(Collectors.toCollection(HashSet::new))	// Collect filtered AuthorityModels into a HashSet.
			);
		}

		// Save the updated user model to the userRepository.
		userRepository.save(userModel);

		// Indicates successful update.
		return true;


		// Tried ReflectionUtils.class to update the Non-null fields
//		Class<UserRequest> userRequestClass = UserRequest.class;
//
//		for (Field field : userRequestClass.getDeclaredFields()) {
//			field.setAccessible(true);
//			try {
//				Object value = field.get(userRequest);
//				if (value != null) {
//					Field fieldFromUserModel = ReflectionUtils.findField(UserModel.class, field.getName());
//					if (fieldFromUserModel != null) {
//						fieldFromUserModel.setAccessible(true);
//
//						if (field.getName().equals("authorities")) {
//							Set<AuthorityModel> setOfAuthorityModel = userRequest.getAuthorities()
//									.stream()
//									.map(authorityString -> new AuthorityModel(authorityString != null ? Enum.valueOf(Authority.class, authorityString) : null))
//									.collect(Collectors.toSet());
//							ReflectionUtils.setField(fieldFromUserModel, userModel, setOfAuthorityModel);
//						} else
//							ReflectionUtils.setField(fieldFromUserModel, userModel, Jsoup.clean(value.toString(), Safelist.simpleText()));
//
//					}
//				}
//			}
//			catch (IllegalAccessException e) {
//				throw new RuntimeException(e);
//			}
//		}
//
//		if (userRequest.getPassword() != null)
//			userModel.setPassword(passwordEncoder.encode(Jsoup.clean(userRequest.getPassword(), Safelist.simpleText())));
//
//	}

//

 */

}
