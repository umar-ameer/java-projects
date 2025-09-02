import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGameP1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random rand = new Random();

        boolean playAgain = true;
        int totalScore = 0;
        int round = 0;

        while (playAgain) {
            round++;
            // Difficulty selection
            System.out.println("\n=== Round " + round + " ===");
            System.out.println("Choose difficulty level:");
            System.out.println("1. Easy (10 attempts)");
            System.out.println("2. Medium (5 attempts)");
            System.out.println("3. Hard (3 attempts)");
            System.out.print("Enter choice (1/2/3): ");

            int attempts;
            int multiplier;
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Defaulting to Medium.");
                scanner.next();
                attempts = 5; multiplier = 2;
            } else {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1: attempts = 10; multiplier = 1; break;
                    case 2: attempts = 5;  multiplier = 2; break;
                    case 3: attempts = 3;  multiplier = 3; break;
                    default:
                        System.out.println("Invalid choice. Defaulting to Medium.");
                        attempts = 5; multiplier = 2;
                }
            }

            int numberToGuess = rand.nextInt(100) + 1; // 1–100
            boolean guessed = false;
            int remaining = attempts;

            System.out.println("\nGame started. Guess a number between 1 and 100. You have " + attempts + " attempts.");

            for (int i = 0; i < attempts; i++) {
                System.out.print("Enter your guess: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // clear invalid token
                    i--;             // don’t count invalid input
                    continue;
                }

                int guess = scanner.nextInt();

                if (guess == numberToGuess) {
                    guessed = true;
                    remaining = attempts - i - 1; // attempts left after correct guess
                    System.out.println("Correct! You guessed the number.");
                    break;
                } else if (guess < numberToGuess) {
                    System.out.println("Too low.");
                } else {
                    System.out.println("Too high.");
                }
            }

            int roundScore = 0;
            if (guessed) {
                roundScore = 10 * multiplier + (remaining * 2 * multiplier);
                totalScore += roundScore;
                System.out.println("Round score: " + roundScore + " (remaining attempts: " + remaining + ")");
            } else {
                System.out.println("Out of attempts. The number was: " + numberToGuess);
                System.out.println("Round score: " + roundScore);
            }

            System.out.println("Total score so far: " + totalScore);

            // Replay?
            System.out.print("\nDo you want to play again? (yes/no): ");
            String response = scanner.next().toLowerCase();
            playAgain = response.equals("yes") || response.equals("y");
        }

        System.out.println("\nThanks for playing. Your final score is: " + totalScore);
        scanner.close();
    }
}