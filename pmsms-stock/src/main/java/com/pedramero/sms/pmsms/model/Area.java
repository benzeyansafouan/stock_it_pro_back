package com.pedramero.sms.pmsms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("Areas")
public class Area {

    @Id
    String id;
    String name;
    Integer ordinalNumber;
    String warehouseId;
}
