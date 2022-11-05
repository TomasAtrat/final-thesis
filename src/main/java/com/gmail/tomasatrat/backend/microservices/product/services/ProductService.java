package com.gmail.tomasatrat.backend.microservices.product.services;

import com.gmail.tomasatrat.backend.data.entity.Barcode;
import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.data.entity.Product;
import com.gmail.tomasatrat.backend.data.entity.VTop20MostTestedProduct;
import com.gmail.tomasatrat.backend.repositories.BarcodeRepository;
import com.gmail.tomasatrat.backend.repositories.ProductRepository;
import com.gmail.tomasatrat.backend.repositories.VTop20MostTestedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private BarcodeRepository barcodeRepository;
    private VTop20MostTestedProductRepository vTop20MostTestedProductRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          BarcodeRepository barcodeRepository,
                          VTop20MostTestedProductRepository vTop20MostTestedProductRepository){
        this.productRepository = productRepository;
        this.barcodeRepository = barcodeRepository;
        this.vTop20MostTestedProductRepository = vTop20MostTestedProductRepository;
    }

    public List<Product> findAll(){
        return this.productRepository.findAll();
    }

    public List<Barcode> getBarcodesByProduct(Product product){
        return this.barcodeRepository.findByProductCode(product);
    }

    public Product findById(String id){
        return this.productRepository.findById(id).get();
    }

    public List<VTop20MostTestedProduct> getTop20MostTestedProductsByBranch(Branch branch) {
        return vTop20MostTestedProductRepository.findTop20ByBranchId(branch.getId());
    }
}
