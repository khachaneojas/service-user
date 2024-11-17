package com.sprk.service.user.dto.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sprk.commons.document.dto.MainTab;
import com.sprk.service.user.util.deserializer.CDateValidator;
import com.sprk.service.user.util.deserializer.EMainTabValidator;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;




@Data
@AllArgsConstructor
@NoArgsConstructor
public class MEmployeeCreationRequest {

    @Pattern(regexp = "^[a-zA-Z '.]{2,40}$",
            message = "Firstname should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 40 characters.")
    private String firstname;

    @Pattern(regexp = "^[a-zA-Z '.]{2,40}$",
            message = "Lastname should be restricted to alphabetic characters (A-Z, a-z), and its length must be within the range of 2 to 40 characters.")
    private String lastname;

    @Pattern(regexp="^\\d{10}$", message = "Mobile number must be a 10-digit number.")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9._%+\\-!]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,100}$",
            message = "Email address you entered is not valid. only letters (a-z), numbers (0-9) and (-, ., !, +) are allowed.")
    private String email;

    private HashSet<String> authorities = new HashSet<>();

    @JsonDeserialize(using = CDateValidator.class)
    private Instant joined_at;

    @JsonDeserialize(using = EMainTabValidator.class)
    private HashSet<MainTab> entitlements;
}
