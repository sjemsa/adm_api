package com.jemsa.adm_api;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AdmService {
    private final AdmDatabaseRepository admDatabaseRepository;

    public AdmService(AdmDatabaseRepository admDatabaseRepository) {
        this.admDatabaseRepository = admDatabaseRepository;
    }

    public DecisionResultDTO calculateDecision(@RequestBody DecisionDTO decisionDTO){
       return admDatabaseRepository.getDecision(decisionDTO.getPersonalCode(),
                                    decisionDTO.getLoanAmount(),
                                    decisionDTO.getLoanPeriod());
    }
}
