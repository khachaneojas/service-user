package com.sprk.service.user.util;

import com.sprk.service.user.dto.response.common.ExperienceResponse;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TextHelper {
    boolean isBlank(Collection<?> collection);
    boolean isBlank(String str);
    boolean isNonBlank(String str);
    String buildFullName(String... nameFields);
    String getFirstNLetters(String str, int length);
    String getLastNLetters(String str, int length);
    Set<String> parseStringSet(String stringSet);
    List<String> parseStringList(String stringList);
    String getElementsByStringList(String stringList);
    String parseJsonAddress(String jsonAddressString);
    String generateJsonAddress(String jsonAddressString, String... values);
    Set<ExperienceResponse> parseJsonExperience(String jsonExperienceString);
    <T extends Enum<T>> T stringToEnum(Class<T> enumClass, String str);
}
