package com.csdg1t3.ryverbankapi.content;

import java.util.List;

public interface ContentService {
    List<Content> listContent();
    Content getContent(Long id);

    /**
     * Return newly added content
     */
    Content addContent(Content content);

    /**
     * Return updated content
     * @param id
     * @param content
     * @return
     */
    Content updateContent(Long id, Content content);

    /**
     * Delete content. If unsuccessful, throws exception
     * @param id
     */
    void deleteContent(Long id);
}