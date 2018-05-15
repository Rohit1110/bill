package model;

/**
 * Created by Rohit on 5/10/2018.
 */

public class ListTwo {
    private String name;
    private int imgUrl;
    private String newspaperpcs;
    private String rates;
    private  boolean expanded;
    private String newsPaperInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(int imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public String getNewspaperpcs() {
        return newspaperpcs;
    }

    public void setNewspaperpcs(String newspaperpcs) {
        this.newspaperpcs = newspaperpcs;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getNewsPaperInfo() {
        return newsPaperInfo;
    }

    public void setNewsPaperInfo(String newsPaperInfo) {
        this.newsPaperInfo = newsPaperInfo;
    }
}
