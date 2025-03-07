package dao;

import com.sg.flooringmastery.dao.OrderDao;
import com.sg.flooringmastery.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class OrderDaoFileImplTest {


    private OrderDao testDao;
    private static final String TEST_FILE_PATH = Paths.get(System.getProperty("user.dir"),  "Orders", "Orders_03282025.txt")
            .toAbsolutePath().toString();
    private static final LocalDate TEST_DATE = LocalDate.of(2025, 3, 28);


    @BeforeEach
    void setUp() throws Exception {
        // Clear the test file before each run
        new FileWriter(TEST_FILE_PATH);

        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        testDao = appContext.getBean("orderDaoFile", OrderDao.class);
    }

    @Test
    void testAddAndGetOrder() throws Exception {
        // Create test order with all necessary attributes
        Order order = new Order();
        order.setCustomerName("Test Customer");
        order.setState("CA");
        order.setTaxRate(new BigDecimal("25.00"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("249.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.00"));
        order.setMaterialCost(order.getArea().multiply(order.getCostPerSquareFoot()));
        order.setLaborCost(order.getArea().multiply(order.getLaborCostPerSquareFoot()));
        order.setTax(order.getMaterialCost().add(order.getLaborCost()).multiply(order.getTaxRate().divide(BigDecimal.valueOf(100))));
        order.setTotal(order.getMaterialCost().add(order.getLaborCost()).add(order.getTax()));

        // Add order
        testDao.addOrder(order, TEST_DATE);

        // Retrieve orders for this date
        List<Order> orders = testDao.getAllOrdersByDate(TEST_DATE);
        assertEquals(1, orders.size(), "There should be 1 order.");
        assertEquals("Test Customer", orders.get(0).getCustomerName(), "Customer name should match.");
    }

    @Test
    void testRemoveOrder() throws Exception {
        // Create test order
        Order order = new Order();
        order.setCustomerName("Remove Test");
        order.setState("CA");
        order.setTaxRate(new BigDecimal("25.00"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("200.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.00"));
        order.setMaterialCost(order.getArea().multiply(order.getCostPerSquareFoot()));
        order.setLaborCost(order.getArea().multiply(order.getLaborCostPerSquareFoot()));
        order.setTax(order.getMaterialCost().add(order.getLaborCost()).multiply(order.getTaxRate().divide(BigDecimal.valueOf(100))));
        order.setTotal(order.getMaterialCost().add(order.getLaborCost()).add(order.getTax()));

        testDao.addOrder(order, TEST_DATE);
        assertEquals(1, testDao.getAllOrdersByDate(TEST_DATE).size());

        // Remove order
        testDao.removeOrder(order.getOrderNumber(), TEST_DATE);
        assertEquals(0, testDao.getAllOrdersByDate(TEST_DATE).size(), "Order should be removed.");
    }

    @Test
    void testGetNonExistentOrder() throws Exception {
        Order order = testDao.getByOrderAndDate(9999, TEST_DATE);
        assertNull(order, "Should return null for a non-existent order.");
    }


    @Test
    void testRemoveNonExistentOrder() throws Exception {
        Order removedOrder = testDao.removeOrder(9999, TEST_DATE);
        assertNull(removedOrder, "Removing a non-existent order should return null.");
    }


    @Test
    void testUpdateOrder() throws Exception {
        // Create and add an order
        Order order = new Order();
        order.setCustomerName("Initial Name");
        order.setState("CA");
        order.setTaxRate(new BigDecimal("25.00"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("200.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.00"));
        order.setMaterialCost(order.getArea().multiply(order.getCostPerSquareFoot()));
        order.setLaborCost(order.getArea().multiply(order.getLaborCostPerSquareFoot()));
        order.setTax(order.getMaterialCost().add(order.getLaborCost()).multiply(order.getTaxRate().divide(BigDecimal.valueOf(100))));
        order.setTotal(order.getMaterialCost().add(order.getLaborCost()).add(order.getTax()));

        testDao.addOrder(order, TEST_DATE);

        // Modify the order
        order.setCustomerName("Updated Name");
        testDao.updateOrder(order.getOrderNumber(), TEST_DATE, order);

        // Fetch the updated order
        Order updatedOrder = testDao.getByOrderAndDate(order.getOrderNumber(), TEST_DATE);
        assertNotNull(updatedOrder, "Updated order should exist.");
        assertEquals("Updated Name", updatedOrder.getCustomerName(), "Customer name should be updated.");
    }


    @Test
    void testUpdateNonExistentOrder() throws Exception {
        Order nonExistentOrder = new Order();
        nonExistentOrder.setOrderNumber(9999);
        nonExistentOrder.setCustomerName("Fake Order");

        Order result = testDao.updateOrder(9999, TEST_DATE, nonExistentOrder);
        assertNull(result, "Updating a non-existent order should return null.");
    }






}