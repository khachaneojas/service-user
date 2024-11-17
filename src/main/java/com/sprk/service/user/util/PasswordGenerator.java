package com.sprk.service.user.util;

import com.sprk.service.user.enums.PasswordGenerationStrategy;

public interface PasswordGenerator {
    PasswordGenerator useLower(boolean useLower);
    PasswordGenerator useUpper(boolean useUpper);
    PasswordGenerator useDigits(boolean useDigits);
    PasswordGenerator useSpecialCharacters(boolean useSpecialCharacters);
    PasswordGenerator strategy(PasswordGenerationStrategy strategy);
    String generate(int length);
}
