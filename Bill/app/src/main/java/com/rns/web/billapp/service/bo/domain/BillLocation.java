package com.rns.web.billapp.service.bo.domain;

import java.io.Serializable;
import java.util.Date;


public class BillLocation implements Serializable, Cloneable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String code;
	private String mapLocation;
	private Date createdDate;
	private String status;
	private String city;

	public BillLocation(Integer id) {
		setId(id);
	}

	public BillLocation() {

	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMapLocation() {
		return mapLocation;
	}
	public void setMapLocation(String mapLocation) {
		this.mapLocation = mapLocation;
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

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
