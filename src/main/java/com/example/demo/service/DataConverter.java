package com.example.demo.service;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataConverter implements IDataConverter {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T convert(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}