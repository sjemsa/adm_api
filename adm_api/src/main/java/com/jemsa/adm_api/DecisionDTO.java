package com.jemsa.adm_api;

import java.math.BigDecimal;

public class DecisionDTO {

    private String     personalCode;
    private BigDecimal loanAmount;
    private BigDecimal loanPeriod;

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setLoanPeriod(BigDecimal loanPeriod) {
        this.loanPeriod = loanPeriod;
    }

    public String getPersonalCode() {
        return personalCode;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal getLoanPeriod() {
        return loanPeriod;
    }
}
