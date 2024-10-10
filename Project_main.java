/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

// Scanner for scanning user input
import java.util.Scanner;
// File to be able to work with files
import java.io.File;
// to write to files
import java.io.FileWriter;
// to read files
import java.io.FileReader;
import java.io.BufferedReader;
// to handle exceptions
import java.io.IOException;

public class Project_main {

    
    public static void main(String[] args) {
	// create file object for the blocklist 
	File blocklist = new File("/opt/blocklist.txt");
	// check if blocklist exists
	if (!blocklist.exists()){
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
       		         addBannedDomain(blocklist, sc);
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
    public static void addBannedDomain(File blocklist, Scanner sc){
	// clear scanner because it is dirty
	sc.nextLine();
	try{
		// Create writer
       		FileWriter writer = new FileWriter(blocklist, true);
		// Write some stuff into blocklist
		System.out.print("Enter a domain to block : ");
		// store scanner buffer in toBlock string
		String toBlock = sc.nextLine();
		// check if already in the list
		
		if (checkInBlocklist(blocklist, toBlock)) {
			System.out.println("Already in blocklist");
			return;
		}


		// use the writer to write to the blocklist file
		writer.write(toBlock + "\n");
		// make sure it gets written 
		// idk why but it doesn't write otherwise
		writer.flush();
		// inform the user
		System.out.println("Domain has been added to the blocklist.");
		// close the scanner
		writer.close();
	} catch (IOException e){
		// catch any and all exeptions, print them
		System.out.println("Error : " + e.getMessage());
	}
    }
    public static void removeBannedDomain(){
	
        System.out.println("Domain has been removed from the blocklist.");
    }
    public static boolean checkInBlocklist(File blocklist, String toBlock){

	// check
	try {
		BufferedReader br = new BufferedReader(new FileReader(blocklist));
		String currentLine;
		while ((currentLine = br.readLine()) != null) {
			if (currentLine.equals(toBlock)) {
				br.close();
				return true;
			}
		}
		br.close();
		return false;
	} catch (IOException e) {
		// catch exeptions
		System.out.println("Error : " + e.getMessage());
	}
	return false;
    }
}
