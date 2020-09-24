package com.csdg1t3.ryverbankapi.Content;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {
    private ContentService contentService;

    public ContentController (ContentService cs) {
        this.contentService = cs;
    }

    /**
     * List all content in the system
     * @return list of all content
     */
    @GetMapping("/content")
    public List<Content> getContent() {
        return contentService.listContent();
    }

    /**
     * Search for content with the given id
     * If there isn't one with the given id, throw ContentNotFoundException
     * @param id
     * @return content with the given id
     */
    @GetMapping("/content/{id}")
    public Content getContent(@PathVariable Long id) {
        Content content = contentService.getContent(id);
        
        // Handle "content not found" error using appropriate http codes
        if (content == null) throw new ContentNotFoundException(id);
        return contentService.getContent(id);
    }

    /**
     * Add new content with POST request to "/content"
     * Note the use of @RequestBody
     * @param content
     * @return list of all content
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/content")
    public Content addContent(@RequestBody Content content) {
        return contentService.addContent(content);
    }

    /**
     * If there isn't a content with given id, throw ContentNotFoundException
     * @param id
     * @param newContentInfo
     * @return updated content
     */
    @PutMapping("/content/{id}")
    public Content updateContent(@PathVariable Long id, @RequestBody Content newContentInfo) {
        Content content = contentService.updateContent(id, newContentInfo);

        if (content == null) throw new ContentNotFoundException(id);

        return content;
    }

    /**
     * Remove content with the DELETE request to "/content/{id}"
     * If there isn't a content with the given id, throw ContentNotFoundException
     * @param id
     */
    @DeleteMapping("/content/{id}")
    public void deleteContent(@PathVariable Long id) {
        if (contentService.deleteContent(id) == 0) throw new ContentNotFoundException(id);
    }
}