package com.serviceplus.metadata.dto;

public class UserProfile {

    private Data data;

    public static class Data {

        private Profile profile;

        private Integer status;

        private String token;

        public static class Profile {

            private String u_mobileNo;

            private String u_emailId;

            private Integer u_clcId;

            private String u_clcName;

            private String u_userName;

            private Integer u_designationId;

            private String u_cugType;

            private Boolean u_approvalRequired;

            private Integer ul_entityId;

            private Integer ul_locationId;

            private String ul_locationName;

            private Integer ul_entityLevelId;

            private String ul_entityLevelName;

            private Integer ul_categoryId;

            private String ld_signNo;

            public String getU_mobileNo() {
                return u_mobileNo;
            }

            public void setU_mobileNo(String u_mobileNo) {
                this.u_mobileNo = u_mobileNo;
            }

            public String getU_emailId() {
                return u_emailId;
            }

            public void setU_emailId(String u_emailId) {
                this.u_emailId = u_emailId;
            }

            public Integer getU_clcId() {
                return u_clcId;
            }

            public void setU_clcId(Integer u_clcId) {
                this.u_clcId = u_clcId;
            }

            public String getU_clcName() {
                return u_clcName;
            }

            public void setU_clcName(String u_clcName) {
                this.u_clcName = u_clcName;
            }

            public String getU_userName() {
                return u_userName;
            }

            public void setU_userName(String u_userName) {
                this.u_userName = u_userName;
            }

            public Integer getU_designationId() {
                return u_designationId;
            }

            public void setU_designationId(Integer u_designationId) {
                this.u_designationId = u_designationId;
            }

            public String getU_cugType() {
                return u_cugType;
            }

            public void setU_cugType(String u_cugType) {
                this.u_cugType = u_cugType;
            }

            public Boolean getU_approvalRequired() {
                return u_approvalRequired;
            }

            public void setU_approvalRequired(Boolean u_approvalRequired) {
                this.u_approvalRequired = u_approvalRequired;
            }

            public Integer getUl_entityId() {
                return ul_entityId;
            }

            public void setUl_entityId(Integer ul_entityId) {
                this.ul_entityId = ul_entityId;
            }

            public Integer getUl_locationId() {
                return ul_locationId;
            }

            public void setUl_locationId(Integer ul_locationId) {
                this.ul_locationId = ul_locationId;
            }

            public String getUl_locationName() {
                return ul_locationName;
            }

            public void setUl_locationName(String ul_locationName) {
                this.ul_locationName = ul_locationName;
            }

            public Integer getUl_entityLevelId() {
                return ul_entityLevelId;
            }

            public void setUl_entityLevelId(Integer ul_entityLevelId) {
                this.ul_entityLevelId = ul_entityLevelId;
            }

            public String getUl_entityLevelName() {
                return ul_entityLevelName;
            }

            public void setUl_entityLevelName(String ul_entityLevelName) {
                this.ul_entityLevelName = ul_entityLevelName;
            }

            public Integer getUl_categoryId() {
                return ul_categoryId;
            }

            public void setUl_categoryId(Integer ul_categoryId) {
                this.ul_categoryId = ul_categoryId;
            }

            @Override
            public String toString() {
                return "Profile{" +
                        "u_mobileNo='" + u_mobileNo + '\'' +
                        ", u_emailId='" + u_emailId + '\'' +
                        ", u_clcId=" + u_clcId +
                        ", u_clcName='" + u_clcName + '\'' +
                        ", u_userName='" + u_userName + '\'' +
                        ", u_designationId=" + u_designationId +
                        ", u_cugType='" + u_cugType + '\'' +
                        ", u_approvalRequired=" + u_approvalRequired +
                        ", ul_entityId=" + ul_entityId +
                        ", ul_locationId=" + ul_locationId +
                        ", ul_locationName='" + ul_locationName + '\'' +
                        ", ul_entityLevelId=" + ul_entityLevelId +
                        ", ul_entityLevelName='" + ul_entityLevelName + '\'' +
                        ", ul_categoryId=" + ul_categoryId +
                        '}';
            }

            public String getLd_signNo() {
                return ld_signNo;
            }

            public void setLd_signNo(String ld_signNo) {
                this.ld_signNo = ld_signNo;
            }
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}