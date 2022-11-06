package com.jemsa.adm_api;

import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdmApi {
   private final AdmService admService;

    private AdmApi(AdmService admService) {
       this.admService = admService;
    }

    @PostMapping ("/api/calculate")
    public DecisionResultDTO calculate(@RequestBody DecisionDTO decisionDTO) {
        return admService.calculateDecision(decisionDTO);
    }

    @ExceptionHandler(value = Exception.class)
        public ErrorResponseDTO handleException(Exception exception) {
            return new ErrorResponseDTO(exception.getMessage());
    }

    @ExceptionHandler(value = UncategorizedSQLException.class)
    public ErrorResponseDTO handleSqlException(UncategorizedSQLException sqlException) {
          String errorCode = new String(sqlException.getCause().getMessage());
        return new ErrorResponseDTO(errorCode.substring(11,errorCode.indexOf("\n")));
    }
}