package model;

/**
 * Created by Rohit on 5/15/2018.
 */

public class SelectNewsPapers {
    private  boolean expanded;
    private String newsPaperInfo;
    private int imgUrl;
    private boolean isSelected;

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

    public int getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(int imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
