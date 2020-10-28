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
    private Long id;
    @NotNull(message = "Title should not be null")
    private String title;
    @NotNull(message = "Summary should not be null")
    private String summary;
    @NotNull(message = "Content should not be null")
    private String content;
    @NotNull(message = "Link should not be null")
    private String link;
    private Boolean approved;

    public Content() {}

    public Content(long id, String title, String summary, String content, String link,
    Boolean approved) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.link = link;
        this.approved = approved;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getSummary() { return summary; }

    public String getContent() { return content; }

    public String getLink() { return link; }

    public Boolean getApproved() { return approved; }

    public void setId(Long id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setSummary(String summary) { this.summary = summary; }

    public void setContent(String content) { this.content = content; }

    public void setLink(String link) { this.link = link; }

    public void setApproved(Boolean approved) { this.approved = approved; }
}