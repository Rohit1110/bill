package model;

import com.rns.web.billapp.service.bo.domain.BillUser;

/**
 * Created by Rohit on 5/10/2018.
 */

public class BillCustomer {

    private BillUser user;
    private boolean expanded;

    public BillCustomer(BillUser user) {
        this.user = user;
    }

    public BillCustomer() {

    }

    public void setUser(BillUser user) {
        this.user = user;
    }

    public BillUser getUser() {
        return user;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
