package com.csdg1t3.ryverbankapi.content;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import com.csdg1t3.ryverbankapi.security.UserAuthenticator;
import com.csdg1t3.ryverbankapi.user.User;

/**
 * Controller that manages HTTP GET/POST/PUT/DELETE requests
 */
@RestController
public class ContentController {
    private ContentRepository contentRepo;
    private UserAuthenticator uAuth;

    public ContentController (ContentRepository contentRepo, UserAuthenticator uAuth) {
        this.contentRepo = contentRepo;
        this.uAuth = uAuth;
    }

    /**
     * Retrieve all content in the system. 
     * 
     * ROLE_MANAGER and ROLE_ANALYST can view all content,
     * while ROLE_USER can only view approved content
     * @return a List of all content
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/contents")
    public List<Content> getContents() {
        User authenticatedUser = uAuth.getAuthenticatedUser();
        
        if (authenticatedUser.getAuthorities().contains(uAuth.MANAGER) ||
            authenticatedUser.getAuthorities().contains(uAuth.ANALYST))
            return contentRepo.findAll();
        
        return contentRepo.findByApproved(true);
    }

    /**
     * Search for content with the given ID.
     * If there isn't a content with the given ID, throw ContentNotFoundException.
     * 
     * @param id ID of the content to be retrieved.
     * @return Content with the given ID.
     * @throws ContentNotFoundException If content ID is not found.
     * @throws ContentNotApprovedException If Content is not approved by a ROLE_ADMIN.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/contents/{id}")
    public Content getContent(@PathVariable Long id) {
        Optional<Content> result = contentRepo.findById(id);
        if (!result.isPresent()) {
            throw new ContentNotFoundException(id);
        }
        Content content = result.get();

        User authenticatedUser = uAuth.getAuthenticatedUser();
        
        if (authenticatedUser.getAuthorities().contains(uAuth.MANAGER) ||
            authenticatedUser.getAuthorities().contains(uAuth.ANALYST))
            return content;
        
        if (content.getApproved())
            return content;
        
        throw new ContentNotApprovedException(id);
    }

    /**
     * Creates a new content.
     * 
     * Only ROLE_MANAGER and ROLE_ANALYST can create new content, as validated in security config.
     * @param content The new content to be added into the database.
     * @return The content after being added into the database.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/contents")
    public Content createContent(@Valid @RequestBody Content content) {
        User authenticatedUser = uAuth.getAuthenticatedUser();
        
        if (!authenticatedUser.getAuthorities().contains(uAuth.MANAGER))
            content.setApproved(false);
        return contentRepo.save(content);
    }

    /**
     * Updates the content with the given ID with new details.
     * If there isn't a content with the given ID, throw ContentNotFoundException.
     * 
     * @param id The ID of the content to be changed.
     * @param newContent The new content.
     * @return The new content saved in the databased.
     * @throws ContentNotFoundException If content with the given ID is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/api/contents/{id}")
    public Content updateContent(@PathVariable Long id, @RequestBody Content newContent) {
        Optional<Content> result = contentRepo.findById(id);
        if (!result.isPresent()) {
            throw new ContentNotFoundException(id);
        }
        Content content = result.get();

        if (newContent.getTitle() != null)
            content.setTitle(newContent.getTitle());
        if (newContent.getSummary() != null)
            content.setSummary(newContent.getSummary());
        if (newContent.getContent() != null)
            content.setContent(newContent.getContent());
        if (newContent.getLink() != null)
            content.setLink(newContent.getLink());
        if (newContent.getApproved() != null)
            content.setApproved(newContent.getApproved());

        User authenticatedUser = uAuth.getAuthenticatedUser();
    
        if (!authenticatedUser.getAuthorities().contains(uAuth.MANAGER))
            content.setApproved(false);

        return contentRepo.save(content);
    }

    /**
     * Deletes content with the given ID.
     * If there isn't a content with the given ID, throw ContentNotFoundException.
     * 
     * @param id The ID of the content to be deleted.
     * @throws ContentNotFoundException If content with given ID is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/api/contents/{id}")
    public void deleteContent(@PathVariable Long id) {
        Optional<Content> result = contentRepo.findById(id);
        if (!result.isPresent()) {
            throw new ContentNotFoundException(id);
        }

        contentRepo.deleteById(id);
    }
}