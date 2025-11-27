package edu.uwi.comp3607;

import edu.uwi.comp3607.engine.GameEngine;
import edu.uwi.comp3607.ui.ConsoleUI;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("To exit the game enter QUIT at any point");
            
            System.out.print("Please enter the path to the questions file (CSV, XML, or Json): ");
            String filePath = scanner.nextLine().trim().replace("\"", ""); // strip quotes

            File file = new File(filePath);

            GameEngine engine = new GameEngine("game_event_log.csv", filePath);
            ConsoleUI ui = new ConsoleUI(engine);

            ui.start(file); 
        }
    }
}