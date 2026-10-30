package com.serviceplus.metadata.dto;

import java.util.List;
import java.util.Map;

public class FileViewResponse {

    private Map<String, Data> results;

    public Map<String, Data> getResults() {
        return results;
    }

    public void setResults(Map<String, Data> results) {
        this.results = results;
    }

    public static class Data {
        private List<TypedSignedUrl> data;

        public List<TypedSignedUrl> getData() {
            return data;
        }

        public void setData(List<TypedSignedUrl> data) {
            this.data = data;
        }

        public static class TypedSignedUrl {

            private String type;
            private SignedUrlResponse data;

            public TypedSignedUrl(String type, SignedUrlResponse data) {
                this.type = type;
                this.data = data;
            }

            public String getType() {
                return type;
            }

            public SignedUrlResponse getData() {
                return data;
            }
        }

    }


}
