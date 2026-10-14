package comicbookmanagement;

public class AboutUs {

    /**
     * Function 6: About Us
     * Displays comprehensive project and team member information.
     */
    public static void displayAboutUs() {
        System.out.println("\n+---------------------------------------------------------------------------------------------------+");
        System.out.println("|                                        ABOUT US - PROJECT INFO                                    |");
        System.out.println("+---------------------------------------------------------------------------------------------------+");
        System.out.println("|  1. University : FPT University Campus Can Tho                                                    |");
        System.out.println("|  2. College    : Information Technology College - Software Engineering Department                 |");
        System.out.println("|  3. Subject    : PRO192 - Object-Oriented Programming                                             |");
        System.out.println("|  4. Class      : SE2102                                                                           |");
        System.out.println("|  5. Mentor     : Vo Hong Khanh                                                                    |");
        System.out.println("|  6. Group      : Group 05                                                                         |");
        System.out.println("+---------------------------------------------------------------------------------------------------+");
        System.out.println("|                                           TEAM MEMBERS                                            |");
        System.out.println("+-----+-----------+------------------------------+--------------------+-----------------------------+");
        System.out.printf("| %-3s | %-9s | %-28s | %-18s | %-27s |\n", "No.", "ID", "Full Name", "Role", "Assigned Task");
        System.out.println("+-----+-----------+------------------------------+--------------------+-----------------------------+");
        
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                1, "CE210032", "Ngo Thi Thao Huynh", "Leader", "Testing & Data");
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                2, "CE210356", "Ngo Ngoc Anh Thu", "Secretary", "Class ComicBookManagement");
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                3, "CE210006", "Nguyen Huu Loc", "Member", "About Us");
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                4, "CE210031", "Le Ngoc Ngan", "Member", "Menu");
        System.out.printf("| %-3d | %-9s | %-28s | %-18s | %-27s |\n", 
                5, "CE190377", "Dinh Minh Tien", "Member", "Class ComicBook");
                
        System.out.println("+-----+-----------+------------------------------+--------------------+-----------------------------+");
        System.out.println();
    }

    public static void main(String[] args) {
        displayAboutUs();
    }
}
