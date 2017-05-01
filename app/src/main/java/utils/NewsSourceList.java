package utils;

import java.io.Serializable;

public class NewsSourceList implements Serializable {
    String newsListSource;
    String newsListHeading;
    String newsListLink;
    String newsListArticle;
    int sourceIndex = 0;
    String newsSourceShort="";

    public NewsSourceList() {
    }

    public String getNewsSourceShort() {
        return newsSourceShort;
    }

    public void setNewsSourceShort(String newsSourceShort) {
        this.newsSourceShort = newsSourceShort;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }


    public String getNewsListSource() {
        return newsListSource;
    }

    public void setNewsListSource(String newsListSource) {
        this.newsListSource = newsListSource;
    }

    public String getNewsListHeading() {
        return newsListHeading;
    }

    public void setNewsListHeading(String newsListHeading) {
        this.newsListHeading = newsListHeading;
    }

    public String getNewsListArticle() {
        return newsListArticle;
    }

    public void setNewsListArticle(String newsListArticle) {
        this.newsListArticle = newsListArticle;
    }

    public String getNewsListLink() {
        return newsListLink;
    }

    public void setNewsListLink(String newsListLink) {
        this.newsListLink = newsListLink;
    }
}
