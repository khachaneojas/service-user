package com.sprk.service.user.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


@Component
public class FileUtils {


    public boolean ensureDirectoryExists(String directoryPath) {
        File file = new File(directoryPath);
        return createDirectoryIfNotExist(file);
    }
    public boolean ensureDirectoryExists(File file) {
        return createDirectoryIfNotExist(file);
    }
    public String getExtension(String fileName) throws IllegalArgumentException {
        if (StringUtils.isBlank(fileName))
            return null;

        int index = indexOfExtension(fileName);
        return index == -1
                ? null
                : fileName.substring(index + 1);
    }
    public boolean hasValidImageExtension(String fileName) {
        if (StringUtils.isBlank(fileName))
            return false;

        return hasValidFileExtension(
                fileName,
                new HashSet<>(Set.of("jpg", "jpeg", "png"))
        );
    }
    public boolean hasValidPdfExtension(String fileName) {
        if (StringUtils.isBlank(fileName))
            return false;

        return hasValidFileExtension(
                fileName,
                new HashSet<>(Set.of("pdf"))
        );
    }






    private boolean createDirectoryIfNotExist(File file) {
        return file.exists() || file.mkdirs();
    }

    private boolean hasValidFileExtension(String fileName, Set<String> allowedExtensions) {
        if (StringUtils.isBlank(fileName))
            return false;

        String fileExtension = getExtension(fileName);
        if (null == fileExtension)
            return false;

        return allowedExtensions.contains(fileExtension.toLowerCase());
    }

    private int indexOfExtension(String fileName) throws IllegalArgumentException {
        if (StringUtils.isBlank(fileName))
            return -1;

        int offset = fileName.lastIndexOf(46);
        int lastSeparator = indexOfLastSeparator(fileName);
        return lastSeparator > offset
                ? -1
                : offset;
    }

    private int indexOfLastSeparator(String fileName) {
        if (StringUtils.isBlank(fileName))
            return -1;

        int lastUnixPos = fileName.lastIndexOf(47);
        int lastWindowsPos = fileName.lastIndexOf(92);
        return Math.max(lastUnixPos, lastWindowsPos);
    }


}
