package Hamza.scrapper.business;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents a Comparison (phone comparison).
 * Java annotation is used for mapping.
 */


@Entity
@Table(name = "phone_comparison")
public class Comparison implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "phone_id")
    private int phoneId;

    @Column(name = "price")
    private float price;

    @Column(name = "url")
    private String url;

    @Column(name = "site_name")
    private String site;

    /**
     * Empty constructor
     */
    public Comparison() {
    }

    /**
     * Get comparison id
     */
    public int getId() {
        return id;
    }

    /**
     * Get phone id
     */
    public int getPhoneId() {
        return phoneId;
    }

    /**
     * Get comparison price
     */
    public float getPrice() {
        return price;
    }

    /**
     * Get comparison URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get comparison site name
     */
    public String getSite() {
        return site;
    }


    /**
     * Set comparison id
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set phone id
     *
     * @param phoneId the phoneId
     */
    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    /**
     * Set comparison price
     *
     * @param price the price
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * Set comparison URL
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Set comparison site id
     *
     * @param site the site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * Returns a String representation of the Comparison
     *
     * @return String representation of the Comparison
     */
    @Override
    public String toString() {
        String str = "Comparison. id: " + id + "; phone id: " + phoneId + "; site: " + site + "; price: " + price + "; url: " + url;
        return str;
    }
}
