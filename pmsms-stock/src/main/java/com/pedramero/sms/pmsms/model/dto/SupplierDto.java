package com.pedramero.sms.pmsms.model.dto;

import com.pedramero.sms.pmsms.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupplierDto {

    String id;
    String name;
    String accountNumber;
    String email;
    String phoneNumber;
    String note;
    Address address;
}
