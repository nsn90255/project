package proyecto;

// Scanner for scanning user input
import java.util.Scanner;
// File to be able to work with files
import java.io.File;
// to write to files
import java.io.FileWriter;
// to handle exceptions
import java.io.IOException;

public class Proyecto {

    
    public static void main(String[] args) {
        // We create our file object for the blocklist
//        File blocklist = new File("blocklist.txt");
	// Check if the blocklist file exists
	File check = new File("/home/normal-user/project/blocklist.txt");
	if (!check.exists()){
		System.out.println("Blocklist does not exist");
		return;
	}
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
       		         addBannedDomain(sc);
	                break;
	            // We remove a domain from the list
	            case 4:
	                removeBannedDomain();
	                break;
	            default:
	                System.out.println("Wrong option");
	        }
	sc.close();
	
        
        
    }
    public static void block(){
        // stuff here
	//
        System.out.println("The blocklist is up.");
    }
    public static void unblock(){
        System.out.println("The blocklist is down.");
    }
    public static void addBannedDomain(Scanner sc){
	sc.nextLine();
	try{
		// Create file object
	        File blocklist = new File("/home/normal-user/project/blocklist.txt");
		// Create writer
       		FileWriter writer = new FileWriter(blocklist, true);
		// Write some stuff into blocklist
		System.out.print("Enter a domain to block : ");
		String toBlock = sc.nextLine();
		writer.write(toBlock + "\n");
		// make sure it gets written
		writer.flush();	
		System.out.println("Domain has been added to the blocklist.");
		writer.close();
	} catch (IOException e){
		System.out.println("Error : " + e.getMessage());
	}
    }
    public static void removeBannedDomain(){
        System.out.println("Domain has been removed from the blocklist.");
    }
}
