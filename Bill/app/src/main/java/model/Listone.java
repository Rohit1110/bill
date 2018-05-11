package model;

/**
 * Created by Rohit on 5/10/2018.
 */

public class Listone {
    private String name;
    private String address;
    private  boolean expanded;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public  boolean isExpanded() {
        return expanded;
    }

    public  void  setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
