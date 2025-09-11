package com.neasaa.familytree.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == 0 || dotIndex == fileName.length() - 1) {
            // No dot, or dot is the first/last character
            return null;
        }

        return fileName.substring(dotIndex + 1);
    }

    public static long getFileSizeInKB (String filepath) throws IOException {
        Path path = Paths.get(filepath);
        long sizeInBytes = Files.size(path);
        return sizeInBytes / 1024;
    }
}
