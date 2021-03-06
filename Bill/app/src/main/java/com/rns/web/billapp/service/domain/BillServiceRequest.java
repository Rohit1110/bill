package com.rns.web.billapp.service.domain;

import com.rns.web.billapp.service.bo.domain.BillBusiness;
import com.rns.web.billapp.service.bo.domain.BillCustomerGroup;
import com.rns.web.billapp.service.bo.domain.BillInvoice;
import com.rns.web.billapp.service.bo.domain.BillItem;
import com.rns.web.billapp.service.bo.domain.BillSector;
import com.rns.web.billapp.service.bo.domain.BillUser;

import java.util.Date;
import java.util.List;

public class BillServiceRequest {

    private BillUser user;
    private BillBusiness business;
    private BillItem item;
    private BillInvoice invoice;
    private BillSector sector;
    private String requestType;
    private Date requestedDate;
    private List<BillItem> items;
    private BillFile file;
    private List<BillUser> users;
    private BillCustomerGroup customerGroup;

    public BillUser getUser() {
        return user;
    }

    public void setUser(BillUser user) {
        this.user = user;
    }

    public BillBusiness getBusiness() {
        return business;
    }

    public void setBusiness(BillBusiness business) {
        this.business = business;
    }

    public BillItem getItem() {
        return item;
    }

    public void setItem(BillItem item) {
        this.item = item;
    }

    public BillInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(BillInvoice invoice) {
        this.invoice = invoice;
    }

    public BillSector getSector() {
        return sector;
    }

    public void setSector(BillSector sector) {
        this.sector = sector;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Date getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Date requestedDate) {
        this.requestedDate = requestedDate;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }

    public BillFile getFile() {
        return file;
    }

    public void setFile(BillFile file) {
        this.file = file;
    }

    public List<BillUser> getUsers() {
        return users;
    }

    public void setUsers(List<BillUser> users) {
        this.users = users;
    }

    public BillCustomerGroup getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(BillCustomerGroup customerGroup) {
        this.customerGroup = customerGroup;
    }
}
