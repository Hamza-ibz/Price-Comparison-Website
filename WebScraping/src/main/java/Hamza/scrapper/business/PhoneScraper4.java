package Hamza.scrapper.business;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Forth PhoneScraper using thread
 */
public class PhoneScraper4 extends Thread {

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
                Document doc = Jsoup.connect("https://www.mobilephone4u.co.uk/refurbished-iphones#/pageSize=12&viewMode=grid&orderBy=11&pageNumber=1").get();
                Elements prods = doc.select(".product-item");

                //Work through the products
                for (int i = 0; i < prods.size(); ++i) {
                    Elements prod = prods.get(i).select(".product-title");
                    String prods_ = prod.text();
                    if (prods_.contains("GB")) {

                        //Get the product name
                        Elements name = prods.get(i).select(".product-title");
                        String phoneName = name.text();
                        if (phoneName.contains("Refurbished Apple")) {
                            phoneName = phoneName.replace("Refurbished Apple", "");
                        } else if (phoneName.contains("Apple")) {
                            phoneName = phoneName.replace("Apple", "");
                        } else if (phoneName.contains("Refurbished")) {
                            phoneName = phoneName.replace("Refurbished", "");
                        }
                        phoneName = phoneName.substring(phoneName.indexOf(" ") + 1);
                        String phoneType = phoneName.substring(phoneName.indexOf(" ") + 1);
                        phoneType = phoneType.substring(0, phoneType.indexOf(" "));
                        phoneName = phoneName.substring(0, phoneName.indexOf(" ")) + " " + phoneType;

                        String[] phoneNameArray = phoneName.split(" ");

                        if (phoneNameArray[1].contains("X")) {
                            phoneName = phoneNameArray[0] + " X";
                        } else if (phoneNameArray[1].contains("SE")) {
                            phoneName = phoneNameArray[0] + " SE";
                        } else {
                            phoneName = phoneNameArray[0] + " " + phoneNameArray[1].replaceAll("[^0-9]", "");
                        }

                        //Get the colour
                        Elements colour = prods.get(i).select(".product-title");
                        String phoneColour = colour.text();
                        phoneColour = phoneColour.substring(phoneColour.indexOf("B") + 1);
                        if (phoneColour.contains("Unlocked")) {
                            phoneColour = phoneColour.replace("Unlocked", "");
                        }
                        if (phoneColour.contains("- Grade A")) {
                            phoneColour = phoneColour.replace("- Grade A", "");
                        }
                        phoneColour = phoneColour.substring(phoneColour.indexOf(" ") + 1);

                        //Get the storage
                        Elements storage = prods.get(i).select(".product-title");
                        String phoneStorage = storage.text();
                        phoneStorage = phoneStorage.substring(phoneStorage.indexOf(" ") + 1);
                        phoneStorage = phoneStorage.substring(phoneStorage.indexOf(" ") + 1);
                        phoneStorage = phoneStorage.substring(phoneStorage.indexOf(" ") + 1);
                        phoneStorage = phoneStorage.replaceAll("[^0-9]", "") + "GB";

                        //Get the model
                        Elements model = prods.get(i).select(".product-title");
                        String phoneModel = model.text();
                        if (phoneModel.contains("Refurbished Apple")) {
                            phoneModel = phoneModel.replace("Refurbished Apple", "");
                        } else if (phoneModel.contains("Apple")) {
                            phoneModel = phoneModel.replace("Apple", "");
                        } else if (phoneModel.contains("Refurbished")) {
                            phoneModel = phoneModel.replace("Refurbished", "");
                        }
                        if (phoneModel.contains("6S")) {
                            phoneModel = phoneModel.replace("6S", "6s");
                        }

                        phoneModel = phoneModel.substring(phoneModel.indexOf(" ") + 1);
                        String models_ = phoneModel.substring(phoneModel.indexOf(" ") + 1);
                        models_ = models_.substring(0, models_.indexOf(" "));
                        phoneModel = phoneModel.substring(0, phoneModel.indexOf(" ")) + " " + models_;

                        //Get the siteUrl
                        Elements siteUrl = prods.get(i).select("a");
                        String phoneUrl = "https://www.mobilephone4u.co.uk" + siteUrl.attr("href");

                        //Get price
                        Elements prices = prods.get(i).select(".actual-price");
                        String phonePrice = prices.text();
                        phonePrice = phonePrice.substring(phonePrice.indexOf("£") + 1);
                        if (phonePrice == "") {
                            phonePrice = "111.49";
                        }

                        //image url
                        Elements imageUrl = prods.get(i).select("img");
                        String imageSrc = imageUrl.attr("src");

                        // Get the product description
                        String description = phoneName + "-" + phoneStorage + "-" + phoneColour;
                        description = description.replaceAll(" ", "");


                        //Output the data that we have downloaded
                        System.out.println("\nNAME: " + phoneName + "\ndescription: " + description + "\ncolour: " + phoneColour + "\nstorage: " + phoneStorage + "\nModel: " + phoneModel + "\nsiteUrl: " + phoneUrl + "\nPRICE:" + phonePrice + "\nimage:" + imageSrc);
                        System.out.println("------------------------------------------------------------------------------------");

                        // use spring and hibernate to use the scraped for example adding it to database
                        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                        Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                        hibernate.addPhone(phoneName, phoneModel, phoneColour, description, phoneStorage, imageSrc, Float.parseFloat(phonePrice), "mobilephone4u", phoneUrl);
                        hibernate.shutDown();

                        try {
                            sleep(1000 * CRAWL_DELAY);
                        } catch (InterruptedException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                }
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }

        }
    }
}

