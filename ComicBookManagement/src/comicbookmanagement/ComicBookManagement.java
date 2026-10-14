package comicbookmanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ComicBookManagement {

    // Declare ArrayList to store comic books and set the constant file name
    private ArrayList<ComicBook> comicList = new ArrayList<>();
    private final String FILE_NAME = "ComicBooks.txt";

    public ComicBookManagement() {
    }

    // Getter for team members to access the comic book list
    public ArrayList<ComicBook> getComicList() {
        return comicList;
    }

    /**
     * Loads data from 'ComicBooks.txt' into the ArrayList.
     * File structure:
     * - Line 1: Total number of comic books (N)
     * - Every subsequent 5 lines represent 1 comic book: ID, Title, Price, Author, Volume
     */
    public void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("Data file '" + FILE_NAME + "' not found. Starting with empty list.");
            return;
        }

        try (Scanner sc = new Scanner(file)) {
            if (sc.hasNextInt()) {
                int total = Integer.parseInt(sc.nextLine().trim());
                for (int i = 0; i < total; i++) {
                    if (!sc.hasNextLine()) break;
                    
                    int id = Integer.parseInt(sc.nextLine().trim());
                    String title = sc.nextLine().trim();
                    double price = Double.parseDouble(sc.nextLine().trim());
                    String author = sc.nextLine().trim();
                    String volume = sc.nextLine().trim();

                    comicList.add(new ComicBook(id, title, price, author, volume));
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }

    // MENU
    public void saveData() {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(new File(FILE_NAME));
            writer.println(comicList.size()); 
            
            for (ComicBook book : comicList) {
                writer.println(book.getId());
                writer.println(book.getTitle());
                writer.println(book.getBookRentalPrice());
                writer.println(book.getAuthor());
                writer.println(book.getVolume());
            }
            
            writer.close();
            System.out.println("Data saved successfully to " + FILE_NAME);
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    /**
     * Function 6: About Us
     * Displays comprehensive project and team member information.
     */
    public void displayAboutUs() {
        System.out.println("\n+-----------------------------------------------------------------------------------+");
        System.out.println("|                              ABOUT US - PROJECT INFO                              |");
        System.out.println("+-----------------------------------------------------------------------------------+");
        System.out.println("|  1. University : FPT University Campus Can Tho                                    |");
        System.out.println("|  2. College    : Information Technology College - Software Engineering Department |");
        System.out.println("|  3. Subject    : PRO192 - Object-Oriented Programming                             |");
        System.out.println("|  4. Class      : SE2102                                                           |");
        System.out.println("|  5. Mentor     : Vo Hong Khanh                                                    |");
        System.out.println("|  6. Group      : Group 05                                                         |");
        System.out.println("+-----------------------------------------------------------------------------------+");
        System.out.println("|                                    TEAM MEMBERS                                   |");
        System.out.println("+-----+-----------+------------------------------+----------------------------------+");
        System.out.printf("| %-3s | %-9s | %-28s | %-32s |\n", "No.", "ID", "Full Name", "Role");
        System.out.println("+-----+-----------+------------------------------+----------------------------------+");
        
        System.out.printf("| %-3d | %-9s | %-28s | %-32s |\n", 
                1, "CE210032", "Ngo Thi Thao Huynh", "Leader");
        System.out.printf("| %-3d | %-9s | %-28s | %-32s |\n", 
                2, "CE210356", "Ngo Ngoc Anh Thu", "Secretary");
        System.out.printf("| %-3d | %-9s | %-28s | %-32s |\n", 
                3, "CE210006", "Nguyen Huu Loc", "Member");
        System.out.printf("| %-3d | %-9s | %-28s | %-32s |\n", 
                4, "CE210031", "Le Ngoc Ngan", "Member");
        System.out.printf("| %-3d | %-9s | %-28s | %-32s |\n", 
                5, "CE190377", "Dinh Minh Tien", "Member");
                
        System.out.println("+-----+-----------+------------------------------+----------------------------------+");
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ComicBookManagement manager = new ComicBookManagement();
            
        manager.loadData();

        int choice = -1;
        do {
            System.out.println("          COMIC BOOK RENTAL SHOP # SE2102-Group05");
            System.out.println("1. Add new comic book.");
            System.out.println("2. Search book by title.");
            System.out.println("3. Search book of an author.");
            System.out.println("4. Update book rental price.");
            System.out.println("5. Delete comic book.");
            System.out.println("6. About us.");
            System.out.println("7. Quit.");
            System.out.print("     Please select a function: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());

                switch (choice) {
                    case 1:
                        System.out.println("Function 1 is under construction.");
                        break;
                    case 2:
                        System.out.println("Function 2 is under construction.");
                        break;
                    case 3:
                        System.out.println("Function 3 is under construction.");
                        break;
                    case 4:
                        System.out.println("Function 4 is under construction.");
                        break;
                    case 5:
                        System.out.println("Function 5 is under construction.");
                        break;
                    case 6:
                        manager.displayAboutUs();
                        break;
                    case 7:
                        manager.saveData();
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a number from 1 to 7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter an integer number.");
            }
            System.out.println(); 
        } while (choice != 7);
        
        sc.close();
    }
}
