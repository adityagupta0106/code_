package com.serviceplus.metadata.entity;

import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fnc_generate_seq", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class SpeSequenceLastNo implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="fnc_generate_seq")
	private Integer fnc_generate_seq;

	public Integer getFnc_generate_seq() {
		return fnc_generate_seq;
	}

	public void setFnc_generate_seq(Integer fnc_generate_seq) {
		this.fnc_generate_seq = fnc_generate_seq;
	}
}
