package com.jemsa.adm_api;

import java.math.BigDecimal;

public class DecisionResultDTO {

    private BigDecimal loanAmount;
    private BigDecimal loanPeriod;

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setLoanPeriod(BigDecimal loanPeriod) {
        this.loanPeriod = loanPeriod;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal getLoanPeriod() {
        return loanPeriod;
    }
}
