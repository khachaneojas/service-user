package com.sprk.service.user.util.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.sprk.commons.exception.InvalidDataException;
import com.sprk.service.user.util.InstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;





@Component
public class CDateTimeValidator extends JsonDeserializer<Instant> {
    private final InstantUtils instantUtils;

    @Autowired
    public CDateTimeValidator(InstantUtils instantUtils) {
        this.instantUtils = instantUtils;
    }

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            String text = jsonParser.getText();
            return instantUtils.parseInstant(text, true);
        } catch (Exception exception) {
            throw new InvalidDataException("Invalid date-time format, Expected format: (yyyy-MM-dd HH:mm:ss).");
        }
    }
}