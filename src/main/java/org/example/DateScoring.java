package org.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateScoring {

    public static int calculateScore(LocalDate submissionDate) {
        LocalDate currentDate = LocalDate.now();
        long daysDifference = ChronoUnit.DAYS.between(submissionDate, currentDate);

        if (daysDifference <= 7) {
            return 1; // Low
        } else if (daysDifference <= 15) {
            return 2; // Medium
        } else {
            return 3; // High
        }
    }

    public static void main(String[] args) {
        // Example usage
        LocalDate submissionDate = LocalDate.of(2022, 8, 15);
        int dateScore = calculateScore(submissionDate);
        System.out.println("Score: " + dateScore);
    }

    public int getDateScore() {
        return calculateScore(LocalDate.of(2022, 8, 15));
    }
}
