import java.util.*;

public class EmergencyAmbulanceOptimization {

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
        System.out.println("==================================================");
        System.out.println("   EMERGENCY AMBULANCE ROUTE OPTIMIZATION (EAROP) ");
        System.out.println("==================================================\n");

        System.out.println("=== PART 1: ROUTE OPTIMIZATION (MEMBER A) ===");
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

        System.out.println("=== PART 2: SORTING & SEARCHING (MEMBER B) ===");
        int[] responseTimes = {8, 3, 5, 1, 9, 2};
        System.out.println("Original Array: " + Arrays.toString(responseTimes));
        insertionSort(responseTimes);
        System.out.println("Sorted Array (Insertion Sort): " + Arrays.toString(responseTimes));
        System.out.println("Binary Search (Response Time 5 found at index): " + binarySearch(responseTimes, 5));
        System.out.println("Binary Search (Response Time 7 found at index): " + binarySearch(responseTimes, 7));
        System.out.println();

        System.out.println("=== PART 3: ADVANCED DATA STRUCTURES (MEMBER B) ===");
        System.out.println("--- 1. MIN-HEAP DEMO ---");
        MinHeap heap = new MinHeap();
        heap.insert(10);
        heap.insert(3);
        heap.insert(15);
        System.out.print("Current Min-Heap structure elements: ");
        heap.printHeap();
        System.out.println("Min-Heap Extract Min: " + heap.extractMin());
        System.out.print("Min-Heap structure after extraction: ");
        heap.printHeap();
        System.out.println();

        System.out.println("--- 2. SPLAY TREE DEMO ---");
        SplayTree tree = new SplayTree();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.printRoot();
        System.out.println("Splay Tree Search (10 found): " + tree.search(10));
        tree.printRoot();
        System.out.println("\n==================================================");
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
        for (boolean v : visited) if (!v) return false;
        return true;
    }

    public static String dynamicProgrammingEAROP(int[][] dist) {
        String errorMsg = validateCostMatrix(dist);
        if (errorMsg != null) return errorMsg;

        int n = dist.length;
        int VISITED_ALL = (1 << n) - 1;
        int[][] memo = new int[n][1 << n];
        String[][] paths = new String[n][1 << n];
        for (int[] row : memo) Arrays.fill(row, -1);

        int minCost = dynamicProgrammingEAROPHelper(0, 1, dist, memo, VISITED_ALL, paths);
        String fullPath = locations[0] + paths[0][1];
        return "Route: " + fullPath + "\nTotal Cost: " + minCost;
    }

    private static int dynamicProgrammingEAROPHelper(int pos, int mask, int[][] dist, int[][] memo, int VISITED_ALL, String[][] paths) {
        if (mask == VISITED_ALL) {
            paths[pos][mask] = " -> " + locations[0];
            return dist[pos][0];
        }
        if (memo[pos][mask] != -1) return memo[pos][mask];

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

    public static String insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
        return Arrays.toString(arr);
    }

    public static String binarySearch(int[] arr, int target) {
        if (arr == null || arr.length == 0) return "Invalid: array is empty";
        for (int i = 1; i < arr.length; i++) {
            if (arr[i - 1] > arr[i]) return "Invalid: array must be sorted";
        }
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) return String.valueOf(mid);
            else if (arr[mid] < target) low = mid + 1;
            else high = mid - 1;
        }
        return "Not found";
    }

    static class MinHeap {
        private ArrayList<Integer> heap = new ArrayList<>();
        private int getParentIndex(int i) { return (i - 1) / 2; }
        private int getLeftChildIndex(int i) { return (2 * i) + 1; }
        private int getRightChildIndex(int i) { return (2 * i) + 2; }
        private void swap(int i1, int i2) {
            int temp = heap.get(i1);
            heap.set(i1, heap.get(i2));
            heap.set(i2, temp);
        }
        public void insert(int element) {
            heap.add(element);
            heapifyUp(heap.size() - 1);
        }
        private void heapifyUp(int index) {
            while (index > 0 && heap.get(index) < heap.get(getParentIndex(index))) {
                swap(index, getParentIndex(index));
                index = getParentIndex(index);
            }
        }
        public int extractMin() {
            if (heap.isEmpty()) return -1;
            int minVal = heap.get(0);
            int lastVal = heap.remove(heap.size() - 1);
            if (!heap.isEmpty()) {
                heap.set(0, lastVal);
                heapifyDown(0);
            }
            return minVal;
        }
        private void heapifyDown(int index) {
            int smallest = index, left = getLeftChildIndex(index), right = getRightChildIndex(index);
            if (left < heap.size() && heap.get(left) < heap.get(smallest)) smallest = left;
            if (right < heap.size() && heap.get(right) < heap.get(smallest)) smallest = right;
            if (smallest != index) {
                swap(index, smallest);
                heapifyDown(smallest);
            }
        }
        public void printHeap() { System.out.println(heap.toString()); }
    }

    static class SplayTree {
        static class Node {
            int key;
            Node left, right;
            Node(int key) { this.key = key; this.left = this.right = null; }
        }
        private Node root;
        private Node rightRotate(Node x) {
            Node y = x.left; x.left = y.right; y.right = x; return y;
        }
        private Node leftRotate(Node x) {
            Node y = x.right; x.right = y.left; y.left = x; return y;
        }
        private Node splay(Node root, int key) {
            if (root == null || root.key == key) return root;
            if (root.key > key) {
                if (root.left == null) return root;
                if (root.left.key > key) {
                    root.left.left = splay(root.left.left, key);
                    root = rightRotate(root);
                } else if (root.left.key < key) {
                    root.left.right = splay(root.left.right, key);
                    if (root.left.right != null) root.left = leftRotate(root.left);
                }
                return (root.left == null) ? root : rightRotate(root);
            } else {
                if (root.right == null) return root;
                if (root.right.key > key) {
                    root.right.left = splay(root.right.left, key);
                    if (root.right.left != null) root.right = rightRotate(root.right);
                } else if (root.right.key < key) {
                    root.right.right = splay(root.right.right, key);
                    root = leftRotate(root);
                }
                return (root.right == null) ? root : leftRotate(root);
            }
        }
        public boolean search(int key) {
            root = splay(root, key);
            return (root != null && root.key == key);
        }
        public void insert(int key) {
            if (root == null) { root = new Node(key); return; }
            root = splay(root, key);
            if (root.key == key) return;
            Node newNode = new Node(key);
            if (root.key > key) {
                newNode.right = root; newNode.left = root.left; root.left = null;
            } else {
                newNode.left = root; newNode.right = root.right; root.right = null;
            }
            root = newNode;
        }
        public void printRoot() {
            if (root != null) System.out.println("Current Splay Tree Root node is: " + root.key);
            else System.out.println("Tree is empty");
        }
    }
}
