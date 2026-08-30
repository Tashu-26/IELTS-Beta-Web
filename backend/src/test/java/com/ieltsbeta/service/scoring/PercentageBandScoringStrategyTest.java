package com.ieltsbeta.service.scoring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit tests for the concrete Strategy (no mocking needed -- the whole
 * point of Strategy is that the algorithm is isolated and independently
 * testable). Exercises every branch of the percentage -> band lookup table.
 */
class PercentageBandScoringStrategyTest {

    private final BandScoringStrategy strategy = new PercentageBandScoringStrategy();

    @ParameterizedTest
    @CsvSource({
            "100, 9.0",
            "90,  9.0",
            "85,  8.5",
            "80,  8.0",
            "75,  7.5",
            "70,  7.0",
            "65,  6.5",
            "60,  6.0",
            "55,  5.5",
            "50,  5.0",
            "45,  4.5",
            "40,  4.0",
            "35,  3.5",
            "30,  3.0",
            "20,  2.5",
            "10,  2.0",
            "5,   1.0",
            "0,   1.0"
    })
    void mapsPercentageToExpectedBand(double percentage, String expectedBand) {
        BigDecimal result = strategy.toBand(percentage);
        assertThat(result).isEqualByComparingTo(new BigDecimal(expectedBand));
    }

    @Test
    void resultAlwaysHasOneDecimalScale() {
        assertThat(strategy.toBand(72.3).scale()).isEqualTo(1);
    }

    @Test
    void justBelowAThresholdFallsToTheLowerBand() {
        // 84.9% should NOT round up to the 85 -> 8.5 bucket
        assertThat(strategy.toBand(84.9)).isEqualByComparingTo(new BigDecimal("8.0"));
    }
}
