package com.cafe.management.utils;

import com.google.common.base.Strings;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.context.expression.MapAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j

public class CafeUtils {

    private CafeUtils(){
    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}",httpStatus);
    }

    public static String getUUID(){

        Date date = new Date();
        long time = date.getTime();
        return "BILL-"+time;

    }


    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data) {
        if (!Strings.isNullOrEmpty(data)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(data, new TypeReference<Map<String, Object>>(){});
            } catch (Exception e) {
                e.printStackTrace(); // Handle the exception or log it as needed
            }
        }
        return new HashMap<>();
    }
    public static Boolean isFileExist(String path){
        log.info("Inside isFileExist{}",path);
        try {
            File file= new File(path);
            return (file != null && file.exists()) ? Boolean.TRUE :Boolean.FALSE;

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

}


