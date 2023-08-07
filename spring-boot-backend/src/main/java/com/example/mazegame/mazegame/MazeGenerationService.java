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

    public int[][] generateMaze() {
        // Initialize maze and stack
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                maze[y][x] = 0;
            }
        }

        int startX = new Random().nextInt(mazeWidth);
        int startY = new Random().nextInt(mazeHeight);
        stack.push(new int[]{startX, startY});
        maze[startY][startX] = CELL_VISITED;
        visitedCells = 1;

        // Maze generation loop
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
                stack.pop();
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
}
