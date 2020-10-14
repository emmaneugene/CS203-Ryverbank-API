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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import javax.validation.Valid;

/**
 * Controller that manages HTTP GET/POST/PUT/DELETE requests
 */
@RestController
public class ContentController {
    private ContentRepository contentRepo;

    public ContentController (ContentRepository contentRepo) {
        this.contentRepo = contentRepo;
    }

    /**
     * List all content in the system. ROLE_MANAGER and ROLE_ANALYST can view all content,
     * while ROLE_USER can only view approved content
     * @return list of all content
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/contents")
    public List<Content> getContent() {
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        Collection<? extends GrantedAuthority> uAuthorities = uDetails.getAuthorities();
        if (uAuthorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER")) || 
            uAuthorities.contains(new SimpleGrantedAuthority("ROLE_ANALYST")))
            return contentRepo.findAll();
        
        return contentRepo.findByApproval(true);
    }

    /**
     * Search for content with the given id
     * If there isn't one with the given id, throw ContentNotFoundException
     * @param id
     * @return content with the given id
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/contents/{id}")
    public Content getContent(@PathVariable Long id) {
        Optional<Content> result = contentRepo.findById(id);
        if (!result.isPresent()) {
            throw new ContentNotFoundException(id);
        }
        Content content = result.get();

        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        Collection<? extends GrantedAuthority> uAuthorities = uDetails.getAuthorities();

        if (uAuthorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER")) || 
        uAuthorities.contains(new SimpleGrantedAuthority("ROLE_ANALYST")))
            return content;
        
        if (content.getApproval())
            return content;
        
        throw new ContentNotApprovedException(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/contents")
    public Content createContent(@Valid @RequestBody Content content) {
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        Collection<? extends GrantedAuthority> uAuthorities = uDetails.getAuthorities();

        if (!uAuthorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER")))
            content.setApproval(false);
        return contentRepo.save(content);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/contents/{id}")
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
        if (newContent.getApproval() != null)
            content.setApproval(newContent.getApproval());

        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
        Collection<? extends GrantedAuthority> uAuthorities = uDetails.getAuthorities();
        if (!uAuthorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER")))
            content.setApproval(false);

        return contentRepo.save(content);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/contents/{id}")
    public void deleteContent(@PathVariable Long id) {
        Optional<Content> result = contentRepo.findById(id);
        if (!result.isPresent()) {
            throw new ContentNotFoundException(id);
        }

        contentRepo.deleteById(id);
    }
}