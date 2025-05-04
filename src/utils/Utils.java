package utils;

import entity.CellType;
import entity.GenericCell;
import entity.Node;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public final class Utils {

    public static final String INPUT_PATH = "input/input.txt";
    public static final String OUTPUT_PATH = "output/output.txt";
    public static final String EMPTY_SPACE = " ";

    public static final int NUMBER_OF_ITERATIONS = 2689;

    private Utils() {}

    private static final int[][] directions = {
            {-1, 0}, // sus
            {1, 0},  // jos
            {0, -1}, // stanga
            {0, 1},   // dreapta
            {-1, -1}, // sus-stânga
            {-1, 1},  // sus-dreapta
            {1, -1},  // jos-stânga
            {1, 1}    // jos-dreapta
    };

    public static List<Queue<Pair<Integer, Integer>>> findShortestPaths(GenericCell[][] map,
                                                                        Pair<Integer, Integer> start,
                                                                        Pair<Integer, Integer> end,
                                                                        int maxPaths) {
        int n = map.length;
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        boolean[][] visited = new boolean[n][n];
        List<Queue<Pair<Integer, Integer>>> foundPaths = new LinkedList<>();
        Map<Pair<Integer, Integer>, Queue<Pair<Integer, Integer>>> pathMap = new HashMap<>();

        queue.offer(start);
        pathMap.put(start, new LinkedList<>(List.of(start)));

        while (!queue.isEmpty() && foundPaths.size() < maxPaths) {
            int size = queue.size();
            boolean[][] levelVisited = new boolean[n][n];

            for (int i = 0; i < size; i++) {
                Pair<Integer, Integer> currentCell = queue.poll();
                int x = currentCell.getFirst();
                int y = currentCell.getSecond();

                if (x == end.getFirst() && y == end.getSecond()) {
                    pathMap.get(currentCell).poll();
                    foundPaths.add(pathMap.get(currentCell));
                    continue; // găsit un drum, dar continuăm
                }

                for (int[] direction : directions) {
                    int newX = x + direction[0];
                    int newY = y + direction[1];

                    if (newX >= 0 && newY >= 0 && newX < n && newY < n
                            && !map[newX][newY].getCellType().equals(CellType.OBSTACLE)
                            && !visited[newX][newY] && !levelVisited[newX][newY]) {

                        Pair<Integer, Integer> nextCell = new Pair<>(newX, newY);
                        queue.offer(nextCell);

                        Queue<Pair<Integer, Integer>> newPath = new LinkedList<>(pathMap.get(currentCell));
                        newPath.add(nextCell);
                        pathMap.put(nextCell, newPath);

                        levelVisited[newX][newY] = true;
                    }
                }
            }

            // marcăm nodurile de pe nivelul actual ca vizitate după ce toate au fost procesate
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (levelVisited[i][j]) {
                        visited[i][j] = true;
                    }
                }
            }
        }

        return foundPaths;
    }
}
