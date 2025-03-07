package service;

import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.service.FlooringServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sg.flooringmastery.view.InvalidUserInputException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlooringServiceImplTest {

    private FlooringServiceImpl service;

    private final LocalDate TEST_DATE = LocalDate.of(2025, 3, 28);

    @BeforeEach
    void setUp() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        service = appContext.getBean("flooringService", FlooringServiceImpl.class);
    }

    @Test
    void testCreateOrderSuccess() throws Exception {
        Order order = service.createOrder(TEST_DATE, "Test Customer", "CA", "Tile", new BigDecimal("200.00"));

        assertNotNull(order, "Order should be created.");
        assertEquals("Test Customer", order.getCustomerName());
        assertEquals("CA", order.getState());
        assertEquals("Tile", order.getProductType());
        assertEquals(new BigDecimal("200.00"), order.getArea());
        assertEquals(new BigDecimal("3.50"), order.getCostPerSquareFoot());
        assertEquals(new BigDecimal("4.00"), order.getLaborCostPerSquareFoot());

        BigDecimal expectedMaterialCost = new BigDecimal("200.00").multiply(new BigDecimal("3.50"));
        BigDecimal expectedLaborCost = new BigDecimal("200.00").multiply(new BigDecimal("4.00"));
        BigDecimal expectedTax = (expectedMaterialCost.add(expectedLaborCost)).multiply(new BigDecimal("0.25"));
        BigDecimal expectedTotal = expectedMaterialCost.add(expectedLaborCost).add(expectedTax);

        assertEquals(expectedMaterialCost.setScale(2), order.getMaterialCost());
        assertEquals(expectedLaborCost.setScale(2), order.getLaborCost());
        assertEquals(expectedTax.setScale(2), order.getTax());
        assertEquals(expectedTotal.setScale(2), order.getTotal());
    }

    @Test
    void testCreateOrderInvalidState() {
        InvalidUserInputException exception = assertThrows(InvalidUserInputException.class, () ->
                service.createOrder(TEST_DATE, "Customer", "XX", "Tile", new BigDecimal("200.00"))
        );

        assertEquals("We do not work in this state.", exception.getMessage());
    }

    @Test
    void testCreateOrderInvalidProduct() {
        InvalidUserInputException exception = assertThrows(InvalidUserInputException.class, () ->
                service.createOrder(TEST_DATE, "Customer", "CA", "InvalidProduct", new BigDecimal("200.00"))
        );

        assertEquals("Invalid product type.", exception.getMessage());
    }

    @Test
    void testCreateOrderInvalidArea() {
        InvalidUserInputException exception = assertThrows(InvalidUserInputException.class, () ->
                service.createOrder(TEST_DATE, "Customer", "CA", "Tile", new BigDecimal("50.00")) // Below minimum area
        );

        assertEquals("Minimum order size is 100 sq ft.", exception.getMessage());
    }

    @Test
    void testGetOrdersByDate() throws Exception {
        List<Order> orders = service.getOrdersByDate(TEST_DATE);

        assertEquals(1, orders.size());
        assertEquals("Test Customer", orders.get(0).getCustomerName());
    }

    @Test
    void testRemoveOrder() throws Exception {
        // Create and add an order
        Order order = new Order();
        order.setOrderNumber(1);
        order.setCustomerName("Test Customer");
        order.setState("CA");
        order.setTaxRate(new BigDecimal("25.00"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("200.00"));
        service.addOrder(order);

        // Verify order exists before removal
        Order existingOrder = service.getOrderByNumber(TEST_DATE, 1);
        assertNotNull(existingOrder, "Order should exist before removal.");

        // Remove the order
        Order removedOrder = service.removeOrder(TEST_DATE, 1);
        assertNotNull(removedOrder, "Removing an existing order should not return null.");
        assertEquals(1, removedOrder.getOrderNumber(), "Removed order number should match.");

        // Verify order no longer exists in the system
        assertNull(service.getOrderByNumber(TEST_DATE, 1), "Order should be removed and return null.");
    }



    @Test
    void testUpdateOrderSuccess() throws Exception {
        Order existingOrder = service.getOrderByNumber(TEST_DATE, 1);
        assertNotNull(existingOrder);

        Order updatedOrder = service.updateOrder(existingOrder, "Bob", "CA", "Tile", new BigDecimal("300.00"));

        assertEquals("Bob", updatedOrder.getCustomerName());
        assertEquals(new BigDecimal("300.00"), updatedOrder.getArea());
    }

    @Test
    void testAddOrder() throws Exception {
        Order order = service.createOrder(TEST_DATE, "Alice", "CA", "Tile", new BigDecimal("150.00"));
        service.addOrder(order);

        Order retrievedOrder = service.getOrderByNumber(TEST_DATE, order.getOrderNumber());
        assertNotNull(retrievedOrder, "Order should be retrievable after being added.");
        assertEquals("Alice", retrievedOrder.getCustomerName());
    }


    @Test
    void testUpdateOrderInvalidData() throws Exception {
        Order existingOrder = service.getOrderByNumber(TEST_DATE, 1);
        assertNotNull(existingOrder);

        assertThrows(InvalidUserInputException.class, () ->
                        service.updateOrder(existingOrder, "", "CA", "Tile", new BigDecimal("300.00")),
                "Empty customer name should trigger an exception."
        );

        assertThrows(InvalidUserInputException.class, () ->
                        service.updateOrder(existingOrder, "Bob", "XX", "Tile", new BigDecimal("300.00")),
                "Invalid state should trigger an exception."
        );

        assertThrows(InvalidUserInputException.class, () ->
                        service.updateOrder(existingOrder, "Bob", "CA", "InvalidProduct", new BigDecimal("300.00")),
                "Invalid product should trigger an exception."
        );

        assertThrows(InvalidUserInputException.class, () ->
                        service.updateOrder(existingOrder, "Bob", "CA", "Tile", new BigDecimal("50.00")),
                "Below minimum area should trigger an exception."
        );
    }

    @Test
    void testRemoveNonExistentOrder() throws Exception {
        Order removedOrder = service.removeOrder(TEST_DATE, 9999);
        assertNull(removedOrder, "Removing a non-existent order should return null.");
    }

    @Test
    void testExportAllData() {
        assertDoesNotThrow(() -> service.exportAllData(), "Exporting data should not throw exceptions.");
    }



}
