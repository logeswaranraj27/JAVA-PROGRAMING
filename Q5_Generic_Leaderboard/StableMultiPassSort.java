package Q5_Generic_Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Alternative Concept for Q5:
 * Demonstrating 3-Pass Stable Sorting vs Single Chained Comparator.
 *
 * Principle: Because TimSort (Java's default sort for Objects) is STABLE,
 * sorting by least significant criterion first (Name), then intermediate (GoalDiff),
 * then most significant criterion (Points) preserves the relative order of earlier passes.
 */
public class StableMultiPassSort {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("Q5 Alternative Concept: Multi-Pass Stable Sort vs Chained Comparator");
        System.out.println("==========================================================================\n");

        List<Team> teams = new ArrayList<>();
        teams.add(new Team("Panthers", 9, 4));
        teams.add(new Team("Eagles", 9, 6));
        teams.add(new Team("Cobras", 6, 1));
        teams.add(new Team("Bisons", 9, 4));

        System.out.println("Original Teams: " + teams);

        // Pass 1: Sort by Name (Ascending) - Least significant key
        teams.sort(Comparator.comparing(Team::getName));
        System.out.println("After Pass 1 (Name): " + teams);

        // Pass 2: Sort by Goal Difference (Descending) - Intermediate key (Stable sort preserves Name order on ties)
        teams.sort(Comparator.comparingInt(Team::getGoalDiff).reversed());
        System.out.println("After Pass 2 (GoalDiff): " + teams);

        // Pass 3: Sort by Points (Descending) - Most significant key (Stable sort preserves GoalDiff and Name order on ties)
        teams.sort(Comparator.comparingInt(Team::getPoints).reversed());
        System.out.println("After Pass 3 (Points): " + teams);

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Complexity & Architectural Comparison:");
        System.out.println("1. Multi-Pass Stable Sort:");
        System.out.println("   - Time Complexity: 3 x O(N log N). Performs 3 full sorting passes.");
        System.out.println("   - Memory: Modifies list in-place or allocates multiple passes.");
        System.out.println("   - Fragility: Relies strictly on sorting stability. If an unstable sort (like Dual-Pivot Quicksort)");
        System.out.println("     is used, the entire ordering collapses.");
        System.out.println("2. Single Chained Comparator (Recommended):");
        System.out.println("   - Time Complexity: Exactly 1 x O(N log N). Evaluates tie-breakers only when needed.");
        System.out.println("   - Reusability: Extensively reusable via Comparator.comparing().thenComparing().");
        System.out.println("   - Decoupled from entity classes (Player, College, QuizTeam can define custom comparators).");
        System.out.println("--------------------------------------------------------------------------");
    }
}

