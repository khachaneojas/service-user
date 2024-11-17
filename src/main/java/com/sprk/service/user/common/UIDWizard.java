package com.sprk.service.user.common;

import com.sprk.service.user.enums.Identifier;
import com.sprk.commons.exception.InvalidDataException;
import com.sprk.service.user.util.InstantUtils;
import com.sprk.service.user.util.TextHelper;
import com.sprk.service.user.util.UIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;


@Component
public class UIDWizard implements UIDGenerator {

    private final InstantUtils instantUtils;
    private final TextHelper textHelper;
    @Autowired
    public UIDWizard(
            InstantUtils instantUtils,
            TextHelper textHelper
    ) {
        this.instantUtils = instantUtils;
        this.textHelper = textHelper;
    }








    @Override
    public String generateEmployeeId(Instant joinedAt, String name) {
        String yearString = getYearString(joinedAt);
        String firstNLetters = textHelper.getFirstNLetters(name, 3);
        if (textHelper.isBlank(firstNLetters))
            throw new InvalidDataException("Providing a Firstname is mandatory.");

        int length = firstNLetters.length() < 3 ? ((3 - firstNLetters.length()) + 2) : 2;
        if (length % 2 != 0) length++;
        String employeeIdentifier = (Identifier.SPRK.name() + yearString + firstNLetters + generateUUID(length)).toUpperCase();
        return textHelper.getFirstNLetters(employeeIdentifier, 11);
    }

    @Override
    public String generateRequestId(Instant createdAt) {
        return Identifier.REQ.name() + getIdentityString(createdAt);
    }

    @Override
    public String generateLeaveId(Instant createdAt) {
        return Identifier.LVE.name() + getIdentityString(createdAt);
    }

    @Override
    public String generateEnquiryId(Instant createdAt) {
        return Identifier.ENQ.name() + getIdentityString(createdAt);
    }

    @Override
    public String generateStudentId(Instant createdAt) {
        return Identifier.STU.name() + getIdentityString(createdAt);
    }

    @Override
    public String generateBookingId(Instant createdAt) {
        return Identifier.BKG.name() + getIdentityString(createdAt);
    }

    @Override
    public String generateTransactionId(Instant createdAt) {
        return Identifier.TRN.name() + getIdentityString(createdAt);
    }

    @Override
    public String generateReceiptId(Instant createdAt) {
        return Identifier.REC.name() + getIdentityString(createdAt);
    }






    @Override
    public String generateUUID(int length) {
        if (length <= 0 || length % 2 != 0)
            throw new IllegalArgumentException("Length must be a positive even number");

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length / 2];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(length);
        for (byte b : bytes)
            sb.append(String.format("%02x", b));

        return sb.toString();
    }

    private String getYearString(Instant instant) {
        return instantUtils.toString(instant, "yy");
    }

    private String getIdentityString(Instant instant) {
        String yearString = getYearString(instant);
        return (yearString + generateUUID(6)).toUpperCase();
    }

}

