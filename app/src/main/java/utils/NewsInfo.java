package utils;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bunny on 23/04/17.
 */

public class NewsInfo implements Parcelable ,Serializable{

    private String newsHeadline="";
    private String newsSummary="";
    private String newsSource;
    private String newsCategory;
    private String newsDate;
    private String newsTime;
    private String newsNotify;
    private String newsImageLink;
    private Bitmap newsImage;
    private HashMap<String, NewsSourceList> newsSourceListHashMap;
    ArrayList<NewsSourceList> newsSourceListArrayList;

    private HashMap<String, Long> newsTweetListHashMap;

    String newsSourceShort="";

    public NewsInfo() {
    }

    public String getNewsSourceShort() {
        return newsSourceShort;
    }

    public void setNewsSourceShort(String newsSourceShort) {
        this.newsSourceShort = newsSourceShort;
    }

    public HashMap<String, Long> getNewsTweetListHashMap() {
        return newsTweetListHashMap;
    }

    public void setNewsTweetListHashMap(HashMap<String, Long> newsTweetListHashMap) {
        this.newsTweetListHashMap = newsTweetListHashMap;
    }


    public ArrayList<NewsSourceList> getNewsSourceListArrayList() {
        return newsSourceListArrayList;
    }

    public void setNewsSourceListArrayList(ArrayList<NewsSourceList> newsSourceListArrayList) {
        this.newsSourceListArrayList = newsSourceListArrayList;
    }


    public HashMap<String, NewsSourceList> getNewsSourceListHashMap() {
        return newsSourceListHashMap;
    }

    public void setNewsSourceListHashMap(HashMap<String, NewsSourceList> newsSourceListHashMap) {
        this.newsSourceListHashMap = newsSourceListHashMap;
    }

    public String getNewsHeadline() {
        return newsHeadline;
    }

    public void setNewsHeadline(String newsHeadline) {
        this.newsHeadline = newsHeadline;
    }

    public String getNewsSummary() {
        return newsSummary;
    }

    public void setNewsSummary(String newsSummary) {
        this.newsSummary = newsSummary;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewsNotify() {
        return newsNotify;
    }

    public void setNewsNotify(String newsNotify) {
        this.newsNotify = newsNotify;
    }

    public String getNewsImageLink() {
        return newsImageLink;
    }

    public void setNewsImageLink(String newsImageLink) {
        this.newsImageLink = newsImageLink;
    }

    public Bitmap getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(Bitmap newsImage) {
        this.newsImage = newsImage;
    }

    public void resolveHashmap() {
        newsSourceListArrayList = new ArrayList<>();
        if (this.newsSourceListHashMap != null) {
            for (NewsSourceList newsSourceList : newsSourceListHashMap.values()) {
                newsSourceListArrayList.add(newsSourceList);

            }
        }
        newsSourceListHashMap = null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(newsHeadline);
        dest.writeString(newsSummary);
        dest.writeString(newsSource);
        dest.writeString(newsCategory);
        dest.writeString(newsDate);
        dest.writeString(newsTime);
        dest.writeParcelable(newsImage, 1);
        dest.writeString(newsNotify);
        dest.writeString(newsImageLink);


    }

    private NewsInfo(Parcel in) {

        this.newsHeadline = in.readString();
        this.newsSummary = in.readString();
        this.newsSource = in.readString();
        this.newsCategory = in.readString();
        this.newsDate = in.readString();
        this.newsTime = in.readString();
        this.newsNotify = in.readString();
        this.newsImageLink = in.readString();


    }

    public static final Parcelable.Creator<NewsInfo> CREATOR = new Parcelable.Creator<NewsInfo>() {

        @Override
        public NewsInfo createFromParcel(Parcel source) {
            return new NewsInfo(source);
        }

        @Override
        public NewsInfo[] newArray(int size) {
            return new NewsInfo[size];
        }
    };


}
