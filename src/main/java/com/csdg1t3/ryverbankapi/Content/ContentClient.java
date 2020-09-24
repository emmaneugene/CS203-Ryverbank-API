package com.csdg1t3.ryverbankapi.Content;

import java.beans.BeanProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ContentClient{
    private RestTemplate template;

    @Autowired
    void setTemplate(final RestTemplate template) {
        this.template = template;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
    * Get contant with given URI and id
    *
    * @param URI
    * @param id
    * @return
    */
    public Content getContent(final String URI, final long id) {
        final Content content = template.getForObject(URI + "/" + id, Content.class);
        return content;
    }

    /**
     * Add new content
     * 
     * @param URI
     * @param newContent
     * @return
     */
    public Content addContente(final String URI, final Content newContent) {
        final Content toReturn = template.postForObject(URI, newContent, Content.class);
        return toReturn;
    }
}