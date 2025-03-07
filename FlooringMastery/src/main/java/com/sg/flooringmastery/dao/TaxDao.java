package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Tax;

public interface TaxDao {

    Tax getByStateAbbreviation(String stateAbbrevation) throws DataPersistenceException;



}
