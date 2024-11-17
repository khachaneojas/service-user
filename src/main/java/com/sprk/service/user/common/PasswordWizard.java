package com.sprk.service.user.common;

import com.sprk.service.user.enums.PasswordGenerationStrategy;
import com.sprk.service.user.util.PasswordGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;



@Component
public class PasswordWizard implements PasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";

    private boolean useLower;
    private boolean useUpper;
    private boolean useDigits;
    private boolean useSpecialCharacters;
    private PasswordGenerationStrategy strategy;



    @Override
    public PasswordGenerator useLower(boolean useLower) {
        this.useLower = useLower;
        return this;
    }

    @Override
    public PasswordGenerator useUpper(boolean useUpper) {
        this.useUpper = useUpper;
        return this;
    }

    @Override
    public PasswordGenerator useDigits(boolean useDigits) {
        this.useDigits = useDigits;
        return this;
    }

    @Override
    public PasswordGenerator useSpecialCharacters(boolean useSpecialCharacters) {
        this.useSpecialCharacters = useSpecialCharacters;
        return this;
    }

    @Override
    public PasswordGenerator strategy(PasswordGenerationStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public String generate(int length) {
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        List<String> charCategories = new ArrayList<>(4);
        if (useLower)
            charCategories.add(LOWERCASE);
        if (useUpper)
            charCategories.add(UPPERCASE);
        if (useDigits)
            charCategories.add(DIGITS);
        if (useSpecialCharacters)
            charCategories.add(SPECIAL_CHARACTERS);
        if (length <= 4)
            length = 10;

        if (null == strategy || charCategories.isEmpty()) {
            strategy = PasswordGenerationStrategy.EASY_TO_READ;
            charCategories.add(LOWERCASE);
            charCategories.add(UPPERCASE);
            charCategories.add(DIGITS);
        }

        switch (strategy) {
            case EASY_TO_SAY:
                charCategories.remove(DIGITS);
                charCategories.remove(SPECIAL_CHARACTERS);
                break;
            case EASY_TO_READ:
                charCategories.remove(SPECIAL_CHARACTERS);
                break;
            case ALL_CHARACTERS:
                break;
        }

        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            password.append(charCategory.charAt(position));
        }

        return new String(password);
    }

}
