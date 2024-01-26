package com.pedramero.sms.pmsms.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {

    String productId;
    String productName;
    String categoryId;
    BigDecimal quantity;
    String note;
}
