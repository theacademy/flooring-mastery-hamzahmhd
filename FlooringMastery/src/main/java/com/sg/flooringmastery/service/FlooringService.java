package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.DataPersistenceException;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.view.InvalidUserInputException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FlooringService {

    List<Order> getOrdersByDate(LocalDate date) throws DataPersistenceException;

    Order getOrderByNumber(LocalDate date, int orderNumber) throws DataPersistenceException;

    Order createOrder(LocalDate date, String customerName, String state, String productType, BigDecimal area)
            throws DataPersistenceException, InvalidUserInputException;

    void addOrder(Order order) throws DataPersistenceException;

    Order updateOrder(Order existingOrder, String customerName, String state, String productType, BigDecimal area)
            throws DataPersistenceException, InvalidUserInputException;

    void saveUpdatedOrder(Order order) throws DataPersistenceException;

    Order removeOrder(LocalDate date, int orderNumber) throws DataPersistenceException;

    void exportAllData() throws DataPersistenceException;
}
