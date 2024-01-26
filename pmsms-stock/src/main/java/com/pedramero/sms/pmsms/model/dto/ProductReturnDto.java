package com.pedramero.sms.pmsms.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductReturnDto {

    String id;
    String productId;
    String productEntryId;
    String returnedBy;
    String warehouseId;
    LocalDateTime returnDate;
    BigDecimal returnedQuantity;
}
