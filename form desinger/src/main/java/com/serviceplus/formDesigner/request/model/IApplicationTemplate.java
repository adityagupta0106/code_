package com.serviceplus.formDesigner.request.model;

import java.util.Date;

public interface IApplicationTemplate {
    String getFormId();
    void setFormId(String formId);

    String getHolderId();
    void setHolderId(String holderId);

    String getFormName();
    void setFormName(String formName);

    Object getFormJsonData();
    void setFormJsonData(Object formJsonData);

    String getStatus();
    void setStatus(String status);

    Long getCreatedBy();
    void setCreatedBy(Long createdBy);

    Integer getStateId();
    void setStateId(Integer stateId);

    Integer getDepartmentId();
    void setDepartmentId(Integer departmentId);

    Integer getLocationId();
    void setLocationId(Integer locationId);

    Date getUpdateOn();
    void setUpdateOn(Date updateOn);

    Date getCreatedOn();
    void setCreatedOn(Date createdOn);

    String getInstantiable();
    void setInstantiable(String instantiable);

    String getEncryptionKey();
    void setEncryptionKey(String encryptionKey);

    String getEncryptionKeyAtRest();
    void setEncryptionKeyAtRest(String encryptionKeyAtRest);

    String getTenantId();
    void setTenantId(String tenantId);

}