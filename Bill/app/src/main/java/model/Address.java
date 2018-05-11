package model;

/**
 * Created by Rohit on 5/11/2018.
 */

public class Address {
    String Address;
    private boolean expanded;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
