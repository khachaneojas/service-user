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
public class CDateValidator extends JsonDeserializer<Instant> {
    private final InstantUtils instantUtils;

    @Autowired
    public CDateValidator(InstantUtils instantUtils) {
        this.instantUtils = instantUtils;
    }

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            String text = jsonParser.getText();
            return instantUtils.parseInstant(text, false);
        } catch (Exception exception) {
            throw new InvalidDataException("Invalid date format, Expected format: (yyyy-MM-dd).");
        }
    }
}