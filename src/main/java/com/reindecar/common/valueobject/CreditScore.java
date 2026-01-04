package com.reindecar.common.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditScore {

    @Column(name = "credit_score")
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_rating", length = 20)
    private CreditRating rating;

    private CreditScore(Integer score) {
        validateScore(score);
        this.score = score;
        this.rating = calculateRating(score);
    }

    public static CreditScore of(Integer score) {
        return new CreditScore(score);
    }

    public static CreditScore defaultScore() {
        return new CreditScore(1000);
    }

    public void updateScore(Integer newScore) {
        validateScore(newScore);
        this.score = newScore;
        this.rating = calculateRating(newScore);
    }

    private void validateScore(Integer score) {
        if (score == null) {
            throw new IllegalArgumentException("Credit score cannot be null");
        }
        if (score < 0 || score > 2000) {
            throw new IllegalArgumentException("Credit score must be between 0 and 2000");
        }
    }

    private CreditRating calculateRating(Integer score) {
        if (score >= 1600) {
            return CreditRating.EXCELLENT;
        } else if (score >= 1200) {
            return CreditRating.GOOD;
        } else if (score >= 800) {
            return CreditRating.FAIR;
        } else if (score >= 400) {
            return CreditRating.POOR;
        } else {
            return CreditRating.BAD;
        }
    }

    public boolean isExcellent() {
        return rating == CreditRating.EXCELLENT;
    }

    public boolean isGoodOrBetter() {
        return rating == CreditRating.EXCELLENT || rating == CreditRating.GOOD;
    }

    public boolean isBad() {
        return rating == CreditRating.BAD;
    }

    public enum CreditRating {
        EXCELLENT,
        GOOD,
        FAIR,
        POOR,
        BAD
    }
}
