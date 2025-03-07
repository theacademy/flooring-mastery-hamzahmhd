package com.sg.flooringmastery.model;

import java.math.BigDecimal;

public class Tax {

    // data members
    private String stateAbbreviation;
    private String stateName;
    private BigDecimal taxRate;


    // default constructor
    public Tax(){}

    // single-parameter constructor
    public Tax(String stateAbbreviation){
        this.stateAbbreviation = stateAbbreviation;
    }

    // accessors and mutators
    public void setStateAbbreviation(String stateAbbreviation){
        this.stateAbbreviation = stateAbbreviation;
    }

    public String getStateAbbreviation(){
        return stateAbbreviation;
    }

    public void setStateName(String stateName){
        this.stateName = stateName;
    }

    public String getStateName(){
        return stateName;
    }

    public void setTaxRate(BigDecimal taxRate){
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxRate(){
        return taxRate;
    }





}
