package com.csdg1t3.ryverbankapi.content;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Allows us to store content as persisten data through JPA.
 * Methods do not have to by explicitly declared, supports save(), findBy() and delete()
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long>{
    Content save(Content content);
    void deleteById(Long id);
    List<Content> findAll();
    List<Content> findByApproved(Boolean approved);
    Optional<Content> findById(Long id);
       
}