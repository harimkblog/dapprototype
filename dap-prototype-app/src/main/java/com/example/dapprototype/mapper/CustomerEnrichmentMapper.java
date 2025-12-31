package com.example.dapprototype.mapper;

import com.example.dapprototype.model.CustomerEnrichment;
import com.example.dapprototype.model.RequestPayload;
import org.springframework.stereotype.Component;

@Component
public class CustomerEnrichmentMapper {

    /**
     * Maps RequestPayload to CustomerEnrichment
     * Extracts activityId from the nested requestInfo object
     * 
     * @param requestPayload the source object
     * @return CustomerEnrichment object with mapped attributes
     */
    public CustomerEnrichment mapToCustomerEnrichment(RequestPayload requestPayload) {
        if (requestPayload == null) {
            return null;
        }
        
        CustomerEnrichment enrichment = new CustomerEnrichment(requestPayload.getRequestInfo().getActivityId());

        return enrichment;
    }
}
