package com.microservices.orderservice.service;


import com.microservices.orderservice.dto.InventoryResponse;
import com.microservices.orderservice.dto.OrderLineItemsDto;
import com.microservices.orderservice.dto.OrderRequest;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.OrderLineItems;
import com.microservices.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public OrderService(OrderRepository orderRepository, WebClient webClient) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
    }

    public void placeOrder(OrderRequest orderRequest){
        System.out.println("orderrequest printed below");
        System.out.println(orderRequest);

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());


        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodeList = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();



        //Communicate with inventory service to check if product is in stock, place order if in stock.
        InventoryResponse[] inventoryInStockResultArray = webClient.get()
                .uri("http://localhost:8095/api/inventory", uriBuilder ->
                        uriBuilder.queryParam("skuCodeList",skuCodeList).build())
                .retrieve().bodyToMono(InventoryResponse[].class).block();

        boolean allProductInStock = Arrays.
                                    stream(inventoryInStockResultArray)
                                    .allMatch(inventoryResponse -> inventoryResponse.isInStock());

        if(allProductInStock){
            orderRepository.save(order);

        }else{
            throw new IllegalArgumentException("Product not in stock.");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }


}
