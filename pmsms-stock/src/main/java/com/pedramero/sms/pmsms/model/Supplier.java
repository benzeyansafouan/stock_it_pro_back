package com.pedramero.sms.pmsms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("Suppliers")
public class Supplier {

    @Id
    String id;
    String name;
    String accountNumber;
    String email;
    String phoneNumber;
    String note;
    Address address;
}
