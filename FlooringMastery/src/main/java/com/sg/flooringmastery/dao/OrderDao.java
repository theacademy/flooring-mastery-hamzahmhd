package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderDao {

    Order addOrder(Order order, LocalDate date) throws DataPersistenceException;
    List<Order> getAllOrdersByDate(LocalDate date) throws DataPersistenceException;
    Order removeOrder(int orderNumber, LocalDate date) throws DataPersistenceException;
    Order updateOrder(int orderNumber, LocalDate date, Order order) throws DataPersistenceException;
    Order getByOrderAndDate(int orderNumber, LocalDate date) throws DataPersistenceException;
    void exportAll() throws DataPersistenceException;


}
