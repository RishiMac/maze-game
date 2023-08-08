package com.example.mazegame.mazegame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin; // Import this

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Replace with the actual origin of your React app
public class MazeController {

    private final MazeGenerationService mazeGenerationService;

    @Autowired
    public MazeController(MazeGenerationService mazeGenerationService) {
        this.mazeGenerationService = mazeGenerationService;
    }

    @GetMapping("/generate-maze")
    public int[][] generateMaze() {
        return mazeGenerationService.generateMaze();
    }
}
