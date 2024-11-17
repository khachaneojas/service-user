package com.sprk.service.user.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sprk.commons.exception.InvalidDataException;
import com.sprk.service.user.util.InstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;


@Component
public class CDateSerializer extends JsonSerializer<Instant> {

    private final InstantUtils instantUtils;

    @Autowired
    public CDateSerializer(InstantUtils instantUtils) {
        this.instantUtils = instantUtils;
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            gen.writeString(instantUtils.toString(value, false));
        } catch (Exception exception) {
            throw new InvalidDataException("Failed to serialize date. " + exception.getMessage());
        }
    }

}
