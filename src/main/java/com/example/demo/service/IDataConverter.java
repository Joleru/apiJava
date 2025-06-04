package com.example.demo.service;

public interface IDataConverter {
    <T> T convert(String json, Class<T> clazz);
}
