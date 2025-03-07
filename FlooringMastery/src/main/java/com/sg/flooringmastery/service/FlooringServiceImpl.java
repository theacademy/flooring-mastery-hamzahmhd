package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.DataPersistenceException;
import com.sg.flooringmastery.dao.OrderDao;
import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.TaxDao;

import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sg.flooringmastery.view.InvalidUserInputException;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
public class FlooringServiceImpl implements FlooringService{

    private OrderDao orderDao;
    private ProductDao productDao;
    private TaxDao taxDao;

    @Autowired
    public FlooringServiceImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws DataPersistenceException {
        return orderDao.getAllOrdersByDate(date);
    }

    @Override
    public Order getOrderByNumber(LocalDate date, int orderNumber) throws DataPersistenceException {
        return orderDao.getByOrderAndDate(orderNumber, date);
    }

    @Override
    public Order createOrder(LocalDate date, String customerName, String state, String productType, BigDecimal area)
            throws DataPersistenceException, InvalidUserInputException {

        // Validate business rules
        validateOrderDate(date);
        validateCustomerName(customerName);
        Tax tax = validateState(state);
        Product product = validateProduct(productType);
        validateArea(area);

        // Generate new order and calculate costs
        Order newOrder = new Order();
        newOrder.setCustomerName(customerName);
        newOrder.setState(state);
        newOrder.setTaxRate(tax.getTaxRate());
        newOrder.setProductType(productType);
        newOrder.setArea(area);
        newOrder.setCostPerSquareFoot(product.getCostPerSquareFoot());
        newOrder.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());
        newOrder.setDate(date);

        // Calculate costs
        calculateCosts(newOrder);

        // return to after assign order number and save
        return newOrder;
    }

    @Override
    public void addOrder(Order order) throws DataPersistenceException {
        orderDao.addOrder(order, order.getDate());
    }

    @Override
    public Order updateOrder(Order existingOrder, String customerName, String state, String productType, BigDecimal area)
            throws DataPersistenceException, InvalidUserInputException {

        validateCustomerName(customerName);
        Tax tax = validateState(state);
        Product product = validateProduct(productType);
        validateArea(area);

        existingOrder.setCustomerName(customerName);
        existingOrder.setState(state);
        existingOrder.setTaxRate(tax.getTaxRate());
        existingOrder.setProductType(productType);
        existingOrder.setArea(area);
        existingOrder.setCostPerSquareFoot(product.getCostPerSquareFoot());
        existingOrder.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());

        calculateCosts(existingOrder);

        return existingOrder;
    }

    @Override
    public void saveUpdatedOrder(Order order) throws DataPersistenceException {
        orderDao.updateOrder(order.getOrderNumber(), order.getDate(), order);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws DataPersistenceException {
        return orderDao.removeOrder(orderNumber, date);
    }



    @Override
    public void exportAllData() throws DataPersistenceException {
        orderDao.exportAll();
    }


    // validation methods

    private void validateOrderDate(LocalDate date) throws InvalidUserInputException {
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidUserInputException("Order date must be in the future.");
        }
    }

    private void validateCustomerName(String name) throws InvalidUserInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserInputException("Customer name cannot be blank.");
        }
        if (!name.matches("[a-zA-Z0-9., ]+")) {
            throw new InvalidUserInputException("Invalid characters in customer name.");
        }
    }

    private Tax validateState(String state) throws InvalidUserInputException, DataPersistenceException {
        try {
            Tax tax = taxDao.getByStateAbbreviation(state);
            if (tax == null) {
                throw new InvalidUserInputException("We do not work in this state.");
            }
            return tax;
        } catch (DataPersistenceException e) {
            throw new InvalidUserInputException("We do not work in this state.");
        }
    }

    private Product validateProduct(String productType) throws InvalidUserInputException, DataPersistenceException {
        try {
            Product product = productDao.getByProductType(productType);
            if (product == null) {
                throw new InvalidUserInputException("Invalid product type.");
            }
            return product;
        } catch (DataPersistenceException e) {
            throw new InvalidUserInputException("Invalid product type.");
        }
    }

    private void validateArea(BigDecimal area) throws InvalidUserInputException {
        if (area.compareTo(BigDecimal.valueOf(100)) < 0) {
            throw new InvalidUserInputException("Minimum order size is 100 sq ft.");
        }
    }


    // cost calculation

    private void calculateCosts(Order order) {
        BigDecimal materialCost = order.getArea().multiply(order.getCostPerSquareFoot());
        BigDecimal laborCost = order.getArea().multiply(order.getLaborCostPerSquareFoot());

        BigDecimal taxRate = order.getTaxRate().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal tax = (materialCost.add(laborCost)).multiply(taxRate);
        BigDecimal total = materialCost.add(laborCost).add(tax);

        order.setMaterialCost(materialCost.setScale(2, RoundingMode.HALF_UP));
        order.setLaborCost(laborCost.setScale(2, RoundingMode.HALF_UP));
        order.setTax(tax.setScale(2, RoundingMode.HALF_UP));
        order.setTotal(total.setScale(2, RoundingMode.HALF_UP));
    }



}
