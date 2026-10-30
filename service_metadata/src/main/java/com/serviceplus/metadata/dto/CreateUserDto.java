package com.serviceplus.metadata.dto;

import java.io.Serializable;
import java.util.List;

public class CreateUserDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer createdRole;
	private Integer createUserAs;
	private String lType;
	private Integer lTypeId;
	private Boolean approvalRequired;
	private Integer userCount;
	private DepartmentDetails departmentDetails;
	private Integer parentLocation;
	private List<SpecificLocation> specificLocation;
	private Integer allOrSpecific;
	private Integer state;
	private Integer designationId;
	private String kioskType;
	transient private Integer locationTypeId;
    transient private Boolean isTopLevelAdmin = false;
    private String referenceId;
	
	public static class DepartmentDetails{
		private Integer id;
		private Integer deptLevel;
		private Integer subOffice;
		transient private String departmentName;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getDeptLevel() {
			return deptLevel;
		}
		public void setDeptLevel(Integer deptLevel) {
			this.deptLevel = deptLevel;
		}
		public Integer getSubOffice() {
			return subOffice;
		}
		public void setSubOffice(Integer subOffice) {
			this.subOffice = subOffice;
		}
		public String getDepartmentName() {
			return departmentName;
		}
		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}
	}
	
	public static class SpecificLocation{
		private Integer id;
		transient private String name;
		transient private String parentName;

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

		public String getParentName() {
			return parentName;
		}

		public void setParentName(String parentName) {
			this.parentName = parentName;
		}
	}

	public Integer getCreatedRole() {
		return createdRole;
	}

	public void setCreatedRole(Integer createdRole) {
		this.createdRole = createdRole;
	}

	public Boolean getApprovalRequired() {
		return approvalRequired;
	}

	public void setApprovalRequired(Boolean approvalRequired) {
		this.approvalRequired = approvalRequired;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public DepartmentDetails getDepartmentDetails() {
		return departmentDetails;
	}

	public void setDepartmentDetails(DepartmentDetails departmentDetails) {
		this.departmentDetails = departmentDetails;
	}

	public List<SpecificLocation> getSpecificLocation() {
		return specificLocation;
	}

	public void setSpecificLocation(List<SpecificLocation> specificLocation) {
		this.specificLocation = specificLocation;
	}

	public Integer getAllOrSpecific() {
		return allOrSpecific;
	}

	public void setAllOrSpecific(Integer allOrSpecific) {
		this.allOrSpecific = allOrSpecific;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getLocationTypeId() {
		return locationTypeId;
	}

	public void setLocationTypeId(Integer locationTypeId) {
		this.locationTypeId = locationTypeId;
	}

	public String getlType() {
		return lType;
	}

	public void setlType(String lType) {
		this.lType = lType;
	}

	public Integer getlTypeId() {
		return lTypeId;
	}

	public void setlTypeId(Integer lTypeId) {
		this.lTypeId = lTypeId;
	}

	public Integer getCreateUserAs() {
		return createUserAs;
	}

	public void setCreateUserAs(Integer createUserAs) {
		this.createUserAs = createUserAs;
	}

	public Integer getParentLocation() {
		return parentLocation;
	}

	public void setParentLocation(Integer parentLocation) {
		this.parentLocation = parentLocation;
	}

	public Integer getDesignationId() {
		return designationId;
	}

	public void setDesignationId(Integer designationId) {
		this.designationId = designationId;
	}

	public String getKioskType() {
		return kioskType;
	}

	public void setKioskType(String kioskType) {
		this.kioskType = kioskType;
	}

    public Boolean getTopLevelAdmin() {
        return isTopLevelAdmin;
    }

    public void setTopLevelAdmin(Boolean topLevelAdmin) {
        isTopLevelAdmin = topLevelAdmin;
    }

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	
}
