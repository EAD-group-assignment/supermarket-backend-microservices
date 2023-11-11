package com.ead.productservice.service;

import com.ead.productservice.dto.ProductDTO;
import com.ead.productservice.model.Product;
import com.ead.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductDTO(product))
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.map(product1 -> new ProductDTO(product1)).orElse(null);
    }

    public List<ProductDTO> searchProducts(String productName) {
        List<Product> products = productRepository.findAllByNameContainingIgnoreCase(productName);
        return products.stream()
                .map(product -> new ProductDTO(product))
                .collect(Collectors.toList());
    }

    public Product saveProducts(Product product) {
        return productRepository.save(product);
    }

    public ProductDTO updateProduct(Long productId, Product updatedProduct) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            product.setDescription(updatedProduct.getDescription());
            product.setStockQuantity(updatedProduct.getStockQuantity());

            productRepository.save(product);
            return new ProductDTO(product);
        }
        return null;
    }

    public List<ProductDTO> deleteProduct(Long productId) {
        productRepository.deleteById(productId);
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductDTO(product))
                .collect(Collectors.toList());
    }

}