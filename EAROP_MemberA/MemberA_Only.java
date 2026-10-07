import java.util.*;

public class MemberA_Only {

    static int[][] costMatrix = {
        {0, 15, 25, 35},
        {15, 0, 30, 28},
        {25, 30, 0, 20},
        {35, 28, 20, 0}
    };

    static String[] locations = {
        "Hospital",
        "Emergency Location B",
        "Emergency Location C",
        "Emergency Location D"
    };

    static int minBacktrackCost = Integer.MAX_VALUE;
    static String bestBacktrackPath = "";
    static int minDnCCost = Integer.MAX_VALUE;
    static String bestDncPath = "";

    // === DATA VALIDATION METHOD ===
    public static String validateCostMatrix(int[][] dist) {
        if (dist == null || dist.length == 0) {
            return "Invalid: Cost matrix is null or empty.";
        }
        int n = dist.length;
        for (int i = 0; i < n; i++) {
            if (dist[i] == null || dist[i].length != n) {
                return "Invalid: Cost matrix must be a non-empty square matrix (N x N).";
            }
            for (int j = 0; j < n; j++) {
                if (dist[i][j] < 0) {
                    return "Invalid: Cost matrix contains negative travel cost at [" + i + "][" + j + "].";
                }
            }
        }
        return null; // Matrix valid
    }

    public static void main(String[] args) {
        System.out.println("=== MEMBER A: ROUTE OPTIMIZATION ALGORITHMS ===\n");

        System.out.println("--- 1. GREEDY ALGORITHM ---");
        System.out.println(greedyEAROP(costMatrix));
        System.out.println();

        System.out.println("--- 2. DIVIDE AND CONQUER ALGORITHM ---");
        System.out.println(divideAndConquerEAROP(costMatrix));
        System.out.println();

        System.out.println("--- 3. DYNAMIC PROGRAMMING ALGORITHM ---");
        System.out.println(dynamicProgrammingEAROP(costMatrix));
        System.out.println();

        System.out.println("--- 4. BACKTRACKING ALGORITHM ---");
        System.out.println(backtrackingEAROP(costMatrix));
        System.out.println();
    }

    public static String greedyEAROP(int[][] dist) {
        String errorMsg = validateCostMatrix(dist);
        if (errorMsg != null) return errorMsg;

        int n = dist.length;
        boolean[] visited = new boolean[n];
        int current = 0;
        visited[current] = true;
        
        StringBuilder path = new StringBuilder(locations[current]);
        int totalCost = 0;

        for (int step = 0; step < n - 1; step++) {
            int nextNode = -1;
            int minCost = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!visited[i] && dist[current][i] < minCost) {
                    minCost = dist[current][i];
                    nextNode = i;
                }
            }

            if (nextNode != -1) {
                visited[nextNode] = true;
                totalCost += minCost;
                path.append(" -> ").append(locations[nextNode]);
                current = nextNode;
            }
        }

        totalCost += dist[current][0];
        path.append(" -> ").append(locations[0]);

        return "Route: " + path.toString() + "\nTotal Cost: " + totalCost;
    }

    public static String divideAndConquerEAROP(int[][] dist) {
        String errorMsg = validateCostMatrix(dist);
        if (errorMsg != null) return errorMsg;

        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true;

        minDnCCost = Integer.MAX_VALUE;
        bestDncPath = "";

        StringBuilder path = new StringBuilder(locations[0]);
        divideAndConquerHelper(0, visited, 0, dist, n, path);

        return "Route: " + bestDncPath + "\nTotal Cost: " + minDnCCost;
    }

    private static int divideAndConquerHelper(int pos, boolean[] visited, int currentCost, int[][] dist, int n, StringBuilder path) {
        if (allVisited(visited)) {
            int totalCost = currentCost + dist[pos][0];
            if (totalCost < minDnCCost) {
                minDnCCost = totalCost;
                bestDncPath = path.toString() + " -> " + locations[0];
            }
            return totalCost;
        }

        for (int next = 0; next < n; next++) {
            if (!visited[next]) {
                visited[next] = true;
                int lenBefore = path.length();
                path.append(" -> ").append(locations[next]);

                divideAndConquerHelper(next, visited, currentCost + dist[pos][next], dist, n, path);

                path.setLength(lenBefore);
                visited[next] = false;
            }
        }
        return minDnCCost;
    }

    private static boolean allVisited(boolean[] visited) {
        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    public static String dynamicProgrammingEAROP(int[][] dist) {
        String errorMsg = validateCostMatrix(dist);
        if (errorMsg != null) return errorMsg;

        int n = dist.length;
        int VISITED_ALL = (1 << n) - 1;
        int[][] memo = new int[n][1 << n];
        String[][] paths = new String[n][1 << n];

        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        int minCost = dynamicProgrammingEAROPHelper(0, 1, dist, memo, VISITED_ALL, paths);
        String fullPath = locations[0] + paths[0][1];

        return "Route: " + fullPath + "\nTotal Cost: " + minCost;
    }

    private static int dynamicProgrammingEAROPHelper(int pos, int mask, int[][] dist, int[][] memo, int VISITED_ALL, String[][] paths) {
        if (mask == VISITED_ALL) {
            paths[pos][mask] = " -> " + locations[0];
            return dist[pos][0];
        }

        if (memo[pos][mask] != -1) {
            return memo[pos][mask];
        }

        int ans = Integer.MAX_VALUE;
        String bestSubPath = "";

        for (int next = 0; next < dist.length; next++) {
            if ((mask & (1 << next)) == 0) {
                int newCost = dist[pos][next] + dynamicProgrammingEAROPHelper(next, mask | (1 << next), dist, memo, VISITED_ALL, paths);

                if (newCost < ans) {
                    ans = newCost;
                    bestSubPath = " -> " + locations[next] + paths[next][mask | (1 << next)];
                }
            }
        }

        paths[pos][mask] = bestSubPath;
        return memo[pos][mask] = ans;
    }

    public static String backtrackingEAROP(int[][] dist) {
        String errorMsg = validateCostMatrix(dist);
        if (errorMsg != null) return errorMsg;

        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true;

        minBacktrackCost = Integer.MAX_VALUE;
        bestBacktrackPath = "";

        StringBuilder path = new StringBuilder(locations[0]);
        earopBacktracking(0, dist, visited, n, 1, 0, path);

        return "Route: " + bestBacktrackPath + "\nTotal Cost: " + minBacktrackCost;
    }

    private static int earopBacktracking(int pos, int[][] dist, boolean[] visited, int n, int count, int cost, StringBuilder path) {
        if (count == n) {
            int totalCost = cost + dist[pos][0];
            if (totalCost < minBacktrackCost) {
                minBacktrackCost = totalCost;
                bestBacktrackPath = path.toString() + " -> " + locations[0];
            }
            return totalCost;
        }

        for (int next = 0; next < n; next++) {
            if (!visited[next]) {
                visited[next] = true;
                int lenBefore = path.length();
                path.append(" -> ").append(locations[next]);

                earopBacktracking(next, dist, visited, n, count + 1, cost + dist[pos][next], path);

                path.setLength(lenBefore);
                visited[next] = false;
            }
        }
        return minBacktrackCost;
    }
}
