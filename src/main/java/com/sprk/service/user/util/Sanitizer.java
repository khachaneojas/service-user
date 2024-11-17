package com.sprk.service.user.util;

import com.sprk.commons.exception.InvalidDataException;
import org.apache.commons.lang.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;





@Component
public class Sanitizer {
    private final Map<Class<?>, Field[]> fieldsCache = new ConcurrentHashMap<>();
    private TextHelper textHelper;
    @Autowired
    public Sanitizer(TextHelper textHelper) {
        this.textHelper = textHelper;
    }



    public String sanitize(String str) {
        if (textHelper.isBlank(str))
            return null;

        return Jsoup.clean(
                str.trim(),
                Safelist.simpleText()
        );
    }



    public <T> T sanitize(T object) {
        Class<?> clazz = object.getClass();
        Field[] fields = fieldsCache.computeIfAbsent(clazz, Class::getDeclaredFields);

        for (Field field : fields) {
            field.setAccessible(true);

            try {
//                if (field.getType() == HashSet.class) {
//                    Object fieldValue = field.get(object);
//                    if (fieldValue instanceof Set) {
//                        Set<String> stringSet = sanitize((Set<?>) fieldValue);
//                        Set<String> stringSet = (Set<String>) fieldValue;
//                        Set<String> cleanedValue = sanitize(stringSet);
//                        field.set(object, cleanedValue);
//                    }
//                }

                if (field.getType() == String.class) {
                    String fieldValue = (String) field.get(object);
                    String cleanedValue = sanitize(fieldValue);
                    if (null != cleanedValue)
                        field.set(object, cleanedValue);
                }
            } catch (IllegalAccessException e) {
                throw new InvalidDataException("Failed to access field: " + field.getName() + "\n" + e.getMessage());
            }
        }

        return object;
    }



    public Set<String> sanitize(Set<String> set) {
        if (null == set)
            return Collections.emptySet();

        return set
                .stream()
                .filter(Objects::nonNull)
                .map(this::sanitize)
                .collect(Collectors.toSet());
    }
}
