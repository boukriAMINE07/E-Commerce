package com.boukriinfo.ecommerce.entities2;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotEmpty
    @Size(min = 4,max = 50)
    private String name;
    @NotEmpty
    private String description;
    @NotNull(message = "Le champ 'price' ne peut pas être nul.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le champ 'price' doit être un nombre positif.")
    private double price;
    @NotEmpty
    private String slug;
    private String image;
    private Date createdAt;
    private Date updatedAt;
    private boolean deleted;

    @ManyToOne
    private Category category;

    @Transient
    private String categoryName; // Ajout du champ categoryName
    public boolean getDeleted() {
        return deleted;
    }






}
