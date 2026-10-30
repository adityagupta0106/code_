package com.serviceplus.metadata.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowAssignmentTokenRequest {

    private Integer serviceId;

    private Map<String, TaskAssignmentNode> node;

    public WorkflowAssignmentTokenRequest(){
        this.node = new HashMap<>();
    }

    public static class TaskAssignmentNode {

        private String taskName;

        private String locationId;

        private String locationName;

        private List<Users> users;

        public static class Users{

            private Long id;
            private String name;
            private List<String> holderIds;
            private PreviousAssignment history;

            public static class PreviousAssignment {

                private Long id;
                private String name;

                public Long getId() {
                    return id;
                }

                public void setId(Long id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<String> getHolderIds() {
                return holderIds;
            }

            public void setHolderIds(List<String> holderIds) {
                this.holderIds = holderIds;
            }

            public PreviousAssignment getHistory() {
                return history;
            }

            public void setHistory(PreviousAssignment history) {
                this.history = history;
            }
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public List<Users> getUsers() {
            return users;
        }

        public void setUsers(List<Users> users) {
            this.users = users;
        }

        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Map<String, TaskAssignmentNode> getNode() {
        return node;
    }

    public void setNode(Map<String, TaskAssignmentNode> node) {
        this.node = node;
    }
}