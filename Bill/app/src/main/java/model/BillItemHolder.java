package model;

import com.rns.web.billapp.service.bo.domain.BillItem;

/**
 * Created by Rohit on 5/15/2018.
 */

public class BillItemHolder {
    private boolean expanded;
    private boolean isSelected;
    private BillItem item;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public BillItem getItem() {
        return item;
    }

    public void setItem(BillItem item) {
        this.item = item;
    }
}
