package com.serviceplus.metadata.entity;

import java.util.Date;

import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "external_system_user", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ExternalSystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_system_reg_id")
    private Long externalSystemRegId;
    
    @Column(name="external_client_id")
    private String clientId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "sign_no")
    private String signNo;

    @Column(name = "c_date")
    private Date createdOn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getExternalSystemRegId() {
		return externalSystemRegId;
	}

	public void setExternalSystemRegId(Long externalSystemRegId) {
		this.externalSystemRegId = externalSystemRegId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getSignNo() {
		return signNo;
	}

	public void setSignNo(String signNo) {
		this.signNo = signNo;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
    
    
}
