/**
 * 
 */
package com.prabal.loanservice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @author Prabal Nandi
 *
 */
public class InterestCalculator {
	private static final BigDecimal NUM_DAYS_IN_A_YEAR = new BigDecimal(365);

	/**
	 * Calculate Interest
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param principalAmount
	 * @param interestRate
	 * @return Interest+Principal
	 */
	public static String calculateSimpleInterest(LocalDate fromDate, LocalDate toDate, String principalAmountStr,
			String interestRateStr) {

		// Recommended to use String in BigDecimal instead of float or double
		BigDecimal principalAmount = new BigDecimal(principalAmountStr);
		BigDecimal interestRate = new BigDecimal(interestRateStr);

		long days = java.time.temporal.ChronoUnit.DAYS.between(fromDate, toDate);

		BigDecimal numOfDays = new BigDecimal(days).divide(NUM_DAYS_IN_A_YEAR, 5, RoundingMode.HALF_EVEN);

		BigDecimal interestInDecimal = interestRate.multiply(new BigDecimal("0.01")).setScale(2,
				RoundingMode.HALF_EVEN);

		// Days * interest rate * (0.01) * principal amount
		return numOfDays.multiply(interestInDecimal).multiply(principalAmount).add(principalAmount)
				.setScale(2, RoundingMode.HALF_EVEN).toString();
	}
}
