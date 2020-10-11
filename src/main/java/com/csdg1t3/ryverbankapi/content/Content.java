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
    @NotNull(message = "Title should not be null")
    private String title;
    @NotNull(message = "Summary should not be null")
    private String summary;
    @NotNull(message = "Content should not be null")
    private String content;
    @NotNull(message = "Link should not be null")
    private String link;
    private Boolean approval;

    public Content() {}

    public Content(long Id, String title, String summary, String content, String link,
    Boolean approval) {
        this.Id = Id;
        this.title = title;
        this.summary = summary;
        this.content = content;
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

    public String getContent() {
        return content;
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

    public void setContent(String content) {
        this.content = content;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setApproval(Boolean approval) {
        this.approval = approval;
    }
}