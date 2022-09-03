package com.microservices.notificationservice;

import lombok.Data;

@Data
public class OrderDto {
    private String orderNumber;


    public OrderDto() {
    }

    public OrderDto(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
