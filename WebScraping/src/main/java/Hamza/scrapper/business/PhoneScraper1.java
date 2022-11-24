package Hamza.scrapper.business;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;


/**
 * First PhoneScraper using thread
 */
public class PhoneScraper1 extends Thread {

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
            // item to be sreached
            String itemName = "iphones";

            // number of pages to scrape from
            for (int page = 1; page < 8; page++) {

                try {
                    Document doc = Jsoup.connect("https://www.backmarket.co.uk/search?page=" + page + "&q=" + itemName).get();
                    Elements prods = doc.select("._1ZlCRqz2JUYpCj9FH8PF7C");

                    //Work through the products
                    for (int i = 0; i < prods.size(); ++i) {

                        //Get the product name
                        Elements name = prods.get(i).select("h2._2xkoCemRpVvAyafHpgIPdC");
                        String phoneName = "" + name.text();
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
                        String imageSRC = imageUrl.attr("src");

                        imageSRC = imageSRC.substring(imageSRC.indexOf("0/") + 2);

                        //Get the product price
                        Elements phonePrice = prods.get(i).select(".font-body.text-2.leading-2.font-bold._2SrrvPwuOVjCyULC_FKjin");
                        String finalPrice = phonePrice.text().substring(1);

                        //Get the colour
                        Elements colour = prods.get(i).select(".MttglY_0Jxht6nSKuM3lf");

                        String phoneColour = "" + colour.text();
                        if (phoneColour.contains("(PRODUCT)")) {
                            phoneColour = phoneColour.substring(phoneColour.indexOf(")") + 1);
                            phoneColour = phoneColour.substring(0, phoneColour.indexOf("-"));
                        } else {
                            phoneColour = phoneColour.substring(phoneColour.indexOf("-") + 1);
                            phoneColour = phoneColour.substring(0, phoneColour.indexOf("-"));
                        }

                        //Get the storage
                        Elements storage = prods.get(i).select(".MttglY_0Jxht6nSKuM3lf");
                        String phoneStorage = "" + storage.text();
                        phoneStorage = phoneStorage.substring(0, phoneStorage.indexOf("-"));
                        phoneStorage = phoneStorage.replaceAll(" ", "");

                        //Get the model
                        Elements model = prods.get(i).select("h2._2xkoCemRpVvAyafHpgIPdC");
                        String phoneModel = model.text();
                        if (phoneModel.contains("6S")) {
                            phoneModel = phoneModel.replace("6S", "6s");
                        }


                        //Get the siteURL
                        Elements siteURL = prods.get(i).select("a");
                        String phoneUrl = siteURL.attr("href");

                        phoneUrl = "https://www.backmarket.co.uk" + phoneUrl;

                        //Get the product description
                        String description = phoneModel + " - " + phoneStorage + "-" + phoneColour;
                        description = description.replaceAll(" ", "");

                        // use spring and hibernate to use the scraped data for example adding it to database
                        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                        Hibernate hibernate = (Hibernate) context.getBean("hibernate");

                        hibernate.addPhone(phoneName, phoneModel, phoneColour, description, phoneStorage, imageSRC, Float.parseFloat(finalPrice), "BackMarket", phoneUrl);
                        hibernate.shutDown();

                        try {
                            sleep(1000 * CRAWL_DELAY);
                        } catch (InterruptedException ex) {
                            System.err.println(ex.getMessage());
                        }


                        //Output the data that we have downloaded
                        System.out.println("\nname:" + phoneName + "\ndescription: " + description + "\nimageUrl: " + imageSRC + "\ncolour: " +
                                phoneColour + "\nstorage: " + phoneStorage + "\nModel: " + phoneModel + "\nsiteURL: https://www.backmarket.co.uk" + phoneUrl + "\nprice: " + phonePrice.text());
                        System.out.println("------------------------------------------------------------------------------------");
                    }
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}
