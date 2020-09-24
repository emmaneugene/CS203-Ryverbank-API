package com.csdg1t3.ryverbankapi.content;

import java.util.List;
import java.util.Optional;

/**
 * Data access object
 */
public interface ContentRepository {
    Long save(Content content);
    int update(Content content);
    int deleteByID(Long id);
    List<Content> findAll();

    Optional<Content> findByID(Long id);
}