package com.jemsa.adm_api;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Repository
public class AdmDatabaseRepository {
    private SimpleJdbcCall simpleJdbcCall;

       public AdmDatabaseRepository(DataSource dataSource) {
        this.simpleJdbcCall = new SimpleJdbcCall(dataSource).withSchemaName("DECISION_MAKER").withCatalogName("API").withProcedureName("GET_DECISION");
        }

      public DecisionResultDTO getDecision(String personalCode, BigDecimal loanAmount, BigDecimal loanPeriod) {
            SqlParameterSource in = new MapSqlParameterSource().addValue("I_PERSONAL_CODE", personalCode)
                                                               .addValue("IO_LOAN_AMOUNT", loanAmount)
                                                               .addValue("IO_LOAN_PERIOD", loanPeriod);
          var result = simpleJdbcCall.execute(in);

          var resultDTO = new DecisionResultDTO();
          resultDTO.setLoanAmount((BigDecimal)result.get("IO_LOAN_AMOUNT"));
          resultDTO.setLoanPeriod((BigDecimal)result.get("IO_LOAN_PERIOD"));
          return resultDTO;
      }
}