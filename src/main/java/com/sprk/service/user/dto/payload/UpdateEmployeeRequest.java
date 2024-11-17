package com.sprk.service.user.dto.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sprk.service.user.util.deserializer.CDateValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeRequest {

    @Pattern(regexp = "^[a-zA-Z '.]{2,40}$",
            message = "Firstname should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 40 characters.")
    private String firstname;

    @Pattern(regexp = "^[a-zA-Z '.]{2,40}$",
            message = "Middlename should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 40 characters.")
    private String middlename;

    @Pattern(regexp = "^[a-zA-Z '.]{2,40}$",
            message = "Lastname should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 40 characters.")
    private String lastname;

    @JsonDeserialize(using = CDateValidator.class)
    private Instant joined_at;

    @JsonDeserialize(using = CDateValidator.class)
    private Instant birth_date;

    @Pattern(regexp="^\\d{10}$", message = "Mobile number must be a 10-digit number.")
    private String phone;

    @Pattern(regexp="^\\d{10}$", message = "Alternate mobile number must be a 10-digit number.")
    private String alt_phone;

    @Pattern(regexp = "^[a-zA-Z ]{2,20}$",
            message = "Gender should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 20 characters.")
    private String gender;

    @Pattern(regexp = "^[a-zA-Z0-9._%+\\-!]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,100}$",
            message = "Email address you entered is not valid. only letters (a-z), numbers (0-9) and (-, ., !, +) are allowed.")
    private String email;

    @Pattern(regexp = "^[a-zA-Z0-9._%+\\-!]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,100}$",
            message = "Alternate email address you entered is not valid. only letters (a-z), numbers (0-9) and (-, ., !, +) are allowed.")
    private String alt_email;

    @Pattern(regexp = "^[a-zA-Z +-]{2,20}$",
            message = "Blood group should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 20 characters.")
    private String blood_group;

    @Pattern(regexp = "^[a-zA-Z +-]{2,20}$",
            message = "Marital status should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 20 characters.")
    private String marital_status;

    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,100}$",
            message = "Nationality should be restricted to characters (A-Z, a-z) and within the range of 2 to 100 characters.")
    private String nationality;

    @Pattern(regexp = "^[a-zA-Z0-9, .+/()'-]{2,50}$",
            message = "Current flat/house should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 100 characters.")
    private String current_flat_house;

    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,50}$",
            message = "Current building/apartment should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 100 characters.")
    private String current_building_apartment;

    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,50}$",
            message = "Current area/street/sector/village name must be 2 to 50 characters long and can only include letters, numbers, spaces, and the following special characters: comma (,), period (.), plus (+), slash (/), ampersand (&), parentheses (()), and hyphen (-).")
    private String current_area_street_sector_village;

    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,50}$",
            message = "Current landmark name must be 2 to 50 characters long and can only include letters, numbers, spaces, and the following special characters: comma (,), period (.), plus (+), slash (/), ampersand (&), parentheses (()), and hyphen (-).")
    private String current_landmark;

    @Pattern(regexp = "^[a-zA-Z0-9 -]{6}$",
            message = "Current pincode must be restricted to characters (A-Z, a-z, 0-9) and within the range of 6 characters.")
    private String current_pin_code;

    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,50}$",
            message = "City/Town name must be 2 to 50 characters long and can only include letters, numbers, spaces, and the following special characters: comma (,), period (.), plus (+), slash (/), ampersand (&), parentheses (()), and hyphen (-).")
    private String current_city_town;

    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,50}$",
            message = "State name must be 2 to 50 characters long and can only include letters, numbers, spaces, and the following special characters: comma (,), period (.), plus (+), slash (/), ampersand (&), parentheses (()), and hyphen (-).")
    private String current_state;

    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,50}$",
            message = "Country name must be 2 to 50 characters long and can only include letters, numbers, spaces, and the following special characters: comma (,), period (.), plus (+), slash (/), ampersand (&), parentheses (()), and hyphen (-).")
    private String current_country;

    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,100}$",
            message = "Permanent flat/house name must be 2 to 50 characters long and can only include letters, numbers, spaces, and the following special characters: comma (,), period (.), plus (+), slash (/), ampersand (&), parentheses (()), and hyphen (-).")
    private String permanent_flat_house;

//    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,100}$",
//            message = "Permanent building/apartment should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 100 characters.")
    private String permanent_building_apartment;

//    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,100}$",
//            message = "Permanent area/street/sector/village should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 100 characters.")
    private String permanent_area_street_sector_village;

//    @Pattern(regexp = "^[a-zA-Z0-9, .+/&()'-]{2,100}$",
//            message = "Permanent landmark should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 100 characters.")
    private String permanent_landmark;

//    @Pattern(regexp = "^[a-zA-Z0-9 -]{6}$",
//            message = "Permanent pincode must should be restricted to characters (A-Z, a-z, 0-9) and within the range of 6 characters.")
    private String permanent_pin_code;

//    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,100}$",
//            message = "Permanent city/town should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 100 characters.")
    private String permanent_city_town;

//    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,100}$",
//            message = "Permanent state should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 100 characters.")
    private String permanent_state;

//    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,100}$",
//            message = "Permanent country should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 100 characters.")
    private String permanent_country;

    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9,-]{2,200}$",
            message = "University should be restricted to alphanumeric characters, ',' and '-' and within the range of 2 to 200 characters.")
    private String university;

    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,200}$",
            message = "Degree should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 200 characters.")
    private String degree;

    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,200}$",
            message = "Stream should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 200 characters.")
    private String stream;

    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,200}$",
            message = "College should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 200 characters.")
    private String college;

    @Pattern(regexp = "^[\\p{N}0-9 .%]{2,10}$",
            message = "Please enter your CGPA or percentage using only numbers, a decimal point (.) or a percentage sign (%). The input should be between 2 to 10 characters long.")
    private String cgpa;

    @Pattern(regexp="^\\d{4}$",
            message = "Must be 4 digit number indicating passing year.")
    private String passing_year;

    private Set<String> skill_set = new HashSet<>();
    private Set<String> authorities = new HashSet<>();
    private Set<ExperienceRequest> experience = new HashSet<>();

    @Pattern(regexp = "^[a-zA-Z '.]{2,100}$",
            message = "Emergency contact's name should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 100 characters.")
    private String eme_name;

    @Pattern(regexp="^\\d{10}$",
            message = "Emergency contact's mobile number must be a 10-digit number.")
    private String eme_phone;

    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,20}$",
            message = "Emergency contact's relationship should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 20 characters.")
    private String eme_relation;

    @Pattern(regexp = "^[a-zA-Z '.]{2,40}$",
            message = "Physician's name should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 40 characters.")
    private String physician_name;

    @Pattern(regexp="^\\d{10}$", message = "Physician's mobile number must be a 10-digit number.")
    private String physician_phone;

    @Pattern(regexp = "^.{2,500}$",
            message = "Medical conditions should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 250 characters.")
    private String medical_conditions;

}
