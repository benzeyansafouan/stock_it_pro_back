package com.pedramero.sms.pmsms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryDto {
    String id;
    String name;
    String description;
    byte[] categoryImage;
    String categoryParentId;
    Integer ordinalNumber;
}
