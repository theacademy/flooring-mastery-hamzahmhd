package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Product;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.*;

@Component
public class ProductDaoFileImpl implements ProductDao {

    private final String PRODUCT_FILEPATH;
    public static final String DELIMITER = ",";

    private Map<String, Product> productMap = new HashMap<>();

    public ProductDaoFileImpl(){
        this.PRODUCT_FILEPATH = Paths.get( "FlooringMastery", "Data", "Products.txt").toAbsolutePath().toString();
    }

    public ProductDaoFileImpl(String filePath){
        this.PRODUCT_FILEPATH = filePath;
    }

    private Product unmarshallProduct(String productAsText){
        String[] productTokens = productAsText.split(DELIMITER);
        String productType = productTokens[0];

        Product productFromFile = new Product(productType);

        productFromFile.setCostPerSquareFoot(new BigDecimal(productTokens[1]));
        productFromFile.setLaborCostPerSquareFoot(new BigDecimal(productTokens[2]));

        return productFromFile;
    }


    private void load() throws DataPersistenceException {
        Scanner sc;

        try{
            sc = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILEPATH)));
        }
        catch (FileNotFoundException e){
            throw new DataPersistenceException("Could not load product data into memory.", e);
        }

        if (sc.hasNextLine()){
            sc.nextLine(); // skip header row
        }

        while (sc.hasNextLine()){
            String currentLine = sc.nextLine();
            Product currentProduct = unmarshallProduct(currentLine);
            productMap.put(currentProduct.getProductType(), currentProduct);
        }
        sc.close();
    }



    @Override
    public List<Product> getAllProducts() throws DataPersistenceException {
        load();
        return new ArrayList<>(productMap.values());
    }

    @Override
    public Product getByProductType(String productType) throws DataPersistenceException {
        load();
        return productMap.get(productType);
    }
}
