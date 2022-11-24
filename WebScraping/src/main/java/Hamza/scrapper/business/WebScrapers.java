package Hamza.scrapper.business;

import java.util.List;

/**
 * Managing the scraping
 */
public class WebScrapers {

    private static List<Thread> scraperList;

    /**
     * Empty constructor
     */
    //Empty constructor
    public WebScrapers() {
    }

    /**
     * Get scraper list
     */
    //Getter
    public static List<Thread> getScraperList() {
        return scraperList;
    }

    /**
     * Set scraper list
     * @param sList
     */
    //Setter
    public static void setScraperList(List<Thread> sList) {
        scraperList = sList;
    }

    /**
     * Start thread for phone scraper
     */
    //Start PhoneScrapers
    public void startThreads() {
        for (Thread PhoneScrapper : scraperList) {
            PhoneScrapper.start();
        }
    }

    /**
     * Join PhoneScrapers
     */
    //Join PhoneScrapers
    public void joinThreads() {
        for (Thread PhoneScraper : scraperList) {

            try {
                PhoneScraper.join();
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}