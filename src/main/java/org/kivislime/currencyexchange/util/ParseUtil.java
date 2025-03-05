package org.kivislime.currencyexchange.util;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ParseUtil {
    public static Map<String, String> parseBody(String input) {
        Map<String, String> parsedParams = new HashMap<>();
        String[] token = input.split("&");
        for (String pair : token) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                parsedParams.put(key, value);
            }
        }
        return parsedParams;
    }

    public static String requestBodyToString(HttpServletRequest request) {
        StringBuilder requestString = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestString.append(line);
            }
            return requestString.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
