package com.sprk.service.user.service;


import com.sprk.commons.document.EntitlementModel;
import com.sprk.commons.document.dto.*;
import com.sprk.commons.entity.primary.audit.Auditable;
import com.sprk.commons.entity.primary.common.*;
import com.sprk.commons.entity.website.GalleryModel;
import com.sprk.commons.entity.website.enums.GalleryType;
import com.sprk.commons.lang.EnvProfile;
import com.sprk.commons.lang.JwtWizard;
import com.sprk.commons.tag.Action;
import com.sprk.commons.tag.View;
import com.sprk.commons.tag.Authority;
import com.sprk.commons.entity.primary.course.CourseModel;
import com.sprk.commons.entity.primary.examination.CertificateModel;
import com.sprk.commons.entity.primary.examination.ExamModel;
import com.sprk.commons.entity.primary.examination.mapping.ExamUserMapping;
import com.sprk.commons.entity.primary.examination.tag.FacultyExamStatus;
import com.sprk.commons.entity.primary.user.mapping.NotificationUserMapping;
import com.sprk.commons.entity.primary.user.mapping.UserSkillAttemptMapping;
import com.sprk.commons.entity.primary.user.mapping.UserSkillMapping;
import com.sprk.commons.entity.primary.user.tag.*;
import com.sprk.service.user.dto.InstantRange;
import com.sprk.service.user.dto.payload.*;
import com.sprk.service.user.dto.response.*;
import com.sprk.commons.dto.APIResponse;
import com.sprk.service.user.dto.response.common.FileDataResponse;
import com.sprk.service.user.dto.response.common.JwtTokenResponse;
import com.sprk.commons.dto.TokenValidationResponse;
import com.sprk.commons.entity.primary.user.*;
import com.sprk.service.user.enums.*;
import com.sprk.commons.exception.*;
import com.sprk.service.user.repository.primary.*;
import com.sprk.service.user.repository.mongo.EntitlementRepository;
import com.sprk.service.user.util.*;
import com.sprk.service.user.util.email.EmailUtils;
import com.sprk.service.user.util.hcaptcha.HCaptchaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;







@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final FileDataRepository fileDataRepository;
    private final OrganizationRepository organizationRepository;
    private final ActivityRepository activityRepository;
    private final RequestRepository requestRepository;
    private final HolidayRepository holidayRepository;
    private final NotificationRepository notificationRepository;
    private final EntitlementRepository entitlementRepository;
    private final CourseRepository courseRepository;
    private final LeaveRepository leaveRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserSkillMappingRepository userSkillMappingRepository;
    private final UserSkillAttemptMappingRepository userSkillAttemptMappingRepository;
    private final NotificationUserMappingRepository notificationUserMappingRepository;
    private final ToDoRepository toDoRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final UIDGenerator uidGenerator;
    private final FileUtils fileUtils;
    private final HCaptchaUtils hCaptchaUtils;
    private final EmailUtils emailUtils;
    private final InstantUtils instantUtils;
    private final Sanitizer sanitizer;
    private final ModelMapper modelMapper;
    private final TextHelper textHelper;
    private final JwtWizard jwtUtils;
    private final EnvProfile envProfile;
    private final JsonConverter jsonConverter;

    @Value("${app.upload.dir.doc}")
    private String documentDirectory;

    @Value("${app.upload.dir.profile}")
    private String profileDirectory;

    @Value("${app.email.endpoint}")
    private String defaultExternalEmployeeFormLink;




