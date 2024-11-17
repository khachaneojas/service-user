package com.sprk.service.user.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprk.service.user.dto.response.common.ExperienceResponse;
import com.sprk.service.user.enums.AddressField;
import com.sprk.commons.exception.InvalidDataException;
import com.sprk.service.user.util.TextHelper;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class TextWizard implements TextHelper {

    private final ObjectMapper objectMapper;
    @Autowired
    public TextWizard(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }






    public boolean isBlank(Collection<?> collection) {
        if (null == collection)
            return true;
        while(collection.remove(null));
        return collection.isEmpty();
    }

    public boolean isBlank(String str) {
        if (null == str || str.isEmpty())
            return true;

        int strLen = str.length();
        for (int i = 0; i < strLen; ++i) {
            if (!isAsciiWhitespace(str.charAt(i)))
                return false;
        }

        return true;
    }

    public boolean isNonBlank(String str) {
        return !isBlank(str);
    }






    @Override
    public <T extends Enum<T>> T stringToEnum(Class<T> enumClass, String str) {
        try {
            if (StringUtils.isBlank(str) || null == enumClass)
                return null;
            return Enum.valueOf(enumClass, str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }








    @Override
    public String buildFullName(String... nameFields) {
        if (null == nameFields || nameFields.length == 0)
            return null;

        return Arrays
                .stream(nameFields)
                .filter(str -> !isBlank(str))
                .collect(Collectors.joining(" "));
    }

//    private







    @Override
    public String getFirstNLetters(String str, int length) {
        if (isBlank(str) || length <= 0) return null;
        return str.substring(0, Math.min(str.length(), length));
    }


    @Override
    public String getLastNLetters(String str, int length) {
        if (isBlank(str) || length <= 0) return null;
        return str.substring(Math.max(0, str.length() - length));
    }








    @Override
    public Set<String> parseStringSet(String stringSet) {
        if (isBlank(stringSet))
            return Set.of();

        int startIndex = stringSet.indexOf('[');
        int endIndex = stringSet.lastIndexOf(']');

        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex)
            return Set.of();

        String finalString = stringSet.substring(startIndex + 1, endIndex);
        return Arrays
                .stream(finalString.split(","))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public List<String> parseStringList(String stringList) {
        if (isBlank(stringList))
            return List.of();

        return Arrays.stream(stringList.split(",\\s*"))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public String getElementsByStringList(String stringList) {
        if (isBlank(stringList))
            return null;

        int startIndex = stringList.indexOf('[');
        int endIndex = stringList.lastIndexOf(']');

        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex)
            return null;

        return stringList.substring(startIndex + 1, endIndex);
    }








    @Override
    public String parseJsonAddress(String jsonAddressString) {
        if (isBlank(jsonAddressString))
            return null;

        try {
            AddressField[] addressFields = AddressField.values();
            List<String> addressFieldsList = new ArrayList<>(addressFields.length);

            JSONObject jsonAddressObject = new JSONObject(jsonAddressString);
            for (AddressField field : addressFields) {
                String fieldValue = jsonAddressObject.optString(field.name(), null);
                if (!isBlank(fieldValue)) {
                    addressFieldsList.add(fieldValue);
                }
            }

            return addressFieldsList.isEmpty() ? null : String.join(", ", addressFieldsList);
        } catch (JSONException e) {
            return null;
        }
    }








    @Override
    public String generateJsonAddress(String jsonAddressString, String... values) {
        AddressField[] addressFields = AddressField.values();
        if (values.length < addressFields.length)
            throw new InvalidDataException("The provided values array does not match the expected number of fields. Please ensure that the values array has the correct number of elements corresponding to the address fields.");

        JSONObject result = new JSONObject();
        try {
            JSONObject jsonAddressObject = isBlank(jsonAddressString) ? new JSONObject() : new JSONObject(jsonAddressString);

            for (int i = 0; i < addressFields.length; i++) {
                String key = addressFields[i].name();
                String value = values[i];
                if (!isBlank(value))
                    result.put(key, value);
                else if (jsonAddressObject.has(key))
                    result.put(key, jsonAddressObject.getString(key));
            }
        } catch (JSONException e) {
            return null;
        }

        return result.toString();
    }








    @Override
    public Set<ExperienceResponse> parseJsonExperience(String jsonExperienceString) {
        try {
            return objectMapper.readValue(jsonExperienceString, new TypeReference<>() {});
        } catch (Exception exception) {
            return Collections.emptySet();
        }
    }








    private boolean isAsciiWhitespace(char ch) {
        return ch == 32 || ch == 9 || ch == 10 || ch == 12 || ch == 13;
    }

}
