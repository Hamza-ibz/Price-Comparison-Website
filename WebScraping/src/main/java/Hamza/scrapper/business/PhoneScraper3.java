package Hamza.scrapper.business;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Third PhoneScraper using thread
 */
public class PhoneScraper3 extends Thread {

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

            // number of pages to scrape from
            for (int page = 1; page < 8; page++) {
                try {
                    Document doc = Jsoup.connect("https://www.theioutlet.com/product-category/iphones/page/" + page + "/").get();
                    Elements prods = doc.select(".type-product");

                    //Work through the products
                    for (int i = 0; i < prods.size(); ++i) {

                        //Get the product name
                        Elements name = prods.get(i).select(".woocommerce-loop-product__title");
                        String phoneName = name.text();
                        phoneName = phoneName.substring(0, phoneName.indexOf("–"));
                        String[] phoneNameArray = phoneName.split(" ");

                        if (phoneNameArray[1].contains("X")) {
                            phoneName = phoneNameArray[0] + " X";
                        } else if (phoneNameArray[1].contains("SE")) {
                            phoneName = phoneNameArray[0] + " SE";
                        } else {
                            phoneName = phoneNameArray[0] + " " + phoneNameArray[1].replaceAll("[^0-9]", "");
                        }


                        //Get the product description
                        Elements description = prods.get(i).select(".woocommerce-loop-product__title");
                        String phoneDescription = description.text();
                        phoneDescription = phoneDescription.substring(0, phoneDescription.indexOf("– Grade"));
                        phoneDescription = phoneDescription.replaceAll("–", "-");
                        phoneDescription = phoneDescription.replaceAll(" ", "");

                        //Get the colour
                        Elements colour = prods.get(i).select(".woocommerce-loop-product__title");
                        String phoneColour = colour.text();
                        phoneColour = phoneColour.substring(phoneColour.indexOf("–") + 1);
                        phoneColour = phoneColour.substring(phoneColour.indexOf("–") + 2);
                        phoneColour = phoneColour.substring(0, phoneColour.indexOf("–"));

                        //Get the storage
                        Elements storage = prods.get(i).select(".woocommerce-loop-product__title");
                        String phoneStorage = storage.text();
                        phoneStorage = phoneStorage.substring(phoneStorage.indexOf("–") + 2);
                        phoneStorage = phoneStorage.substring(0, phoneStorage.indexOf("–"));

                        //Get the model
                        Elements model = prods.get(i).select(".woocommerce-loop-product__title");
                        String phoneModel = model.text();
                        if (phoneModel.contains("6S")) {
                            phoneModel = phoneModel.replace("6S", "6s");
                        }
                        phoneModel = phoneModel.substring(0, phoneModel.indexOf("–"));

                        //Get the siteUrl
                        Elements siteUrl = prods.get(i).select("a");
                        String phoneUrl = siteUrl.attr("href");

                        //Get price
                        Elements prices = prods.get(i).select("bdi");
                        String phonePrice = "" + prices.text() + " ";
                        phonePrice = phonePrice.substring(1, phonePrice.indexOf(" "));

                        //image url
                        Elements imageUrl = prods.get(i).select("img");
                        String imageSrc = imageUrl.attr("src");

                        //Output the data that we have downloaded
                        System.out.println("\nNAME:" + phoneName + "\ndescription:" + phoneDescription + "\ncolour:" + phoneColour + "\nstorage:" + phoneStorage + "\nModel:" + phoneModel + "\nsiteUrl:" + phoneUrl + "\nPRICE:" + phonePrice + "\nimage:" + imageSrc);
//                        + "\nModel: " + model.text() + "\nsite_url: https://www.backmarket.co.uk" + phoneUrl + "\nPRICE: " + finalPrice.text());
                        System.out.println("------------------------------------------------------------------------------------");

                        // use spring and hibernate to use the scraped for example adding it to database
                        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                        Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                        hibernate.addPhone(phoneName, phoneModel, phoneColour, phoneDescription, phoneStorage, imageSrc, Float.parseFloat(phonePrice), "The iOutlet", phoneUrl);
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