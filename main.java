package proyecto;

// Scanner for scanning user input
import java.util.Scanner;
// File to be able to work with files
import java.io.File;
// 

public class Proyecto {

    
    public static void main(String[] args) {
        // We create our file object for the blocklist
        File blocklist = new File("blocklist.txt");
        // Print options
        System.out.print("1. Block\n2. Unblock\n3. Add a banned domain\n4. Remove a banned domain\nChose: ");
        // Start the scanner
        Scanner sc = new Scanner(System.in);
        // Switch with the options
        switch(sc.nextInt()) {
            // We add the domains to /etc/hosts
            case 1:
                block();
                break;
            // We remove the domains from /etc/hosts
            case 2:
                unblock();
                break;
            // We add a domain to the list
            case 3:
                addBannedDomain();
                break;
            // We remove a domain from the list
            case 4:
                removeBannedDomain();
                break;
            default:
                System.out.println("Wrong option");
        }
        
        
    }
    public static void block(){
        // stuff here
        System.out.println("The firewall is up.");
    }
    public static void unblock(){
        System.out.println("The firewall is down.");
    }
    public static void addBannedDomain(){
        System.out.println("Domain has been added to the blocklist.");
    }
    public static void removeBannedDomain(){
        System.out.println("Domain has been removed from the blocklist.");
    }
}
