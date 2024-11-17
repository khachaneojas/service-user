package com.sprk.service.user.util;

import java.time.Instant;



public interface UIDGenerator {
    String generateRequestId(Instant createdAt);
    String generateEmployeeId(Instant joinedAt, String name);
    String generateLeaveId(Instant createdAt);
    String generateEnquiryId(Instant createdAt);
    String generateStudentId(Instant createdAt);
    String generateBookingId(Instant createdAt);
    String generateTransactionId(Instant createdAt);
    String generateReceiptId(Instant createdAt);
    String generateUUID(int length);
}

