package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Tax;

import java.math.BigDecimal;

public class TaxDaoStubImpl implements TaxDao {

    private final Tax testTax;

    public TaxDaoStubImpl() {
        testTax = new Tax("CA");
        testTax.setStateName("California");
        testTax.setTaxRate(new BigDecimal("25.00"));
    }

    @Override
    public Tax getByStateAbbreviation(String stateAbbreviation) throws DataPersistenceException {
        if (testTax.getStateAbbreviation().equalsIgnoreCase(stateAbbreviation)) {
            return testTax;
        }
        throw new DataPersistenceException("State tax information not found.");
    }
}
