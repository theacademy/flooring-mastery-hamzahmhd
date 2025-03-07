package com.sg.flooringmastery.model;

import java.math.BigDecimal;

public class Product {


    // data members
    private String productType;
    private BigDecimal costPerSquareFoot;
    private BigDecimal laborCostPerSquareFoot;

    // default constructor
    public Product(){}

    // single parameter constructor
    public Product(String productType){
        this.productType = productType;
    }

    // accessors and mutators
    public void setProductType(String productType){
        this.productType = productType;
    }

    public String getProductType(){
        return productType;
    }

    public void setCostPerSquareFoot(BigDecimal costPerSquareFoot){
        this.costPerSquareFoot = costPerSquareFoot;
    }

    public BigDecimal getCostPerSquareFoot(){
        return costPerSquareFoot;
    }

    public void setLaborCostPerSquareFoot(BigDecimal laborCostPerSquareFoot){
        this.laborCostPerSquareFoot = laborCostPerSquareFoot;
    }

    public BigDecimal getLaborCostPerSquareFoot(){
        return laborCostPerSquareFoot;
    }



}
