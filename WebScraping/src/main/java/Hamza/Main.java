package Hamza;

import Hamza.scrapper.business.AppConfig;
import Hamza.scrapper.business.WebScrapers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        WebScrapers handler = (WebScrapers) context.getBean("scraperHandler");

        handler.startThreads();
        handler.joinThreads();
    }
}
