package app.com.weatherzone.pojo;

/**
 * This class represents the parsed and populated Image Response
 * Author : Varun
 * Date : 2/11/2016.
 */

public class Image {

    //Image thumbnail url
    private String thumbnailURL;
    //Image title
    private String title;
    //Image creation time
    private String creationTime;
    //Image full url
    private String imageURL;
    //Name of the author
    private String fullName;
    //Name of the country;
    private String country;

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
