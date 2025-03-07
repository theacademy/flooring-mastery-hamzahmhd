package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoStubImpl implements ProductDao {

    private final Product testProduct;

    public ProductDaoStubImpl() {
        testProduct = new Product("Tile");
        testProduct.setCostPerSquareFoot(new BigDecimal("3.50"));
        testProduct.setLaborCostPerSquareFoot(new BigDecimal("4.00"));
    }

    @Override
    public List<Product> getAllProducts() throws DataPersistenceException {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        return products;
    }

    @Override
    public Product getByProductType(String productType) throws DataPersistenceException {
        if (testProduct.getProductType().equalsIgnoreCase(productType)) {
            return testProduct;
        }
        throw new DataPersistenceException("Product not found.");
    }
}

