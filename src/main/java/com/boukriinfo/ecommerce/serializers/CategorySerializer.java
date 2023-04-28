package com.boukriinfo.ecommerce.serializers;


import com.boukriinfo.ecommerce.entities.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class CategorySerializer implements Serializer<Category> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, Category data) {
        byte[] serializedData = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            serializedData = objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serializedData;
    }

    @Override
    public void close() {}

}

