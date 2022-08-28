package com.microservices.inventoryservice.service;


import com.microservices.inventoryservice.dto.InventoryResponse;
import com.microservices.inventoryservice.model.Inventory;
import com.microservices.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    private InventoryRepository inventoryRepository;

    public InventoryService() {
    }

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodeList) {
        List<InventoryResponse> inventoryResponseList = inventoryRepository.
                findBySkuCodeIn(skuCodeList)
                .stream()
                .map(inventory ->
                    InventoryResponse.builder().
                            skuCode(inventory.getSkuCode()).
                            isInStock(inventory.getQuantity() >0).build()
                ).toList();
        return inventoryResponseList;
//        return currentInventory;
    }


}
