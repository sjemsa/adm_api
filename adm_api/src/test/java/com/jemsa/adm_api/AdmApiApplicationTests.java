package com.jemsa.adm_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.UncategorizedSQLException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdmApiApplicationTests {
	@Autowired
	private AdmDatabaseRepository databaseRepository;

	private final BigDecimal minimumAmount 				= BigDecimal.valueOf(2000);
	private final BigDecimal maximumAmount 				= BigDecimal.valueOf(10000);
	private final BigDecimal minimumPeriod 				= BigDecimal.valueOf(12);
	private final BigDecimal maximumPeriod 				= BigDecimal.valueOf(60);
	private final String customerWithDebt  				= "49002010965";
	private final String customerWithLowSegment  		= "49002010976";


	@Test
	void getDecisionInvalidAmountTooSmall() throws UncategorizedSQLException {
		var personalCode = customerWithDebt;
		var loanAmount= BigDecimal.valueOf(1000);
		var loanPeriod = minimumPeriod;
		var expectedException = "ORA-20003: Invalid amount input. Minimum possible amount is 2000 and maximum amount is 10000!";
		UncategorizedSQLException sqlException = assertThrows(UncategorizedSQLException.class, () -> databaseRepository.getDecision(personalCode, loanAmount, loanPeriod));
		assertEquals(expectedException, sqlException.getMostSpecificCause().getMessage().split("\n")[0]);
	}

	@Test
	void getDecisionInvalidAmountTooBig() throws UncategorizedSQLException{
		var personalCode = customerWithDebt;
		var loanAmount= BigDecimal.valueOf(11000);
		var loanPeriod = minimumPeriod;
		var expectedException = "ORA-20003: Invalid amount input. Minimum possible amount is 2000 and maximum amount is 10000!";
		UncategorizedSQLException sqlException = assertThrows(UncategorizedSQLException.class, () -> databaseRepository.getDecision(personalCode, loanAmount, loanPeriod));
		assertEquals(expectedException, sqlException.getMostSpecificCause().getMessage().split("\n")[0]);
	}

	@Test
	void getDecisionInvalidPeriodTooShort() throws UncategorizedSQLException{
		var personalCode = customerWithDebt;
		var loanAmount= minimumAmount;
		var loanPeriod = BigDecimal.valueOf(11);
		var expectedException = "ORA-20004: Invalid period input. Minimum possible period is 12 months and maximum period is 60 months!";
		UncategorizedSQLException sqlException = assertThrows(UncategorizedSQLException.class, () -> databaseRepository.getDecision(personalCode, loanAmount, loanPeriod));
		assertEquals(expectedException, sqlException.getMostSpecificCause().getMessage().split("\n")[0]);
	}

	@Test
	void getDecisionInvalidPeriodTooLong() throws UncategorizedSQLException{
		var personalCode = customerWithDebt;
		var loanAmount= minimumAmount;
		var loanPeriod = BigDecimal.valueOf(61);
		var expectedException = "ORA-20004: Invalid period input. Minimum possible period is 12 months and maximum period is 60 months!";
		UncategorizedSQLException sqlException = assertThrows(UncategorizedSQLException.class, () -> databaseRepository.getDecision(personalCode, loanAmount, loanPeriod));
		assertEquals(expectedException, sqlException.getMostSpecificCause().getMessage().split("\n")[0]);
	}

	@Test
	void getDecisionCustomerWithDebt() throws UncategorizedSQLException{
		var personalCode = customerWithDebt;
		var loanAmount= minimumAmount;
		var loanPeriod = minimumPeriod;
		var expectedException = "ORA-20006: Customer has debt! Decision NEGATIVE!";
		UncategorizedSQLException sqlException = assertThrows(UncategorizedSQLException.class, () -> databaseRepository.getDecision(personalCode, loanAmount, loanPeriod));
		assertEquals(expectedException, sqlException.getMostSpecificCause().getMessage().split("\n")[0]);
	}

	@Test
	void getDecisionCustomerSegmentCheckImpossible() throws UncategorizedSQLException {
		String customerWithNoExternalCheck = "47901262217";
		var personalCode = customerWithNoExternalCheck;
		var loanAmount= minimumAmount;
		var loanPeriod = minimumPeriod;
		var expectedException = "ORA-20005: Customer check is impossible! Please try again later!";
		UncategorizedSQLException sqlException = assertThrows(UncategorizedSQLException.class, () -> databaseRepository.getDecision(personalCode, loanAmount, loanPeriod));
		assertEquals(expectedException, sqlException.getMostSpecificCause().getMessage().split("\n")[0]);
	}

	@Test
	void getDecisionAmountTooBigReturnSmaller() {
		DecisionResultDTO result  = databaseRepository.getDecision(customerWithLowSegment, maximumAmount, maximumPeriod);
		assertEquals(BigDecimal.valueOf(6000), result.getLoanAmount());
		assertEquals(maximumPeriod, result.getLoanPeriod());
	}

	@Test
	void getDecisionAmountTooSmallReturnMaximumPossible() {
		DecisionResultDTO result  = databaseRepository.getDecision(customerWithLowSegment, minimumAmount, maximumPeriod);
		assertEquals(BigDecimal.valueOf(6000), result.getLoanAmount());
		assertEquals(maximumPeriod, result.getLoanPeriod());
	}

	@Test
	void getDecisionPeriodTooShortReturnLonger() {
		DecisionResultDTO result  = databaseRepository.getDecision(customerWithLowSegment, BigDecimal.valueOf(2000), BigDecimal.valueOf(16));
		assertEquals(BigDecimal.valueOf(2000), result.getLoanAmount());
		assertEquals(BigDecimal.valueOf(20), result.getLoanPeriod());
	}

	@Test
	void getDecisionCustomerWithHighSegment() {
		String customerWithHighSegment = "49002010998";
		DecisionResultDTO result  = databaseRepository.getDecision(customerWithHighSegment, BigDecimal.valueOf(5000), BigDecimal.valueOf(24));
		assertEquals(BigDecimal.valueOf(10000), result.getLoanAmount());
		assertEquals(BigDecimal.valueOf(24), result.getLoanPeriod());
	}

	@Test
	void getDecisionCustomerWithMediumSegment() {
		String customerWithMediumSegment = "49002010987";
		DecisionResultDTO result  = databaseRepository.getDecision(customerWithMediumSegment, BigDecimal.valueOf(2500), BigDecimal.valueOf(24));
		assertEquals(BigDecimal.valueOf(7200), result.getLoanAmount());
		assertEquals(BigDecimal.valueOf(24), result.getLoanPeriod());
	}

}
