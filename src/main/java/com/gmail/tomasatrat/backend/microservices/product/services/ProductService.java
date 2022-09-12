package com.gmail.tomasatrat.backend.microservices.product.services;

import com.gmail.tomasatrat.backend.data.entity.Barcode;
import com.gmail.tomasatrat.backend.data.entity.Product;
import com.gmail.tomasatrat.backend.repositories.BarcodeRepository;
import com.gmail.tomasatrat.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private BarcodeRepository barcodeRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, BarcodeRepository barcodeRepository){
        this.productRepository = productRepository;
        this.barcodeRepository = barcodeRepository;
    }

    public List<Product> findAll(){
        return this.productRepository.findAll();
    }

    public List<Barcode> getBarcodesByProduct(Product product){
        return this.barcodeRepository.findByProductCode(product);
    }

}
