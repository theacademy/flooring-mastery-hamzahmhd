package dao;

import com.sg.flooringmastery.dao.TaxDao;
import com.sg.flooringmastery.model.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TaxDaoFileImplTest {

    private TaxDao testDao;
    private static final String TEST_FILE_PATH = Paths.get(System.getProperty("user.dir"), "Data", "TestTaxes.txt")
            .toAbsolutePath().toString();

    @BeforeEach
    void setUp() throws Exception {
        // Clear the test file and populate with sample tax data before each test
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write("State,StateName,TaxRate\n");
            writer.write("CA,California,25.00\n");
            writer.write("TX,Texas,8.25\n");
            writer.write("OH,Ohio,6.75\n");
        }
        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        testDao = appContext.getBean("taxDaoFile", TaxDao.class);
    }

    @Test
    void testGetTaxByStateSuccess() throws Exception {
        Tax tax = testDao.getByStateAbbreviation("CA");
        assertNotNull(tax, "Tax record for CA should exist.");
        assertEquals("California", tax.getStateName(), "State name should match.");
        assertEquals(new BigDecimal("25.00"), tax.getTaxRate(), "Tax rate should match.");
    }

    @Test
    void testGetTaxByStateInvalid() throws Exception {
        Tax tax = testDao.getByStateAbbreviation("NY");
        assertNull(tax, "Non-existent state should return null.");
    }

    @Test
    void testEmptyTaxFile() throws Exception {
        // Empty the file before running the test
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write("");
        }

        Tax tax = testDao.getByStateAbbreviation("CA");
        assertNull(tax, "Retrieving tax from an empty file should return null.");
    }
}
