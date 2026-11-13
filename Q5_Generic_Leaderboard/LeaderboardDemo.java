package Q5_Generic_Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Team {
    final String name;
    final int points;
    final int goalDiff;

    Team(String name, int points, int goalDiff) {
        this.name = name;
        this.points = points;
        this.goalDiff = goalDiff;
    }

    public String getName() { return name; }
    public int getPoints() { return points; }
    public int getGoalDiff() { return goalDiff; }

    @Override
    public String toString() {
        return name + "(" + points + "," + goalDiff + ")";
    }
}

class Leaderboard<T> {
    private final List<T> items = new ArrayList<>();
    private final Comparator<? super T> order;

    Leaderboard(Comparator<? super T> order) {
        this.order = order;
    }

    void add(T item) {
        items.add(item);
    }

    List<T> ranking() {
        // TODO 1: Return a new sorted copy of items using order, without modifying items
        List<T> sortedCopy = new ArrayList<>(items);
        sortedCopy.sort(order);
        return sortedCopy;
    }
}

public class LeaderboardDemo {
    public static void main(String[] args) {
        // TODO 2: Build the comparator: points (high first), then goal difference (high
        // first), then name (A-Z)
        Comparator<Team> byRank = Comparator
                .comparingInt(Team::getPoints).reversed()
                .thenComparing(Comparator.comparingInt(Team::getGoalDiff).reversed())
                .thenComparing(Team::getName);

        Leaderboard<Team> board = new Leaderboard<>(byRank);
        board.add(new Team("Panthers", 9, 4));
        board.add(new Team("Eagles", 9, 6));
        board.add(new Team("Cobras", 6, 1));
        board.add(new Team("Bisons", 9, 4));

        System.out.println("Actual output:");
        System.out.println(board.ranking());

        System.out.println("\nExpected output:");
        System.out.println("[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]");

        System.out.println("\nExplanation of Ranking:");
        System.out.println("1. Eagles is ranked 1st because it has 9 points and highest goal difference (+6).");
        System.out.println("2. Bisons and Panthers both have 9 points and equal goal difference (+4).");
        System.out.println("   The tertiary tie-breaker resolves alphabetically by team name.");
        System.out.println("   'Bisons' precedes 'Panthers' lexicographically ('B' < 'P'), placing Bisons 2nd and Panthers 3rd.");
        System.out.println("3. Cobras is ranked 4th due to having the lowest points (6 points).");
    }
}

