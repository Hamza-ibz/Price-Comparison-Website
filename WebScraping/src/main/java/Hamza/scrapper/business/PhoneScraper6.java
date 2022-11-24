package Hamza.scrapper.business;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Fifth PhoneScraper using thread
 */
public class PhoneScraper6 extends Thread {

    private final int CRAWL_DELAY = 1;

    volatile private boolean runThread = false;

    /**
     * run thread
     */
    @Override
    public void run() {
        runThread = true;

        while (runThread) {

            // number of pages to scrape from
            for (int page = 1; page < 9; page++) {
                try {
                    Document doc = Jsoup.connect("https://www.mac4sale.co.uk/iphone.html?dir=asc&max=950&min=300&order=price&p=" + page).get();
                    Elements prods = doc.select(".product-item-info");

                    //Work through the products
                    for (int i = 0; i < prods.size(); ++i) {
                        Elements prod = prods.get(i).select(".product-name");
                        String prods_ = prod.text();

                        //Get the product name
                        Elements name = prods.get(i).select(".product-name");
                        String phoneName = name.text();
                        phoneName = phoneName.substring(phoneName.indexOf("Apple") + 6);
                        String[] phoneNameArray = phoneName.split("\\s+");
                        phoneName = phoneNameArray[0] + " " + phoneNameArray[1];

                        //Get the colour
                        Elements colour = prods.get(i).select(".product-name");
                        String phoneColour = colour.text();
                        phoneColour = phoneColour.substring(phoneColour.indexOf("GB") + 3);
                        phoneColour = phoneColour.substring(0, phoneColour.indexOf(","));
                        if (phoneColour.contains("Product")) {
                            phoneColour = phoneColour.replace("Product", "");
                        }

                        //Get the storage
                        Elements storage = prods.get(i).select(".product-name");
                        String phoneStorage = storage.text();
                        phoneStorage = phoneStorage.substring(0, phoneStorage.indexOf("GB") + 2);
                        phoneStorage = phoneStorage.substring(phoneStorage.lastIndexOf(" ") + 1);

                        //Get the model
                        Elements model = prods.get(i).select(".product-name");
                        String phoneModel = model.text();
                        phoneModel = phoneModel.substring(phoneModel.indexOf("Apple") + 6);
                        phoneModel = phoneModel.substring(0, phoneModel.indexOf("GB") + 2);
                        phoneModel = phoneModel.substring(0, phoneModel.lastIndexOf(" "));
                        if (phoneModel.contains("(2nd Generation)")) {
                            phoneModel = phoneModel.replace("(2nd Generation)", "");
                        }
                        //Get the siteUrl
                        Elements siteUrl = prods.get(i).select("a");
                        String phoneUrl = siteUrl.attr("href");

                        //Get price
                        Elements prices = prods.get(i).select(".price");
                        String phonePrice = prices.text();
                        phonePrice = phonePrice.substring(0, phonePrice.indexOf(" "));
                        phonePrice = phonePrice.substring(phonePrice.indexOf("£") + 1);

                        //image url
                        Elements imageUrl = prods.get(i).select("img");
                        String imageSrc = imageUrl.attr("src");

                        // Get the product description
                        String description = phoneName + "-" + phoneStorage + "-" + phoneColour;
                        description = description.replaceAll(" ", "");


                        //Output the data that we have downloaded
                        System.out.println("\nNAME: " + phoneName + "\ncolour:" + phoneColour + "\nstorage:" + phoneStorage + "\nModel:" + phoneModel + "\nsiteUrl:" + phoneUrl + "\nPRICE:" + phonePrice + "\nimage:" + imageSrc + "\ndescription:" + description);
                        System.out.println("------------------------------------------------------------------------------------");

                        // use spring and hibernate to use the scraped for example adding it to database
                        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                        Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                        hibernate.addPhone(phoneName, phoneModel, phoneColour, description, phoneStorage, imageSrc, Float.parseFloat(phonePrice), "mac4sale", phoneUrl);
                        hibernate.shutDown();

                        try {
                            sleep(1000 * CRAWL_DELAY);
                        } catch (InterruptedException ex) {
                            System.err.println(ex.getMessage());
                        }

                    }
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }

        }
    }

}
