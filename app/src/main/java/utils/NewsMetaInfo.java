package utils;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by bunny on 23/04/17.
 */

public class NewsMetaInfo implements Serializable {
    String newsHeading = "";
    String newsDate = "";
    String newsPushKeyId;
    Bitmap newsImage;

    long newsTime = 0l;
    String newsSource = "";
    int newsSourceimageIndex = 0;

    boolean newsNotification = false;


    String newsSourceShort = "";

    String newsImageLocalPath = "";



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

    public String getNewsImageLocalPath() {
        return newsImageLocalPath;
    }

    public void setNewsImageLocalPath(String newsImageLocalPath) {
        this.newsImageLocalPath = newsImageLocalPath;
    }

    public static String resolveDateString(long newsTime) {
        Calendar calendar = Calendar.getInstance();


        long currenttime = calendar.getTimeInMillis();


        //calculate difference in time
        //long timeDifference = (currenttime - newsTime);

        if ((currenttime - newsTime) <= 0 || newsTime <= 1493013649175l) {
            return "";
        }

        long numberOfHour = (currenttime - newsTime) / 3600000;
        if (numberOfHour == 0) {
            return "less than hour ago";
        } else if (numberOfHour < 24) {
            return String.valueOf(numberOfHour) + " hour ago";
        } else {

            long numberOfDays = numberOfHour / 24;

            if (numberOfDays < 7) {
                return String.valueOf(numberOfDays) + " day ago";
            } else {

                long numberOfWeek = numberOfDays / 7;
                if (numberOfWeek <= 4) {
                    return String.valueOf(numberOfWeek) + " week ago";
                } else {

                    long numberOfMonth = numberOfWeek / 4;
                    if (numberOfMonth <= 12) {
                        return String.valueOf(numberOfMonth) + " month ago";
                    } else {

                        long numberOfYear = numberOfMonth / 12;

                        return String.valueOf(numberOfYear) + " year ago";

                    }

                }

            }

        }


    }


}