//  UTIL
    /**
     * Retrieves a user model by its unique identifier.
     *
     * @param userUid The unique identifier of the user.
     * @return The UserModel corresponding to the provided userUid.
     * @throws ResourceNotFoundException If the user with the specified userUid is not found.
     */
    public UserModel getUserModelByUid(String userUid) {
        return modelMapper.getDataById(
                String.valueOf(userUid),
                () -> userRepository.findByUserUid(userUid)
        );
    }

    /**
     * Retrieves a user model by its unique identifier.
     *
     * @param userPid The unique identifier of the user.
     * @return The UserModel corresponding to the provided userUid.
     * @throws ResourceNotFoundException If the user with the specified userUid is not found.
     */
    public UserModel getUserModelByPid(Long userPid) {
        return modelMapper.getDataById(
                String.valueOf(userPid),
                () -> userRepository.findByUserPid(userPid)
        );
    }

    /**
     * Retrieves an employee request model by its unique identifier.
     *
     * @param requestUid The unique identifier of the request.
     * @return The RequestModel corresponding to the provided requestUid.
     * @throws ResourceNotFoundException If the request with the specified requestUid is not found.
     */
    public RequestModel getEmployeeRequestModelByUid(String requestUid) {
        return modelMapper.getDataById(
                String.valueOf(requestUid),
                () -> requestRepository.findByRequestUid(requestUid)
        );
    }

    /**
     * Retrieves an employee request model by its process ID.
     *
     * @param requestPid The process ID of the request.
     * @return The RequestModel corresponding to the provided requestPid.
     * @throws ResourceNotFoundException If the request with the specified process ID is not found.
     */
    public RequestModel getEmployeeRequestModelByPid(Long requestPid) {
        return modelMapper.getDataById(
                String.valueOf(requestPid),
                () -> requestRepository.findByRequestPid(requestPid)
        );
    }

    /**
     * Retrieves an entitlement model by the unique identifier of the request.
     *
     * @param requestUid The unique identifier of the request.
     * @return The EntitlementModel corresponding to the provided requestUid.
     * @throws ResourceNotFoundException If the entitlement model with the specified requestUid is not found.
     */
    public EntitlementModel getEntitlementModelByRequestUid(String requestUid) {
        return modelMapper.getDataById(
                String.valueOf(requestUid),
                () -> entitlementRepository.findByRequestUid(requestUid)
        );
    }

    /**
     * Retrieves an entitlement model by the unique identifier of the user.
     *
     * @param userUid The unique identifier of the user.
     * @return The EntitlementModel corresponding to the provided userUid.
     * @throws ResourceNotFoundException If the entitlement model with the specified userUid is not found.
     */
    public EntitlementModel getEntitlementModelByUserUid(String userUid) {
        return modelMapper.getDataById(
                String.valueOf(userUid),
                () -> entitlementRepository.findByUserUid(userUid)
        );
    }

    /**
     * Retrieves a notification model by its identifier.
     *
     * @param notificationId The identifier of the notification.
     * @return The NotificationModel corresponding to the provided notificationId, if found; otherwise null.
     */
    public NotificationModel getNotificationModelById(Long notificationId) {
        return modelMapper.getOptionalDataById(
                String.valueOf(notificationId),
                () -> notificationRepository.findById(notificationId)
        );
    }

    /**
     * Retrieves a leave request model by its unique identifier.
     *
     * @param leaveRequestUid The unique identifier of the leave request.
     * @return The LeaveRequestModel corresponding to the provided leaveRequestUid.
     * @throws ResourceNotFoundException If the leave request model with the specified leaveRequestUid is not found.
     */
    public LeaveRequestModel getLeaveRequestModelByUid(String leaveRequestUid) {
        return modelMapper.getDataById(
                String.valueOf(leaveRequestUid),
                () -> leaveRequestRepository.findByLeaveRequestUid(leaveRequestUid)
        );
    }




    /**
     * Saves a file locally in the specified directory.
     *
     * @param directory The directory where the file will be saved.
     * @param file      The file to be saved locally.
     * @return The FileDataModel containing information about the saved file.
     * @throws InvalidDataException If the file is empty, has an invalid filename, or encounters an error during saving.
     */
    public FileDataModel saveFileLocally(
            String directory,
            MultipartFile file
    ) {
        if (null == file || file.isEmpty())
            throw new InvalidDataException("No file found.");

        String fileExtension = fileUtils.getExtension(file.getOriginalFilename());
        if (textHelper.isBlank(file.getOriginalFilename()) || null == fileExtension)
            throw new InvalidDataException("Invalid filename or extension.");

        String fileName = UUID.randomUUID() + "-" + formattedNOW() + "." + fileExtension;
        try {
            Path filePath = Paths.get(directory, fileName);
            file.transferTo(filePath);

            return FileDataModel.builder()
                    .fileName(fileName)
                    .fileUid(generateFileUid())
                    .fileOriginal(sanitizer.sanitize(file.getOriginalFilename()))
                    .fileType(file.getContentType())
                    .filePath(filePath.toString())
                    .build();

        } catch (Exception exception) {
            throw new InvalidDataException(exception.getMessage());
        }
    }




    /**
     * Copies a file locally to the specified directory.
     *
     * @param directory         The directory where the file will be copied.
     * @param existingFileModel The FileDataModel of the existing file to be copied.
     * @return The FileDataModel containing information about the copied file.
     * @throws InvalidDataException If the existingFileModel is null or has an empty file path, or encounters an error during copying.
     */
    public FileDataModel copyFileLocally(
            String directory,
            FileDataModel existingFileModel
    ) {
        if (null == existingFileModel || textHelper.isBlank(existingFileModel.getFilePath()))
            return null;

        Path existingFilePath = Paths.get(existingFileModel.getFilePath());
        String fileExtension = fileUtils.getExtension(existingFilePath.getFileName().toString());
        if (null == fileExtension)
            return null;

        String fileName = UUID.randomUUID() + "-" + formattedNOW() + "." + fileExtension;
        try {
            Path newFilePath = Paths.get(directory, fileName);
            Files.copy(existingFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);

            return FileDataModel.builder()
                    .fileName(fileName)
                    .fileUid(generateFileUid())
                    .fileOriginal(sanitizer.sanitize(existingFileModel.getFileOriginal()))
                    .fileType(existingFileModel.getFileType())
                    .filePath(newFilePath.toString())
                    .build();

        } catch (Exception exception) {
            throw new InvalidDataException(exception.getMessage());
        }
    }

    public static String formattedNOW() {
        return Instant.now()
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
    }


    /**
     * Deletes a file locally using the specified file path.
     *
     * @param filePath The path of the file to be deleted.
     * @return true if the file is successfully deleted or does not exist; false otherwise.
     */
    private boolean deleteFileLocally(String filePath) {
        File existingFile = new File(filePath);
        return !existingFile.exists() || existingFile.delete();
    }

    /**
     * Checks if any documents are present in the provided array of MultipartFiles.
     *
     * @param documents The array of MultipartFiles to be checked.
     * @return true if any of the documents is not null and not empty; false otherwise.
     */
    private boolean hasDocuments(
            MultipartFile... documents
    ) {
        for (MultipartFile document : documents) {
            if (document != null && !document.isEmpty())
                return true;
        }

        return false;
    }

    /**
     * Updates a document by saving it locally and setting it in the specified consumer.
     *
     * @param document            The document to be updated.
     * @param directory           The directory where the updated document will be saved.
     * @param existingSupplier    A supplier to get the existing FileDataModel.
     * @param setConsumer         A consumer to set the updated FileDataModel.
     * @param isImageOnly         A flag indicating whether the document should be an image only.
     * @throws InvalidDataException If the document format is not supported or an error occurs during file operations.
     */
    private void updateDocumentByFileDataModel(
            MultipartFile document,
            String directory,
            Supplier<FileDataModel> existingSupplier,
            Consumer<FileDataModel> setConsumer,
            boolean isImageOnly
    ) {
        if (document != null && !document.isEmpty()) {
            boolean hasValidImageExtension = fileUtils.hasValidImageExtension(document.getOriginalFilename());
            boolean hasValidPdfExtension = fileUtils.hasValidPdfExtension(document.getOriginalFilename());
            if (isImageOnly && !hasValidImageExtension)
                throw new InvalidDataException("We are unable to continue with the document named (" + document.getOriginalFilename() + "). The document must be an image in JPG, JPEG, or PNG format.");
            if (!isImageOnly && !hasValidImageExtension && !hasValidPdfExtension)
                throw new InvalidDataException("We are unable to continue with the document named (" + document.getOriginalFilename() + "). The document must be an PDF or an image in JPG, JPEG, or PNG format.");

            FileDataModel existingFileDataModel = existingSupplier.get();
            FileDataModel savedFileDataModel = saveFileLocally(directory, document);

            if (existingFileDataModel != null) {
                deleteFileLocally(existingFileDataModel.getFilePath());
                savedFileDataModel.setFileId(existingFileDataModel.getFileId());
            }

            setConsumer.accept(savedFileDataModel);
        }
    }

    /**
     * Finds missing enum models based on the given enum values and existing models.
     *
     * @param enumValues      The list of enum values.
     * @param existingModels  The list of existing models.
     * @param keyFunction     A function to extract a unique key from the existing models.
     * @param modelConstructor A function to construct a new model from the enum value.
     * @param <E>             The enum type.
     * @param <M>             The model type.
     * @return A set of missing models.
     */
    private <E extends Enum<E>, M> Set<M> findMissingEnumModels(
            List<E> enumValues,
            List<M> existingModels,
            Function<M, String> keyFunction,
            Function<E, M> modelConstructor
    ) {
        Map<String, M> existingModelMap = existingModels
                .stream()
                .collect(Collectors.toMap(keyFunction, model -> model));

        return enumValues
                .stream()
                .filter(enumValue -> !existingModelMap.containsKey(enumValue.toString()))
                .map(modelConstructor)
                .collect(Collectors.toSet());
    }

    /**
     * Generates a unique UID using the specified supplier, counter function, and maximum attempts.
     *
     * @param uidGenerationSupplier The supplier for generating UIDs.
     * @param counterFunction        A function to get the counter based on the generated UID.
     * @param maxAttempts            The maximum number of attempts to generate a unique UID.
     * @return The generated unique UID.
     */
    private String generateUniqueUid(Supplier<String> uidGenerationSupplier, Function<String, Long> counterFunction, int maxAttempts) {
        String generatedRequestUid;
        long counter;
        do {
            generatedRequestUid = uidGenerationSupplier.get();
            counter = counterFunction.apply(generatedRequestUid);
            if (counter <= 0)
                break;

            maxAttempts--;
        } while (maxAttempts > 0);

        if (maxAttempts == 0)
            generatedRequestUid += counter + 1;

        return generatedRequestUid;
    }


    /**
     * Generates a list of InstantRange objects representing consecutive ranges of time intervals between the provided start and end instants.
     *
     * @param start The start instant of the range (inclusive).
     * @param end   The end instant of the range (exclusive).
     * @return A list of InstantRange objects representing consecutive ranges of time intervals.
     */
    private List<InstantRange> generateInstantRanges(Instant start, Instant end) {
        List<InstantRange> instantRanges = new ArrayList<>();
        Instant currentStart = start;

        while (currentStart.isBefore(end)) {
            Instant currentEnd = currentStart.plus(1, ChronoUnit.DAYS);
            if (currentEnd.isAfter(end))
                currentEnd = end;

//            Instant truncatedStart = currentStart.truncatedTo(ChronoUnit.DAYS);
//            Instant truncatedEnd = currentEnd.truncatedTo(ChronoUnit.DAYS);
//            if (!truncatedStart.equals(currentStart) && truncatedEnd.equals(currentEnd))
//                currentEnd = currentEnd.truncatedTo(ChronoUnit.DAYS);

            instantRanges.add(new InstantRange(currentStart, currentEnd));
            currentStart = currentEnd;
        }

        if (instantRanges.isEmpty())
            instantRanges.add(new InstantRange(start, end));

        return instantRanges;
    }











    /**
     * This method is annotated with @PostConstruct, which means it is automatically invoked after the Spring Bean is initialized.
     * It performs the initialization of certain data when the application starts up. It ensures that specific directories exist and
     * creates certain database records for authorities and actions if they are missing.
     * The method performs the following actions:
     * 1. Ensure that the 'documentDirectory' and 'profileDirectory' directories exist using the 'fileUtils.ensureDirectoryExists' method.
     * 2. It checks for missing AuthorityModels based on the values in the 'Authority' enum and the existing records in the 'authorityRepository'.
     *    If any missing authorities are found, they are created and saved in the repository.
     * 3. It checks for missing ActionModels based on the values in the 'Action' enum and the existing records in the 'actionRepository'.
     *    If any missing actions are found, they are created and saved in the repository.
     * This method is transactional, which means that the changes made to the database during its execution will be rolled back in case of an error.
     * It is important to make sure that the 'authorityRepository' and 'actionRepository' are properly configured as Spring Data JPA repositories
     * to interact with the database.
     */
    @PostConstruct
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public void init() {
        // Ensure the existence of required directories
        fileUtils.ensureDirectoryExists(documentDirectory);
        fileUtils.ensureDirectoryExists(profileDirectory);

/*
        Optional<OrganizationModel> organization = organizationRepository.findById(1L);
        if(organization.isPresent()){
            organization.get().setAcademicValidity(7);
            organization.get().setFinancialValidity(60);
            organizationRepository.save(organization.get());
        }
*/

        /*
        // Create missing view models based on the View enum
        Set<ViewModel> viewsToCreate = findMissingEnumModels(
                Arrays.asList(View.values()),
                viewRepository.findAll(),
                viewModel -> viewModel.getView().name(),
                view -> ViewModel.builder().view(view).build()
        );

        // Create missing action models based on the Action enum
        Set<ActionModel> actionsToCreate = findMissingEnumModels(
                Arrays.asList(Action.values()),
                actionRepository.findAll(),
                actionModel -> actionModel.getAction().name(),
                action -> ActionModel.builder().action(action).build()
        );

        // Save the newly created views and actions if any
        if (!viewsToCreate.isEmpty())
            viewRepository.saveAll(viewsToCreate);

        if (!actionsToCreate.isEmpty())
            actionRepository.saveAll(actionsToCreate);
         */


//        List<FileDataModel> files = fileDataRepository.findAll().stream().filter(file -> null == file.getFileUid() || file.getFileUid().isEmpty() || file.getFileUid().isBlank()).collect(Collectors.toList());
//
//        if(!files.isEmpty()) {
//            for(FileDataModel file : files){
//                boolean existsInDatabase;
//                int attempts = 0;
//                String fileUid;
//                LocalDateTime createdAt = LocalDateTime.ofInstant(file.getCreatedAt(), ZoneOffset.UTC);
//                String prefix = "F" + DateTimeFormatter.ofPattern("yyMMddHHmm").format(createdAt);
//
//                do {
//                    String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
//                    fileUid = prefix + uuid;
//                    existsInDatabase = fileDataRepository.existsByFileUid(fileUid);
//                    attempts++;
//                }while (existsInDatabase && attempts <= 100);
//
//                if (existsInDatabase) {
//                    throw new InvalidDataException("Failed to add file.Try again");
//                }
//                file.setFileUid(fileUid);
//            }
//            fileDataRepository.saveAll(files);
//        }


/*
        List<RequestModel> employeeRequests = requestRepository
                .findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(model -> {
                    model.setFirstname(
                            StringUtils.capitalize(model.getFirstname())
                    );
                    model.setMiddlename(
                            StringUtils.capitalize(model.getMiddlename())
                    );
                    model.setLastname(
                            StringUtils.capitalize(model.getLastname())
                    );

                    return model;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        requestRepository.saveAll(employeeRequests);

        List<UserModel> users = userRepository
                .findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(model -> {
                    model.setFirstname(
                            StringUtils.capitalize(model.getFirstname())
                    );
                    model.setMiddlename(
                            StringUtils.capitalize(model.getMiddlename())
                    );
                    model.setLastname(
                            StringUtils.capitalize(model.getLastname())
                    );

                    return model;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        userRepository.saveAll(users);
 */






        // Populate holidays if the holiday repository is empty
        if(0 == holidayRepository.count()) {
            // Define holiday models for the years 2023 and 2024
            List<HolidayModel> holidayModels = List.of(
                    HolidayModel.builder().holidayUid(null).holidayName("NEW YEAR").holidayStart(Instant.parse("2022-12-31T18:30:00.000Z")).holidayEnd(Instant.parse("2023-01-01T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("REPUBLIC DAY").holidayStart(Instant.parse("2023-01-25T18:30:00.000Z")).holidayEnd(Instant.parse("2023-01-26T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("HOLI").holidayStart(Instant.parse("2023-03-07T18:30:00.000Z")).holidayEnd(Instant.parse("2023-03-08T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("GUDI PADWA").holidayStart(Instant.parse("2023-03-21T18:30:00.000Z")).holidayEnd(Instant.parse("2023-03-22T18:00:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("MAHARASHTRA DAY").holidayStart(Instant.parse("2023-04-30T18:30:00.000Z")).holidayEnd(Instant.parse("2023-05-01T18:00:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("INDEPENDENCE DAY").holidayStart(Instant.parse("2023-07-14T18:30:00.000Z")).holidayEnd(Instant.parse("2023-08-15T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("GANESH CHATURTI").holidayStart(Instant.parse("2023-09-18T18:30:00.000Z")).holidayEnd(Instant.parse("2023-09-19T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("ANANT CHATURTI").holidayStart(Instant.parse("2023-09-27T18:30:00.000Z")).holidayEnd(Instant.parse("2023-09-28T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("DUSSHERA").holidayStart(Instant.parse("2023-10-23T18:30:00.000Z")).holidayEnd(Instant.parse("2023-10-24T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("DIWALI").holidayStart(Instant.parse("2023-11-11T18:30:00.000Z")).holidayEnd(Instant.parse("2023-11-12T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("DIWALI").holidayStart(Instant.parse("2023-11-12T18:30:00.000Z")).holidayEnd(Instant.parse("2023-11-13T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("DIWALI").holidayStart(Instant.parse("2023-11-13T18:30:00.000Z")).holidayEnd(Instant.parse("2023-11-14T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("CHRISTMAS").holidayStart(Instant.parse("2023-12-24T18:30:00.000Z")).holidayEnd(Instant.parse("2023-12-25T18:30:00.000Z")).build()

            );

            List<HolidayModel> holidayModels1 = List.of(
                    HolidayModel.builder().holidayUid(null).holidayName("NEW YEAR").holidayStart(Instant.parse("2023-12-31T18:30:00.000Z")).holidayEnd(Instant.parse("2024-01-01T18:00:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("REPUBLIC DAY").holidayStart(Instant.parse("2024-01-25T18:30:00.000Z")).holidayEnd(Instant.parse("2024-01-26T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("HOLI").holidayStart(Instant.parse("2023-03-24T18:30:00.000Z")).holidayEnd(Instant.parse("2023-03-25T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("GUDI PADWA").holidayStart(Instant.parse("2024-04-21T18:30:00.000Z")).holidayEnd(Instant.parse("2024-04-22T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("MAHARASHTRA DAY").holidayStart(Instant.parse("2024-04-30T18:30:00.000Z")).holidayEnd(Instant.parse("2024-05-01T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("INDEPENDENCE DAY").holidayStart(Instant.parse("2024-08-14T18:30:00.000Z")).holidayEnd(Instant.parse("2024-08-15T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("GANESH CHATURTI").holidayStart(Instant.parse("2024-09-06T18:30:00.000Z")).holidayEnd(Instant.parse("2024-09-07T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("ANANT CHATURTI").holidayStart(Instant.parse("2024-09-15T18:30:00.000Z")).holidayEnd(Instant.parse("2024-09-16T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("DUSSHERA").holidayStart(Instant.parse("2024-10-11T18:30:00.000Z")).holidayEnd(Instant.parse("2024-10-12T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("DIWALI").holidayStart(Instant.parse("2024-10-30T18:30:00.000Z")).holidayEnd(Instant.parse("2024-10-31T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("DIWALI").holidayStart(Instant.parse("2024-10-31T18:30:00.000Z")).holidayEnd(Instant.parse("2024-11-01T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("DIWALI").holidayStart(Instant.parse("2024-11-01T18:30:00.000Z")).holidayEnd(Instant.parse("2024-11-02T18:30:00.000Z")).build(),
                    HolidayModel.builder().holidayUid(null).holidayName("CHRISTMAS").holidayStart(Instant.parse("2024-12-24T18:30:00.000Z")).holidayEnd(Instant.parse("2024-12-25T18:30:00.000Z")).build()

            );
            // Save the holiday models to the repository
            holidayRepository.saveAll(holidayModels);
        }


        // If the organization repository is empty, populate it with initial organization data
        if (0 != organizationRepository.count())
            return;

        // If there are existing organizations, delete all entitlements
        if (0 != entitlementRepository.count())
            entitlementRepository.deleteAll();

        // Create and save the main organization
        OrganizationModel organizationModel = OrganizationModel.builder()
                .code("KHAR")
                .address("102-105, 1st floor, Royal Palace, Sector-2, Plot: 11, Opp. Glomax Mall, Kharghar, Navi Mumbai, Maharashtra, India")
                .zone("Asia/Calcutta")
                .maxFileUploadLimit(10485760L)
                .certificateReleaseInDays(15)
                .academicValidity(7)
                .financialValidity(60)
                .build();
        OrganizationModel addedOrganization = organizationRepository.save(organizationModel);

        // Create and save a super admin user with entitlements
        EmployeeModel superEmployeeModel = EmployeeModel.builder()
                .annualLeaves(15)
                .inTime(
                        toUTC(LocalTime.of(10, 0))
                )
                .outTime(
                        toUTC(LocalTime.of(20, 0))
                )
                .isMonday(true)
                .isTuesday(true)
                .isWednesday(true)
                .isThursday(true)
                .isFriday(true)
                .isSaturday(true)
                .isSunday(true)
                .build();
        UserModel superUserModel = UserModel.builder()
                .userUid(
                        uidGenerator.generateEmployeeId(
                                Instant.now(),
                                "Kavita"
                        )
                )
                .joinedAt(Instant.now())
                .tokenAt(Instant.now())
                .firstname("Kavita")
                .middlename("Pankaj")
                .lastname("Pawar")
                .email("kavita@sprktechnologies.in")
                .phone("1234567890")
                .password(passwordEncoder.encode("Kavita@123"))
                .enabled(true)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .employeeDetails(superEmployeeModel)
                .organization(addedOrganization)
                .build();
        superEmployeeModel.setUser(superUserModel);
        UserModel addedSuperUserModel = userRepository.save(superUserModel);

        HashSet<MainTab> superEntitlements = new HashSet<>(Set.of(
                MainTab.builder()
                        .name(View.LMS)
                        .sub(new HashSet<>(Set.of(
                                SubTab.builder()
                                        .name(View.LEADS)
                                        .actions(new HashSet<>(
                                                Set.of(Action.CREATE, Action.VIEW, Action.DELETE)
                                        ))
                                        .build()
                        )))
                        .build(),
                MainTab.builder()
                        .name(View.CENTER)
                        .sub(new HashSet<>(Set.of(
                                SubTab.builder()
                                        .name(View.ENROLLMENTS)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.RBC)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.COLLECTIONS)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.BOOKINGS)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build()
                        )))
                        .build(),
                MainTab.builder()
                        .name(View.EMS)
                        .sub(new HashSet<>(Set.of(
                                SubTab.builder()
                                        .name(View.EMPLOYEES)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.PORTAL_ACCESS_REQUEST)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.FACULTY_EXAM)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.ACTIVITIES)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.LEAVE_REQUESTS)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build()
                        )))
                        .build(),
                MainTab.builder()
                        .name(View.BATCH_SCHEDULING)
                        .sub(new HashSet<>(Set.of(
                                SubTab.builder()
                                        .name(View.BATCHES)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.FACULTY_SCHEDULE)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.STUDENT_SCHEDULE)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build()
                        )))
                        .build()
        ));

        EntitlementModel superEntitlementModel = EntitlementModel.builder()
                .requestUid(null)
                .userUid(addedSuperUserModel.getUserUid())
                .authorities(new HashSet<>(Set.of(
                        Authority.ROLE_ADMIN.name()
                )))
                .entitlements(superEntitlements)
                .build();
        entitlementRepository.save(superEntitlementModel);


        // Create and save a sales user with entitlements
        EmployeeModel salesEmployeeModel = EmployeeModel.builder()
                .annualLeaves(15)
                .inTime(
                        toUTC(LocalTime.of(10, 0))
                )
                .outTime(
                        toUTC(LocalTime.of(20, 0))
                )
                .isMonday(true)
                .isTuesday(true)
                .isWednesday(true)
                .isThursday(true)
                .isFriday(true)
                .isSaturday(false)
                .isSunday(true)
                .build();
        UserModel salesUserModel = UserModel.builder()
                .userUid(
                        uidGenerator.generateEmployeeId(
                                Instant.now(),
                                "Krutika"
                        )
                )
                .joinedAt(Instant.now())
                .tokenAt(Instant.now())
                .firstname("Krutika")
                .middlename("Rushi")
                .lastname("Rushi")
                .email("krutika@sprktechnologies.in")
                .phone("1234567890")
                .password(passwordEncoder.encode("Krutika@123"))
                .enabled(true)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .employeeDetails(salesEmployeeModel)
                .organization(addedOrganization)
                .build();
        salesEmployeeModel.setUser(salesUserModel);
        UserModel addedSalesUserModel = userRepository.save(salesUserModel);

        HashSet<MainTab> salesEntitlements = new HashSet<>(Set.of(
                MainTab.builder()
                        .name(View.LMS)
                        .sub(new HashSet<>(Set.of(
                                SubTab.builder()
                                        .name(View.LEADS)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW, Action.DELETE
                                        )))
                                        .build()
                        )))
                        .build(),
                MainTab.builder()
                        .name(View.CENTER)
                        .sub(new HashSet<>(Set.of(
                                SubTab.builder()
                                        .name(View.ENROLLMENTS)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.COLLECTIONS)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.BOOKINGS)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build()
                        )))
                        .build()
        ));

        EntitlementModel salesEntitlementModel = EntitlementModel.builder()
                .requestUid(null)
                .userUid(addedSalesUserModel.getUserUid())
                .authorities(new HashSet<>(Set.of(
                        Authority.ROLE_SALES.name()
                )))
                .entitlements(salesEntitlements)
                .build();
        entitlementRepository.save(salesEntitlementModel);


        // Create and save faculty members with entitlements
        EmployeeModel faculty1EmployeeModel = EmployeeModel.builder()
                .annualLeaves(15)
                .inTime(
                        toUTC(LocalTime.of(10, 0))
                )
                .outTime(
                        toUTC(LocalTime.of(21, 0))
                )
                .isMonday(false)
                .isTuesday(false)
                .isWednesday(false)
                .isThursday(false)
                .isFriday(true)
                .isSaturday(true)
                .isSunday(true)
                .skillSet(Arrays.toString(Arrays.asList("C", "Java", "Python", "Tableau").toArray()))
                .build();
        UserModel faculty1UserModel = UserModel.builder()
                .userUid(
                        uidGenerator.generateEmployeeId(
                                Instant.now(),
                                "Pankaj"
                        )
                )
                .joinedAt(Instant.now())
                .tokenAt(Instant.now())
                .firstname("Pankaj")
                .middlename(null)
                .lastname("Pawar")
                .email("pankaj@sprktechnologies.in")
                .phone("1234567890")
                .password(passwordEncoder.encode("Pankaj@123"))
                .enabled(true)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .employeeDetails(faculty1EmployeeModel)
                .organization(addedOrganization)
                .build();
        faculty1EmployeeModel.setUser(faculty1UserModel);
        UserModel addedFaculty1UserModel = userRepository.save(faculty1UserModel);

        HashSet<MainTab> faculty1Entitlements = new HashSet<>(Set.of(
                MainTab.builder()
                        .name(View.BATCH_SCHEDULING)
                        .sub(new HashSet<>(Set.of(
                                SubTab.builder()
                                        .name(View.BATCHES)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.FACULTY_SCHEDULE)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.STUDENT_SCHEDULE)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build()
                        )))
                        .build()
        ));

        EntitlementModel faculty1EntitlementModel = EntitlementModel.builder()
                .requestUid(null)
                .userUid(addedFaculty1UserModel.getUserUid())
                .authorities(new HashSet<>(Set.of(
                        Authority.ROLE_FACULTY.name()
                )))
                .entitlements(faculty1Entitlements)
                .build();
        entitlementRepository.save(faculty1EntitlementModel);



        EmployeeModel faculty2EmployeeModel = EmployeeModel.builder()
                .annualLeaves(15)
                .inTime(
                        toUTC(LocalTime.of(13, 0))
                )
                .outTime(
                        toUTC(LocalTime.of(21, 0))
                )
                .isMonday(true)
                .isTuesday(true)
                .isWednesday(true)
                .isThursday(true)
                .isFriday(false)
                .isSaturday(true)
                .isSunday(true)
                .skillSet(Arrays.toString(Arrays.asList("C", "C++", "C#", "Dot Net", "HTML").toArray()))
                .build();
        UserModel faculty2UserModel = UserModel.builder()
                .userUid(
                        uidGenerator.generateEmployeeId(
                                Instant.now(),
                                "Vivek"
                        )
                )
                .joinedAt(Instant.now())
                .tokenAt(Instant.now())
                .firstname("Vivek")
                .middlename(null)
                .lastname(null)
                .email("vivek@sprktechnologies.in")
                .phone("2234567890")
                .password(passwordEncoder.encode("Vivek@123"))
                .enabled(true)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .employeeDetails(faculty2EmployeeModel)
                .organization(addedOrganization)
                .build();
        faculty2EmployeeModel.setUser(faculty2UserModel);
        UserModel addedFaculty2UserModel = userRepository.save(faculty2UserModel);

        HashSet<MainTab> faculty2Entitlements = new HashSet<>(Set.of(
                MainTab.builder()
                        .name(View.BATCH_SCHEDULING)
                        .sub(new HashSet<>(Set.of(
                                SubTab.builder()
                                        .name(View.BATCHES)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.FACULTY_SCHEDULE)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build(),
                                SubTab.builder()
                                        .name(View.STUDENT_SCHEDULE)
                                        .actions(new HashSet<>(Set.of(
                                                Action.CREATE, Action.VIEW
                                        )))
                                        .build()
                        )))
                        .build()
        ));

        EntitlementModel faculty2EntitlementModel = EntitlementModel.builder()
                .requestUid(null)
                .userUid(addedFaculty2UserModel.getUserUid())
                .authorities(new HashSet<>(Set.of(
                        Authority.ROLE_FACULTY.name()
                )))
                .entitlements(faculty2Entitlements)
                .build();
        entitlementRepository.save(faculty2EntitlementModel);


    }



    /**
     * Generates a unique file UID based on the current date-time and a random UUID suffix.
     * The method ensures that the generated UID does not already exist in the database.
     *
     * @return A unique file UID.
     * @throws InvalidDataException If the method fails to generate a unique UID after 100 attempts.
     */
    public String generateFileUid() {
        // Generate the prefix using the current date-time in a specific format
        String prefix = "F" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        String fileUid;
        boolean existsInDatabase;
        int attempts = 0;
        do {
            // Generate a random UUID suffix and combine it with the prefix to create the UID
            String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            fileUid = prefix + uuid;
            // Check if the generated UID already exists in the database
            existsInDatabase = fileDataRepository.existsByFileUid(fileUid);
            attempts++;
        } while (existsInDatabase && attempts <= 100);

        // If the generated UID still exists after 100 attempts, throw an exception
        if (existsInDatabase) {
            throw new InvalidDataException("Failed to add file.Try again");
        }

        return fileUid;
    }






//  GET
    /**
     * Retrieves user details based on the provided token validation response.
     *
     * @param validationResponse The token validation response containing the user's unique identifier (UID).
     * @return An APIResponse containing the user details corresponding to the provided UID.
     */
    @Override
    public APIResponse<?> getUserDetailsByToken(
            TokenValidationResponse validationResponse
    ) {
        return APIResponse.builder()
                .data(getUserDetails(validationResponse.getUid()))
                .build();
    }

    /**
     * Retrieves user details based on the provided user UID.
     *
     * @param userUid The unique identifier (UID) of the user.
     * @return An APIResponse containing the user details corresponding to the provided UID.
     */
    @Override
    public APIResponse<?> getUserDetailsByUid(
            String userUid
    ) {
        return APIResponse.builder()
                .data(getUserDetails(userUid))
                .build();
    }

    /**
     * Retrieves employees who are transferable for enquiries.
     *
     * @return An APIResponse containing the transferable employees for enquiries.
     */
    @Override
    public APIResponse<?> getEnquiryTransferableEmployees() {
        return APIResponse.builder()
                .data(getUsersByAuthorities(Set.of(Authority.ROLE_ADMIN.name(), Authority.ROLE_SALES.name())))
                .build();
    }

    /**
     * Retrieves all faculty members.
     *
     * @return An APIResponse containing all faculty members.
     */
    @Override
    public APIResponse<?> getAllFaculties() {
        return APIResponse.builder()
                .data(getUsersByAuthorities(Set.of(Authority.ROLE_FACULTY.name())))
                .build();
    }

    /**
     * Retrieves all faculties associated with a specific course.
     *
     * @param courseName The name of the course.
     * @return An APIResponse containing all faculties associated with the specified course.
     */
    @Override
    public APIResponse<?> getAllFacultiesByCourse(String courseName) {
        return APIResponse.builder()
                .data(getUsersBySkill(courseName))
                .build();
    }

    /**
     * Retrieves a table of employees.
     *
     * @return An APIResponse containing a table of employees.
     */
    @Override
    public APIResponse<?> getTableOfEmployees() {
        return APIResponse.builder()
                .data(getAllTEmployees())
                .build();
    }

    /**
     * Retrieves a table of employee requests.
     *
     * @return An APIResponse containing a table of employee requests.
     */
    @Override
    public APIResponse<?> getTableOfEmployeeRequests() {
        return APIResponse.builder()
                .data(getAllTEmployeeRequests())
                .build();
    }

    /**
     * Retrieves notifications based on the provided token validation response.
     *
     * @param validationResponse The token validation response containing the process ID (PID).
     * @return An APIResponse containing notifications associated with the provided PID.
     */
    @Override
    public APIResponse<?> getNotificationsByToken(
            TokenValidationResponse validationResponse
    ) {
        return APIResponse.builder()
                .data(getAllNotifications(validationResponse.getPid(), false))
                .build();
    }

    /**
     * Retrieves a table of activities.
     *
     * @return An APIResponse containing a table of activities.
     */
    @Override
    public APIResponse<?> getTableOfActivities() {
        return APIResponse.builder()
                .data(getEmployeeActivities(null))
                .build();
    }

    /**
     * Retrieves employee details based on the provided user UID.
     *
     * @param userUid The unique identifier (UID) of the employee.
     * @return An APIResponse containing details of the employee corresponding to the provided UID.
     */
    @Override
    public APIResponse<?> getEmployeeDetailsByUid(
            String userUid
    ) {
        return APIResponse.builder()
                .data(getEmployeeDetails(userUid))
                .build();
    }

    /**
     * Retrieves employee request details based on the provided user UID.
     *
     * @param userUid The unique identifier (UID) of the user.
     * @return An APIResponse containing details of employee requests associated with the provided user UID.
     */
    @Override
    public APIResponse<?> getEmployeeRequestDetailsByUserUid(
            String userUid
    ) {
        return APIResponse.builder()
                .data(getEmployeeRequestDetails(userUid, false, null))
                .build();
    }

    /**
     * Retrieves employee request details based on the provided request UID.
     *
     * @param requestUid The unique identifier (UID) of the request.
     * @return An APIResponse containing details of the employee request corresponding to the provided request UID.
     */
    @Override
    public APIResponse<?> getEmployeeRequestDetailsByRequestUid(
            String requestUid
    ) {
        return APIResponse.builder()
                .data(getEmployeeRequestDetails(requestUid, true, null))
                .build();
    }

    /**
     * Retrieves employee request details based on the provided token validation response.
     *
     * @param authorizationHeader The token validation response containing the user's UID.
     * @return An APIResponse containing details of the employee request associated with the user UID from the validation response.
     */
    @Override
    public APIResponse<?> getEmployeeRequestDetailsByToken(
            String authorizationHeader
    ) {
        String subject = jwtUtils.getSubject(authorizationHeader);
        Long upt = jwtUtils.getPayload(authorizationHeader);
        return APIResponse.builder()
                .data(getEmployeeRequestDetails(subject, true, upt))
                .build();
    }

    /**
     * Retrieves activities based on the provided user UID.
     *
     * @param userUid The unique identifier (UID) of the user.
     * @return An APIResponse containing activities associated with the provided user UID.
     */
    @Override
    public APIResponse<?> getActivitiesByUid(
            String userUid
    ) {
        return APIResponse.builder()
                .data(getEmployeeActivities(userUid))
                .build();
    }

    /**
     * Retrieves the profile photo of the user based on the provided token validation response.
     *
     * @param validationResponse The token validation response containing the user's UID.
     * @return A FileDataResponse containing the profile photo of the user.
     */
    @Override
    public FileDataResponse getProfilePhotoByToken(
            TokenValidationResponse validationResponse
    ) {
        return getDocumentById(
                validationResponse,
                validationResponse.getUid(),
                DocumentType.DOCUMENT_PROFILE,
                false
        );
    }

    /**
     * Retrieves the profile photo of the user based on the provided user UID and token validation response.
     *
     * @param validationResponse The token validation response containing the user's UID.
     * @param userUid            The unique identifier (UID) of the user.
     * @return A FileDataResponse containing the profile photo of the user.
     */
    @Override
    public FileDataResponse getProfilePhotoByUid(
            TokenValidationResponse validationResponse,
            String userUid
    ) {
        return getDocumentById(
                validationResponse,
                userUid,
                DocumentType.DOCUMENT_PROFILE,
                false
        );
    }

    /**
     * Retrieves an employee document based on the provided user UID, document type, and token validation response.
     *
     * @param validationResponse The token validation response containing the user's UID.
     * @param documentType       The type of document to retrieve.
     * @param userUid            The unique identifier (UID) of the user.
     * @return A FileDataResponse containing the requested employee document.
     */
    @Override
    public FileDataResponse getEmployeeDocumentByUid(
            TokenValidationResponse validationResponse,
            String documentType,
            String userUid
    ) {
        return getDocumentById(
                validationResponse,
                userUid,
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
                ),
                false
        );
    }

    /**
     * Retrieves an employee request document based on the provided request UID, document type, and token validation response.
     *
     * @param validationResponse The token validation response containing the user's UID.
     * @param documentType       The type of document to retrieve.
     * @param requestUid         The unique identifier (UID) of the request.
     * @return A FileDataResponse containing the requested employee request document.
     */
    @Override
    public FileDataResponse getEmployeeRequestDocumentByUid(
            TokenValidationResponse validationResponse,
            String documentType,
            String requestUid
    ) {
        return getDocumentById(
                validationResponse,
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
                ),
                true
        );
    }

    /**
     * Retrieves all employees' leaves.
     *
     * @param validationResponse The token validation response containing the process ID (PID).
     * @return An APIResponse containing all employees' leaves.
     */
    @Override
    public APIResponse<?> getAllEmployeesLeaves(TokenValidationResponse validationResponse) {
        return APIResponse.builder()
                .data(getAllLeaves(null, true, false))
                .build();
    }

    /**
     * Retrieves an employee's leave based on the provided token validation response.
     *
     * @param validationResponse The token validation response containing the process ID (PID).
     * @return An APIResponse containing the leave details of the employee.
     */
    @Override
    public APIResponse<?> getEmployeeLeave(TokenValidationResponse validationResponse) {
        return APIResponse.builder()
                .data(getAllLeaves(validationResponse.getPid(), false, true))
                .build();
    }



    /**
     * Retrieves public holidays for the specified year.
     *
     * @param yearInString The year for which public holidays are to be retrieved.
     * @return An APIResponse containing public holidays for the specified year.
     * @throws InvalidDataException   If the provided year is invalid or null.
     * @throws NoContentException If no holidays are found for the specified year.
     */
    @Override
    public APIResponse<?> getPublicHolidaysByYear(
            TokenValidationResponse validationResponse,
            String yearInString
    ) {

        UserModel loggedInUser = getCurrentRequestUserModel(validationResponse);

        Year yearObject = Optional.ofNullable(yearInString)
                .map(Year::parse)
                .orElseThrow(() -> new InvalidDataException("Looks like there's an issue with the year—it's either wrong or hasn't been entered (null) yet."));

        List<SessionEventResponse> responses = holidayRepository
                .findByYear(yearObject.getValue())
                .stream()
                .map(holidayModel ->
                        SessionEventResponse.builder()
                                .start(holidayModel.getHolidayStart())
                                .end(holidayModel.getHolidayEnd())
                                .event(holidayModel.getHolidayName())
                                .build()
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
//
//        if (textHelper.isBlank(responses))
//            throw new NoContentException("Couldn't find any holidays for the year (" + yearObject + ").");

        return APIResponse.builder()
                .data(responses)
                .build();
    }





//    private static LocalTime toUTC(LocalTime localTime) {
//        Instant instant = localTime.atDate(LocalDate.now()).toInstant(ZoneOffset.UTC);
//        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalTime();
//    }


    /**
     * Converts a given local time to UTC (Coordinated Universal Time).
     *
     * @param localTime the local time to convert
     * @return the equivalent time in UTC
     */
    private static Instant toUTC(LocalTime localTime) {
        // Create a ZonedDateTime object with the given local time and the system default time zone
        // Convert the ZonedDateTime to UTC
        return ZonedDateTime
                .of(
                        LocalDateTime.of(LocalDate.now(), localTime),
                        ZoneId.systemDefault()
                )
                .withZoneSameInstant(ZoneOffset.UTC)
                .toInstant();
    }




    /**
     * Retrieves the schedule of an employee based on the provided user ID.
     *
     * @param validationResponse the validation response containing the user's authentication token
     * @param userId             the ID of the employee whose schedule is to be retrieved
     * @return an {@code APIResponse} containing the employee's schedule information
     * @throws InvalidDataException if the provided user ID is blank or if no employee is found with the provided ID
     */
    @Override
    public APIResponse<?> getEmployeeSchedule(
            TokenValidationResponse validationResponse,
            String userId
    ) {

//        UserModel loggedInUser = getCurrentRequestUserModel(validationResponse);

        // Check if the provided user ID is blank
        if(textHelper.isBlank(userId))
            throw new InvalidDataException("Apologies, we couldn't locate Employee ID. Kindly furnish a valid one. Thank you.");

        // Retrieve the user information based on the provided user ID
        UserModel reqUser = userRepository.findByUserUid(userId);

        // Check if no user is found with the provided ID
        if(null == reqUser)
            throw new InvalidDataException("Apologies, we couldn't locate an employee with the provided ID. Please double-check the ID and try again.");

        // Retrieve the employee details of the found user
        EmployeeModel reqEmployee = reqUser.getEmployeeDetails();

        // Initialize a list to store the days of the week
        List<String> daysOfWeek = new ArrayList<>();

        // Define mappings between boolean supplier methods and corresponding day names
        Map<BooleanSupplier, String> dayMappings = new LinkedHashMap<>();
        dayMappings.put(reqEmployee::isMonday, "MONDAY");
        dayMappings.put(reqEmployee::isTuesday, "TUESDAY");
        dayMappings.put(reqEmployee::isWednesday, "WEDNESDAY");
        dayMappings.put(reqEmployee::isThursday, "THURSDAY");
        dayMappings.put(reqEmployee::isFriday, "FRIDAY");
        dayMappings.put(reqEmployee::isSaturday, "SATURDAY");

        // Iterate over the mappings and add days to the list if the corresponding boolean method returns true
        for (Map.Entry<BooleanSupplier, String> entry : dayMappings.entrySet()) {
            if (entry.getKey().getAsBoolean()) {
                daysOfWeek.add(entry.getValue());
            }
        }

        // Convert the employee's inTime and outTime to string representations or null if they are null
        String inTime = Optional
                .ofNullable(reqEmployee.getInTime())
                .map(String::valueOf)
                .orElse(null);

        String outTime = Optional
                .ofNullable(reqEmployee.getOutTime())
                .map(String::valueOf)
                .orElse(null);

        // Build the response object containing the employee's schedule information
        EmployeeScheduleResponse response = EmployeeScheduleResponse
                .builder()
                .req_id(userId)
                .in_time(inTime)
                .out_time(outTime)
                .leaves(reqEmployee.getAnnualLeaves())
                .days(daysOfWeek)
                .build();

        // Build and return an APIResponse containing the response object
        return APIResponse.builder()
                .data(response)
                .build();
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> getToDoList(
            TokenValidationResponse validationResponse
    ) {
        UserModel loggedInUser = getCurrentRequestUserModel(validationResponse);

        List<ToDoModel> toDoModelList = toDoRepository.findByCreatedBy(loggedInUser);

        List<ToDoResponse> responses = toDoModelList.stream()
                .map(toDo -> ToDoResponse.builder()
                        .id(toDo.getToDoUid())
                        .created_at(toDo.getCreatedAt())
                        .description(toDo.getDescription())
                        .status(toDo.getStatus())
                        .build()
                ).collect(Collectors.toList());

        return APIResponse.builder()
                .data(responses)
                .build();
    }


    //    POST
    /**
     * Validates the provided authorization token.
     *
     * @param authorizationHeader The authorization header containing the token.
     * @param validationRequest   The validation request containing authorized views and actions.
     * @return A TokenValidationResponse indicating the validity of the token.
     */
    @Override
    public TokenValidationResponse isTokenValid(
            String authorizationHeader,
            TokenValidationRequest validationRequest
    ) {
        EnumSet<View> authorizedViews = validationRequest.getViews()
                .stream()
                .map(viewString -> textHelper.stringToEnum(View.class, viewString))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(View.class)));

        EnumSet<Action> authorizedActions = validationRequest.getActions()
                .stream()
                .map(actionString -> textHelper.stringToEnum(Action.class, actionString))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Action.class)));

        return isTokenValid(
                authorizationHeader,
                authorizedViews,
                authorizedActions
        );
    }




    /**
     * Validates the provided authorization token against authorized views and actions.
     *
     * @param authorizationHeader The authorization header containing the token.
     * @param authorizedViews     The set of authorized views.
     * @param authorizedActions   The set of authorized actions.
     * @return A TokenValidationResponse indicating the validity of the token.
     * @throws UnauthorizedException      If the token validation fails due to unauthorized access.
     * @throws BadCredentialsException     If the token validation fails due to invalid or non-existing user credentials.
     * @throws AccountDisabledException   If the user's account is disabled.
     */
    @Override
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    public TokenValidationResponse isTokenValid(
            String authorizationHeader,
            EnumSet<View> authorizedViews,
            EnumSet<Action> authorizedActions
    ) {
        // Extract the token identifier from the authorization header
        String tokenId = jwtUtils.getSubject(authorizationHeader);
        // Check if the token is external
        boolean isExternal = tokenId.contains(Identifier.REQ.name());

        // Handle external tokens
        if (isExternal) {
            RequestModel requestModel;
            try {
                // Retrieve the request model by UID
                requestModel = getEmployeeRequestModelByUid(tokenId);
                // Ensure that the token has a tokenAt value
                Objects.requireNonNull(requestModel.getTokenAt());
            } catch (Exception exception) {
                // Throw UnauthorizedException if the request model is not found or tokenAt is null
                throw new UnauthorizedException();
            }

            // Check if the token's UPT matches the tokenAt value
            if (jwtUtils.getPayload(authorizationHeader) != requestModel.getTokenAt().toEpochMilli())
                throw new UnauthorizedException();

            // Return a TokenValidationResponse for external tokens
            return TokenValidationResponse.builder()
                    .pid(requestModel.getRequestPid())
                    .uid(requestModel.getRequestUid())
                    .external(true)
                    .none(true)
                    .build();
        }

        // Handle internal tokens
        UserModel userModel;
        EntitlementModel entitlementModel;
        try {
            // Retrieve the user model and entitlement model by UID
            userModel = userRepository.findByUserUid(tokenId);
            entitlementModel = entitlementRepository.findByUserUid(tokenId);

            // Ensure that the userModel has a tokenAt value
            Objects.requireNonNull(userModel.getTokenAt());
        } catch (Exception exception) {
            // Throw BadCredentialsException if the user model or entitlement model is not found, or tokenAt is null
            throw new BadCredentialsException();
        }

        // Check if an organization is linked to the user's account
        if (Boolean.FALSE.equals(userRepository.existsByOrganizationIsNotNullAndUserPid(userModel.getUserPid())))
            throw new AccountDisabledException("Oops, We couldn't find an organization linked to your account. Please reach out to our support team or administrator to get your organization assignment sorted out.");

        // Check if the user's account is enabled
        if (!Boolean.TRUE.equals(userModel.isEnabled()))
            throw new AccountDisabledException("Oops! Looks like your account has been disabled by the administrator.");

        // Check if the token's UPT matches the tokenAt value
        if (jwtUtils.getPayload(authorizationHeader) != userModel.getTokenAt().toEpochMilli())
            throw new UnauthorizedException("Your account may have been updated. To ensure you have access to the updated information, please log in again.");

        // Retrieve the user's entitlements
        Set<MainTab> entitlements = entitlementModel.getEntitlements();
        // Check if the user has any entitlements
        if (textHelper.isBlank(entitlements))
            throw new UnauthorizedException();

        // Check if the user has the required authorized views and actions
        if (!authorizedViews.isEmpty() && !authorizedActions.isEmpty() &&
                entitlements.stream()
                        .flatMap(mainTabModel -> mainTabModel.getSub().stream())
                        .noneMatch(subTabModel -> authorizedViews.contains(subTabModel.getName()) && subTabModel.getActions().stream().anyMatch(authorizedActions::contains))
        ) throw new UnauthorizedException();

        // Retrieve the user's authorities
        Set<String> authorities = entitlementModel.getAuthorities();
        // Return a TokenValidationResponse containing information about the user associated with the token
        return TokenValidationResponse.builder()
                .pid(userModel.getUserPid())
                .uid(userModel.getUserUid())
                .none(textHelper.isBlank(authorities))
                .adminRole(authorities.contains(Authority.ROLE_ADMIN.name()))
                .salesRole(authorities.contains(Authority.ROLE_SALES.name()))
                .facultyRole(authorities.contains(Authority.ROLE_FACULTY.name()))
                .build();
    }


    /**
     * Signs in a user with the provided login credentials and generates a JWT token.
     *
     * @param loginRequest The login request containing user credentials.
     * @return A JwtTokenResponse containing the generated JWT token.
     * @throws CaptchaFailedException   If the HCaptcha validation fails.
     * @throws BadCredentialsException   If the provided credentials are invalid.
     * @throws AccountDisabledException  If the user's account is inactive or disabled.
     */
    @Override
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    public JwtTokenResponse signInUser(
            LoginRequest loginRequest
    ) {
        // Sanitize input parameters
        String eid = sanitizer.sanitize(loginRequest.getEid());
        String password = sanitizer.sanitize(loginRequest.getPassword());
        String hCaptchaResponse = sanitizer.sanitize(loginRequest.getHcaptcha_response());

        if (envProfile.isProductionProfile() && (StringUtils.isBlank(hCaptchaResponse) || !hCaptchaUtils.validateHCaptchaOnWeb(hCaptchaResponse))) {
            throw new CaptchaFailedException();
        }

        // Retrieve user model by email or user UID
        UserModel userModel = userRepository.findByEmailOrUserUid(eid, eid);
        // Check if the user exists and the password matches
        if (null == userModel || !passwordEncoder.matches(password, userModel.getPassword()))
            throw new BadCredentialsException();

        // Check if the user's joined date is in the future
        if (instantUtils.isFutureDate(userModel.getJoinedAt(), Instant.now()))
            throw new AccountDisabledException("Inactive account. Contact administrator.");

        // Check if the user's account is enabled
        if (!Boolean.TRUE.equals(userModel.isEnabled()))
            throw new AccountDisabledException("Account disabled by the administrator.");

        // Generate JWT token for the user
        return JwtTokenResponse.builder()
                .token(
                        jwtUtils.issueToken(
                                userModel.getUserUid(),
                                userModel.getTokenAt().toEpochMilli()
                        )
                )
                .build();
    }



    /**
     * Refreshes the JWT token for the user identified by the provided validation response.
     *
     * @param validationResponse    The validation response containing user information.
     * @param signOutFromOtherDevices    Indicates whether to sign out the user from other devices.
     * @return  A JwtTokenResponse containing the refreshed JWT token.
     * @throws UnauthorizedException   If the user is not authorized or not found.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public JwtTokenResponse refreshToken(TokenValidationResponse validationResponse, boolean signOutFromOtherDevices) {
        UserModel userModel;
        try {
            // Retrieve the UserModel based on the user ID extracted from the JWT token.
            userModel = getUserModelByUid(validationResponse.getUid());
        } catch (ResourceNotFoundException exception) {
            throw new UnauthorizedException(exception.getMessage());
        }

        if (signOutFromOtherDevices) {
            // If signOutFromOtherDevices is requested, update the user's updatedAt timestamp.
            userModel.setTokenAt(Instant.now());
            userRepository.save(userModel);
        }

        // Ensure userModel changes are committed to the database before generating the token.
        userRepository.flush();

        // Generate a new JWT token for the user and return it in a JwtTokenResponse.
        return JwtTokenResponse.builder()
                .token(
                        jwtUtils.issueToken(
                                userModel.getUserUid(),
                                userModel.getTokenAt().toEpochMilli()
                        )
                )
                .build();
    }




    /**
     * Creates a new employee request based on the provided employee creation request.
     *
     * @param validationResponse       The validation response containing user information.
     * @param employeeCreationRequest  The employee creation request.
     * @return  An APIResponse indicating the success of the operation.
     * @throws UnauthorizedException           If the user is not authorized or not found.
     * @throws DataAlreadyExistException       If the email or phone number already exists in the system.
     * @throws InvalidDataException            If the provided data is invalid.
     * @throws CaptchaFailedException         If the HCaptcha validation fails.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> createEmployeeRequest(
            TokenValidationResponse validationResponse,
            MEmployeeCreationRequest employeeCreationRequest
    ) {
        // Extract the PID from the validation response
        Long tokenPid = validationResponse.getPid();
        UserModel createdByModel;
        OrganizationModel organizationModel;
        try {
            // Retrieve the user model by PID and ensure that it belongs to a valid organization
            createdByModel = getUserModelByPid(tokenPid);
            if (null == createdByModel || null == createdByModel.getOrganization())
                throw new UnauthorizedException();

            organizationModel = createdByModel.getOrganization();
        } catch (Exception exception) {
            // Throw UnauthorizedException if the user model is not found or does not belong to a valid organization
            throw new UnauthorizedException();
        }


        // Sanitize the employee creation request
        MEmployeeCreationRequest request = sanitizer.sanitize(employeeCreationRequest);
        // Validate the email on the web
        if (envProfile.isProductionProfile()) {
            emailUtils.validateEmailOnWeb(request.getEmail());
        }

        if (LocalDateTime.now(ZoneOffset.UTC).minusYears(5).atZone(ZoneId.of("UTC")).toInstant().isAfter(request.getJoined_at())) {
            throw new InvalidDataException("An employee's joining date cannot be more than 5 years in the past.");
        }

        // Check if an employee with the provided email already exists
        if (userRepository.existsByEmail(request.getEmail()))
            throw new DataAlreadyExistException("The email is already associated with an existing employee; further processing is not possible.");

        // Check if there are existing requests with the same email or phone number
        List<RequestModel> existingRequestsWithEmail = requestRepository.findByEmail(request.getEmail());
        for (RequestModel requestModel : existingRequestsWithEmail) {
            if (!EmployeeRequestStatus.DECLINED.equals(requestModel.getRequestStatus()))
                throw new DataAlreadyExistException("Email already exists cannot proceed further.");
        }
        List<RequestModel> existingRequestsWithPhone = requestRepository.findByPhone(request.getPhone());
        for (RequestModel requestModel : existingRequestsWithPhone) {
            if (!EmployeeRequestStatus.DECLINED.equals(requestModel.getRequestStatus()))
                throw new DataAlreadyExistException("Phone number already exists cannot proceed further.");
        }

        // Convert authority strings to enums and validate them
        HashSet<String> authoritiesSet = new HashSet<>();
        for (String authorityString : request.getAuthorities()) {
            Authority authority = textHelper.stringToEnum(Authority.class, authorityString);
            if (null == authority)
                throw new InvalidDataException("No such authority can be found. (" + authorityString + ")");

            authoritiesSet.add(authority.name());
        }

        // Generate a unique request UID
        String generatedRequestUid = generateUniqueUid(
                () -> uidGenerator.generateRequestId(Instant.now()),
                requestRepository::countByRequestUid,
                5
        );
        // Build the request model
        RequestModel requestModel = RequestModel.builder()
                .requestUid(generatedRequestUid)
                .tokenAt(Instant.now())
                .joinedAt(request.getJoined_at())
                .organization(organizationModel)
                .requestStatus(EmployeeRequestStatus.PENDING)
                .firstname(
                        StringUtils.capitalize(request.getFirstname())
                )
                .lastname(
                        StringUtils.capitalize(request.getLastname())
                )
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        // Set the createdBy and lastModifiedBy fields of the request model
        requestModel.setCreatedBy(createdByModel);
        requestModel.setLastModifiedBy(createdByModel);
        // Save the request model
        RequestModel addedRequestModel = requestRepository.save(requestModel);

        // Build the entitlement model
        EntitlementModel entitlementModel = EntitlementModel.builder()
                .requestUid(addedRequestModel.getRequestUid())
                .authorities(authoritiesSet)
                .entitlements(request.getEntitlements())
                .build();
        // Save the entitlement model
        entitlementRepository.save(entitlementModel);

        // Construct the recipient details for the email
        String recipientName = textHelper.buildFullName(
                addedRequestModel.getFirstname(),
                addedRequestModel.getLastname()
        );
        String recipientEmail = addedRequestModel.getEmail();
        String recipientLink = defaultExternalEmployeeFormLink + jwtUtils.issueToken(
                addedRequestModel.getRequestUid(),
                addedRequestModel.getTokenAt().toEpochMilli(),
                JwtWizard.EMPLOYEE_REGISTRATION_EXPIRY_DURATION
        );

        // Send the employee creation mail
        emailUtils.sendEmployeeCreationMail(
                recipientName,
                recipientEmail,
                recipientLink
        );

        // Return an API response indicating the success of the operation
        return APIResponse.builder()
                .message("Email sent to " + recipientEmail +" successfully.")
                .build();
    }





    /**
     * Creates a new leave request based on the provided leave request details.
     *
     * @param validationResponse   The validation response containing user information.
     * @param applyLeaveRequest    The leave request details.
     * @return  An APIResponse indicating the success of the operation.
     * @throws InvalidDataException    If the provided data is invalid.
     */
    @Override
//    @Async
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> createLeaveRequest(
            TokenValidationResponse validationResponse,
            ApplyLeaveRequest applyLeaveRequest
    ) {
        // Sanitize the apply leave request
        ApplyLeaveRequest request = sanitizer.sanitize(applyLeaveRequest);

        // Validate the leave request data
        if (null == request.getStart())
            throw new InvalidDataException("Start date cannot be left blank.");
        if (null == request.getEnd())
            throw new InvalidDataException("End date cannot be left blank.");
        if (null == request.getType())
            throw new InvalidDataException("Leave type cannot be left blank.");
        if (textHelper.isBlank(request.getReason()))
            throw new InvalidDataException("Leave reason cannot be left blank.");

        if(request.getReason().length() > 500)
            throw  new InvalidDataException("Leave reason cannot exceed 500 characters.");

        // Convert the leave type string to an enum and validate it
        LeaveType leaveType = textHelper.stringToEnum(LeaveType.class, request.getType());
        if (null == leaveType || LeaveType.WEEKOFF.equals(leaveType))
            throw new InvalidDataException("Leave type cannot have value as '" + request.getType() + "'.");

        // Validate the start and end dates of the leave request
        Instant startInstant = request.getStart();
        Instant endInstant = request.getEnd();

        if(startInstant.equals(endInstant))
            throw new InvalidDataException("Leave start and end times must differ.");

//        Instant startOfPreviousMonth = ZonedDateTime.now()
//                .minusMonths(1)
//                .with(TemporalAdjusters.firstDayOfMonth())
//                .withZoneSameInstant(ZoneId.of("UTC"))
//                .toInstant();

        Instant startOfPrevious6Month = ZonedDateTime.now()
                .minusMonths(6)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant();

        if (startInstant.isBefore(startOfPrevious6Month))
            throw new InvalidDataException("Hey, the start date can't be earlier than 6 months (" + startOfPrevious6Month.atZone(ZoneId.of("UTC")).toLocalDate() + " - UTC). It needs to be in the range of the last month.");
        if (startInstant.isAfter(endInstant))
            throw new InvalidDataException("The start date can't come after the end date.");

        // Generate a unique leave request UID
        String generatedLeaveRequestUid = generateUniqueUid(
                () -> uidGenerator.generateLeaveId(Instant.now()),
                leaveRequestRepository::countByLeaveRequestUid,
                5
        );
        // Retrieve the user model who applied for the leave
        UserModel appliedByUserModel = getUserModelByPid(validationResponse.getPid());
        // Build the leave request model
        LeaveRequestModel leaveRequestModel = LeaveRequestModel.builder()
                .leaveRequestUid(generatedLeaveRequestUid)
                .startDate(startInstant)
                .endDate(endInstant)
                .reason(request.getReason())
                .appliedBy(appliedByUserModel)
                .status(LeaveStatus.PENDING)
                .type(leaveType)
                .build();

        // Save the leave request model
        leaveRequestRepository.save(leaveRequestModel);
        // Construct the full name of the user who applied for leave
        String fullName = textHelper.buildFullName(
                appliedByUserModel.getFirstname(),
                appliedByUserModel.getMiddlename(),
                appliedByUserModel.getLastname()
        );

        // Log a notification for users with entitlements to view leave requests
        notificationLogger(entitlementRepository
                        .findByUserUidIsNotNullAndEntitlementsSubName(View.LEAVE_REQUESTS)
                        .stream()
                        .map(EntitlementModel::getUserUid)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()),
                generatedLeaveRequestUid,
                View.LEAVE_REQUESTS,
                fullName + " has applied for a leave request. Kindly review it."
        );
//        CompletableFuture
//                .runAsync(() -> {
//                    notificationLogger(entitlementRepository
//                                    .findByUserUidIsNotNullAndEntitlementsSubName(View.LEAVE_REQUESTS)
//                                    .stream()
//                                    .map(EntitlementModel::getUserUid)
//                                    .filter(Objects::nonNull)
//                                    .distinct()
//                                    .collect(Collectors.toList()),
//                            generatedLeaveRequestUid,
//                            View.LEAVE_REQUESTS,
//                            fullName + " has applied for a leave request. Kindly review it."
//                    );
//                    LoggerFactory.getLogger(EmployeeServiceImpl.class).info("Notification Sent By: " + Thread.currentThread().getName());
//                })
//                .join();

        // Return an API response indicating the success of the operation
        return APIResponse.builder()
                .message("Your leave request has been received and is under review.")
                .build();
    }







    /**
     * Retrieves the rectification link for an employee request based on the provided validation response and request details.
     *
     * @param validationResponse   The validation response containing user information.
     * @param request              The rectification link request details.
     * @return  An APIResponse containing the rectification link.
     */
    @Override
    public APIResponse<?> getRectificationEmployeeRequestLink(
            TokenValidationResponse validationResponse,
            RectificationLinkRequest request
    ) {
        return getEmployeeRequestLinkOrToken(
                validationResponse,
                request,
                false
        );
    }

    /**
     * Retrieves the rectification token for an employee request based on the provided validation response and request details.
     *
     * @param validationResponse   The validation response containing user information.
     * @param request              The rectification link request details.
     * @return  An APIResponse containing the rectification token.
     */
    @Override
    public APIResponse<?> getRectificationEmployeeRequestToken(
            TokenValidationResponse validationResponse,
            RectificationLinkRequest request
    ) {
        return getEmployeeRequestLinkOrToken(
                validationResponse,
                request,
                true
        );
    }








//  IMPL
    /**
     * Retrieves the details of a user based on the provided user UID.
     *
     * @param userUid The unique identifier of the user.
     * @return  A UserDetailsResponse containing the details of the user.
     * @throws ResourceNotFoundException If the user details are not found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private UserDetailsResponse getUserDetails(
            String userUid
    ) {
        UserModel userModel = getUserModelByUid(userUid);
        EntitlementModel entitlementModel = getEntitlementModelByUserUid(userUid);
        UserDetailsResponse response = modelMapper.mapToUserDetailsResponse(
                userModel,
                entitlementModel,
                true
        );
        if (null == response)
            throw new ResourceNotFoundException("No such user can be found.");

        return response;
    }


    /**
     * Retrieves a list of mini employee responses based on the provided authorities.
     *
     * @param authorities The authorities to filter by.
     * @return  A list of TEmployeeMiniResponse objects representing the employees.
     * @throws NoContentException If no users are found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    public List<TEmployeeMiniResponse> getUsersByAuthorities(
            Set<String> authorities
    ) {
        List<EntitlementModel> entitlementModelList = entitlementRepository.findByAuthoritiesIn(authorities);
        List<TEmployeeMiniResponse> responses = entitlementModelList
                .stream()
                .map(entitlementModel -> {
                    if (null == entitlementModel.getUserUid())
                        return null;

                    UserModel userModel = getUserModelByUid(entitlementModel.getUserUid());
                    return TEmployeeMiniResponse.builder()
                            .emp_id(userModel.getUserPid())
                            .uid(userModel.getUserUid())
                            .name(
                                    textHelper.buildFullName(
                                            userModel.getFirstname(),
                                            userModel.getMiddlename(),
                                            userModel.getLastname()
                                    )
                            )
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (responses.isEmpty())
            throw new NoContentException("there are no users.");

        return responses;
    }


    /**
     * Retrieves a list of mini employee responses based on the provided skill.
     *
     * @param skill The skill to filter by.
     * @return  A list of TEmployeeMiniResponse objects representing the employees.
     * @throws NoContentException If no users are found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    public List<TEmployeeMiniResponse> getUsersBySkill(
            String skill
    ) {
        if (textHelper.isBlank(skill))
            return List.of();

        List<UserSkillMapping> mappings = userSkillMappingRepository.findByCourseCourseName(skill);
        List<TEmployeeMiniResponse> responses = mappings
                .stream()
                .map(mapping -> {
                    UserModel model = mapping.getUser();
                    return TEmployeeMiniResponse.builder()
                            .emp_id(model.getUserPid())
                            .uid(model.getUserUid())
                            .name(
                                    textHelper.buildFullName(
                                            model.getFirstname(),
                                            model.getMiddlename(),
                                            model.getLastname()
                                    )
                            )
                            .status(mapping.getStatus())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (responses.isEmpty())
            throw new NoContentException("there are no users.");

        return responses;
    }


    /**
     * Retrieves a list of all employees.
     *
     * @return A list of TEmployeeResponse objects representing the employees.
     * @throws NoContentException If no employees are found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private List<TEmployeeResponse> getAllTEmployees() {
        List<TEmployeeResponse> responses = modelMapper.getListOfResponses(
                userRepository.findAll(),
                modelMapper::mapToTEmployeeResponse
        );
        if (null == responses || responses.isEmpty())
            throw new NoContentException("there are no employees.");

        return responses;
    }


    /**
     * Retrieves a list of all employee requests.
     *
     * @return A list of TEmployeeRequestResponse objects representing the employee requests.
     * @throws NoContentException If no requests are found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private List<TEmployeeRequestResponse> getAllTEmployeeRequests() {
        List<TEmployeeRequestResponse> responses = modelMapper.getListOfResponses(
                requestRepository.findAll(),
                modelMapper::mapToTEmployeeRequestResponse
        );
        if (null == responses || responses.isEmpty())
            throw new NoContentException("there are no requests.");

        return responses;
    }


    /**
     * Retrieves a list of notifications for a user.
     *
     * @param userPid The PID of the user.
     * @param isLimit Flag indicating whether to limit the number of notifications.
     * @return A list of TNotificationResponse objects representing the notifications.
     * @throws NoContentException If no notifications are found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private List<TNotificationResponse> getAllNotifications(
            Long userPid,
            boolean isLimit
    ) {
        List<TNotificationResponse> responses = modelMapper.getListOfResponses(
                isLimit ? notificationUserMappingRepository.findTop5ByUserUserPid(userPid) : notificationUserMappingRepository.findByUserUserPid(userPid),
                modelMapper::mapToTNotificationResponse
        );
        if (null == responses || responses.isEmpty())
            throw new NoContentException("there are no notifications.");

        return responses;
    }


    /**
     * Retrieves a list of activities for a user.
     *
     * @param userUid The UID of the user.
     * @return A list of TEmployeeActivityResponse objects representing the activities.
     * @throws NoContentException If no activities are found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private List<TEmployeeActivityResponse> getEmployeeActivities(
            String userUid
    ) {
        List<TEmployeeActivityResponse> responses = modelMapper.getListOfResponses(
                null == userUid ? activityRepository.findAll() : activityRepository.findByUserUserUid(userUid),
                modelMapper::mapToTEmployeeActivityResponse
        );
        if (null == responses || responses.isEmpty())
            throw new NoContentException("there's no activity.");

        return responses;
    }


    /**
     * Retrieves details of an employee by user UID.
     *
     * @param userUid The UID of the user.
     * @return An EmployeeResponse object representing the employee details.
     * @throws ResourceNotFoundException If the employee details are not found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private EmployeeResponse getEmployeeDetails(
            String userUid
    ) {
        UserModel userModel = getUserModelByUid(userUid);
        EmployeeResponse response = modelMapper.mapToEmployeeResponse(
                userModel
        );
        if (null == response)
            throw new ResourceNotFoundException("No employee can be found.");

        return response;
    }


    /**
     * Retrieves details of an employee request.
     *
     * @param uid       The UID of the request or user.
     * @param isExternal Flag indicating whether the request is external.
     * @return An EEmployeeRequestResponse object representing the employee request details.
     * @throws ResourceNotFoundException If the employee request details are not found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private EEmployeeRequestResponse getEmployeeRequestDetails(
            String uid,
            boolean isExternal,
            Long upt
    ) {
        EEmployeeRequestResponse response;
        EntitlementModel entitlementModel;
        if (isExternal) {
            entitlementModel = getEntitlementModelByRequestUid(uid);
            RequestModel requestModel = getEmployeeRequestModelByUid(uid);
            if (upt != null && requestModel.getTokenAt().toEpochMilli() != upt) {
                throw new UnauthorizedException();
            }

            response = modelMapper.mapToEEmployeeRequestResponse(requestModel, entitlementModel);
        } else {
            entitlementModel = getEntitlementModelByUserUid(uid);
            UserModel userModel = getUserModelByUid(uid);
            response = modelMapper.mapToEEmployeeRequestResponse(userModel, entitlementModel);
        }

        if (null == response)
            throw new ResourceNotFoundException("No employee can be found.");

        return response;
    }


    /**
     * Retrieves document data by document ID.
     *
     * @param validationResponse The token validation response.
     * @param uid                The UID of the document.
     * @param documentType       The type of document.
     * @param isExternal         Flag indicating whether the document is external.
     * @return A FileDataResponse object containing document data.
     * @throws UnauthorizedException     If the user is not authorized to access the document.
     * @throws ResourceNotFoundException If the document or document type is not found.
     * @throws InvalidDataException      If the document type is invalid.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private FileDataResponse getDocumentById(
            TokenValidationResponse validationResponse,
            String uid,
            DocumentType documentType,
            boolean isExternal
    ) {
        // Check authorization
        if (null != validationResponse && !validationResponse.isAdminRole() && !validationResponse.getUid().equals(uid))
            throw new UnauthorizedException();

        UserModel userModel = null;
        RequestModel requestModel = null;
        String exceptionString = "No such document can be found.";
        if (null == documentType)
            throw new ResourceNotFoundException(exceptionString);

        // Retrieve document model based on external flag
        if (isExternal)
            requestModel = getEmployeeRequestModelByUid(uid);
        else {
            userModel = getUserModelByUid(uid);
            if (null == userModel || null == userModel.getEmployeeDetails())
                throw new ResourceNotFoundException(exceptionString);
        }

        FileDataModel fileDataModel = null;
        switch (documentType) {
            case DOCUMENT_PROFILE:
                fileDataModel = isExternal ? null : userModel.getProfile();
                break;
            case DOCUMENT_IDENTITY_PROOF:
                fileDataModel = isExternal ? requestModel.getIdentityDocument() : userModel.getEmployeeDetails().getIdentityDocument();
                break;
            case DOCUMENT_ADDRESS_PROOF:
                fileDataModel = isExternal ? requestModel.getAddressDocument() : userModel.getEmployeeDetails().getAddressDocument();
                break;
            case DOCUMENT_OFFER_LETTER:
                fileDataModel = isExternal ? requestModel.getOfferLetterDocument() : userModel.getEmployeeDetails().getOfferLetterDocument();
                break;
            case DOCUMENT_EDUCATION:
                fileDataModel = isExternal ? requestModel.getEducationDocument() : userModel.getEmployeeDetails().getEducationDocument();
                break;
            case DOCUMENT_EXPERIENCE_LETTER:
                fileDataModel = isExternal ? requestModel.getExperienceDocument() : userModel.getEmployeeDetails().getExperienceDocument();
                break;
            case DOCUMENT_SALARY_SLIP:
                fileDataModel = isExternal ? requestModel.getSalarySlipDocument() : userModel.getEmployeeDetails().getSalarySlipDocument();
                break;
        }
        if (null == fileDataModel)
            throw new ResourceNotFoundException(exceptionString);

        String filePath = fileDataModel.getFilePath();
        String fileType = fileDataModel.getFileType();
        switch (fileType) {
            case MediaType.APPLICATION_PDF_VALUE:
            case MediaType.IMAGE_JPEG_VALUE:
            case MediaType.IMAGE_PNG_VALUE:
                break;
            case "image/jpg":
                fileType = MediaType.IMAGE_JPEG_VALUE;
                break;
            default:
                throw new IllegalArgumentException();
        }

        // Read document file and return response
        try {
            File documentFileObj = new File(filePath);
            return FileDataResponse.builder()
                    .contentType(fileType)
                    .data(Files.readAllBytes(documentFileObj.toPath()))
                    .build();
        } catch (Exception e) {
            throw new ResourceNotFoundException(exceptionString);
        }
    }


    /**
     * Retrieves a list of leave responses based on the provided parameters.
     *
     * @param userPid            The PID of the user for whom leaves are retrieved.
     * @param isAll              Flag indicating whether to retrieve all leaves or leaves for a specific user.
     * @param isWithdrewIncluded Flag indicating whether withdrawn leaves should be included.
     * @return A list of LeaveResponse objects.
     * @throws NoContentException If no leaves are found.
     */
    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    private List<LeaveResponse> getAllLeaves(
            Long userPid,
            boolean isAll,
//            String userUid,
            boolean isWithdrewIncluded
    ) {
        List<LeaveResponse> responses = modelMapper.getListOfResponses(
                isAll ? leaveRequestRepository.findAllByAppliedByIsNotNull() : leaveRequestRepository.findAllByAppliedBy(userPid),
                (leaveRequestModel) -> modelMapper.mapToLeaveResponse(leaveRequestModel, isWithdrewIncluded)
        );
        if (null == responses || responses.isEmpty())
            throw new NoContentException("there are no leaves.");

        return responses;
    }






//  POST
    /**
     * Uploads documents for a user or an employee request.
     *
     * @param validationResponse   The validation response containing user information and role.
     * @param uid                  The unique identifier of the user or employee request.
     * @param isExternal           Flag indicating whether the document upload is for an external employee request.
     * @param profileDocument      The profile document to upload.
     * @param identityDocument     The identity document to upload.
     * @param addressDocument      The address document to upload.
     * @param offerLetterDocument  The offer letter document to upload.
     * @param educationDocument    The education document to upload.
     * @param experienceDocument   The experience document to upload.
     * @param salarySlipDocument   The salary slip document to upload.
     * @return APIResponse containing a success message.
     * @throws UnauthorizedException   If the user does not have permission to upload documents.
     * @throws InvalidDataException    If no document is found for upload.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> uploadDocumentsById(
            TokenValidationResponse validationResponse,
            String uid,
            boolean isExternal,
            MultipartFile profileDocument,
            MultipartFile identityDocument,
            MultipartFile addressDocument,
            MultipartFile offerLetterDocument,
            MultipartFile educationDocument,
            MultipartFile experienceDocument,
            MultipartFile salarySlipDocument
    ) {
        // Check if the user is authorized to upload documents
        if (null != validationResponse && !validationResponse.isAdminRole() && !validationResponse.getUid().equals(uid))
            throw new UnauthorizedException();

        UserModel userModel = null;
        EmployeeModel employeeModel = null;
        RequestModel requestModel = null;
        // Fetch user or employee request based on the provided UID
        if (isExternal) {
            requestModel = getEmployeeRequestModelByUid(uid);
            // Check if the employee request is already approved, in which case document upload is not allowed
            if (EmployeeRequestStatus.APPROVED.equals(requestModel.getRequestStatus()))
                throw new InvalidDataException("Once we approve an employee request, we can't make any changes.");
        } else {
            userModel = getUserModelByUid(uid);
            // Check if user or employee details exist for the provided UID
            if (null == userModel || null == userModel.getEmployeeDetails())
                throw new UnauthorizedException();
            employeeModel = userModel.getEmployeeDetails();
        }


        // Update profile document
        updateDocumentByFileDataModel(
                profileDocument,
                profileDirectory,
                isExternal ? null : userModel::getProfile,
                isExternal ? null : userModel::setProfile,
                true
        );
        // Update other documents based on document type
        updateDocumentByFileDataModel(
                identityDocument,
                documentDirectory,
                isExternal ? requestModel::getIdentityDocument : employeeModel::getIdentityDocument,
                isExternal ? requestModel::setIdentityDocument : employeeModel::setIdentityDocument,
                false
        );
        updateDocumentByFileDataModel(
                addressDocument,
                documentDirectory,
                isExternal ? requestModel::getAddressDocument : employeeModel::getAddressDocument,
                isExternal ? requestModel::setAddressDocument : employeeModel::setAddressDocument,
                false
        );
        updateDocumentByFileDataModel(
                offerLetterDocument,
                documentDirectory,
                isExternal ? requestModel::getOfferLetterDocument : employeeModel::getOfferLetterDocument,
                isExternal ? requestModel::setOfferLetterDocument : employeeModel::setOfferLetterDocument,
                false
        );
        updateDocumentByFileDataModel(
                educationDocument,
                documentDirectory,
                isExternal ? requestModel::getEducationDocument : employeeModel::getEducationDocument,
                isExternal ? requestModel::setEducationDocument : employeeModel::setEducationDocument,
                false
        );
        updateDocumentByFileDataModel(
                experienceDocument,
                documentDirectory,
                isExternal ? requestModel::getExperienceDocument : employeeModel::getExperienceDocument,
                isExternal ? requestModel::setExperienceDocument : employeeModel::setExperienceDocument,
                false
        );
        updateDocumentByFileDataModel(
                salarySlipDocument,
                documentDirectory,
                isExternal ? requestModel::getSalarySlipDocument : employeeModel::getSalarySlipDocument,
                isExternal ? requestModel::setSalarySlipDocument : employeeModel::setSalarySlipDocument,
                false
        );
        // Check if there are any documents to be saved
        boolean isProfileToBeSaved = hasDocuments(profileDocument);
        boolean isDocumentsToBeSaved = hasDocuments(
                identityDocument,
                addressDocument,
                offerLetterDocument,
                educationDocument,
                experienceDocument,
                salarySlipDocument
        );

        // If no document is found for upload, throw an exception
        if (!isProfileToBeSaved && !isDocumentsToBeSaved)
            throw new InvalidDataException("No document found.");

        // Save documents to the database based on whether it's an external employee request or a user
        if (isExternal && isDocumentsToBeSaved)
            requestRepository.save(requestModel);

        if (!isExternal) {
            if (isDocumentsToBeSaved)
                employeeRepository.save(employeeModel);
            if (isProfileToBeSaved)
                userRepository.save(userModel);
        }

        // Return success message
        return APIResponse.builder()
                .message("Documents uploaded successfully.")
                .build();
    }







    /**
     * Logs user activities such as viewing or performing actions on specific resources.
     *
     * @param validationResponse The validation response containing user information.
     * @param view               The view or resource being accessed.
     * @param action             The action performed on the resource.
     * @param referenceIds       The set of reference IDs related to the activity.
     * @throws InvalidDataException If required parameters are missing or an error occurs during logging.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public void activityLogger(
            TokenValidationResponse validationResponse,
            View view,
            Action action,
            Set<String> referenceIds
    ) {
        // Fetch the user model based on the provided PID from the validation response
        UserModel userModel = getUserModelByPid(validationResponse.getPid());

        // Check if the set of reference IDs is blank or empty
        if (textHelper.isBlank(referenceIds))
            throw new InvalidDataException("Cannot proceed without references.");

        /*
        // Fetch the action model based on the provided action
        ActionModel actionModel = actionRepository.findByAction(action);
        if (null == actionModel)
            throw new InvalidDataException("Cannot proceed without action.");

        // Fetch the view model based on the provided view
        ViewModel viewModel = viewRepository.findByView(view);
        if (null == viewModel)
            throw new InvalidDataException("Cannot proceed without view.");
         */

        ;

        // Build the activity model
        ActivityModel activityModel = ActivityModel.builder()
                .user(userModel)
                .action(action.name())
                .view(view.name())
                .referenceId(jsonConverter.getJsonStringFromList(
                        referenceIds.stream().filter(Objects::nonNull).toList()
                ))
                .build();

        // Save the activity model to the database
        try {
            activityRepository.save(activityModel);
        } catch (Exception e) {
            throw new InvalidDataException(e.getMessage() + " Cannot proceed.");
        }
    }







    /**
     * Logs notifications for specific users related to a particular reference and view.
     *
     * @param notifyToUserModelIds The list of user model IDs to whom the notification should be sent.
     * @param referenceId           The reference ID related to the notification.
     * @param view                  The view or resource associated with the notification.
     * @param message               The notification message.
     * @throws InvalidDataException If required parameters are missing or an error occurs during logging.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public void notificationLogger(
            List<String> notifyToUserModelIds,
            String referenceId,
            View view,
            String message
    ) {
        // Check if the reference ID is blank or empty
        if (textHelper.isBlank(referenceId))
            throw new InvalidDataException("Cannot proceed without reference.");

        // Check if the notification message is blank or empty
        if (textHelper.isBlank(message))
            throw new InvalidDataException("Further processing is not possible without specifying the notification message.");

        /*
        // Fetch the view model based on the provided view
        ViewModel viewModel = Optional
                .ofNullable(view)
                .map(viewRepository::findByView)
                .orElseThrow(() -> new InvalidDataException("Further processing is not possible without specifying the view of notification."));
         */

        // Filter and collect distinct user IDs from the provided list
        List<String> userIds = Optional
                .ofNullable(notifyToUserModelIds)
                .orElse(new ArrayList<>())
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Check if the list of user IDs is empty
        if (userIds.isEmpty())
            throw new InvalidDataException("Further processing is not possible without specifying the user to whom the notification needs to be sent.");

        // Build the notification model
        NotificationModel notification = NotificationModel
                .builder()
                .referenceId(referenceId)
                .message(message)
                .view(view.name())
                .build();

        // Create notification-user mapping entries for each user
        List<NotificationUserMapping> mapping = Optional
                .ofNullable(userRepository.findAllByUserUidIn(userIds))
                .map(users -> {
                    if (users.isEmpty())
                        throw new InvalidDataException("Further processing is not possible without specifying the user to whom the notification needs to be sent.");

                    return users;
                })
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(user -> NotificationUserMapping
                        .builder()
                        .user(user)
                        .notification(notification)
                        .build()
                )
                .collect(Collectors.toList());


