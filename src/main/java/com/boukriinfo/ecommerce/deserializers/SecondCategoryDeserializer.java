package com.boukriinfo.ecommerce.deserializers;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.boukriinfo.ecommerce.entities2.Category;


public class SecondCategoryDeserializer extends JsonDeserializer<Category> {

    public SecondCategoryDeserializer() {
        super(Category.class);
    }
}

