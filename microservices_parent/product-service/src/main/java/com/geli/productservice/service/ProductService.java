package com.geli.productservice.service;

import com.geli.productservice.dto.ProductRequest;
import com.geli.productservice.dto.ProductResponse;
import com.geli.productservice.model.Product;
import com.geli.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductService() {
    }


    public void createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setDescription(productRequest.getDescription());
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        productRepository.save(product);

    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
