package app.com.weatherzone.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the parsed and populated APIResponse
 * Author : Varun
 * Date : 2/11/2016.
 */

public class APIResponse {

    //Current page number of results
    private int currentPageNumber;
    //Total Pages
    private int totalPages;
    //List of image instances
    private List<Image> images;

    public APIResponse()
    {
        this.currentPageNumber=1;
        this.totalPages=1;
        this.images=new ArrayList<>();
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Image> getImages() {
        return images;
    }

}
