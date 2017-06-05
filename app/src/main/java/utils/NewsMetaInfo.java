package utils;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by bunny on 23/04/17.
 */

public class NewsMetaInfo implements Serializable{
    String newsHeading="";
    String newsDate="";
    String newsPushKeyId;
    Bitmap newsImage;

    long newsTime = 0l;
    String newsSource  ="";
    int newsSourceimageIndex =0;

    boolean newsNotification =false;


    String newsSourceShort="";


    public NewsMetaInfo() {
    }

    public boolean isNewsNotification() {
        return newsNotification;
    }

    public void setNewsNotification(boolean newsNotification) {
        this.newsNotification = newsNotification;
    }

    public String getNewsSourceShort() {
        return newsSourceShort;
    }

    public void setNewsSourceShort(String newsSourceShort) {
        this.newsSourceShort = newsSourceShort;
    }
    public long getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(long newsTime) {
        this.newsTime = newsTime;
    }


    public int getNewsSourceimageIndex() {
        return newsSourceimageIndex;
    }

    public void setNewsSourceimageIndex(int newsSourceimageIndex) {
        this.newsSourceimageIndex = newsSourceimageIndex;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }


    public String getNewsHeading() {
        return newsHeading;
    }

    public void setNewsHeading(String newsHeading) {
        this.newsHeading = newsHeading;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    public String getNewsPushKeyId() {
        return newsPushKeyId;
    }

    public void setNewsPushKeyId(String newsPushKeyId) {
        this.newsPushKeyId = newsPushKeyId;
    }

    public Bitmap getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(Bitmap newsImage) {
        this.newsImage = newsImage;
    }

}
