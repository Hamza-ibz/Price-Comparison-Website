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
public class PhoneScraper5 extends Thread {

    private final int CRAWL_DELAY = 1;

    //Allows us to shut down our application cleanly
    volatile private boolean runThread = false;

    /**
     * run thread
     */
    @Override
    public void run() {
        runThread = true;

        while (runThread) {

            try {
                Document doc = Jsoup.connect("https://www.alloallo.com/uk/refurbished-iphone/").get();
                Elements prods = doc.select(".ps-product");

                //Work through the products
                for (int i = 0; i < prods.size(); ++i) {

                    //Get the product name
                    Elements name = prods.get(i).select(".ps-product__vendor");
                    String phoneName = name.text();
                    phoneName = phoneName.substring(0, phoneName.indexOf("GB") - 3).trim();
                    if (phoneName.contains("6S")) {
                        phoneName = phoneName.replace("6S", "6");
                    }
                    String[] phoneNameArray = phoneName.split("\\s+");
                    phoneName = phoneNameArray[0] + " " + phoneNameArray[1];


                    //Get the colour
                    Elements colour = prods.get(i).select(".ps-product__vendor");
                    String phoneColour = colour.text();
                    phoneColour = phoneColour.substring(phoneColour.indexOf("B") + 2);
                    phoneColour = phoneColour.substring(0, phoneColour.indexOf("-")).trim();

                    //Get the storage
                    Elements storage = prods.get(i).select(".ps-product__vendor");
                    String phoneStorage = storage.text();
                    phoneStorage = phoneStorage.substring(0, phoneStorage.indexOf("GB"));
                    phoneStorage = phoneStorage.substring(phoneStorage.lastIndexOf(" ") + 1) + "GB";

                    //Get the model
                    Elements model = prods.get(i).select(".ps-product__vendor");
                    String phoneModel = model.text();

                    if (phoneModel.contains("6S")) {
                        phoneModel = phoneModel.replace("6S", "6s");
                    }
                    phoneModel = phoneModel.substring(0, phoneModel.indexOf("GB") - 3).trim();

                    //Get the siteUrl
                    Elements siteUrl = prods.get(i).select("a");
                    String phoneUrl = "https://www.alloallo.com/" + siteUrl.attr("href");

                    //Get price
                    Elements prices = prods.get(i).select(".ps-product__price");
                    String phonePrice = prices.text();
                    phonePrice = phonePrice.substring(phonePrice.indexOf("£") + 1);
                    phonePrice = phonePrice.substring(0, phonePrice.indexOf(" "));

                    //image url
                    Elements imageUrl = prods.get(i).select("img");
                    String imageSrc = imageUrl.attr("data-src");


                    //Get the product description
                    String description = phoneModel + "-" + phoneStorage + "-" + phoneColour;
                    description = description.replaceAll(" ", "");
                    ;

                    //Output the data that we have downloaded
                    System.out.println("\nNAME: " + phoneName + "\ndescription: " + description + "\ncolour: " + phoneColour + "\nstorage: " + phoneStorage + "\nModel: " + phoneModel + "\nsiteUrl:" + phoneUrl + "\nPRICE: " + phonePrice + "\nimage: " + imageSrc);
                    System.out.println("------------------------------------------------------------------------------------");

                    // use spring and hibernate to use the scraped for example adding it to database
                    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                    Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                    hibernate.addPhone(phoneName, phoneModel, phoneColour, description, phoneStorage, imageSrc, Float.parseFloat(phonePrice), "alloallo", phoneUrl);
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