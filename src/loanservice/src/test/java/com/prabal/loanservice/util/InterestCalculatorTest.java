/**
 * 
 */
package com.prabal.loanservice.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Prabal Nandi
 *
 */
class InterestCalculatorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		String interest = InterestCalculator.calculateSimpleInterest(LocalDate.now(), LocalDate.now().plusYears(1),
				"1000", "10");
		assertTrue("1100.00".equals(interest));

	}

}
