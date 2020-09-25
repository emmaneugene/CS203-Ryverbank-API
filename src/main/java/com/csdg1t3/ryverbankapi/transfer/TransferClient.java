package com.csdg1t3.ryverbankapi.transfer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransferClient{
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
    public Transfer getTransfer(final String URI, final long id) {
        final Transfer transfer = template.getForObject(URI + "/" + id, Transfer.class);
        return transfer;
    }

    /**
     * Add new transfer
     * 
     * @param URI
     * @param newContent
     * @return
     */
    public Transfer addTransfer(final String URI, final Transfer newTransfer) {
        final Transfer toReturn = template.postForObject(URI, newTransfer, Transfer.class);
        return toReturn;
    }
}