package ticketmanagement.dao;

public class ConditionScoreCalculator {

    public static int calculateConditionScore(String conditions) {
        switch (conditions.toLowerCase()) {
            case "monthly air filter change":
            case "monthly pest control":
                return 1; // Low
            case "pest control":
            case "broken blinds":
                return 2; // Medium
            case "flooding":
            case "broken hvac unit":
                return 3; // High
            default:
                return 0; // Unknown condition
        }
    }

    public static void main(String[] args) {
        String conditions = "flooding";
        int conditionScore = calculateConditionScore(conditions);
        System.out.println("Score: " + conditionScore);
    }

    public static int getConditionScore() {
        return calculateConditionScore("monthly air filter change");
    }

}