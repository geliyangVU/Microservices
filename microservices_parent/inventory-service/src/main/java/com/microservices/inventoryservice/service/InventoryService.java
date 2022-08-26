package com.microservices.inventoryservice.service;


import com.microservices.inventoryservice.model.Inventory;
import com.microservices.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean isInStock(String skuCode) {
        Optional<Inventory> currentInventory = inventoryRepository.findBySkuCode(skuCode);
        return currentInventory.isPresent();
    }


}
