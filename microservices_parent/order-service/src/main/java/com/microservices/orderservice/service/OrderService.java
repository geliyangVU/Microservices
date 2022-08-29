package com.microservices.orderservice.service;


import com.microservices.orderservice.dto.InventoryResponse;
import com.microservices.orderservice.dto.OrderLineItemsDto;
import com.microservices.orderservice.dto.OrderRequest;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.OrderLineItems;
import com.microservices.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
    }


    public void placeOrder(OrderRequest orderRequest){

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());


        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        System.out.println("orderLineItems printed below");
        System.out.println(orderLineItems);

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodeList = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();

        //Communicate with inventory service to check if product is in stock, place order if in stock.
        InventoryResponse[] inventoryInStockResultArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder ->
                        uriBuilder.queryParam("skuCodeList",skuCodeList).build())
                .retrieve().bodyToMono(InventoryResponse[].class).block();
        boolean allProductInStock = Stream.of(inventoryInStockResultArray).
                                    allMatch(InventoryResponse::isInStock);

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
