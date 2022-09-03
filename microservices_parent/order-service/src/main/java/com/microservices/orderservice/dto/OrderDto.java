package com.microservices.orderservice.dto;

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
