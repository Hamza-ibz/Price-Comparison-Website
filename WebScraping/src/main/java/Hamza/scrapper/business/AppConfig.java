package Hamza.scrapper.business;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Wires different components together such as the scrapers, hibernate and SessionFactory
 */
public class AppConfig {
    SessionFactory sessionFactory;

    /**
     * scraperHandler() is a function that creates an arraylist of scrapers (web scrapers) to scrape from.
     * <p>
     * ScraperHandler Bean
     *
     * @return scraperHandler
     */
    @Bean
    public WebScrapers scraperHandler() {
        WebScrapers webScrapers = new WebScrapers();

        List<Thread> scraperRecord = new ArrayList();
        scraperRecord.add(scraper1());
        scraperRecord.add(scraper2());
        scraperRecord.add(scraper3());
        scraperRecord.add(scraper4());
        scraperRecord.add(scraper5());
        scraperRecord.add(scraper6());
        WebScrapers.setScraperList(scraperRecord);

        // Return Scraper Handler object
        return webScrapers;
    }

    /**
     * PhoneScraper1 is scraped and returned
     * <p>
     * PhoneScraper1 Bean
     *
     * @return scraper1
     */
    @Bean
    public PhoneScraper1 scraper1() {
        PhoneScraper1 scraper1 = new PhoneScraper1();
        return scraper1;
    }

    /**
     * PhoneScraper2 is scraped and returned
     * <p>
     * PhoneScraper2 Bean
     *
     * @return scraper2
     */
    @Bean
    public PhoneScraper2 scraper2() {
        PhoneScraper2 scraper2 = new PhoneScraper2();
        return scraper2;
    }

    /**
     * PhoneScraper3 is scraped and returned
     * <p>
     * PhoneScraper3 Bean
     *
     * @return scraper3
     */
    @Bean
    public PhoneScraper3 scraper3() {
        PhoneScraper3 scraper3 = new PhoneScraper3();
        return scraper3;
    }

    /**
     * PhoneScraper4 is scraped and returned
     * <p>
     * PhoneScraper4 Bean
     *
     * @return scraper4
     */
    @Bean
    public PhoneScraper4 scraper4() {
        PhoneScraper4 scraper4 = new PhoneScraper4();
        return scraper4;
    }
//

    /**
     * PhoneScraper5 is scraped and returned
     * <p>
     * PhoneScraper5 Bean
     *
     * @return scraper5
     */
    @Bean
    public PhoneScraper5 scraper5() {
        PhoneScraper5 scraper5 = new PhoneScraper5();
        return scraper5;
    }

    /**
     * PhoneScraper6 is scraped and returned
     * <p>
     * PhoneScraper6 Bean
     *
     * @return scraper6
     */
    @Bean
    public PhoneScraper6 scraper6() {
        PhoneScraper6 scraper6 = new PhoneScraper6();
        return scraper6;
    }


    /**
     * Hibernate Bean
     *
     * @return hibernate
     */
    @Bean
    public Hibernate hibernate() {
        Hibernate hibernate = new Hibernate();
        hibernate.setSessionFactory(sessionFactory());
        return hibernate;
    }

    /**
     * SessionFactory Bean
     *
     * @return sessionFactory
     */
    @Bean
    public SessionFactory sessionFactory() {
        if (sessionFactory == null) {//Build sessionFatory once only
            try {
                //Create a builder for the standard service registry
                StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();

                //Load configuration from hibernate configuration file.
                //Here we are using a configuration file that specifies Java annotations.
                standardServiceRegistryBuilder.configure("hibernate.cfg.xml");

                //Create the registry that will be used to build the session factory
                StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
                try {
                    //Create the session factory - this is the goal of the init method.
                    sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
                } catch (Exception e) {
                    /* The registry would be destroyed by the SessionFactory,
                            but we had trouble building the SessionFactory, so destroy it manually */
                    System.err.println("Session Factory build failed.");
                    e.printStackTrace();
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                //Ouput result
                System.out.println("Session factory built.");
            } catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("SessionFactory creation failed." + ex);
            }
        }
        return sessionFactory;
    }
}
