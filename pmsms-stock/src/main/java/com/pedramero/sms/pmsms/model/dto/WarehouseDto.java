package com.pedramero.sms.pmsms.model.dto;

import com.pedramero.sms.pmsms.model.Address;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WarehouseDto {

    String id;
    String name;
    String note;
    List<AreaDto> areas;
    Address address;
}