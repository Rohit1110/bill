package com.rns.web.billapp.service.bo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class BillOrder implements Serializable {

	private Integer id;
	private BigDecimal amount;
	private Date orderDate;
	private Date createdDate;
	private String status;
	private List<BillItem> items;
	private String orderDateString;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
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
	public List<BillItem> getItems() {
		return items;
	}
	public void setItems(List<BillItem> items) {
		this.items = items;
	}
	public String getOrderDateString() {
		return orderDateString;
	}
	public void setOrderDateString(String orderDateString) {
		this.orderDateString = orderDateString;
	}

}
