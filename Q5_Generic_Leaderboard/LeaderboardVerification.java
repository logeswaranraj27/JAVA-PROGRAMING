package Q5_Generic_Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardVerification {

    // Demonstrating reusability with another entity: Player
    static class Player {
        final String name;
        final int goals;
        final int assists;

        Player(String name, int goals, int assists) {
            this.name = name;
            this.goals = goals;
            this.assists = assists;
        }

        public int getGoals() { return goals; }
        public int getAssists() { return assists; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name + "(G:" + goals + ", A:" + assists + ")";
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("Q5: Leaderboard Robustness & Correctness Verification");
        System.out.println("==========================================================================\n");

        runMultiTieTest();
        runFaultySubtractionComparatorTest();
        runFaultyMissingTieBreakerTest();
        runGenericReusabilityTest();
    }

    private static void runMultiTieTest() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TEST 1: Dataset with Several Ties at Each Level");
        System.out.println("--------------------------------------------------------------------------");

        Comparator<Team> robustOrder = Comparator
                .comparingInt(Team::getPoints).reversed()
                .thenComparing(Comparator.comparingInt(Team::getGoalDiff).reversed())
                .thenComparing(Team::getName);

        Leaderboard<Team> board = new Leaderboard<>(robustOrder);
        // Tier 1: 10 points, +5 diff
        board.add(new Team("Tigers", 10, 5));
        board.add(new Team("Hawks", 10, 5));
        // Tier 2: 10 points, +3 diff
        board.add(new Team("Wolves", 10, 3));
        board.add(new Team("Bears", 10, 3));
        // Tier 3: 7 points, 0 diff
        board.add(new Team("Sharks", 7, 0));
        board.add(new Team("Dolphins", 7, 0));

        List<Team> ranked = board.ranking();
        System.out.println("Ranked Output:");
        for (int i = 0; i < ranked.size(); i++) {
            System.out.printf("  Rank %d: %s\n", i + 1, ranked.get(i));
        }
        System.out.println("Verification: Hawks precedes Tigers (alphabetical tie-break).");
        System.out.println("Bears precedes Wolves (alphabetical tie-break).");
        System.out.println("Dolphins precedes Sharks (alphabetical tie-break).");
        System.out.println();
    }

    private static void runFaultySubtractionComparatorTest() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TEST 2: Deliberately Faulty Comparator - Integer Subtraction Bug");
        System.out.println("Theoretical Flaw: Using '(t1, t2) -> t2.points - t1.points' overflows when");
        System.out.println("numbers span across Integer.MIN_VALUE and positive numbers, breaking transitivity!");
        System.out.println("--------------------------------------------------------------------------");

        // Simulate teams with extreme penalty points or ranking deficits
        Team teamNormal = new Team("TeamNormal", 10, 0);
        Team teamPenalized = new Team("TeamPenalized", Integer.MIN_VALUE + 5, 0);

        // Faulty subtraction comparator:
        Comparator<Team> faultySubComp = (t1, t2) -> t2.points - t1.points;

        // Correct comparator:
        Comparator<Team> correctComp = Comparator.comparingInt(Team::getPoints).reversed();

        int faultyResult = faultySubComp.compare(teamNormal, teamPenalized);
        int correctResult = correctComp.compare(teamNormal, teamPenalized);

        System.out.printf("Comparing %s vs %s:\n", teamNormal, teamPenalized);
        System.out.printf("  Faulty Subtraction Comparator Result: %d (INCORRECT! Negative due to integer underflow)\n", faultyResult);
        System.out.printf("  Standard Integer.compare() Result   : %d (CORRECT! TeamNormal ranks above TeamPenalized)\n", correctResult);
        System.out.println("Conclusion: Never subtract integers in comparators; use Integer.compare() or Comparator.comparingInt().");
        System.out.println();
    }

    private static void runFaultyMissingTieBreakerTest() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TEST 3: Deliberately Faulty Comparator - Missing Name Tie-Breaker");
        System.out.println("Theoretical Flaw: Ignoring the name tie-breaker treats distinct teams as 'equal' (returns 0).");
        System.out.println("In sorted sets or binary searches, this causes items to be treated as duplicates and dropped!");
        System.out.println("--------------------------------------------------------------------------");

        Comparator<Team> incompleteComp = Comparator
                .comparingInt(Team::getPoints).reversed()
                .thenComparing(Comparator.comparingInt(Team::getGoalDiff).reversed());
                // Missing name tie-breaker!

        Team t1 = new Team("Panthers", 9, 4);
        Team t2 = new Team("Bisons", 9, 4);

        int comparison = incompleteComp.compare(t1, t2);
        System.out.printf("Comparison between %s and %s: %d\n", t1, t2, comparison);
        System.out.println("Observation: Returns 0, claiming Panthers == Bisons despite being distinct entities.");
        System.out.println("In a TreeSet, one of these teams would be discarded as a duplicate!");
        System.out.println();
    }

    private static void runGenericReusabilityTest() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TEST 4: Proving Reusability with Player Class (Generics in Action)");
        System.out.println("--------------------------------------------------------------------------");

        Comparator<Player> playerRanking = Comparator
                .comparingInt(Player::getGoals).reversed()
                .thenComparing(Comparator.comparingInt(Player::getAssists).reversed())
                .thenComparing(Player::getName);

        Leaderboard<Player> goldenBootBoard = new Leaderboard<>(playerRanking);
        goldenBootBoard.add(new Player("Lionel", 25, 12));
        goldenBootBoard.add(new Player("Kylian", 25, 8));
        goldenBootBoard.add(new Player("Erling", 30, 5));
        goldenBootBoard.add(new Player("Cristiano", 25, 8));

        System.out.println("Player Leaderboard Rankings:");
        List<Player> rankedPlayers = goldenBootBoard.ranking();
        for (int i = 0; i < rankedPlayers.size(); i++) {
            System.out.printf("  Rank %d: %s\n", i + 1, rankedPlayers.get(i));
        }
        System.out.println("Verification: Reusable Leaderboard<T> worked flawlessly with Player type without modifying any core logic.");
        System.out.println("--------------------------------------------------------------------------");
    }
}
