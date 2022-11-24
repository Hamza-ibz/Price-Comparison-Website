//package PriceChecker;

import Hamza.scrapper.business.AppConfig;
import Hamza.scrapper.business.Hibernate;
import Hamza.scrapper.business.Phones;
import Hamza.scrapper.business.WebScrapers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit testing.
 */
public class AppTest {

    WebScrapers webScrapers;
    static SessionFactory sessionFactory;
    static Session session;
    static int idPhone;
    static int idComparison;

    // Session Factory Test
    @Test
    @Order(1)
    @DisplayName("Session Factory Test")
    void sessionFactoryTest() {
        Hibernate hibernate = new Hibernate();
        AppConfig app = new AppConfig();

        try {
            hibernate.setSessionFactory(app.sessionFactory());
        } catch (Exception ex) {
            fail("Error: Can't Set Session Factory: " + ex.getMessage());
        }

        assertNotNull(hibernate.getSessionFactory());
        System.out.println("Test 1 has been completed");
    }

    //Scraping  Test
    @Test
    @Order(2)
    @DisplayName("Scraping Test")
    void scraperListTest() {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        WebScrapers handler = (WebScrapers) context.getBean("scraperHandler");

        assertNotNull(handler);
        System.out.println("Test 2 has been completed");
    }

    //Running Threads Test
    @Test
    @Order(3)
    @DisplayName("Running Threads Test")
    void runningThreadsTest() {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        WebScrapers handler = (WebScrapers) context.getBean("scraperHandler");

        try {
            for (Thread thread : WebScrapers.getScraperList()) {
                thread.start();
            }
        } catch (Exception ex) {
            fail("Failed to start the threads" + ex.getMessage());
        }

        //Test for each thread if they are running
        for (Thread scraperThread : WebScrapers.getScraperList()) {
            assertEquals(true, scraperThread.isAlive());
        }
        System.out.println("Test 3 has been completed");
    }

    //Test setting threads in WebScraper class
    @Test
    @Order(4)
    @DisplayName("Test setting threads in WebScraper class")
    void scraperManagerListTest(){
        // Instance of the class we want to test
        WebScrapers manager = new WebScrapers();

        // Creating 3 new threads
        Thread scraper1 = new Thread();
        Thread scraper2 = new Thread();
        Thread scraper3 = new Thread();

        // Empty list to store threads
        List<Thread> testList = new ArrayList();

        // Adding threads to empty list
        testList.add(scraper1);
        testList.add(scraper2);
        testList.add(scraper3);

        try{
            // Adding list to our ScraperManager
            manager.setScraperList(testList);
        } catch(Exception ex){

            fail("Failed to set the scraperList in WebScraper class" + ex.getMessage());
        }

        // Chech if the testList matches ScraperManagers list
        assertEquals(testList, manager.getScraperList());

        System.out.println("Test 4 has been completed");
    }

    
    //Check if web scraper is scraping products Test.
    @Test
    @Order(5)
    @DisplayName("Check if web scraper is scraping products Test.")
    void deletePhoneTest() throws IOException {

        //Download HTML document from website
        Document doc = Jsoup.connect("https://www.mobilephone4u.co.uk/refurbished-iphones#/pageSize=12&viewMode=grid&orderBy=11&pageNumber=1")
                .userAgent("Google Chrome - Computer Science Student - Webscraping Coursework Task")
                .get();

        //Get all of the products on the page
        Elements prods = doc.select(".type-product");

        //Work through the products
        for (Element prod : prods) {
            assertNotNull(prod);
        }
        System.out.println("Test 5 has been completed");
    }

    //Save Phone and Comparison and check if Phone and Comparison has been deleted Test
    @Test
    @Order(6)
    @DisplayName("Save Phone and Comparison and check if Phone and Comparison has been deleted Test")
    void savePhoneTest() {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Hibernate hibernate = (Hibernate) context.getBean("hibernate");

        Phones phones = new Phones();
        String randomName = String.valueOf(Math.random() * 10);
        String name = "iPhone";
        int price = 2000;

        phones.setName(name);

        //Use Hibernate to save Phone
        ArrayList<Integer> savedPhoneArray = hibernate.addPhone(name, randomName, "black", randomName, randomName, randomName,price, randomName,randomName);
        idPhone = savedPhoneArray.get(0);
        idComparison = savedPhoneArray.get(1);

        hibernate.deleteComparison(idComparison);
        hibernate.deletePhone(idPhone);


        assertEquals(Hibernate.phonesDeletetionCompleted, true);
        assertEquals(Hibernate.comparisonDeletetionCompleted, true);
        hibernate.shutDown();

        System.out.println("Test 6 has been completed");
    }

    @After
    public void after() {
        sessionFactory.close();
    }
}
