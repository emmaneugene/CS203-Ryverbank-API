package com.csdg1t3.ryverbankapi.content;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Concrete class that implements ContentService
 */
@Service
public class ContentServiceImpl implements ContentService {
    private ContentRepository contents;

    public ContentServiceImpl(ContentRepository contents) {
        this.contents = contents;
    }
    
    @Override
    public List<Content> listContent() {
        return contents.findAll();
    }
    
    @Override
    public Content getContent(Long id) {
        Optional<Content> result = contents.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    @Override
    public Content addContent(Content newContent) {
        return contents.save(newContent);
    }

    @Override
    public Content updateContent(Long id, Content content) {
        Optional<Content> result = contents.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    @Override
    public void deleteContent(Long id) {
        contents.deleteById(id);
    }
    
}