package com.csdg1t3.ryverbankapi.Content;

public class Content {
    private Long conID;
    private String conTitle;
    private String conSummary;
    private String conBody;
    private String conLink;
    private Boolean conApproval;

    public Content(Long conID, String conTitle, String conSummary, String conBody, String conLink, Boolean conApproval) {
        this.conID = conID;
        this.conTitle = conTitle;
        this.conSummary = conSummary;
        this.conBody = conBody;
        this.conLink = conLink;
        this.conApproval = conApproval;
    }

    public Long getContentID() {
        return conID;
    }

    public String getContentTitle() {
        return conTitle;
    }

    public String getContentSummary() {
        return conSummary;
    }

    public String getContentBody() {
        return conBody;
    }

    public String getContentLink() {
        return conLink;
    }

    public Boolean getContentApproval() {
        return conApproval;
    }

    public void setContentID(Long conID) {
        this.conID = conID;
    }

    public void setContentTitle(String conTitle) {
        this.conTitle = conTitle;
    }

    public void setContentSummary(String conSummary) {
        this.conSummary = conSummary;
    }

    public void setContentBody(String conBody) {
        this.conBody = conBody;
    }

    public void setContentLink(String conLink) {
        this.conLink = conLink;
    }

    public void setContentApproval(Boolean conApproval) {
        this.conApproval = conApproval;
    }
}