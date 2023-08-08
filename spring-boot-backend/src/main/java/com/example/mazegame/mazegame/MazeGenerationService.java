package com.example.mazegame.mazegame;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MazeGenerationService {

    private int mazeWidth;
    private int mazeHeight;
    private int[][] maze;
    private Stack<int[]> stack;
    private int pathWidth;
    private int visitedCells;
    private int[] startPoint;
    private int[] endPoint;

    public MazeGenerationService(
            @Value("${maze.width}") int width,
            @Value("${maze.height}") int height,
            @Value("${maze.pathWidth}") int pathWidth
    ) {
        this.mazeWidth = width;
        this.mazeHeight = height;
        this.pathWidth = pathWidth;
        this.maze = new int[mazeHeight][mazeWidth];
        this.stack = new Stack<>();
    }

    private int[] generateRandomPoint() {
        int x = new Random().nextInt(mazeWidth);
        int y = new Random().nextInt(mazeHeight);
        return new int[]{x, y};
    }

    private void generateStartAndEndPoints() {
        startPoint = generateRandomPoint();

        do {
            endPoint = generateRandomPoint();
        } while (Arrays.equals(startPoint, endPoint)); // Ensure start and end points are not the same

        maze[startPoint[1]][startPoint[0]] |= CELL_START;
        maze[endPoint[1]][endPoint[0]] |= CELL_END;
    }

    private void generateMazeWithSolutionPath() {
        int[] startPoint = findCellWithFlag(CELL_START);
        int[] endPoint = findCellWithFlag(CELL_END);

        stack.clear();
        stack.push(startPoint);
        visitedCells = 1;

        // Modified Maze generation loop to ensure path between start and end points
        while (visitedCells < mazeWidth * mazeHeight) {
            int[] currentCell = stack.peek();
            List<Integer> neighbours = new ArrayList<>();

            // North neighbour
            if (currentCell[1] > 0 && (maze[currentCell[1] - 1][currentCell[0]] & CELL_VISITED) == 0) {
                neighbours.add(0);
            }
            // East neighbour
            if (currentCell[0] < mazeWidth - 1 && (maze[currentCell[1]][currentCell[0] + 1] & CELL_VISITED) == 0) {
                neighbours.add(1);
            }
            // South neighbour
            if (currentCell[1] < mazeHeight - 1 && (maze[currentCell[1] + 1][currentCell[0]] & CELL_VISITED) == 0) {
                neighbours.add(2);
            }
            // West neighbour
            if (currentCell[0] > 0 && (maze[currentCell[1]][currentCell[0] - 1] & CELL_VISITED) == 0) {
                neighbours.add(3);
            }

            if (!neighbours.isEmpty()) {
                int nextCellDir = neighbours.get(new Random().nextInt(neighbours.size()));

                switch (nextCellDir) {
                    case 0: // North
                        maze[currentCell[1] - 1][currentCell[0]] |= CELL_VISITED | CELL_PATH_S;
                        maze[currentCell[1]][currentCell[0]] |= CELL_PATH_N;
                        stack.push(new int[]{currentCell[0], currentCell[1] - 1});
                        break;
                    case 1: // East
                        maze[currentCell[1]][currentCell[0] + 1] |= CELL_VISITED | CELL_PATH_W;
                        maze[currentCell[1]][currentCell[0]] |= CELL_PATH_E;
                        stack.push(new int[]{currentCell[0] + 1, currentCell[1]});
                        break;
                    case 2: // South
                        maze[currentCell[1] + 1][currentCell[0]] |= CELL_VISITED | CELL_PATH_N;
                        maze[currentCell[1]][currentCell[0]] |= CELL_PATH_S;
                        stack.push(new int[]{currentCell[0], currentCell[1] + 1});
                        break;
                    case 3: // West
                        maze[currentCell[1]][currentCell[0] - 1] |= CELL_VISITED | CELL_PATH_E;
                        maze[currentCell[1]][currentCell[0]] |= CELL_PATH_W;
                        stack.push(new int[]{currentCell[0] - 1, currentCell[1]});
                        break;
                }

                visitedCells++;
            } else {
                // If stack is empty, restart generation from a new point
                if (stack.isEmpty()) {
                    int[] newStartPoint = findUnvisitedCell();
                    stack.push(newStartPoint);
                    visitedCells++;
                } else {
                    stack.pop();
                }
            }

            // Break if the end point is reached
            if (Arrays.equals(stack.peek(), endPoint)) {
                break;
            }
        }
    }

    private int[] findCellWithFlag(int flag) {
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                if ((maze[y][x] & flag) != 0) {
                    return new int[]{x, y};
                }
            }
        }
        throw new IllegalStateException("Start or end point not found");
    }

    private int[] findUnvisitedCell() {
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                if ((maze[y][x] & CELL_VISITED) == 0) {
                    return new int[]{x, y};
                }
            }
        }
        throw new IllegalStateException("No unvisited cell found");
    }






    private List<int[]> findSolutionPath(int[] start, int[] end, int[][] maze) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fScore));
        Map<int[], int[]> cameFrom = new HashMap<>();
        Map<int[], Double> gScores = new HashMap<>();

        openSet.add(new Node(start, 0, heuristic(start, end)));
        gScores.put(start, 0.0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (Arrays.equals(current.position, end)) {
                return reconstructPath(cameFrom, current.position);
            }

            for (int[] neighbor : getNeighbors(current.position, maze)) {
                double tentativeGScore = gScores.getOrDefault(current.position, Double.MAX_VALUE) + 1;

                if (tentativeGScore < gScores.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current.position);
                    gScores.put(neighbor, tentativeGScore);

                    double fScore = tentativeGScore + heuristic(neighbor, end);
                    openSet.add(new Node(neighbor, tentativeGScore, fScore));
                }
            }
        }

        return new ArrayList<>();
    }

    private List<int[]> getNeighbors(int[] position, int[][] maze) {
        List<int[]> neighbors = new ArrayList<>();
        int x = position[0];
        int y = position[1];

        if (isValidCell(x - 1, y, maze)) neighbors.add(new int[]{x - 1, y});
        if (isValidCell(x + 1, y, maze)) neighbors.add(new int[]{x + 1, y});
        if (isValidCell(x, y - 1, maze)) neighbors.add(new int[]{x, y - 1});
        if (isValidCell(x, y + 1, maze)) neighbors.add(new int[]{x, y + 1});

        return neighbors;
    }

    private boolean isValidCell(int x, int y, int[][] maze) {
        return x >= 0 && x < maze[0].length && y >= 0 && y < maze.length && (maze[y][x] & CELL_VISITED) != 0;
    }

    private double heuristic(int[] start, int[] end) {
        return Math.sqrt(Math.pow(end[0] - start[0], 2) + Math.pow(end[1] - start[1], 2));
    }

    private List<int[]> reconstructPath(Map<int[], int[]> cameFrom, int[] current) {
        List<int[]> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private static class Node {
        int[] position;
        double gScore;
        double fScore;

        Node(int[] position, double gScore, double fScore) {
            this.position = position;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }

    public int[][] generateMaze() {
        while (true) {
            // Initialize maze and stack
            for (int y = 0; y < mazeHeight; y++) {
                for (int x = 0; x < mazeWidth; x++) {
                    maze[y][x] = 0;
                }
            }

            // Generate start and end points
            generateStartAndEndPoints();

            // Perform maze generation with solution path
            generateMazeWithSolutionPath();

            // Check solution path length using A* search algorithm
            List<int[]> solutionPath = findSolutionPath(startPoint, endPoint, maze);
            if (solutionPath.size() >= 5) {
                // Maze is valid, exit the loop
                break;
            }
        }

        return maze;
    }

    // Bit fields for convenience
    private static final int CELL_PATH_N = 0x01;
    private static final int CELL_PATH_E = 0x02;
    private static final int CELL_PATH_S = 0x04;
    private static final int CELL_PATH_W = 0x08;
    private static final int CELL_VISITED = 0x10;
    private static final int CELL_START = 0x20;
    private static final int CELL_END = 0x40;

}
