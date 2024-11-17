package com.sprk.service.user.constant;



public class PayloadMessage {
    public static final String BLANK_FIELD = " field is empty. Kindly enter a valid value to proceed.";
    public static final String BLANK_USERNAME_OR_EMAIL_LOGIN = "Provide either a username or an email to log back into your account.";
    public static final String BLANK_PASSWORD_LOGIN = "Provide your password to proceed.";
    public static final String BLANK_FIRST_NAME = "Provide your firstname to proceed.";
    public static final String BLANK_USERNAME_REGISTRATION = "Username missing. Provide a valid username to proceed.";
    public static final String BLANK_EMAIL_REGISTRATION = "Kindly input an email address. This field is required and cannot be left blank.";
    public static final String BLANK_PASSWORD_REGISTRATION = "Password field is empty. Kindly enter a strong password to proceed.";


    public static final String REGEX_PATTERN_AT_LEAST_ONE_UPPERCASE = ".*[A-Z].*";
    public static final String REGEX_MESSAGE_AT_LEAST_ONE_UPPERCASE = " must contain at least one uppercase letter (A-Z).";
    public static final String REGEX_PATTERN_AT_LEAST_ONE_LOWERCASE = ".*[a-z].*";
    public static final String REGEX_MESSAGE_AT_LEAST_ONE_LOWERCASE = " must contain at least one lowercase letter (a-z).";
    public static final String REGEX_PATTERN_AT_LEAST_ONE_DIGIT = ".*\\d.*";
    public static final String REGEX_MESSAGE_AT_LEAST_ONE_DIGIT = " must contain at least one digit (0-9).";
    public static final String REGEX_PATTERN_AT_LEAST_ONE_ALPHANUMERIC = ".*[@#!%^$&*+=].*";
    public static final String REGEX_MESSAGE_AT_LEAST_ONE_ALPHANUMERIC = " must contain at least one non-alphanumeric character (@, #, %, ^, !, $, &, +, =, *).";
    public static final String REGEX_PATTERN_SIX_CHARACTERS_LONG = ".{6,}$";
    public static final String REGEX_MESSAGE_SIX_CHARACTERS_LONG = " must be at least 6 characters long.";


    public static final String REGEX_PATTERN_NAMES = "^[a-zA-Z]{2,40}$";
    public static final String REGEX_MESSAGE_NAMES = " should be restricted to alphabetic characters (A-Z, a-z), without any whitespace, and its length must be within the range of 2 to 40 characters. Please review the input and try again.";
    public static final String REGEX_PATTERN_USERNAME = "^(?=.*[a-zA-Z])[a-zA-Z\\d]{3,20}$";
    public static final String REGEX_MESSAGE_USERNAME = "Username must be 3 to 20 characters long, containing only letters (A-Z, a-z) and digits (0-9). No spaces allowed, and there should be at least one letter in the username.";
    public static final String REGEX_PATTERN_EMAIL = "^[a-zA-Z0-9._%+\\-!]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
    public static final String REGEX_MESSAGE_EMAIL = "Oops! The email you entered is not valid. It should look like 'name@example.com'.";
    public static final String REGEX_PATTERN_ROLE = "^ROLE_[A-Z]{3,10}$";
    public static final String REGEX_MESSAGE_ROLE = "Role can only have uppercase letters (A-Z) and underscores (_), and it should be 3 to 12 characters long.";
}
