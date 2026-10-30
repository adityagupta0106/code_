package com.serviceplus.metadata.dto;


import java.util.List;

public class FetchRoleResponse {

    private Data data;

    public FetchRoleResponse(Data data) {
        this.data = data;
    }

    public FetchRoleResponse() {

    }

    public static class Data{

        private List<UserSessionDTO.Roles> roles;

        public Data(List<UserSessionDTO.Roles> roles) {
            this.roles = roles;
        }

        public Data() {

        }

        public List<UserSessionDTO.Roles> getRoles() {
            return roles;
        }

        public void setRoles(List<UserSessionDTO.Roles> roles) {
            this.roles = roles;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
