package com.pedramero.sms.pmsms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AreaDto {

    String id;
    String name;
    Integer ordinalNumber;
    String warehouseId;
}
