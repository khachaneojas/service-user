package com.sprk.service.user.util.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sprk.commons.document.dto.MainTab;
import com.sprk.commons.exception.InvalidDataException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class EMainTabValidator extends JsonDeserializer<Set<MainTab>> {

    private final ObjectMapper objectMapper;
    @Autowired
    public EMainTabValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Set<MainTab> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            return objectMapper.readValue(jsonParser, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidDataException(e.getMessage() + " - Invalid value provided, failed to process the request.");
        }
    }

}