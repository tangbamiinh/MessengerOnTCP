package hanu.npr.messengerserver.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class MyFileUtils {
    public static String convertFileToBase64String(String filePath) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static byte[] convertBase64StringToBytes(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }
}
