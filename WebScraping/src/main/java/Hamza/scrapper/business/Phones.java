package Hamza.scrapper.business;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents phone.
 * Java annotation is used for mapping.
 */
@Entity
@Table(name = "phone")
public class Phones implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "model")
    private String model;

    @Column(name = "colour")
    private String colour;

    @Column(name = "storage")
    private String storage;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageURL;

    /**
     * Empty constructor
     */
    public Phones() {
    }

    //Getters
    /**
     * Get phone id
     */
    public int getId() {
        return id;
    }

    /**
     * Get phone name
     */
    public String getName() {
        return name;
    }

    /**
     * Get phone model
     */
    public String getModel() {
        return model;
    }

    /**
     * Get phone storage
     */
    public String getStorage() {
        return storage;
    }

    /**
     * Get phone description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get phone imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Get phone colour
     */
    public String getColour() {
        return colour;
    }

    // Setters

    /**
     * Set phone id
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set phone name
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set phone model
     *
     * @param model the model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Set phone storage
     *
     * @param storage the storage
     */
    public void setStorage(String storage) {
        this.storage = storage;
    }

    /**
     * Set phone description
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set phone imageURL
     *
     * @param imageURL the imageURL
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * Set phone colour
     *
     * @param colour the colour
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Returns a String representation of the Phone
     *
     * @return String representation of the Phone
     */
    @Override
    public String toString() {
        String str = "Phone. id: " + id + "; name: " + name + "; model: " + model + "; storage: " + storage + "; description: " + description + "; colour: " + colour + "image URL: " + imageURL;
        return str;
    }
}
