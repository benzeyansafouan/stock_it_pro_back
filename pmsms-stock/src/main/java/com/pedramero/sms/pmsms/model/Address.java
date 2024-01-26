package com.pedramero.sms.pmsms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Address {

    String street;
    String city;
    String state;
    Integer postalCode;
    String country;
}
