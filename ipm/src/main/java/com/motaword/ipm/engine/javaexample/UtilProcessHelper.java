package com.motaword.ipm.engine.javaexample;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class UtilProcessHelper {

    public static String mapToJson(java.util.Map<String, String> map) throws IOException {
        return new ObjectMapper().writeValueAsString(map);
    }
}
