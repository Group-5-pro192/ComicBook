package main;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ComicBookManagement manager = new ComicBookManagement();
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        do {

            System.out.println("\n          COMIC BOOK RENTAL SHOP # SE2102-Group05");
            System.out.println("1. Add new comic book.");
            System.out.println("2. Search book by title.");
            System.out.println("3. Search book of an author.");
            System.out.println("4. Update book rental price.");
            System.out.println("5. Delete comic book.");
            System.out.println("6. About us.");
            System.out.println("7. Quit.");

            choice = getValidChoice(sc);

            switch (choice) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    manager.saveData();
                    System.out.println("Data saved successfully. Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("The number you entered is out of range. Please choose a number between 1 and 7!");
                    break;
            }
        } while (choice != 7);

        sc.close();
    }

    private static int getValidChoice(Scanner sc) {
        while (true) {
            System.out.print("     Please select a function: ");
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {

                System.out.println("Input format error! You must enter an integer number (1-7), not letters or special characters.");
            }
        }
    }
}
