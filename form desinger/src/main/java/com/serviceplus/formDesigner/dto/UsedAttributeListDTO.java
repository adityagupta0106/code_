package com.serviceplus.formDesigner.dto;

import java.util.HashSet;
import java.util.Set;

public class UsedAttributeListDTO {

    private Set<String> mvelUsedAttrList = new HashSet<>();
    private Set<String> documentUsedAttrList = new HashSet<>();
    private Set<String> paymentUsedAttrList = new HashSet<>();
    private Set<String> inboxSentboxFiltersUsedAttrList = new HashSet<>();
    private Set<String> notificationUsedAttrList = new HashSet<>();


    public Set<String> getMvelUsedAttrList() {
        return mvelUsedAttrList;
    }

    public void setMvelUsedAttrList(Set<String> mvelUsedAttrList) {
        this.mvelUsedAttrList = mvelUsedAttrList;
    }

    public Set<String> getDocumentUsedAttrList() {
        return documentUsedAttrList;
    }

    public void setDocumentUsedAttrList(Set<String> documentUsedAttrList) {
        this.documentUsedAttrList = documentUsedAttrList;
    }

    public Set<String> getPaymentUsedAttrList() {
        return paymentUsedAttrList;
    }

    public void setPaymentUsedAttrList(Set<String> paymentUsedAttrList) {
        this.paymentUsedAttrList = paymentUsedAttrList;
    }

    public Set<String> getInboxSentboxFiltersUsedAttrList() {
        return inboxSentboxFiltersUsedAttrList;
    }

    public void setInboxSentboxFiltersUsedAttrList(Set<String> inboxSentboxFiltersUsedAttrList) {
        this.inboxSentboxFiltersUsedAttrList = inboxSentboxFiltersUsedAttrList;
    }

    public Set<String> getNotificationUsedAttrList() {
        return notificationUsedAttrList;
    }

    public void setNotificationUsedAttrList(Set<String> notificationUsedAttrList) {
        this.notificationUsedAttrList = notificationUsedAttrList;
    }
}