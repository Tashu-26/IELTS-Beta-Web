package com.ieltsbeta.service.scoring;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * STRATEGY PATTERN — concrete strategy. The default (and currently only)
 * scoring algorithm: a fixed percentage-correct -> band lookup table. This
 * is a deliberate simplification, not the official IELTS conversion table
 * (which isn't publicly standardized per-question anyway) -- it exists so
 * the platform can give a consistent, explainable estimate.
 */
@Component
public class PercentageBandScoringStrategy implements BandScoringStrategy {

    @Override
    public BigDecimal toBand(double percentage) {
        double band;
        if (percentage >= 90) band = 9.0;
        else if (percentage >= 85) band = 8.5;
        else if (percentage >= 80) band = 8.0;
        else if (percentage >= 75) band = 7.5;
        else if (percentage >= 70) band = 7.0;
        else if (percentage >= 65) band = 6.5;
        else if (percentage >= 60) band = 6.0;
        else if (percentage >= 55) band = 5.5;
        else if (percentage >= 50) band = 5.0;
        else if (percentage >= 45) band = 4.5;
        else if (percentage >= 40) band = 4.0;
        else if (percentage >= 35) band = 3.5;
        else if (percentage >= 30) band = 3.0;
        else if (percentage >= 20) band = 2.5;
        else if (percentage >= 10) band = 2.0;
        else band = 1.0;
        return BigDecimal.valueOf(band).setScale(1, RoundingMode.HALF_UP);
    }
}
