
/**
 * Mastermind
 *
 * A simple Mastermind game using digits instead of colours.
 * Features:
 *  - User chooses number of digits (2-10)
 *  - Computer generates unique digits
 *  - User guesses until correct or max guesses reached
 *  - Cheater mode
 *  - Hint after 3 incorrect guesses (checksum)
 *  - Hint after 6 incorrect guesses (one digit revealed)
 *  - Max 12 guesses per game
 *  - Average guesses reported at end
 */

import java.util.Scanner;
import java.util.Random;

public class Mastermind {

    /** Cheater mode allowed as a class variable */
    private static boolean cheaterMode = false;

    /** Keyboard scanner allowed as class variable */
    private static Scanner keyboard = new Scanner(System.in);

    /**
     * Print title and instructions.
     */
    public static void printTitle() {
        System.out.println("===============================================");
        System.out.println("Mastermind");
        System.out.println("===============================================");
        System.out.println();
        System.out.println("Instructions:");
        System.out.println("- Choose number of digits (2-10).");
        System.out.println("- All digits in the secret are unique.");
        System.out.println("- Enter guesses without spaces (e.g., 5023).");
        System.out.println("- You get hints after 3 and 6 wrong guesses.");
        System.out.println("- You have at most 12 guesses.");
        System.out.println();
    }

    /**
     * Ask user for integer between min and max.
     */
    public static int promptInt(String message, int min, int max) {
        int value = -1;
        boolean valid = false;

        while (!valid) {
            System.out.print(message);
            String line = keyboard.nextLine();

            boolean isNumber = true;
            for (int i = 0; i < line.length(); i++) {
                if (!Character.isDigit(line.charAt(i))) {
                    isNumber = false;
                }
            }

            if (isNumber) {
                value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    valid = true;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("Please enter digits only.");
            }
        }

        return value;
    }

    /**
     * Generate unique secret digits.
     */
    public static int[] generateSecret(int size) {
        int[] secret = new int[size];
        boolean[] used = new boolean[10];
        Random r = new Random();
        int count = 0;

        while (count < size) {
            int digit = r.nextInt(10);
            if (!used[digit]) {
                used[digit] = true;
                secret[count] = digit;
                count++;
            }
        }

        return secret;
    }

    /**
     * Check if guess is valid: right length, digits only, unique digits.
     */
    public static boolean isValidGuess(String guess, int length) {
        if (guess.length() != length) {
            return false;
        }

        boolean[] seen = new boolean[10];

        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }

            int d = c - '0';
            if (seen[d]) {
                return false;
            }
            seen[d] = true;
        }

        return true;
    }

    /**
     * Convert guess String to int[].
     */
    public static int[] convertGuess(String g) {
        int[] arr = new int[g.length()];
        for (int i = 0; i < g.length(); i++) {
            arr[i] = g.charAt(i) - '0';
        }
        return arr;
    }

    /**
     * Count correct digits in correct place.
     */
    public static int countCorrectPlace(int[] secret, int[] guess) {
        int count = 0;
        for (int i = 0; i < secret.length; i++) {
            if (secret[i] == guess[i]) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count correct digits in wrong place.
     */
    public static int countWrongPlace(int[] secret, int[] guess) {
        boolean[] secretSeen = new boolean[10];
        boolean[] counted = new boolean[10];

        for (int i = 0; i < secret.length; i++) {
            secretSeen[secret[i]] = true;
        }

        int count = 0;

        for (int i = 0; i < guess.length; i++) {
            int d = guess[i];

            if (d != secret[i] && secretSeen[d] && !counted[d]) {
                count++;
                counted[d] = true;
            }
        }

        return count;
    }

    /**
     * Hint after 3 and 6 guesses.
     */
    public static void giveHint(int[] secret, int guesses) {
        if (guesses == 3) {
            int sum = 0;
            for (int i = 0; i < secret.length; i++) {
                sum += secret[i];
            }
            System.out.println("HINT: Checksum = " + sum);
        }

        if (guesses == 6) {
            Random r = new Random();
            int index = r.nextInt(secret.length);
            System.out.println("HINT: Digit #" + (index + 1) + " is " + secret[index]);
        }
    }

    /**
     * Play one full game.
     */
    public static int playOneGame() {
        int maxGuesses = 12;
        int digits = promptInt("How many digits (2-10)? ", 2, 10);
        int[] secret = generateSecret(digits);

        if (cheaterMode) {
            System.out.print("(Cheater mode) Secret: ");
            printArray(secret);
        }

        int guessCount = 0;
        boolean solved = false;

        while (!solved && guessCount < maxGuesses) {

            System.out.print("Enter guess #" + (guessCount + 1) + ": ");
            String guessString = keyboard.nextLine();

            if (guessString.equalsIgnoreCase("quit")) {
                System.out.println("You gave up.");
                System.out.print("Secret was: ");
                printArray(secret);
                return -1;
            }

            if (!isValidGuess(guessString, digits)) {
                System.out.println("Invalid guess. Enter " + digits + " UNIQUE digits.");
            } else {
                int[] guess = convertGuess(guessString);
                guessCount++;

                int cp = countCorrectPlace(secret, guess);
                int wp = countWrongPlace(secret, guess);

                System.out.println("Correct place: " + cp);
                System.out.println("Wrong place: " + wp);

                if (cp == digits) {
                    System.out.println("You guessed it in " + guessCount + " guesses!");
                    solved = true;
                    return guessCount;
                }

                giveHint(secret, guessCount);

                if (cheaterMode && guessCount % 4 == 0) {
                    System.out.print("(Cheater reveal) Secret: ");
                    printArray(secret);
                }
            }
        }

        // If loop ended without solving:
        if (!solved) {
            System.out.println("Out of guesses! Secret was:");
            printArray(secret);
            return -1;
        }

        return guessCount;
    }

    /**
     * Helper to print array nicely.
     */
    public static void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1)
                System.out.print(", ");
        }
        System.out.println("]");
    }

    /**
     * Main program driver.
     */
    public static void main(String[] args) {
        printTitle();

        System.out.print("Turn cheater mode ON? (Y/N): ");
        String ch = keyboard.nextLine().toUpperCase();
        if (ch.startsWith("Y")) {
            cheaterMode = true;
        }

        int totalGuesses = 0;
        int gamesPlayed = 0;

        boolean playAgain = true;

        while (playAgain) {
            int result = playOneGame();

            gamesPlayed++;

            if (result == -1) {
                totalGuesses += 12; // treat failed game as max guesses
            } else {
                totalGuesses += result;
            }

            System.out.print("Play another game? (Y/N): ");
            String ans = keyboard.nextLine().toUpperCase();

            if (!ans.startsWith("Y")) {
                playAgain = false;
            }
        }

        double avg = (double) totalGuesses / gamesPlayed;
        System.out.printf("You played %d game(s). Average guesses: %.2f\n", gamesPlayed, avg);

        keyboard.close();
    }
}
