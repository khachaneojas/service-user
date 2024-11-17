package com.sprk.service.user.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprk.commons.exception.InvalidDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for converting JSON strings to Java objects and vice versa using Jackson's ObjectMapper.
 */
@Component
@RequiredArgsConstructor
public class JsonConverter {

    private final ObjectMapper objectMapper;
    private static final String ERROR_GENERIC_MESSAGE = "Oops! Something went wrong.";

    public <T> T convertToObject(String json, Class<T> targetClass) throws IOException {
        return objectMapper.readValue(json, targetClass);
    }

    public <T> List<T> convertToList(String json, Class<T> targetClass) throws IOException {
        // Define the TypeReference for the target class
        TypeReference<List<T>> typeReference = new TypeReference<>() {
            @Override
            public Type getType() {
                return objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass);
            }
        };

        // Deserialize the JSON string into a list of objects using the defined TypeReference
        return objectMapper.readValue(json, typeReference);
    }

    public <K, V> Map<K, V> convertToMap(String json, Class<K> keyClass, Class<V> valueClass) throws IOException {
        TypeReference<Map<K, V>> typeReference = new TypeReference<>() {
            @Override
            public Type getType() {
                return objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
            }
        };

        return objectMapper.readValue(json, typeReference);
    }

    public <T> String convertObjectToJsonString(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public <T> String convertListToJsonString(List<T> list) throws JsonProcessingException {
        return objectMapper.writeValueAsString(list);
    }

    public <T> String convertSetToJsonString(Set<T> set) throws JsonProcessingException {
        return objectMapper.writeValueAsString(set);
    }

    public <K, V> String convertMapToJsonString(Map<K, V> map) throws JsonProcessingException {
        return objectMapper.writeValueAsString(map);
    }


    public <T> List<T> getListFromJsonString(String jsonString, Class<T> clazz) {
        List<T> list;
        try {
            list = convertToList(jsonString, clazz);
        } catch (IOException e) {
            throw new InvalidDataException(ERROR_GENERIC_MESSAGE);
        }

        if (null == list) return List.of();
        else return list;
    }

    public <T> String getJsonStringFromList(List<T> list) {
        String str;
        try {
            str = convertListToJsonString(list);
        } catch (IOException e) {
            throw new InvalidDataException(ERROR_GENERIC_MESSAGE);
        }

        return str;
    }

    public <T> String getJsonStringFromSet(Set<T> list) {
        String str;
        try {
            str = convertSetToJsonString(list);
        } catch (IOException e) {
            throw new InvalidDataException(ERROR_GENERIC_MESSAGE);
        }

        return str;
    }

    public <T> String getJsonStringFromObject(T obj) {
        String str;
        try {
            str = convertObjectToJsonString(obj);
        } catch (IOException e) {
            throw new InvalidDataException(ERROR_GENERIC_MESSAGE);
        }

        return str;
    }

}
