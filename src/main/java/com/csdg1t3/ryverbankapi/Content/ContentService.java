package com.csdg1t3.ryverbankapi.Content;

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
     * Return status of the delete
     * If 1, the content has been removed
     * If 0, the content does not exist
     * @param id
     * @return
     */
    int deleteContent(Long id);
}