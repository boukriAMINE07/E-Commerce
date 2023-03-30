package com.boukriinfo.ecommerce.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data @ToString @Builder
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private String slug;
    private String image;
    private Date createdAt;
    private Date updatedAt;
    private boolean deleted;
    @ManyToOne
    private Category category;

    public boolean getDeleted() {
        return deleted;
    }


}
