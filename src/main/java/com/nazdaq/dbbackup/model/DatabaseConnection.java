package com.nazdaq.dbbackup.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "database_con_info")
public class DatabaseConnection implements Serializable {

	private static final long serialVersionUID = 1439181827056134270L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "db_port")
	private String dbPort;

	@Column(name = "db_user_name")
	private String dbUserName;

	@Column(name = "db_password")
	private String dbPassword;

	@Column(name = "is_active")
	private boolean active;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified_date")
	private Date modifiedDate;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "operating_system")
	private String operatingSystem;

	@Column(name = "lin_password")
	private String linPassword;
	
	@Column(name = "lin_user_name")
	private String linUserName;
	
	@Column(name = "lin_port")
	private Integer linPort;

	@Transient
	private List<String> dbNames;

	@Transient
	private String location;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getDbPort() {
		return dbPort;
	}

	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<String> getDbNames() {
		return dbNames;
	}

	public void setDbNames(List<String> dbNames) {
		this.dbNames = dbNames;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getLinPassword() {
		return linPassword;
	}

	public void setLinPassword(String linPassword) {
		this.linPassword = linPassword;
	}

	public String getLinUserName() {
		return linUserName;
	}

	public void setLinUserName(String linUserName) {
		this.linUserName = linUserName;
	}

	public Integer getLinPort() {
		return linPort;
	}

	public void setLinPort(Integer linPort) {
		this.linPort = linPort;
	}

}
