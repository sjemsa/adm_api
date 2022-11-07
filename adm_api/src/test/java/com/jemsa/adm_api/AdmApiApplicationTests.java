package com.jemsa.adm_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.UncategorizedSQLException;

import java.math.BigDecimal;
import java.sql.SQLException;

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
	private final String customerWithMediumSegment  	= "49002010987";
	private final String customerWithHighSegment  		= "49002010998";
	private final String customerWithNoExternalCheck 	= "47901262217";


	@Test
	void getDecisionInvalidAmountTooSmall() throws UncategorizedSQLException {
		var personalCode = customerWithDebt;
		var loanAmount= BigDecimal.valueOf(1000);
		var loanPeriod = minimumPeriod;
		var expectedException = "ORA-20003: Invalid amount input. Minimum possible amount is 2000 and maximum amount is 10000!";
		UncategorizedSQLException sqlException = assertThrows(UncategorizedSQLException.class, () -> databaseRepository.getDecision(personalCode, loanAmount, loanPeriod));
		assertEquals(expectedException, sqlException.getMostSpecificCause().getMessage().split("\n")[0]);
	}
	//.indexOf("\n")


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
	void getDecisionCustomerSegmentCheckImpossible() throws SQLException{
		var personalCode = customerWithNoExternalCheck;
		var loanAmount= minimumAmount;
		var loanPeriod = minimumPeriod;
		var expectedException = "ORA-20005: Customer check is impossible! Please try again later!";
		UncategorizedSQLException sqlException = assertThrows(UncategorizedSQLException.class, () -> databaseRepository.getDecision(personalCode, loanAmount, loanPeriod));
		assertEquals(expectedException, sqlException.getMostSpecificCause().getMessage().split("\n")[0]);
	}

	@Test
	void getDecisionAmountTooBigReturnSmaller() {
		var personalCode = customerWithLowSegment;
		var loanAmount= maximumAmount;
		var loanPeriod = maximumPeriod;
		var expectedAmount = BigDecimal.valueOf(6000);
		DecisionResultDTO result  = databaseRepository.getDecision(personalCode, loanAmount, loanPeriod);
		assertEquals(result.getLoanAmount(), expectedAmount);
		assertEquals(result.getLoanPeriod(), loanPeriod);
	}

	@Test
	void getDecisionAmountTooSmallReturnMaximumPossible() {
		var personalCode = customerWithLowSegment;
		var loanAmount= minimumAmount;
		var loanPeriod = maximumPeriod;
		var expectedAmount = BigDecimal.valueOf(6000);
		DecisionResultDTO result  = databaseRepository.getDecision(personalCode, loanAmount, loanPeriod);
		assertEquals(result.getLoanAmount(), expectedAmount);
		assertEquals(result.getLoanPeriod(), loanPeriod);
	}

	@Test
	void getDecisionPeriodTooShortReturnLonger() {
		var personalCode = customerWithLowSegment;
		var loanAmount= BigDecimal.valueOf(2000);
		var loanPeriod = BigDecimal.valueOf(16);
		var expectedPeriod = BigDecimal.valueOf(20);
		DecisionResultDTO result  = databaseRepository.getDecision(personalCode, loanAmount, loanPeriod);
		assertEquals(result.getLoanAmount(), loanAmount);
		assertEquals(result.getLoanPeriod(), expectedPeriod);
	}

	@Test
	void getDecisionCustomerWithHighSegment() {
		var personalCode = customerWithHighSegment;
		var loanAmount= BigDecimal.valueOf(5000);
		var loanPeriod = BigDecimal.valueOf(24);
		var expectedAmount = BigDecimal.valueOf(10000);
		DecisionResultDTO result  = databaseRepository.getDecision(personalCode, loanAmount, loanPeriod);
		assertEquals(result.getLoanAmount(), expectedAmount);
		assertEquals(result.getLoanPeriod(), loanPeriod);
	}

	@Test
	void getDecisionCustomerWithMediumSegment() {
		var personalCode = customerWithMediumSegment;
		var loanAmount= BigDecimal.valueOf(2500);
		var loanPeriod = BigDecimal.valueOf(24);
		var expectedAmount = BigDecimal.valueOf(7200);
		DecisionResultDTO result  = databaseRepository.getDecision(personalCode, loanAmount, loanPeriod);
		assertEquals(result.getLoanAmount(), expectedAmount);
		assertEquals(result.getLoanPeriod(), loanPeriod);
	}

}
