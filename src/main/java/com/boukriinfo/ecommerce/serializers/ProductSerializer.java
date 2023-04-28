package com.boukriinfo.ecommerce.serializers;



import com.boukriinfo.ecommerce.entities.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ProductSerializer implements Serializer<Product> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, Product product) {
        ObjectMapper mapper = new ObjectMapper();
        byte[] data = null;
        try {
            data = mapper.writeValueAsBytes(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void close() {}

}


