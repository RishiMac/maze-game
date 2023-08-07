package com.example.mazegame.mazegame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
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
