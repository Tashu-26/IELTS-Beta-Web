package com.ieltsbeta.service.scoring;

import java.math.BigDecimal;

/**
 * STRATEGY PATTERN — the algorithm interface. TestAttemptService (the
 * "context") holds a reference to one BandScoringStrategy and delegates the
 * percentage -> band-score conversion to it, instead of hard-coding the
 * formula inline. Swapping in a different marking scheme (e.g. a stricter
 * curve for an "Academic" test category, or the real IELTS conversion table
 * if it's ever licensed) means writing a new class here and wiring it in --
 * TestAttemptService itself never changes.
 */
public interface BandScoringStrategy {

    /**
     * Converts a raw percentage of marks correct (0-100) into an IELTS-style
     * band score in 0.5 increments (0.0-9.0).
     */
    BigDecimal toBand(double percentage);
}
