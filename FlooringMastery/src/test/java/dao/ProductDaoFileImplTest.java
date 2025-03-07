package dao;

import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoFileImplTest {

    private ProductDao testDao;
    private static final String TEST_FILE_PATH = Paths.get(System.getProperty("user.dir"), "Data", "TestProducts.txt")
            .toAbsolutePath().toString();

    @BeforeEach
    void setUp() throws Exception {
        // Clear the test file and populate with sample data before each test
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write("ProductType,CostPerSquareFoot,LaborCostPerSquareFoot\n");
            writer.write("Tile,3.50,4.00\n");
            writer.write("Wood,5.15,4.75\n");
            writer.write("Carpet,2.25,2.10\n");
        }
        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        testDao = appContext.getBean("productDaoFile", ProductDao.class);
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = testDao.getAllProducts();
        assertEquals(3, products.size(), "Should retrieve 3 products.");

        assertTrue(products.stream().anyMatch(p -> p.getProductType().equals("Tile")));
        assertTrue(products.stream().anyMatch(p -> p.getProductType().equals("Wood")));
        assertTrue(products.stream().anyMatch(p -> p.getProductType().equals("Carpet")));
    }

    @Test
    void testGetProductByTypeSuccess() throws Exception {
        Product tile = testDao.getByProductType("Tile");
        assertNotNull(tile, "Tile product should be found.");
        assertEquals(new BigDecimal("3.50"), tile.getCostPerSquareFoot(), "Tile cost per square foot should match.");
        assertEquals(new BigDecimal("4.00"), tile.getLaborCostPerSquareFoot(), "Tile labor cost per square foot should match.");
    }

    @Test
    void testGetProductByTypeInvalid() throws Exception {
        Product invalidProduct = testDao.getByProductType("Marble");
        assertNull(invalidProduct, "Non-existent product should return null.");
    }

    @Test
    void testEmptyProductFile() throws Exception {
        // Empty the file before running the test
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write("");
        }

        List<Product> products = testDao.getAllProducts();
        assertEquals(0, products.size(), "Should return an empty list when no products are available.");
    }
}
