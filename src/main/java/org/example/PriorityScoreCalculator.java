package org.example;


import java.time.LocalDate;

public class PriorityScoreCalculator {

    private DateScoring dateScore;
    private ConditionScoreCalculator conditionScore;

    public PriorityScoreCalculator(DateScoring dateScore, ConditionScoreCalculator conditionScore) {
        this.dateScore = dateScore;
        this.conditionScore = conditionScore;
    }

    // Reusing the methods from the previous response
    private int calculatePriorityScore() {
        int dateScoreValue = dateScore.getDateScore(); // Assuming DateScoring has a getScore() method
        int conditionScoreValue = conditionScore.getConditionScore(); // Assuming ConditionScoreCalculator has a getScore() method
        // Weight condition score more heavily
        int weightedConditionScore = conditionScoreValue * 2;
        return dateScoreValue + weightedConditionScore;
    }


    private String determinePriority(int priorityScore) {
        if (priorityScore <= 3) {
            return "Low";
        } else if (priorityScore <= 6) {
            return "Medium";
        } else if (priorityScore <= 9) {
            return "High";
        } else {
            return "Critical";
        }
    }

    public String getPriority() {
        int priorityScore = calculatePriorityScore();
        return determinePriority(priorityScore);
    }

    public static void main(String[] args) {
        // Replace with actual DateScoring and ConditionScoreCalculator instances
        DateScoring dateScoring = new DateScoring(/* ... */);
        ConditionScoreCalculator conditionScoreCalculator = new ConditionScoreCalculator(/* ... */);

        PriorityScoreCalculator calculator = new PriorityScoreCalculator(dateScoring, conditionScoreCalculator);
        String priority = calculator.getPriority();
        System.out.println("Priority: " + priority);
    }
}