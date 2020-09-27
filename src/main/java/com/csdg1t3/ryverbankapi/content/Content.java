package com.csdg1t3.ryverbankapi.content;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * POJO that stores content shared on the online banking app
 */
@Entity
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String title;
    private String summary;
    private String body;
    private String link;
    private Boolean approval;

    public Content(Long Id, String title, String summary, String body, String link,
    Boolean approval) {
        this.Id = Id;
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.link = link;
        this.approval = approval;
    }

    public Long getId() {
        return Id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getBody() {
        return body;
    }

    public String getLink() {
        return link;
    }

    public Boolean getApproval() {
        return approval;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setApproval(Boolean approval) {
        this.approval = approval;
    }
}