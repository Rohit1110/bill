package com.rns.web.billapp.service.bo.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Admin on 18/01/2019.
 */

public class BillCustomerGroup implements Serializable {

    private Integer id;
    private String groupName;
    private String groupDescription;
    private Date createdDate;
    private String status;
    private Integer sequenceNumber;
    private Integer noOfCustomers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getNoOfCustomers() {
        return noOfCustomers;
    }

    public void setNoOfCustomers(Integer noOfCustomers) {
        this.noOfCustomers = noOfCustomers;
    }
}
