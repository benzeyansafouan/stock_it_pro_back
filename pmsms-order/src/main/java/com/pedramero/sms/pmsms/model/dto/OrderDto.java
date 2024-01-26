package com.pedramero.sms.pmsms.model.dto;

import com.pedramero.sms.pmsms.model.OrderItem;
import com.pedramero.sms.pmsms.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDto {

    String id;
    Integer orderNumber;
    LocalDateTime creationDate;
    LocalDateTime confirmedDate;
    LocalDateTime deliveredDate;
    BigDecimal totalQuantity;
    OrderStatus orderStatus;
    List<OrderItem> items;
    String cancelledBy;
    String createdId;
    String confirmedBy;
    String deliveredBy;
}
