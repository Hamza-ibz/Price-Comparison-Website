package Hamza.scrapper.business;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Second PhoneScraper using thread
 */
public class PhoneScraper2 extends Thread {
    //    public class PhoneScrapper extends WebScraper {

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

                Document doc = Jsoup.connect("https://www.ur.co.uk/collections/all-refurbished-iphones").userAgent("Google Chrome - Computer Science Student - Webscraping Coursework Task").get();
                Elements prods = doc.select(".product-item");

                //Work through the products
                for (int i = 0; i < prods.size(); ++i) {

                    //Get the product name
                    Elements name = prods.get(i).select(".product-item__title");
                    String phoneName = "" + name.text();
                    phoneName = phoneName.substring(phoneName.indexOf("Apple") + 6);
                    phoneName = phoneName.substring(0, phoneName.indexOf("-"));
                    String[] phoneNameArray = phoneName.split(" ");

                    if (phoneNameArray[1].contains("X")) {
                        phoneName = phoneNameArray[0] + " X";
                    } else if (phoneNameArray[1].contains("SE")) {
                        phoneName = phoneNameArray[0] + " SE";
                    } else {
                        phoneName = phoneNameArray[0] + " " + phoneNameArray[1].replaceAll("[^0-9]", "");
                    }


                    //Get the product image
                    Elements imageUrl = prods.get(i).select("img");
                    String imageSrc = imageUrl.attr("src");
                    imageSrc = "https:" + imageSrc;

                    //Get the product price
                    Elements price = prods.get(i).select("span.price");
                    String finalPrice = "" + price.text();
                    finalPrice = finalPrice.substring(finalPrice.indexOf("From") + 6);
                    finalPrice = finalPrice.substring(0, finalPrice.indexOf(" "));

                    //Get the colour
                    Elements colour = prods.get(i).select(".color-swatch__item");
                    String phoneColour = colour.attr("title");

                    //Get the storage
                    Elements storage = prods.get(i).select(".product-item__title");
                    String phoneStorage = "" + storage.text();
                    phoneStorage = phoneStorage.substring(phoneStorage.indexOf("Unlocked") + 9);

                    //Get the model
                    Elements model = prods.get(i).select(".product-item__title");
                    String phoneModel = "" + model.text();
                    if (phoneModel.contains("6S")) {
                        phoneModel = phoneModel.replace("6S", "6s");
                    }

                    phoneModel = phoneModel.substring(phoneModel.indexOf("Apple") + 6);
                    phoneModel = phoneModel.substring(0, phoneModel.indexOf("-"));

                    //Get the siteUrl
                    Elements siteUrl = prods.get(i).select(".product-item__title");
                    String phoneUrl = siteUrl.attr("href");
                    phoneUrl = "https://www.ur.co.uk" + phoneUrl;

                    //Get the product description
                    String description = phoneModel + "- " + phoneStorage + " - " + phoneColour;
                    description = description.replaceAll(" ", "");

                    //Output the data that we have downloaded
                    System.out.println("\nname:" + phoneName + "\ndescription: " + description + "\nimage: " + imageSrc + "\nprice: " + finalPrice
                            + "\ncolour: " + phoneColour + "\nstorage: " + phoneStorage + "\nmodel: " + phoneModel + "\nsite url: " + phoneUrl);
                    System.out.println("------------------------------------------------------------------------------------");

                    // use spring and hibernate to use the scraped for example adding it to database
                    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                    Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                    hibernate.addPhone(phoneName, phoneModel, phoneColour, description, phoneStorage, imageSrc, Float.parseFloat(finalPrice), "ur", phoneUrl);
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