//        try {
        // Save notification-user mapping entries
        notificationUserMappingRepository.saveAll(mapping);
//        } catch (Exception e) {
//            throw new InvalidDataException("Uh-oh! Something went wrong: " + e.getMessage() + ". Can't move forward.");
//        }
    }


    /**
     * Retrieves the user model associated with the current request.
     *
     * @param validationResponse The token validation response containing user information.
     * @return The UserModel associated with the current request.
     * @throws UnauthorizedException If the validation response or user ID is null, indicating unauthorized access.
     */
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.MANDATORY
    )
    private UserModel getCurrentRequestUserModel(TokenValidationResponse validationResponse) {
        // Check if the validation response or user ID is null, indicating unauthorized access
        if (null == validationResponse || null == validationResponse.getPid())
            throw new UnauthorizedException();

        // Retrieve the user model based on the provided user ID
        return Optional
                .of(userRepository.findById(validationResponse.getPid()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElseThrow(UnauthorizedException::new);
    }



    /**
     * Marks the specified notifications as read for the current user.
     *
     * @param validationResponse The token validation response containing user information.
     * @param notificationIds    The IDs of the notifications to mark as read.
     * @throws UnauthorizedException     If the current user is not authorized to mark the notifications as read.
     * @throws ResourceNotFoundException If the requested notification ID is not found in the database.
     */
    @Override
//    @Async
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public void markNotificationAsRead(
            TokenValidationResponse validationResponse,
            List<Long> notificationIds
    ) {
        // Check if the notification IDs list is empty or null
        if (textHelper.isBlank(notificationIds)) return;
        // Retrieve the current user model based on the provided token validation response
        UserModel currentRequestUserModel = getCurrentRequestUserModel(validationResponse);

        // Check if the current user is authorized to mark the notifications as read
        if (!notificationUserMappingRepository
                .existsByNotificationNotificationIdAndUser(
                        notificationIds.get(0),
                        currentRequestUserModel
                )
        )
            throw new UnauthorizedException();

        // Retrieve the notification user mapping for the first notification ID in the list
        NotificationUserMapping mapping = Optional
                .of(notificationIds)
                .filter(ids -> !notificationIds.isEmpty())
                .map(ids -> ids.get(0))
                .map(id -> notificationUserMappingRepository.findByNotificationNotificationIdAndUserUserPid(id, validationResponse.getPid()))
                .orElseThrow(() -> new ResourceNotFoundException("Could not find the requested notification, Maybe try refreshing the page?"));

        // Mark the notification as seen and update the seen timestamp
        mapping.setSeen(true);
        mapping.setSeenAt(Instant.now());
        // Save the updated mapping
        notificationUserMappingRepository.save(mapping);

//        CompletableFuture
//                .runAsync(() -> {
//                    if (textHelper.isBlank(notificationIds)) return;
//                    UserModel currentRequestUserModel = getCurrentRequestUserModel(validationResponse);
//                    if (!notificationUserMappingRepository
//                            .existsByNotificationNotificationIdAndUser(
//                                    notificationIds.get(0),
//                                    currentRequestUserModel
//                            )
//                    )
//                        throw new UnauthorizedException();
//
//                    NotificationUserMapping mapping = Optional
//                            .of(notificationIds)
//                            .filter(ids -> !notificationIds.isEmpty())
//                            .map(ids -> ids.get(0))
//                            .map(notificationUserMappingRepository::findByNotificationNotificationId)
//                            .orElseThrow(() -> new ResourceNotFoundException("Could not find the requested notification, Maybe try refreshing the page?"));
//
//                    mapping.setSeen(true);
//                    mapping.setSeenAt(Instant.now());
//                    notificationUserMappingRepository.save(mapping);
//                    LoggerFactory.getLogger(EmployeeServiceImpl.class).info("Notification Seen By: " + Thread.currentThread().getName());
//                });
    }





    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> addToDoList(
            TokenValidationResponse validationResponse,
            AddToDoRequest addToDoRequest
    ) {
        UserModel loggedInUser = getCurrentRequestUserModel(validationResponse);

        if(null == addToDoRequest || textHelper.isBlank(addToDoRequest.getDescription()))
            throw new InvalidDataException("To proceed, provide a description.");

        String description = addToDoRequest.getDescription().trim();

        if(250 < description.length())
            throw new InvalidDataException("Please ensure that the length of description stays within maximum limit(250).");

        ToDoModel toDoModel = ToDoModel.builder()
                .toDoUid(generateToDoUid())
                .description(description)
                .status(ToDoStatus.PENDING)
                .createdBy(loggedInUser)
                .lastModifiedBy(loggedInUser)
                .build();

        toDoRepository.save(toDoModel);

        return APIResponse.builder()
                .message("Successfully added to ToDo-List.")
                .build();
    }





    /**
     * Retrieves either a rectification link or a token for the specified employee request, based on the provided parameters.
     *
     * @param validationResponse     The token validation response containing user information.
     * @param rectificationLinkRequest The request object containing details for generating the link or token.
     * @param isTokenOnly            A flag indicating whether to generate only the token or both the link and token.
     * @return                       An API response containing either the generated token or a success message.
     * @throws InvalidDataException  If the rectification reason is blank.
     * @throws UnauthorizedException If the employee request has already been approved, preventing further rectification.
     */
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    private APIResponse<?> getEmployeeRequestLinkOrToken(
            TokenValidationResponse validationResponse,
            RectificationLinkRequest rectificationLinkRequest,
            boolean isTokenOnly
    ) {
        // Sanitize the rectification link request
        RectificationLinkRequest request = sanitizer.sanitize(rectificationLinkRequest);

        // Check if the rectification reason is provided
        if (textHelper.isBlank(request.getReason()))
            throw new InvalidDataException("Cannot proceed further without rectification reason.");

        // Retrieve the employee request model based on the request ID
        RequestModel requestModel = getEmployeeRequestModelByUid(request.getReq_id());
        // Validate the email associated with the employee request
        if (envProfile.isProductionProfile()) {
            emailUtils.validateEmailOnWeb(requestModel.getEmail());
        }

        // Check if the employee request has already been approved
        if (EmployeeRequestStatus.APPROVED.equals(requestModel.getRequestStatus()))
            throw new UnauthorizedException("Once we approve an employee request, we can't shoot over any links for fixes.");

        // Generate a token for the employee request
        String token = jwtUtils.issueToken(
                requestModel.getRequestUid(),
                requestModel.getTokenAt().toEpochMilli(),
                JwtWizard.EMPLOYEE_REGISTRATION_EXPIRY_DURATION
        );
        // Return the token only if requested
        if (isTokenOnly)
            return APIResponse.builder()
                    .data(token)
                    .build();

        // Generate recipient information and link for the rectification email
        String recipientName = textHelper.buildFullName(
                requestModel.getFirstname(),
                requestModel.getMiddlename(),
                requestModel.getLastname()
        );
        String recipientEmail = requestModel.getEmail();
        String recipientLink = defaultExternalEmployeeFormLink + token;

        // Send the rectification email with the link to the recipient
        emailUtils.sendEmployeeRectificationLinkMail(
                recipientName,
                recipientEmail,
                request.getReason(),
                recipientLink
        );

        // Return a success message indicating the email has been sent successfully
        return new APIResponse<>(
                null,
                "Rectification email sent to " + recipientEmail +" successfully.",
                null
        );
    }










// PATCH
    /**
     * Updates an external employee request based on the provided details.
     *
     * @param authorizationHeader         The token validation response containing user information.
     * @param updateExternalEmployeeRequest The request object containing the updated employee details.
     * @param isSaveAsDraft              A flag indicating whether to save the request as a draft or submit it for review.
     * @return                           An API response indicating the success of the update operation.
     * @throws CaptchaFailedException   If the hCaptcha validation fails.
     * @throws UnauthorizedException     If the employee request has already been approved, preventing further changes.
     * @throws InvalidDataException      If any of the provided data is invalid or missing.
     * @throws DataAlreadyExistException If a phone number already exists and cannot proceed further.
     */
    @Override
//    @Async
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> updateExternalEmployeeRequest(
            String authorizationHeader,
            UpdateExternalEmployeeRequest updateExternalEmployeeRequest,
            boolean isSaveAsDraft
    ) {
        // Sanitize the update request
        UpdateExternalEmployeeRequest request = sanitizer.sanitize(updateExternalEmployeeRequest);
        // Validate hCaptcha if not saving as draft
        if (!isSaveAsDraft && !hCaptchaUtils.validateHCaptchaOnWeb(request.getHcaptcha_response()))
            throw new CaptchaFailedException();

        // Retrieve the user's UID from the validation response
        String tokenUid = jwtUtils.getSubject(authorizationHeader);
        // Retrieve the employee request model based on the UID
        RequestModel requestModel = getEmployeeRequestModelByUid(tokenUid);
        // Check if the employee request has already been approved
        if (EmployeeRequestStatus.APPROVED.equals(requestModel.getRequestStatus()))
            throw new UnauthorizedException("Once we approve an employee request, we can't make any changes.");

//        if (!textHelper.isBlank(request.getFirstname()))
//            requestModel.setFirstname(request.getFirstname());

        if (!textHelper.isBlank(request.getMiddlename()))
            requestModel.setMiddlename(request.getMiddlename());

//        if (!textHelper.isBlank(request.getLastname()))
//            requestModel.setLastname(request.getLastname());

        if (null != request.getBirth_date()) {
            if (!instantUtils.isAgeAtLeast(request.getBirth_date(), 18))
                throw new InvalidDataException("Our policy mandates that users must be 18 or older to register.");

            requestModel.setBirthDate(request.getBirth_date());
        }

        if (!textHelper.isBlank(request.getGender()))
            requestModel.setGender(request.getGender());

        if (!textHelper.isBlank(request.getPhone())) {
            List<RequestModel> existingRequestsWithPhone = requestRepository.findByPhone(request.getPhone());
            for (RequestModel model : existingRequestsWithPhone) {
                if (!Arrays.asList(EmployeeRequestStatus.DECLINED, EmployeeRequestStatus.APPROVED).contains(model.getRequestStatus()) && !model.getRequestPid().equals(requestModel.getRequestPid()))
                    throw new DataAlreadyExistException("Phone number already exists cannot proceed further.");
            }

            requestModel.setPhone(request.getPhone());
        }

        if (!textHelper.isBlank(request.getAlt_phone()))
            requestModel.setAlternatePhone(request.getAlt_phone());

//        if (!textHelper.isBlank(request.getEmail()))
//            requestModel.setEmail(request.getEmail());

        if (!textHelper.isBlank(request.getAlt_email()))
            requestModel.setAlternateEmail(request.getAlt_email());

        if (!textHelper.isBlank(request.getBlood_group()))
            requestModel.setBloodGroup(request.getBlood_group());

        if (!textHelper.isBlank(request.getMarital_status()))
            requestModel.setMaritalStatus(request.getMarital_status());

        if (!textHelper.isBlank(request.getNationality()))
            requestModel.setNationality(request.getNationality());

        String currentJsonAddress = textHelper.generateJsonAddress(
                requestModel.getCurrentAddress(),
                request.getCurrent_flat_house(),
                request.getCurrent_building_apartment(),
                request.getCurrent_area_street_sector_village(),
                request.getCurrent_landmark(),
                request.getCurrent_pin_code(),
                request.getCurrent_city_town(),
                request.getCurrent_state(),
                request.getCurrent_country()
        );
        String permanentJsonAddress = textHelper.generateJsonAddress(
                requestModel.getPermanentAddress(),
                request.getPermanent_flat_house(),
                request.getPermanent_building_apartment(),
                request.getPermanent_area_street_sector_village(),
                request.getPermanent_landmark(),
                request.getPermanent_pin_code(),
                request.getPermanent_city_town(),
                request.getPermanent_state(),
                request.getPermanent_country()
        );
        requestModel.setCurrentAddress(currentJsonAddress);
        requestModel.setPermanentAddress(permanentJsonAddress);


        if (!textHelper.isBlank(request.getUniversity()))
            requestModel.setUniversity(request.getUniversity());

        if (!textHelper.isBlank(request.getDegree()))
            requestModel.setDegree(request.getDegree());

        if (!textHelper.isBlank(request.getStream()))
            requestModel.setStream(request.getStream());

        if (!textHelper.isBlank(request.getCollege()))
            requestModel.setCollege(request.getCollege());

        if (null != request.getCgpa())
            requestModel.setCgpa(request.getCgpa());

        if (null != request.getPassing_year()) {
//            try {
//                instantUtils.parseInstant(request.getPassing_year(), "yyyy");
//            } catch (ParseException exception) {
//                throw new InvalidDataException("`Passing year` seems invalid. provide a valid year of passing.");
//            }

            requestModel.setPassingYear(request.getPassing_year());
        }

        if (!textHelper.isBlank(request.getSkill_set())) {
            requestModel.setSkillSet(
                    jsonConverter.getJsonStringFromSet(request.getSkill_set())
            );
        }

        if (!textHelper.isBlank(request.getExperience())) {
            List<JSONObject> jsonExperienceObjectList = new ArrayList<>();
            for (ExperienceRequest experienceRequest : request.getExperience()) {
                if (null == experienceRequest)
                    continue;

                if (textHelper.isBlank(experienceRequest.getPosition()))
                    throw new InvalidDataException("In work experience, you might have left `Position` field blank. it cannot be left blank.");

                if (textHelper.isBlank(experienceRequest.getCompany()))
                    throw new InvalidDataException("In work experience, you might have left `Company` field blank. it cannot be left blank.");

                if (null == experienceRequest.getStart())
                    throw new InvalidDataException("In work experience, you might have left `Start Date` field blank. it cannot be left blank.");

                if (null == experienceRequest.getEnd())
                    throw new InvalidDataException("In work experience, you might have left `End Date` field blank. it cannot be left blank.");

                if (instantUtils.isPresentDate(experienceRequest.getStart(), experienceRequest.getEnd()))
                    throw new InvalidDataException("In work experience, `Start Date` and `End Date` cannot be same.");

                if (!instantUtils.isPastDate(experienceRequest.getEnd(), Instant.now()))
                    throw new InvalidDataException("In work experience, Make sure the `End Date` is today or earlier.");

                if (!instantUtils.isPastDate(experienceRequest.getStart(), experienceRequest.getEnd()))
                    throw new InvalidDataException("In work experience, Make sure the `End Date` comes after the `Start Date`.");

                JSONObject jsonExperienceObject = new JSONObject();
                jsonExperienceObject.put("position", experienceRequest.getPosition());
                jsonExperienceObject.put("company", experienceRequest.getCompany());
                jsonExperienceObject.put("start", experienceRequest.getStart());
                jsonExperienceObject.put("end", experienceRequest.getEnd());

                jsonExperienceObjectList.add(jsonExperienceObject);
            }

            if (!jsonExperienceObjectList.isEmpty()) {
                JSONArray jsonExperienceArray = new JSONArray(jsonExperienceObjectList);
                requestModel.setExperience(jsonExperienceArray.toString());
            }
        }


        if (!textHelper.isBlank(request.getEme_name()))
            requestModel.setEmergencyName(request.getEme_name());

        if (!textHelper.isBlank(request.getEme_phone()))
            requestModel.setEmergencyPhone(request.getEme_phone());

        if (!textHelper.isBlank(request.getEme_relation()))
            requestModel.setEmergencyRelation(request.getEme_relation());

        if (!textHelper.isBlank(request.getPhysician_name()))
            requestModel.setPhysicianName(request.getPhysician_name());

        if (!textHelper.isBlank(request.getPhysician_phone()))
            requestModel.setPhysicianPhone(request.getPhysician_phone());

        if (!textHelper.isBlank(request.getMedical_conditions()))
            requestModel.setMedicalConditions(request.getMedical_conditions());

        // Send a notification if not saving as draft
        if (!isSaveAsDraft) {
            // Send notification
            notificationLogger(
                    List.of(requestModel.getCreatedBy().getUserUid()),
                    requestModel.getRequestUid(),
                    View.REQ_DETAILS,
                    "An employee access form has been submitted by " + textHelper.buildFullName(requestModel.getFirstname(), requestModel.getMiddlename(), requestModel.getLastname()) + ". Kindly proceed with the review."
            );
//            CompletableFuture
//                    .runAsync(() -> {
//                        notificationLogger(
//                                List.of(requestModel.getCreatedBy().getUserUid()),
//                                requestModel.getRequestUid(),
//                                View.REQ_DETAILS,
//                                "An employee access form has been submitted by " + textHelper.buildFullName(requestModel.getFirstname(), requestModel.getMiddlename(), requestModel.getLastname()) + ". Kindly proceed with the review."
//                        );
//                        LoggerFactory.getLogger(EmployeeServiceImpl.class).info("Notification Sent By: " + Thread.currentThread().getName());
//                    });

            // Update token timestamp
            requestModel.setTokenAt(Instant.now());
        }

        // Set last modified by to null and save the changes
        requestModel.setLastModifiedBy(null);
        requestRepository.save(requestModel);

        return APIResponse.builder()
                .message("We've updated your employee registration form successfully.")
                .build();
    }


    /**
     * Updates an employee request with the provided details.
     *
     * @param validationResponse       The token validation response containing user information.
     * @param requestUid               The unique identifier of the employee request to be updated.
     * @param updateEmployeeRequest    The request object containing the updated employee details.
     * @return                         An API response indicating the success of the update operation.
     * @throws InvalidDataException    If any of the provided data is invalid or missing.
     * @throws DataAlreadyExistException If an email or phone number already exists and cannot proceed further.
     * @throws UnauthorizedException   If the employee request has already been approved, preventing further changes.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> updateEmployeeRequest(
            TokenValidationResponse validationResponse,
            String requestUid,
            UpdateEmployeeRequest updateEmployeeRequest
    ) {
        // Retrieve the employee request model based on the request UID
        RequestModel requestModel = getEmployeeRequestModelByUid(requestUid);

        // Check if the employee request has already been approved
        if (EmployeeRequestStatus.APPROVED.equals(requestModel.getRequestStatus()))
            throw new InvalidDataException("Once we approve an employee request, we can't make any changes.");

        // Retrieve the user who last modified the request
        UserModel lastModifiedByUser = getUserModelByPid(validationResponse.getPid());

        // Sanitize the update request
        UpdateEmployeeRequest request = sanitizer.sanitize(updateEmployeeRequest);

        if (!textHelper.isBlank(request.getAuthorities())) {
            EntitlementModel entitlementModel = getEntitlementModelByRequestUid(requestUid);
            HashSet<String> updatedAuthorities = new HashSet<>();
            for (String authorityString : request.getAuthorities()) {
                Authority authority = textHelper.stringToEnum(Authority.class, authorityString);
                if (null == authority)
                    throw new InvalidDataException("No such authority can be found. (" + authorityString + ")");

                updatedAuthorities.add(authority.name());
            }

            entitlementModel.setAuthorities(updatedAuthorities);
            entitlementRepository.save(entitlementModel);
        }

        if (!textHelper.isBlank(request.getFirstname()))
            requestModel.setFirstname(
                    StringUtils.capitalize(request.getFirstname())
            );

        if (!textHelper.isBlank(request.getMiddlename()))
            requestModel.setMiddlename(
                    StringUtils.capitalize(request.getMiddlename())
            );

        if (!textHelper.isBlank(request.getLastname()))
            requestModel.setLastname(
                    StringUtils.capitalize(request.getLastname())
            );

        if (null != request.getBirth_date()) {
            if (!instantUtils.isAgeAtLeast(request.getBirth_date(), 18))
                throw new InvalidDataException("Our policy mandates that users must be 18 or older to register.");

            requestModel.setBirthDate(request.getBirth_date());
        }

        if (null != request.getJoined_at())
            requestModel.setJoinedAt(request.getJoined_at());

        if (!textHelper.isBlank(request.getPhone())) {
            List<RequestModel> existingRequestsWithPhone = requestRepository.findByPhone(request.getPhone());
            for (RequestModel model : existingRequestsWithPhone) {
                if (!EmployeeRequestStatus.DECLINED.equals(model.getRequestStatus()) && !model.getRequestPid().equals(requestModel.getRequestPid()))
                    throw new DataAlreadyExistException("Phone number already exists cannot proceed further.");
            }

            requestModel.setPhone(request.getPhone());
        }

        if (!textHelper.isBlank(request.getAlt_phone()))
            requestModel.setAlternatePhone(request.getAlt_phone());

        if (!textHelper.isBlank(request.getGender()))
            requestModel.setGender(request.getGender());

        if (!textHelper.isBlank(request.getEmail())) {
            List<RequestModel> existingRequestsWithEmail = requestRepository.findByEmail(request.getEmail());
            for (RequestModel model : existingRequestsWithEmail) {
                if (!EmployeeRequestStatus.DECLINED.equals(model.getRequestStatus()) && !model.getRequestPid().equals(requestModel.getRequestPid()))
                    throw new DataAlreadyExistException("Email already exists cannot proceed further.");
            }

            if (envProfile.isProductionProfile()) {
                emailUtils.validateEmailOnWeb(request.getEmail());
            }

            requestModel.setEmail(request.getEmail());
        }

        if (!textHelper.isBlank(request.getAlt_email()))
            requestModel.setAlternateEmail(request.getAlt_email());

        if (!textHelper.isBlank(request.getBlood_group()))
            requestModel.setBloodGroup(request.getBlood_group());

        if (!textHelper.isBlank(request.getMarital_status()))
            requestModel.setMaritalStatus(request.getMarital_status());

        if (!textHelper.isBlank(request.getNationality()))
            requestModel.setNationality(request.getNationality());

        String currentJsonAddress = textHelper.generateJsonAddress(
                requestModel.getCurrentAddress(),
                request.getCurrent_flat_house(),
                request.getCurrent_building_apartment(),
                request.getCurrent_area_street_sector_village(),
                request.getCurrent_landmark(),
                request.getCurrent_pin_code(),
                request.getCurrent_city_town(),
                request.getCurrent_state(),
                request.getCurrent_country()
        );
        String permanentJsonAddress = textHelper.generateJsonAddress(
                requestModel.getPermanentAddress(),
                request.getPermanent_flat_house(),
                request.getPermanent_building_apartment(),
                request.getPermanent_area_street_sector_village(),
                request.getPermanent_landmark(),
                request.getPermanent_pin_code(),
                request.getPermanent_city_town(),
                request.getPermanent_state(),
                request.getPermanent_country()
        );
        requestModel.setCurrentAddress(currentJsonAddress);
        requestModel.setPermanentAddress(permanentJsonAddress);


        if (!textHelper.isBlank(request.getUniversity()))
            requestModel.setUniversity(request.getUniversity());

        if (!textHelper.isBlank(request.getDegree()))
            requestModel.setDegree(request.getDegree());

        if (!textHelper.isBlank(request.getStream()))
            requestModel.setStream(request.getStream());

        if (!textHelper.isBlank(request.getCollege()))
            requestModel.setCollege(request.getCollege());

        if (null != request.getCgpa())
            requestModel.setCgpa(request.getCgpa());

        if (null != request.getPassing_year()) {
//            try {
//                instantUtils.parseInstant(request.getPassing_year(), "yyyy");
//            } catch (ParseException exception) {
//                throw new InvalidDataException("`Passing year` seems invalid. provide a valid year of passing.");
//            }

            requestModel.setPassingYear(request.getPassing_year());
        }

        if (!textHelper.isBlank(request.getSkill_set())) {
            requestModel.setSkillSet(
                    jsonConverter.getJsonStringFromSet(request.getSkill_set())
            );
        }

        if (!textHelper.isBlank(request.getExperience())) {
            List<JSONObject> jsonExperienceObjectList = new ArrayList<>();
            for (ExperienceRequest experienceRequest : request.getExperience()) {
                if (null == experienceRequest)
                    continue;

                if (textHelper.isBlank(experienceRequest.getPosition()))
                    throw new InvalidDataException("In work experience, you might have left `Position` field blank. it cannot be left blank.");

                if (textHelper.isBlank(experienceRequest.getCompany()))
                    throw new InvalidDataException("In work experience, you might have left `Company` field blank. it cannot be left blank.");

                if (null == experienceRequest.getStart())
                    throw new InvalidDataException("In work experience, you might have left `Start Date` field blank. it cannot be left blank.");

                if (null == experienceRequest.getEnd())
                    throw new InvalidDataException("In work experience, you might have left `End Date` field blank. it cannot be left blank.");

                if (instantUtils.isPresentDate(experienceRequest.getStart(), experienceRequest.getEnd()))
                    throw new InvalidDataException("In work experience, `Start Date` and `End Date` cannot be same.");

                if (!instantUtils.isPastDate(experienceRequest.getEnd(), Instant.now()))
                    throw new InvalidDataException("In work experience, Make sure the `End Date` is today or earlier.");

                if (!instantUtils.isPastDate(experienceRequest.getStart(), experienceRequest.getEnd()))
                    throw new InvalidDataException("In work experience, Make sure the `End Date` comes after the `Start Date`.");

                JSONObject jsonExperienceObject = new JSONObject();
                jsonExperienceObject.put("position", experienceRequest.getPosition());
                jsonExperienceObject.put("company", experienceRequest.getCompany());
                jsonExperienceObject.put("start", experienceRequest.getStart());
                jsonExperienceObject.put("end", experienceRequest.getEnd());

                jsonExperienceObjectList.add(jsonExperienceObject);
            }

            if (!jsonExperienceObjectList.isEmpty()) {
                JSONArray jsonExperienceArray = new JSONArray(jsonExperienceObjectList);
                requestModel.setExperience(jsonExperienceArray.toString());
            }
        }


        if (!textHelper.isBlank(request.getEme_name()))
            requestModel.setEmergencyName(request.getEme_name());

        if (!textHelper.isBlank(request.getEme_phone()))
            requestModel.setEmergencyPhone(request.getEme_phone());

        if (!textHelper.isBlank(request.getEme_relation()))
            requestModel.setEmergencyRelation(request.getEme_relation());

        if (!textHelper.isBlank(request.getPhysician_name()))
            requestModel.setPhysicianName(request.getPhysician_name());

        if (!textHelper.isBlank(request.getPhysician_phone()))
            requestModel.setPhysicianPhone(request.getPhysician_phone());

        if (!textHelper.isBlank(request.getMedical_conditions()))
            requestModel.setMedicalConditions(request.getMedical_conditions());

        // Save the updated employee request
        requestModel.setLastModifiedBy(lastModifiedByUser);
        requestRepository.save(requestModel);

        return APIResponse.builder()
                .message("We've updated employee's details successfully.")
                .build();
    }


    /**
     * Updates employee details based on the provided request.
     *
     * @param validationResponse    The token validation response containing user information.
     * @param userUid               The unique identifier of the user whose details are to be updated.
     * @param updateEmployeeRequest The request containing the updated employee details.
     * @return APIResponse indicating the success of the operation.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> updateEmployee(
            TokenValidationResponse validationResponse,
            String userUid,
            UpdateEmployeeRequest updateEmployeeRequest
    ) {
        // Retrieve the user who initiated the update
        UserModel lastModifiedByUser = getUserModelByPid(validationResponse.getPid());
        // Retrieve the user whose details are to be updated
        UserModel userModel = getUserModelByUid(userUid);
        // Retrieve the employee details of the user
        EmployeeModel employeeModel = userModel.getEmployeeDetails();
        // Retrieve the entitlement model of the user
        EntitlementModel entitlementModel = getEntitlementModelByUserUid(userUid);
        // Sanitize the incoming update request
        UpdateEmployeeRequest request = sanitizer.sanitize(updateEmployeeRequest);

        // Update user authorities if provided
        if (!textHelper.isBlank(request.getAuthorities())) {
            HashSet<String> updatedAuthorities = new HashSet<>();
            for (String authorityString : request.getAuthorities()) {
                Authority authority = textHelper.stringToEnum(Authority.class, authorityString);
                if (null == authority)
                    throw new InvalidDataException("No such authority can be found. (" + authorityString + ")");

                updatedAuthorities.add(authority.name());
            }

            entitlementModel.setAuthorities(updatedAuthorities);
            entitlementRepository.save(entitlementModel);
        }

        // Update user personal details if provided
        if (!textHelper.isBlank(request.getFirstname()))
            userModel.setFirstname(
                    StringUtils.capitalize(request.getFirstname())
            );

        // Update user middle name if provided
        if (!textHelper.isBlank(request.getMiddlename()))
            userModel.setMiddlename(
                    StringUtils.capitalize(request.getMiddlename())
            );

        // Update user last name if provided
        if (!textHelper.isBlank(request.getLastname()))
            userModel.setLastname(
                    StringUtils.capitalize(request.getLastname())
            );

        // Update user birth date if provided
        if (null != request.getBirth_date()) {
            if (!instantUtils.isAgeAtLeast(request.getBirth_date(), 18))
                throw new InvalidDataException("Our policy mandates that users must be 18 or older to register.");

            employeeModel.setBirthDate(request.getBirth_date());
        }

        // Update user phone number if provided
        if (!textHelper.isBlank(request.getPhone())) {
            if (!request.getPhone().equals(userModel.getPhone()) && 0 != userRepository.countByPhone(request.getPhone()))
                throw new DataAlreadyExistException("Phone number already exists cannot proceed further.");

            userModel.setPhone(request.getPhone());
        }

        // Update alternate phone number if provided
        if (!textHelper.isBlank(request.getAlt_phone()))
            employeeModel.setAlternatePhone(request.getAlt_phone());

        // Update user gender if provided
        if (!textHelper.isBlank(request.getGender()))
            employeeModel.setGender(request.getGender());

        // Update user email if provided
        if (!textHelper.isBlank(request.getEmail())) {
            if (!request.getEmail().equals(userModel.getEmail()) && 0 != userRepository.countByEmail(request.getEmail()))
                throw new DataAlreadyExistException("Email already exists cannot proceed further.");

            if (envProfile.isProductionProfile()) {
                emailUtils.validateEmailOnWeb(request.getEmail());
            }

            userModel.setEmail(request.getEmail());
        }

        // Update alternate email if provided
        if (!textHelper.isBlank(request.getAlt_email()))
            employeeModel.setAlternateEmail(request.getAlt_email());

        // Update user blood group if provided
        if (!textHelper.isBlank(request.getBlood_group()))
            employeeModel.setBloodGroup(request.getBlood_group());

        // Update user marital status if provided
        if (!textHelper.isBlank(request.getMarital_status()))
            employeeModel.setMaritalStatus(request.getMarital_status());

        // Update user nationality if provided
        if (!textHelper.isBlank(request.getNationality()))
            employeeModel.setNationality(request.getNationality());

        // Update user addresses if provided
        String currentJsonAddress = textHelper.generateJsonAddress(
                employeeModel.getCurrentAddress(),
                request.getCurrent_flat_house(),
                request.getCurrent_building_apartment(),
                request.getCurrent_area_street_sector_village(),
                request.getCurrent_landmark(),
                request.getCurrent_pin_code(),
                request.getCurrent_city_town(),
                request.getCurrent_state(),
                request.getCurrent_country()
        );
        String permanentJsonAddress = textHelper.generateJsonAddress(
                employeeModel.getPermanentAddress(),
                request.getPermanent_flat_house(),
                request.getPermanent_building_apartment(),
                request.getPermanent_area_street_sector_village(),
                request.getPermanent_landmark(),
                request.getPermanent_pin_code(),
                request.getPermanent_city_town(),
                request.getPermanent_state(),
                request.getPermanent_country()
        );
        employeeModel.setCurrentAddress(currentJsonAddress);
        employeeModel.setPermanentAddress(permanentJsonAddress);


        // Update user education details if provided
        if (!textHelper.isBlank(request.getUniversity()))
            employeeModel.setUniversity(request.getUniversity());

        if (!textHelper.isBlank(request.getDegree()))
            employeeModel.setDegree(request.getDegree());

        if (!textHelper.isBlank(request.getStream()))
            employeeModel.setStream(request.getStream());

        if (!textHelper.isBlank(request.getCollege()))
            employeeModel.setCollege(request.getCollege());

        if (null != request.getCgpa())
            employeeModel.setCgpa(request.getCgpa());

        if (null != request.getPassing_year()) {
//            try {
//                instantUtils.parseInstant(request.getPassing_year(), "yyyy");
//            } catch (ParseException exception) {
//                throw new InvalidDataException("`Passing year` seems invalid. provide a valid year of passing.");
//            }

            employeeModel.setPassingYear(request.getPassing_year());
        }

        // Update user skill set details if provided
        if (!textHelper.isBlank(request.getSkill_set())) {
    //        Skill Set Mapping - 1
            Set<String> skillSetList = request.getSkill_set();

            List<String> allActualCoursesFromDatabase = courseRepository.findAllCourseNames();
            List<String> mappedSoftSkills = skillSetList
                    .stream()
                    .filter(textHelper::isNonBlank)
                    .filter(providedSkill -> allActualCoursesFromDatabase
                            .stream()
                            .noneMatch(actual -> providedSkill.equalsIgnoreCase(actual.toLowerCase()))
                    )
                    .collect(Collectors.toList());

            String softSkillsAsJsonString;
            String skillSetListAsJsonString;
            try {
                softSkillsAsJsonString = jsonConverter.convertListToJsonString(mappedSoftSkills);
                skillSetListAsJsonString = jsonConverter.convertSetToJsonString(skillSetList);
            } catch (JsonProcessingException exception) {
                throw new IllegalArgumentException(exception);
            }
            employeeModel.setSoftSkill(softSkillsAsJsonString);
            employeeModel.setSkillSet(skillSetListAsJsonString);

            // ACTUAL SKILL-SET MAPPED TO COURSE
            Map<String, CourseModel> mappedSkillSet = courseRepository
                    .findBySkillSetContaining(
                            skillSetList.stream().filter(textHelper::isNonBlank).map(String::toLowerCase).collect(Collectors.toList())
                    )
                    .stream()
                    .collect(Collectors.toMap(
                            CourseModel::getCourseName,
                            modal -> modal,
                            (existing, replacement) -> replacement
                    ));

            Map<String, UserSkillMapping> existingSkillSetMapping = userModel.getSkills()
                    .stream()
                    .collect(Collectors.toMap(
                            mapping -> mapping.getCourse().getCourseName(),
                            mapping -> mapping,
                            (existing, replacement) -> replacement
                    ));

            List<UserSkillMapping> mappingToBeRemoved = new ArrayList<>();
            List<UserSkillAttemptMapping> attemptsMappingToBeRemoved = new ArrayList<>();
            List<UserSkillMapping> finalMapping = existingSkillSetMapping
                    .values()
                    .stream()
                    .map(mapping -> {
                        CourseModel model = mapping.getCourse();
                        CourseModel modelFromMap = mappedSkillSet.getOrDefault(model.getCourseName(), null);
                        // SKILL HAS BEEN REMOVED.
                        if (null == modelFromMap) {
                            if (Arrays.asList(SkillClearanceStatus.NOT_CLEARED, SkillClearanceStatus.PENDING).contains(mapping.getStatus())) {
                                attemptsMappingToBeRemoved.addAll(mapping.getUserSkillAttempts());
                                mappingToBeRemoved.add(mapping);
                                return null;
                            }

                            else if (SkillClearanceStatus.CLEARED.equals(mapping.getStatus()))
                                throw new InvalidDataException("Oops! We can't remove the skill '" + model.getCourseName() + "' from the set because the faculty has already cleared it.");
                        }

                        return mapping;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            finalMapping.addAll(
                    mappedSkillSet.entrySet()
                            .stream()
                            .map(entry -> {
                                UserSkillMapping mappingFromMap = existingSkillSetMapping.getOrDefault(entry.getKey(), null);
                                // SKILL HAS BEEN REMOVED.
                                if (null == mappingFromMap)
                                    return UserSkillMapping.builder()
                                            .user(userModel)
                                            .course(entry.getValue())
                                            .status(SkillClearanceStatus.PENDING)
                                            .build();
                                else
                                    return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
            userSkillAttemptMappingRepository.deleteAllInBatch(attemptsMappingToBeRemoved);
            userSkillMappingRepository.deleteAllInBatch(mappingToBeRemoved);
            userModel.setSkills(finalMapping);
        }

        // Update user experience details if provided
        if (!textHelper.isBlank(request.getExperience())) {
            List<JSONObject> jsonExperienceObjectList = new ArrayList<>();
            for (ExperienceRequest experienceRequest : request.getExperience()) {
                if (null == experienceRequest)
                    continue;

                if (textHelper.isBlank(experienceRequest.getPosition()))
                    throw new InvalidDataException("In work experience, you might have left `Position` field blank. it cannot be left blank.");

                if (textHelper.isBlank(experienceRequest.getCompany()))
                    throw new InvalidDataException("In work experience, you might have left `Company` field blank. it cannot be left blank.");

                if (null == experienceRequest.getStart())
                    throw new InvalidDataException("In work experience, you might have left `Start Date` field blank. it cannot be left blank.");

                if (null == experienceRequest.getEnd())
                    throw new InvalidDataException("In work experience, you might have left `End Date` field blank. it cannot be left blank.");

                if (instantUtils.isPresentDate(experienceRequest.getStart(), experienceRequest.getEnd()))
                    throw new InvalidDataException("In work experience, `Start Date` and `End Date` cannot be same.");

                if (!instantUtils.isPastDate(experienceRequest.getEnd(), Instant.now()))
                    throw new InvalidDataException("In work experience, Make sure the `End Date` is today or earlier.");

                if (!instantUtils.isPastDate(experienceRequest.getStart(), experienceRequest.getEnd()))
                    throw new InvalidDataException("In work experience, Make sure the `End Date` comes after the `Start Date`.");

                JSONObject jsonExperienceObject = new JSONObject();
                jsonExperienceObject.put("position", experienceRequest.getPosition());
                jsonExperienceObject.put("company", experienceRequest.getCompany());
                jsonExperienceObject.put("start", experienceRequest.getStart());
                jsonExperienceObject.put("end", experienceRequest.getEnd());

                jsonExperienceObjectList.add(jsonExperienceObject);
            }

            if (!jsonExperienceObjectList.isEmpty()) {
                JSONArray jsonExperienceArray = new JSONArray(jsonExperienceObjectList);
                employeeModel.setExperience(jsonExperienceArray.toString());
            }
        }

        // Update user emergency contact details if provided
        if (!textHelper.isBlank(request.getEme_name()))
            employeeModel.setEmergencyName(request.getEme_name());

        if (!textHelper.isBlank(request.getEme_phone()))
            employeeModel.setEmergencyPhone(request.getEme_phone());

        if (!textHelper.isBlank(request.getEme_relation()))
            employeeModel.setEmergencyRelation(request.getEme_relation());

        // Update user physician details if provided
        if (!textHelper.isBlank(request.getPhysician_name()))
            employeeModel.setPhysicianName(request.getPhysician_name());

        if (!textHelper.isBlank(request.getPhysician_phone()))
            employeeModel.setPhysicianPhone(request.getPhysician_phone());

        // Update user medical conditions if provided
        if (!textHelper.isBlank(request.getMedical_conditions()))
            employeeModel.setMedicalConditions(request.getMedical_conditions());

        // Set the user who last modified the details
        userModel.setLastModifiedBy(lastModifiedByUser);
        // Save the updated user model
        userRepository.save(userModel);

        // Return APIResponse indicating success
        return APIResponse.builder()
                .message("We've updated employee's details successfully.")
                .build();
    }




    /**
     * Updates the entitlements of an employee identified by the provided user UID.
     *
     * @param userUid                  The unique identifier of the user whose entitlements are to be updated.
     * @param updateEntitlementsRequest The request containing the updated entitlements.
     * @return APIResponse indicating the success of the operation.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> updateEmployeeEntitlements(
            String userUid,
            UpdateEntitlementsRequest updateEntitlementsRequest
    ) {
        // Retrieve the user model based on the provided user UID
        UserModel userModel = getUserModelByUid(userUid);
        // Set the updated timestamp for the user model
        userModel.setUpdatedAt(Instant.now());

        // Retrieve the entitlement model based on the provided user UID
        EntitlementModel entitlementModel = getEntitlementModelByUserUid(userUid);
        // Update the entitlements with the ones provided in the request
        entitlementModel.setEntitlements(updateEntitlementsRequest.getEntitlements());

        // Save the updated entitlement model
        entitlementRepository.save(entitlementModel);
        // Save the updated user model
        userRepository.save(userModel);

        // Return APIResponse indicating success
        return APIResponse
                .builder()
                .message("We've updated entitlements of the employee successfully.")
                .data(getUserDetails(userUid))
                .build();
    }












    /**
     * Declines the status of an employee request identified by the provided request UID.
     *
     * @param validationResponse The validation response containing token information.
     * @param requestUid         The unique identifier of the employee request to be declined.
     * @return APIResponse indicating the success of the operation.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> declineEmployeeRequestStatus(
            TokenValidationResponse validationResponse,
            String requestUid
    ) {
        // Retrieve the request model based on the provided request UID
        RequestModel requestModel = getEmployeeRequestModelByUid(requestUid);
        // Check if the request status is already approved or declined
        if (EmployeeRequestStatus.APPROVED.equals(requestModel.getRequestStatus()))
            throw new InvalidDataException("Once approved employee request cannot be set to declined.");

        if (EmployeeRequestStatus.DECLINED.equals(requestModel.getRequestStatus()))
            throw new InvalidDataException("Already declined employee request cannot be set to declined again.");

        String recipientName, recipientEmail;
        // Update the request status to DECLINED
        requestModel.setRequestStatus(EmployeeRequestStatus.DECLINED);

        // Prepare recipient information for notification
        recipientName = textHelper.buildFullName(
                requestModel.getFirstname(),
                requestModel.getMiddlename(),
                requestModel.getLastname()
        );
        recipientEmail = requestModel.getEmail();
        // Send email notification of access denial to the employee
        emailUtils.sendEmployeeAccessDenialMail(
                recipientName,
                recipientEmail
        );

        // Save the updated request model
        requestRepository.save(requestModel);
        // Return APIResponse indicating success
        return APIResponse.builder()
                .message("We've changed employee request status to " + requestModel.getRequestStatus() + " successfully.")
                .build();


    }










    /**
     * Approves the status of an employee request and creates a new employee based on the request details.
     *
     * @param validationResponse  The validation response containing token information.
     * @param approveEmployeeRequest The request containing details to be approved.
     * @return APIResponse indicating the success of the operation.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> approveEmployeeRequestStatus(
            TokenValidationResponse validationResponse,
            ApproveEmployeeRequest approveEmployeeRequest
    ) {
        // Sanitize the request input
        ApproveEmployeeRequest request = sanitizer.sanitize(approveEmployeeRequest);
        // Retrieve the request model based on the provided request UID
        RequestModel requestModel = getEmployeeRequestModelByUid(request.getReq_id());

        // Validate request details
        if (null == request.getIn_time())
            throw new InvalidDataException("IN-time cannot be null.");
        if (null == request.getOut_time())
            throw new InvalidDataException("OUT-time cannot be null.");

//        LocalTime inTime = LocalTime.parse(request.getIn_time(), instantUtils.defaultTimeWithoutSSFormat);
//        LocalTime outTime = LocalTime.parse(request.getOut_time(), instantUtils.defaultTimeWithoutSSFormat);

        // Extract necessary request details
        Instant inTime = request.getIn_time();
        Instant outTime = request.getOut_time();
        int annualLeaves = null == request.getLeaves() ? 0 : request.getLeaves();

        // Validate annual leaves
        if (0 == annualLeaves)
            throw new InvalidDataException("Annual leaves cannot be ZERO.");
        if (annualLeaves < 0)
            throw new InvalidDataException("Annual leaves cannot be in NEGATIVE.");

        // Validate time entries
        if (inTime.equals(outTime))
            throw new InvalidDataException("IN-time and OUT-time cannot be same.");
        if (!inTime.isBefore(outTime))
            throw new InvalidDataException("IN-time cannot be after OUT-time.");

        // Extract and validate working days
        if (textHelper.isBlank(request.getDays()))
            throw new InvalidDataException("Days are not provided");

        List<DayOfWeek> daysOfWeek = request.getDays()
                .stream()
                .map(dayString -> textHelper.isBlank(dayString) ? null : textHelper.stringToEnum(DayOfWeek.class, dayString.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (textHelper.isBlank(daysOfWeek))
            throw new InvalidDataException("DAYS cannot be blank, choose at least one working day.");

        // Check if the request is already approved
        if (EmployeeRequestStatus.APPROVED.equals(requestModel.getRequestStatus()))
            throw new InvalidDataException("Already approved employee request cannot be set to approved again.");

        String recipientName, recipientEmail;
        // Validate user email and phone uniqueness
        if (0 != userRepository.countByEmail(requestModel.getEmail()))
            throw new DataAlreadyExistException("Email already exists cannot approve the request.");

        if (0 != userRepository.countByPhone(requestModel.getPhone()))
            throw new DataAlreadyExistException("Phone number already exists approve the request.");

        // Update request status to APPROVED
        requestModel.setRequestStatus(EmployeeRequestStatus.APPROVED);
        // Retrieve details of the user approving the request
        UserModel createdByUserModel = getUserModelByPid(validationResponse.getPid());
        // Retrieve entitlement details based on the request UID
        EntitlementModel entitlementModel = getEntitlementModelByRequestUid(request.getReq_id());
        // Generate a strong password for the new employee
        String strongPassword = passwordGenerator
                .strategy(PasswordGenerationStrategy.EASY_TO_READ)
                .generate(15);

        // Copy document files locally and save file data models
        FileDataModel identityDocument = copyFileLocally(
                documentDirectory,
                requestModel.getIdentityDocument()
        );
        FileDataModel addressDocument = copyFileLocally(
                documentDirectory,
                requestModel.getAddressDocument()
        );
        FileDataModel offerLetterDocument = copyFileLocally(
                documentDirectory,
                requestModel.getOfferLetterDocument()
        );
        FileDataModel educationDocument = copyFileLocally(
                documentDirectory,
                requestModel.getEducationDocument()
        );
        FileDataModel experienceDocument = copyFileLocally(
                documentDirectory,
                requestModel.getExperienceDocument()
        );
        FileDataModel salarySlipDocument = copyFileLocally(
                documentDirectory,
                requestModel.getSalarySlipDocument()
        );

        final String CANNOT_FIND_MANDATORY_FIELDS = "Oops! Can't find %s. Maybe try rechecking mandatory fields?";
        // Create employee model with mandatory details
        EmployeeModel employeeModel = EmployeeModel.builder()
                .alternatePhone(requestModel.getAlternatePhone())
                .alternateEmail(requestModel.getAlternateEmail())
                .birthDate(Optional
                        .ofNullable(requestModel.getBirthDate())
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "birth-date")
                        ))
                )
                .gender(Optional
                        .ofNullable(requestModel.getGender())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "gender")
                        ))
                )
                .bloodGroup(Optional
                        .ofNullable(requestModel.getBloodGroup())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "blood-group")
                        ))
                )
                .maritalStatus(Optional
                        .ofNullable(requestModel.getMaritalStatus())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "marital status")
                        ))
                )
                .nationality(Optional
                        .ofNullable(requestModel.getNationality())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "nationality")
                        ))
                )
                .currentAddress(Optional
                        .ofNullable(requestModel.getCurrentAddress())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "current address")
                        ))
                )
                .permanentAddress(Optional
                        .ofNullable(requestModel.getPermanentAddress())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "permanent address")
                        ))
                )
                .university(Optional
                        .ofNullable(requestModel.getUniversity())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "university")
                        ))
                )
                .degree(Optional
                        .ofNullable(requestModel.getDegree())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "degree")
                        ))
                )
                .stream(Optional
                        .ofNullable(requestModel.getStream())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "stream")
                        ))
                )
                .college(Optional
                        .ofNullable(requestModel.getCollege())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "college name")
                        ))
                )
                .cgpa(Optional
                        .ofNullable(requestModel.getCgpa())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "CGPA")
                        ))
                )
                .passingYear(Optional
                        .ofNullable(requestModel.getPassingYear())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "passing year")
                        ))
                )
                .skillSet(Optional
                        .ofNullable(requestModel.getSkillSet())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "skill set")
                        ))
                )
                .experience(requestModel.getExperience())
                .emergencyName(Optional
                        .ofNullable(requestModel.getEmergencyName())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "emergency contact's name")
                        ))
                )
                .emergencyPhone(Optional
                        .ofNullable(requestModel.getEmergencyPhone())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "emergency contact's phone number")
                        ))
                )
                .emergencyRelation(Optional
                        .ofNullable(requestModel.getEmergencyRelation())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "relation with provided emergency contact")
                        ))
                )
                .physicianName(requestModel.getPhysicianName())
                .physicianPhone(requestModel.getPhysicianPhone())
                .medicalConditions(requestModel.getMedicalConditions())
                .identityDocument(
                        identityDocument == null
                                ? null : fileDataRepository.save(identityDocument)
                )
                .addressDocument(
                        addressDocument == null
                                ? null : fileDataRepository.save(addressDocument)
                )
                .offerLetterDocument(
                        offerLetterDocument == null
                                ? null : fileDataRepository.save(offerLetterDocument)
                )
                .educationDocument(
                        educationDocument == null
                                ? null : fileDataRepository.save(educationDocument)
                )
                .experienceDocument(
                        experienceDocument == null
                                ? null : fileDataRepository.save(experienceDocument)
                )
                .salarySlipDocument(
                        salarySlipDocument == null
                                ? null
                                : fileDataRepository.save(salarySlipDocument)
                )
                .annualLeaves(annualLeaves)
                .inTime(inTime)
                .outTime(outTime)
                .isSunday(daysOfWeek.contains(DayOfWeek.SUNDAY))
                .isMonday(daysOfWeek.contains(DayOfWeek.MONDAY))
                .isTuesday(daysOfWeek.contains(DayOfWeek.TUESDAY))
                .isWednesday(daysOfWeek.contains(DayOfWeek.WEDNESDAY))
                .isThursday(daysOfWeek.contains(DayOfWeek.THURSDAY))
                .isFriday(daysOfWeek.contains(DayOfWeek.FRIDAY))
                .isSaturday(daysOfWeek.contains(DayOfWeek.SATURDAY))
                .build();

        // Generate a unique employee UID
        String generatedEmployeeUid = generateUniqueUid(
                () -> uidGenerator.generateEmployeeId(requestModel.getJoinedAt(), requestModel.getFirstname()),
                userRepository::countByUserUid,
                5
        );
        // Create user model for the new employee
        UserModel userModel = UserModel.builder()
                .userUid(generatedEmployeeUid)
                .joinedAt(requestModel.getJoinedAt())
                .tokenAt(Instant.now())
                .firstname(Optional
                        .ofNullable(requestModel.getFirstname())
                        .filter(textHelper::isNonBlank)
                        .map(StringUtils::capitalize)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "firstname")
                        ))
                )
                .middlename(
                        StringUtils.capitalize(requestModel.getMiddlename())
                )
                .lastname(Optional
                        .ofNullable(requestModel.getLastname())
                        .filter(textHelper::isNonBlank)
                        .map(StringUtils::capitalize)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "lastname")
                        ))
                )
                .email(Optional
                        .ofNullable(requestModel.getEmail())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "email address")
                        ))
                )
                .phone(Optional
                        .ofNullable(requestModel.getPhone())
                        .filter(textHelper::isNonBlank)
                        .orElseThrow(() -> new InvalidDataException(
                                String.format(CANNOT_FIND_MANDATORY_FIELDS, "phone")
                        ))
                )
                .password(passwordEncoder.encode(strongPassword))
                .enabled(true)
                .employeeDetails(employeeModel)
                .organization(requestModel.getOrganization())
                .build();

//        Skill Set Mapping - 1
        List<String> skillSetList;
        try {
            skillSetList = jsonConverter.convertToList(employeeModel.getSkillSet(), String.class);
        } catch (IOException exception) {
            throw new IllegalArgumentException(exception);
        }

        List<CourseModel> mappedSkillSet = courseRepository.findBySkillSetContaining(
                skillSetList.stream().filter(textHelper::isNonBlank).map(String::toLowerCase).collect(Collectors.toList())
        );


        List<String> allActualCoursesFromDatabase = courseRepository.findAllCourseNames()
                .stream()
                .filter(textHelper::isNonBlank)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> mappedSoftSkills = skillSetList
                .stream()
                .filter(textHelper::isNonBlank)
                .map(String::toLowerCase)
                .filter(providedSkill -> allActualCoursesFromDatabase
                        .stream()
                        .noneMatch(actual -> providedSkill.equals(actual.toLowerCase()))
                )
                .collect(Collectors.toList());

        String softSkillsAsJsonString;
        try {
            softSkillsAsJsonString = jsonConverter.convertListToJsonString(mappedSoftSkills);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(exception);
        }
        employeeModel.setSoftSkill(softSkillsAsJsonString);

        // Set relationships between user, employee, and created by user
        userModel.setCreatedBy(createdByUserModel);
        employeeModel.setUser(userModel);
        employeeModel.setCreatedBy(createdByUserModel);

        // Save user model
        UserModel addedUserModel = userRepository.save(userModel);

//        Skill Set Mapping - 2
        List<UserSkillMapping> skillMappings = mappedSkillSet
                .stream()
                .filter(Objects::nonNull)
                .map(course -> UserSkillMapping.builder().user(addedUserModel).course(course).status(SkillClearanceStatus.PENDING).build())
                .collect(Collectors.toList());
        userSkillMappingRepository.saveAll(skillMappings);


        // Execute stored procedure to insert weekly off leaves
        try {
            leaveRepository.sp_insert_weekly_off_leaves(
                    addedUserModel.getUserPid(),
                    null,
                    null
            );
        } catch (Exception exception) {
            throw new InvalidDataException(exception.getMessage());
        }

        // Set user UID for entitlement model and save
        entitlementModel.setUserUid(addedUserModel.getUserUid());
        entitlementRepository.save(entitlementModel);

        // Send email notification of access approval to the employee
        recipientName = textHelper.buildFullName(
                userModel.getFirstname(),
                userModel.getMiddlename(),
                userModel.getLastname()
        );
        recipientEmail = userModel.getEmail();
        emailUtils.sendEmployeeAccessApprovalMail(
                recipientName,
                recipientEmail,
                generatedEmployeeUid,
                strongPassword
        );

        // Save updated request model
        requestRepository.save(requestModel);
        // Return APIResponse indicating success
        return APIResponse.builder()
                .message("We've changed employee request status to " + requestModel.getRequestStatus() + " successfully.")
                .build();
    }




    /**
     * Updates the schedule of an employee.
     *
     * @param validationResponse  The validation response containing token information.
     * @param approveEmployeeRequest The request containing details to update the employee schedule.
     * @return APIResponse indicating the success of the operation.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> updateEmployeeSchedule(
            TokenValidationResponse validationResponse,
            ApproveEmployeeRequest approveEmployeeRequest
    ) {
        // Sanitize the request input
        ApproveEmployeeRequest request = sanitizer.sanitize(approveEmployeeRequest);
        // Retrieve the currently logged-in user
        UserModel loggedInUser = getCurrentRequestUserModel(validationResponse);

        // Validate IN-time and OUT-time
        if (null == request.getIn_time())
            throw new InvalidDataException("IN-time cannot be null.");

        if (null == request.getOut_time())
            throw new InvalidDataException("OUT-time cannot be null.");

        // Validate Employee ID
        if(textHelper.isBlank(request.getReq_id()))
            throw new InvalidDataException("Apologies, we couldn't locate Employee ID. Kindly furnish a valid one. Thank you.");

        // Retrieve the user to update based on the provided ID
        UserModel userToUpdate = userRepository.findByUserUid(request.getReq_id());

        if(null == userToUpdate)
            throw new InvalidDataException("Apologies, we couldn't locate an employee with the provided ID. Please double-check the ID and try again.");

        // Extract and validate IN-time and OUT-time
        Instant inTime, outTime;
        try{
//            inTime = LocalTime.parse(request.getIn_time(), instantUtils.defaultTimeWithoutSSFormat);
            inTime = request.getIn_time();
        }catch (Exception e){
            throw new InvalidDataException("Kindly input valid clock-in time for the employee. Thank you.");
        }

        try{
//            outTime = LocalTime.parse(request.getOut_time(), instantUtils.defaultTimeWithoutSSFormat);
            outTime = request.getOut_time();
        }catch (Exception e){
            throw new InvalidDataException("Kindly input valid clock-out time for the employee. Thank you.");
        }

        // Validate annual leaves
        int annualLeaves = null == request.getLeaves() ? 0 : request.getLeaves();
        if (0 >= annualLeaves)
            throw new InvalidDataException("To proceed, provide valid number of annual leaves for the employee.");

        if(30 < annualLeaves)
            throw new InvalidDataException("Kindly be aware that the annual leave quota for employees should not surpass 30 days.");

        // Validate that IN-time and OUT-time are not the same and IN-time is before OUT-time
        if (inTime.equals(outTime))
            throw new InvalidDataException("IN-time and OUT-time cannot be same.");
        if (!inTime.isBefore(outTime))
            throw new InvalidDataException("IN-time cannot be after OUT-time.");

        // Validate working days
        if (textHelper.isBlank(request.getDays()))
            throw new InvalidDataException("Days are not provided");

        List<DayOfWeek> daysOfWeek = request.getDays()
                .stream()
                .map(dayString -> textHelper.isBlank(dayString) ? null : textHelper.stringToEnum(DayOfWeek.class, dayString.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (textHelper.isBlank(daysOfWeek))
            throw new InvalidDataException("DAYS cannot be blank, choose at least one working day.");

        // Calculate the duration between the in and out time
        Duration duration = Duration.between(inTime, outTime);

        // Convert duration to minutes
        long durationInMinutes = duration.toMinutes();

        if(60 > durationInMinutes)
            throw new InvalidDataException("The minimum permitted working hours for an employee is 60 minutes.");

        if(900 < durationInMinutes)
            throw new InvalidDataException("The maximum permitted working hours for an employee is 900 minutes");

        // Retrieve the employee details to update
        EmployeeModel employeeToUpdate = userToUpdate.getEmployeeDetails();

        employeeToUpdate.setInTime(inTime);
        employeeToUpdate.setOutTime(outTime);
        employeeToUpdate.setAnnualLeaves(annualLeaves);
        employeeToUpdate.setLastModifiedBy(loggedInUser);
        employeeToUpdate.setSunday(daysOfWeek.contains(DayOfWeek.SUNDAY));
        employeeToUpdate.setMonday(daysOfWeek.contains(DayOfWeek.MONDAY));
        employeeToUpdate.setTuesday(daysOfWeek.contains(DayOfWeek.TUESDAY));
        employeeToUpdate.setWednesday(daysOfWeek.contains(DayOfWeek.WEDNESDAY));
        employeeToUpdate.setThursday(daysOfWeek.contains(DayOfWeek.THURSDAY));
        employeeToUpdate.setFriday(daysOfWeek.contains(DayOfWeek.FRIDAY));
        employeeToUpdate.setSaturday(daysOfWeek.contains(DayOfWeek.SATURDAY));
//        employeeRepository.save(employeeToUpdate);

        // Save the updated user
        userToUpdate.setEmployeeDetails(employeeToUpdate);
        userToUpdate.setLastModifiedBy(loggedInUser);
        userRepository.saveAndFlush(userToUpdate);

        // Execute stored procedures to update weekly off leaves
        try {

            leaveRepository.sp_delete_weekly_off_leaves(
                    userToUpdate.getUserPid(),
                    LocalDate.now().minusDays(1),
                    null
            );

            leaveRepository.sp_insert_weekly_off_leaves(
                    userToUpdate.getUserPid(),
                    LocalDate.now().plusDays(1),
                    null
            );
        } catch (Exception exception) {
            throw new InvalidDataException(exception.getMessage());
        }

        // Return APIResponse indicating success
        return APIResponse.builder()
                .message("Schedule updated successfully for employee with ID ("+request.getReq_id()+")")
                .build();
    }




    /**
     * Updates the access status of an employee.
     *
     * @param validationResponse  The validation response containing token information.
     * @param userUid             The unique identifier of the user whose access is to be updated.
     * @param isEnabled           A boolean indicating whether to enable or disable the user's access.
     * @return APIResponse indicating the success of the operation.
     * @throws UnauthorizedException if the user attempts to modify their own access.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> updateEmployeeAccess(
            TokenValidationResponse validationResponse,
            String userUid,
            boolean isEnabled
    ) {
        // Check if the user is attempting to modify their own access
        if (validationResponse.getUid().equals(userUid))
            throw new UnauthorizedException();

        // Retrieve the user model based on the provided user UID
        UserModel userModel = getUserModelByUid(userUid);
        // Update the access status of the user
        userModel.setEnabled(isEnabled);
        userRepository.save(userModel);

        // Return APIResponse indicating success with appropriate message
        return new APIResponse<>(
                null,
                "We've " + (isEnabled ? "granted" : "revoked") + " access of the employee successfully.",
                null
        );
    }





    /**
     * Updates the status of a leave request.
     *
     * @param validationResponse The validation response containing token information.
     * @param leaveUid           The unique identifier of the leave request to be updated.
     * @param leaveStatus        The new status to be set for the leave request.
     * @return APIResponse indicating the success of the operation.
     * @throws InvalidDataException if the leave request is already in the requested state,
     *                              or if the leave status is invalid.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> updateLeaveStatus(
            TokenValidationResponse validationResponse,
            String leaveUid,
            LeaveStatus leaveStatus
    ) {
        // Retrieve the leave request model based on the provided leave UID
        LeaveRequestModel leaveRequestModel = getLeaveRequestModelByUid(leaveUid);
        // Check if the leave request is already in the requested state
        if (leaveStatus == leaveRequestModel.getStatus())
            throw new InvalidDataException("Leave request is already in the requested state: " + leaveStatus);

        // Convert start and end dates of leave request to formatted strings
        LocalDate startDate = instantUtils.toLocalDate(leaveRequestModel.getStartDate());
        LocalDate endDate = instantUtils.toLocalDate(leaveRequestModel.getEndDate());
        String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        String message = "Your leave for " + (startDate.isEqual(endDate) ? formattedStartDate : formattedStartDate + " - " + formattedEndDate);

        // Update leave request status based on the provided leave status
        switch (leaveStatus) {
            case WITHDREW:
                // Check if the leave request can be set to withdrew
                if (leaveRequestModel.getStatus() == LeaveStatus.APPROVED)
                    throw new InvalidDataException("Once approved leave request cannot be set to withdrew.");

                if (leaveRequestModel.getStatus() == LeaveStatus.DECLINED)
                    throw new InvalidDataException("Once declined leave request cannot be set to withdrew.");

                UserModel appliedBy = leaveRequestModel.getAppliedBy();
                if (!validationResponse.getPid().equals(appliedBy.getUserPid()))
                    throw new InvalidDataException("Withdrawal of a leave request is permissible only by the individual who submitted the request.");

                leaveRequestModel.setHandledBy(null);
                notificationLogger(entitlementRepository
                                .findByUserUidIsNotNullAndEntitlementsSubName(View.LEAVE_REQUESTS)
                                .stream()
                                .map(EntitlementModel::getUserUid)
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toList()),
                        leaveRequestModel.getLeaveRequestUid(),
                        View.LEAVE_REQUESTS,
                        textHelper.buildFullName(appliedBy.getFirstname(), appliedBy.getMiddlename(), appliedBy.getLastname()) + " has withdrawn the leave request."
                );

                break;

            case DECLINED:
                // Check if the leave request can be set to declined
                if (leaveRequestModel.getStatus() == LeaveStatus.APPROVED)
                    throw new InvalidDataException("Once approved leave request cannot be set to declined.");

                // Log notification for the declined leave request
                notificationLogger(
                        List.of(leaveRequestModel.getAppliedBy().getUserUid()),
                        leaveRequestModel.getLeaveRequestUid(),
                        View.EMP_LEAVES,
                        message + " has been declined."
                );
//                CompletableFuture
//                        .runAsync(() -> {
//                            notificationLogger(
//                                    List.of(leaveRequestModel.getAppliedBy().getUserUid()),
//                                    leaveRequestModel.getLeaveRequestUid(),
//                                    null,
//                                    message + " has been declined."
//                            );
//                            LoggerFactory.getLogger(EmployeeServiceImpl.class).info("Notification Sent By: " + Thread.currentThread().getName());
//                        });
                leaveRequestModel.setHandledBy(getUserModelByPid(validationResponse.getPid()));
                break;

            case APPROVED:
                // Generate instant ranges for the leave duration and create corresponding leave models
                List<InstantRange> instantRangeList = generateInstantRanges(leaveRequestModel.getStartDate(), leaveRequestModel.getEndDate());
                List<LeaveModel> leavesToBeAdded = new ArrayList<>();
                for (InstantRange instantRange : instantRangeList) {
                    leavesToBeAdded.add(
                            LeaveModel.builder()
                                    .startDate(instantRange.getStart())
                                    .endDate(instantRange.getEnd())
                                    .reason(leaveRequestModel.getReason())
                                    .appliedBy(leaveRequestModel.getAppliedBy())
                                    .type(leaveRequestModel.getType())
                                    .leaveRequest(leaveRequestModel)
                                    .build()
                    );
                }
                leaveRepository.saveAll(leavesToBeAdded);

                // Log notification for the approved leave request
                notificationLogger(
                        List.of(leaveRequestModel.getAppliedBy().getUserUid()),
                        leaveRequestModel.getLeaveRequestUid(),
                        View.EMP_LEAVES,
                        message + " has been approved."
                );
//                CompletableFuture
//                        .runAsync(() -> {
//                            notificationLogger(
//                                    List.of(leaveRequestModel.getAppliedBy().getUserUid()),
//                                    leaveRequestModel.getLeaveRequestUid(),
//                                    View.EMP_LEAVES,
//                                    message + " has been approved."
//                            );
//                            LoggerFactory.getLogger(EmployeeServiceImpl.class).info("Notification Sent By: " + Thread.currentThread().getName());
//                        });
                leaveRequestModel.setHandledBy(getUserModelByPid(validationResponse.getPid()));
                break;

            default:
                throw new InvalidDataException("Leave status cannot be " + leaveStatus + ".");
        }

        // Set the new status for the leave request and save it
        leaveRequestModel.setStatus(leaveStatus);
        leaveRequestRepository.save(leaveRequestModel);

        // Return APIResponse indicating success with appropriate message
        return new APIResponse<>(
                null,
                "We've changed leave request status to " + leaveRequestModel.getStatus() + " successfully.",
                null
        );
    }





    /**
     * Updates the password of an employee.
     *
     * @param validationResponse    The validation response containing token information.
     * @param updatePasswordRequest The request containing the current and new passwords.
     * @return APIResponse indicating the success of the operation.
     * @throws InvalidDataException if the new password is the same as the current one
     *                              or if the provided current password does not match the user's actual password.
     */
    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> updateEmployeePassword(
            TokenValidationResponse validationResponse,
            UpdatePasswordRequest updatePasswordRequest
    ){
        // Extract the user ID from the token validation response
        String tokenUserId = validationResponse.getUid();
        // Sanitize the update password request
        UpdatePasswordRequest request = sanitizer.sanitize(updatePasswordRequest);

        // Check if the new password is the same as the current one
        if (request.getCurrent_password().equals(request.getNew_password()))
            throw new InvalidDataException("Hey!! your new password can't be the same as the current one.");

        // Retrieve the UserModel based on the user ID extracted from the JWT token.
        UserModel userModel = getUserModelByUid(tokenUserId);
        // Check if the provided current password matches the user's actual password
        if (!passwordEncoder.matches(request.getCurrent_password(), userModel.getPassword()))
            throw new InvalidDataException("The provided password for the user does not match.");

        userModel.setTokenAt(Instant.now());
        // Encode and set the new password for the user
        userModel.setPassword(passwordEncoder.encode(request.getNew_password()));
        userRepository.save(userModel);

        // Return APIResponse indicating success with appropriate message
        return new APIResponse<>(
                null,
                "We've changed the employee password successfully.",
                null
        );

    }








//    DELETE

    /**
     * Deletes a specific document associated with an employee request.
     *
     * @param uid The validation response containing token information.
     * @param requestUid         The unique identifier of the request associated with the document.
     * @param documentType       The type of document to be deleted.
     * @return APIResponse indicating the success of the operation.
     * @throws UnauthorizedException     if the user does not have the necessary permissions to perform the operation.
     * @throws ResourceNotFoundException if the requested document or document type does not exist.
     */
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> deleteEmployeeRequestDocumentById(
            boolean isAdmin,
            String uid,
            String requestUid,
            DocumentType documentType
    ) {
        // Check if the user has the necessary permissions to perform the operation
        if (!isAdmin && !uid.equals(requestUid))
            throw new UnauthorizedException();

        // Exception message for resource not found scenarios
        String exceptionString = "No such document can be found.";
        // Check if the document type is null
        if (null == documentType)
            throw new ResourceNotFoundException(exceptionString);

        // Retrieve the request model based on the provided request UID
        RequestModel requestModel = getEmployeeRequestModelByUid(requestUid);
        // Initialize the fileDataModel to null
        FileDataModel fileDataModel = null;
        // Determine the document type and set the fileDataModel accordingly
        switch (documentType) {
            case DOCUMENT_IDENTITY_PROOF:
                fileDataModel = requestModel.getIdentityDocument();
                requestModel.setIdentityDocument(null);
                break;
            case DOCUMENT_ADDRESS_PROOF:
                fileDataModel = requestModel.getAddressDocument();
                requestModel.setAddressDocument(null);
                break;
            case DOCUMENT_OFFER_LETTER:
                fileDataModel = requestModel.getOfferLetterDocument();
                requestModel.setOfferLetterDocument(null);
                break;
            case DOCUMENT_EDUCATION:
                fileDataModel = requestModel.getEducationDocument();
                requestModel.setEducationDocument(null);
                break;
            case DOCUMENT_EXPERIENCE_LETTER:
                fileDataModel = requestModel.getExperienceDocument();
                requestModel.setExperienceDocument(null);
                break;
            case DOCUMENT_SALARY_SLIP:
                fileDataModel = requestModel.getSalarySlipDocument();
                requestModel.setSalarySlipDocument(null);
                break;
        }
        // Check if the fileDataModel is null
        if (null == fileDataModel)
            throw new ResourceNotFoundException(exceptionString);

        // Delete the document file locally
        deleteFileLocally(fileDataModel.getFilePath());

        try {
            // Save the updated request model and delete the file data model
            requestRepository.save(requestModel);
            fileDataRepository.delete(fileDataModel);
        } catch (Exception e) {
            throw new ResourceNotFoundException(exceptionString);
        }

        // Return APIResponse indicating success with a message
        return APIResponse.builder()
                .message("The requested document (" + fileDataModel.getFileOriginal() + ") has been successfully deleted.")
                .build();
    }




    /**
     * Generates a unique UID based on the current date-time and a random UUID suffix.
     * The method ensures that the generated UID does not already exist in the database.
     * @return A unique UID.
     * @throws InvalidDataException If the method fails to generate a unique UID after 100 attempts.
     */
    public String generateToDoUid() {
        // Generate the prefix using the current date-time in a specific format
        String prefix = "TD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        String toDoUid;
        boolean existsInDatabase;
        int attempts = 0;
        do {
            // Generate a random UUID suffix and combine it with the prefix to create the UID
            String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            toDoUid = prefix + uuid;
            // Check if the generated UID already exists in the database
            existsInDatabase = toDoRepository.existsByToDoUid(toDoUid);
            attempts++;
        } while (existsInDatabase && attempts <= 100);

        // If the generated UID still exists after 100 attempts, throw an exception
        if (existsInDatabase) {
            throw new InvalidDataException("Failed to execute current task.Try again");
        }

        return toDoUid;
    }






    /**
     * Retrieves the skill set information for the logged-in user based on the provided authorization token.
     * This method fetches the skill set details including course information, skill status, certificate details (if available),
     * attempts made, and skill scores for each skill associated with the user.
     * @param validationResponse The token validation response obtained from validating the authorization token.
     * @return An APIResponse containing a list of TSkillSetResponse objects representing the skill set information.
     * @throws UnauthorizedException If the provided token validation response is null or lacks necessary data.
     */
    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<List<TSkillSetResponse>> getSkillSetByToken(TokenValidationResponse validationResponse) {
        // Validate the authorization token and its data
        if (null == validationResponse || null == validationResponse.getPid())
            throw new UnauthorizedException();

        // Retrieve the logged-in user based on the token
        UserModel loggedInUser = userRepository.findByUserPid(validationResponse.getPid());
        // Map the user's skills to TSkillSetResponse objects
        List<TSkillSetResponse> responses = loggedInUser
                .getSkills()
                .stream()
                .filter(Objects::nonNull)
                .map(mapping -> {
                    CourseModel model = mapping.getCourse();
                    List<UserSkillAttemptMapping> skillAttempts = mapping.getUserSkillAttempts();
                    CertificateModel certificate = mapping.getCertificate();
                    Float obtained = null;
                    Float total = null;

                    Optional<UserSkillAttemptMapping> clearedSkillAttempt = Optional.empty();
                    if(!skillAttempts.isEmpty()){
                        clearedSkillAttempt = skillAttempts.stream()
                                .filter(attempt -> FacultyExamStatus.CLEARED.equals(attempt.getExamStatus()))
                                .findFirst();
                    }

                    if (clearedSkillAttempt.isPresent()) {
                         ExamUserMapping examUserMapping = clearedSkillAttempt.get().getExamUserMapping();
                         ExamModel examModel = examUserMapping.getExam();
                         obtained = examUserMapping.getTotalMarks();
                         total = examModel.getTotalMarks();
                    }

                    return TSkillSetResponse
                            .builder()
                            .id(model.getCourseId())
                            .name(model.getCourseName())
                            .img(model.getCourseImageUrl())
                            .hex(model.getCourseColor())
                            .status(mapping.getStatus())
                            .certificate(
                                    null == certificate ? null :
                                    certificate.getCertificateUid()
                            )
                            .attempts(
                                    skillAttempts.isEmpty() ? 0 :
                                    skillAttempts.size()
                            )
                            .issued_at(
                                    null == certificate ? (clearedSkillAttempt.map(Auditable::getUpdatedAt).orElse(null))
                                    : certificate.getCreatedAt()
                            )
                            .obtained(obtained)
                            .total(total)
                            .build();
                })
                .collect(Collectors.toList());

        // Build and return the APIResponse containing the skill set information
        return APIResponse
                .<List<TSkillSetResponse>>builder()
                .data(responses)
                .build();
    }




    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> changeStatusOfToDo(
            TokenValidationResponse validationResponse,
            String toDoUid,
            ToDoStatus status
    ) {
        UserModel loggedInUser = getCurrentRequestUserModel(validationResponse);

        if(textHelper.isBlank(toDoUid))
            throw new InvalidDataException("Requested ID not found.");

        if(null == status)
            throw new InvalidDataException("To proceed, please provide with a status");

        ToDoModel toDoModel = toDoRepository.findByToDoUid(toDoUid);
        if(null == toDoModel)
            throw new InvalidDataException("Record not found for the provided ID.");

        if(!toDoModel.getCreatedBy().getUserPid().equals(loggedInUser.getUserPid()))
            throw new InvalidDataException("Access Denied: Insufficient Permissions to execute current task.");

        toDoModel.setStatus(status);
        toDoRepository.save(toDoModel);

        return APIResponse.builder()
                .message("Status updated successfully.")
                .build();
    }




    @Override
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED
    )
    public APIResponse<?> deleteToDoList(
            TokenValidationResponse validationResponse,
            String toDoUid
    ) {
        UserModel loggedInUser = getCurrentRequestUserModel(validationResponse);

        if(textHelper.isBlank(toDoUid))
            throw new InvalidDataException("Requested ID not found.");

        ToDoModel toDoModel = toDoRepository.findByToDoUid(toDoUid);
        if(null == toDoModel)
            throw new InvalidDataException("Record not found for the provided ID.");

        if(!toDoModel.getCreatedBy().getUserPid().equals(loggedInUser.getUserPid()))
            throw new InvalidDataException("Access Denied: Insufficient Permissions to execute current task.");

        toDoRepository.delete(toDoModel);

        return APIResponse.builder()
                .message("Deleted succesfully")
                .build();
    }

    @Override
    public APIResponse<?> getAllNotificationsByToken(TokenValidationResponse validationResponse, boolean seen, Pageable pageable) {
            // Call the method with the pageable object to get sorted results
            return APIResponse.builder()
                    .data(getAllNotificationsByPid(validationResponse.getPid(), seen, pageable))
                    .build();
    }


    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    private TNotificationPageDTO<TNotificationResponse> getAllNotificationsByPid(Long userPid, boolean seen,Pageable pageable) {
        // Fetch notifications sorted by createdAt (newest first)
        Page<NotificationUserMapping> notificationsPage = (seen)
                ? notificationUserMappingRepository.findByUserUserPid(userPid, pageable)
                : notificationUserMappingRepository.findByUserUserPidAndSeen(userPid, seen, pageable);
        // Convert the page content to your response object
        List<TNotificationResponse> responses = modelMapper.getListOfResponses(
                notificationsPage.getContent(),
                modelMapper::mapToTNotificationResponse
        );

        return TNotificationPageDTO.<TNotificationResponse>builder()
                .totalCount(notificationUserMappingRepository.countByUserUserPid(userPid))
                .unseenCount(notificationUserMappingRepository.countByUserUserPidAndSeenFalse(userPid))
                .content(responses)
                .build();
    }




}
