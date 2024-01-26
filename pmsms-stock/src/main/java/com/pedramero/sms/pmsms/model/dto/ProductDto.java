package com.pedramero.sms.pmsms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDto {

    String id;
    String categoryId;
    String name;
    String description;
    byte[] productImage;
    Integer ordinalNumber;
}
