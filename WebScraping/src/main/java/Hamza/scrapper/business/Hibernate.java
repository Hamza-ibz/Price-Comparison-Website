package Hamza.scrapper.business;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping an object-oriented domain model to the database
 */

public class Hibernate {

    //Creates new Sessions when we need to interact with the database
    private SessionFactory sessionFactory;

    /**
     * Used in testing to check if phone is deleted from database
     */
    public static boolean phonesDeletetionCompleted = false;

    /**
     * Used in testing to check if comparison (phone comparison) is deleted from database
     */
    public static boolean comparisonDeletetionCompleted = false;

    /**
     * Empty constructor
     */
    public Hibernate() {
    }

    /**
     * Adds new phone to the database
     *
     * @param name        the name
     * @param model       the model
     * @param colour      the colour
     * @param description the description
     * @param storage     the storage
     * @param imageURL    the imageURL
     * @param price       the price
     * @param site        the site
     * @param url         the url
     */

    // Adds a new Phone to the database
    public ArrayList<Integer> addPhone(String name, String model, String colour, String description, String storage, String imageURL, float price, String site, String url) {

        //Get a new Session instance from the session factory
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        if (!checkPhonesDuplicates("description", description, "storage", storage)) {

            Phones phones = new Phones();
            Comparison comparison = new Comparison();

            //Set values of Phone class that we want to add
            phones.setName(name);
            phones.setModel(model);
            phones.setColour(colour);
            phones.setDescription(description);
            phones.setImageURL(imageURL);
            phones.setStorage(storage);

            //Add Phone to database
            session.save(phones);

            comparison.setPhoneId(phones.getId());
            comparison.setPrice(price);
            comparison.setUrl(url);
            comparison.setSite(site);
            session.save(comparison);

            //Commit transaction to save it to database
            session.getTransaction().commit();

            ArrayList<Integer> ids = new ArrayList<>();

            ids.add(phones.getId());
            ids.add(comparison.getId());

            //Close the session and release database connection
            session.close();

            // Notifies the user of the status of the phone
            System.out.println("Phones added to database with ID: " + phones.getId());
            return ids;
        } else if (checkPhonesDuplicates("name", name, "imageURL", imageURL)) {
            session.close();
            System.out.println("Phones is already in DB");
            return new ArrayList<>();
        } else if (checkComparisonDuplicates("url", url)) {
            session.close();
            System.out.println("Comparison is already in DB");
            return new ArrayList<>();
        } else {
            //Get a new Session instance from the session factory
            Phones existingPhones = matchPhones("description", description);

            Comparison comparison = new Comparison();

            //Add Comparison to database
            comparison.setPhoneId(existingPhones.getId());
            comparison.setPrice(price);
            comparison.setUrl(url);
            comparison.setSite(site);
            session.save(comparison);

            //Commit transaction to save it to database
            session.getTransaction().commit();

            session.close();

            // Notifies the user of the status of the phone
            System.out.println("New Comparison added to database with ID: " + comparison.getId());
            return new ArrayList<>();
        }

    }

    /**
     * Check if there is duplicates of phone
     *
     * @param column1 the column1
     * @param data1   the data1
     * @param column2 the column2
     * @param data2   the data2
     * @return boolean (of the phone duplicate)
     */
    public boolean checkPhonesDuplicates(String column1, String data1, String column2, String data2) {
        //Get a new Session instance from the session factory
        Session session = sessionFactory.getCurrentSession();

        List<Phones> phonesList = session.createQuery("from Phones where " + column1 + " = '" + data1 + "' AND " + column2 + "= '" + data2 + "'").getResultList();

        return phonesList.size() > 0;
    }

    /**
     * Check if there is duplicates of comparison (phone comparison)
     *
     * @param column1 the column1
     * @param data1   the data1
     * @return boolean (of the comparison duplicate)
     */
    public boolean checkComparisonDuplicates(String column1, String data1) {
        //Get a new Session instance from the session factory
        Session session = sessionFactory.getCurrentSession();

        List<Comparison> comparisonList = session.createQuery("from Comparison where " + column1 + " = '" + data1 + "'").getResultList();

        return comparisonList.size() > 0;
    }

    /**
     * matches phones
     *
     * @param column1 the column1
     * @param data1   the data1
     * @return match phone
     */
    public Phones matchPhones(String column1, String data1) {
        //Get a new Session instance from the session factory
        Session session = sessionFactory.getCurrentSession();

        List<Phones> phoneList = session.createQuery("from Phones where " + column1 + " = '" + data1 + "'").getResultList();

        return phoneList.get(0);
    }

    /**
     * deletes phone in database. (used in testing)
     *
     * @param id the id
     * @return boolean (of the phone being deleted)
     */
    public void deletePhone(int id) {
        Phones phones;

        //Create an instance of a Phone class
        try (Session session = sessionFactory.getCurrentSession()) {
            //Create an instance of a Phone class
            phones = new Phones();
            phones.setId(id);
            //Start transaction
            session.beginTransaction();
            //Search for a Phone in database that has given id
            Object persistentInstance = session.load(Phones.class, id);
            //Delete object if we have found a match
            if (persistentInstance != null) {
                session.delete(persistentInstance);
            } else {
                System.out.println("Phones with id: " + id + "does not exist.");
            }   //Commit transaction to save it to database
            session.getTransaction().commit();
            //Close the session and release database connection
        }
        System.out.println("Phones has been deleted from database. ID: " + phones.getId());
        phonesDeletetionCompleted = true;
    }

    /**
     * deletes comparison (phone comparison) in database. (used in testing)
     *
     * @param id the id
     * @return boolean (of the comparison (phone comparison) being deleted)
     */
    public void deleteComparison(int id) {
        Comparison comparison;

        //Create an instance of a Comparison class
        try (Session session = sessionFactory.getCurrentSession()) {
            //Create an instance of a Comparison class
            comparison = new Comparison();
            comparison.setId(id);
            //Start transaction
            session.beginTransaction();
            //Search for a Phone in database that has given id
            Object persistentInstance = session.load(Comparison.class, id);
            //Delete object if we have found a match
            if (persistentInstance != null) {
                session.delete(persistentInstance);
            } else {
                System.out.println("Comparison with id: " + id + "does not exist.");
            }   //Commit transaction to save it to database
            session.getTransaction().commit();
            //Close the session and release database connection
        }
        System.out.println("comparison has been deleted from database. ID: " + comparison.getId());
        comparisonDeletetionCompleted = true;
    }

    /**
     * Sets sessionFactory
     *
     * @param sessionFactory the sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Gets sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Closes sessionFactory
     */
    public void shutDown() {
        sessionFactory.close();
    }
}
