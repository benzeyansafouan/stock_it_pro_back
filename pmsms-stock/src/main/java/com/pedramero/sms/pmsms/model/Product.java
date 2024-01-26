package com.pedramero.sms.pmsms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("Products")
public class Product {

    @Id
    String id;
    String categoryId;
    String name;
    String description;
    String imageFileObjectId;
    Integer ordinalNumber;
}
