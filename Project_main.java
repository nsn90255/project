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
import java.io.BufferedWriter;
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
	// create scanner
	Scanner sc = new Scanner(System.in);
	// options
	if (args.length == 0){
		help();
		return;
	} else if (args[0].equals("-u") || args[0].equals("--unblock")){
		unblock();
	} else if (args[0].equals("-b") || args[0].equals("--block")){
		block();
	} else if (args[0].equals("-a") || args[0].equals("--add")){
		addBannedDomain(blocklist, sc, args);
	} else if (args[0].equals("-r") || args[0].equals("--remove")){
		removeBannedDomain(blocklist, sc, args);
	} else {
		help();
	}
    }
    public static void block(){
        // stuff here
	//
        System.out.println("The blocklist is up.");
    }
    public static void unblock(){
	// stuff
        System.out.println("The blocklist is down.");
    }
    public static void addBannedDomain(File blocklist, Scanner sc, String[] args){
	try{
		// Create writer
       		BufferedWriter wr = new BufferedWriter(new FileWriter(blocklist, true));
		// use the second cli argument as the domain to block	
		String toBlock = args[1];
		// check if already in the list
		if (checkInBlocklist(blocklist, toBlock)) {
			System.out.println("Already in blocklist");
			return;
		}
		// use the writer to write to the blocklist file
		wr.write(toBlock + "\n");
		// make sure it gets written 
		// idk why but it doesn't write otherwise
		wr.flush();
		// inform the user
		System.out.println(toBlock + " has been added to the blocklist.");
		// close the scanner
		wr.close();
	} catch (IOException e){
		// catch any and all exeptions, print them
		System.out.println("Error : " + e.getMessage());
	}
    }
    public static void removeBannedDomain(File blocklist, Scanner sc, String[] args){
	try{
		// create an object for the temp file
		File tempFile = new File("blocklist.txt.temp");
		// create reader
		BufferedReader rd = new BufferedReader(new FileReader(blocklist));
		// Create writer
       		BufferedWriter wr = new BufferedWriter(new FileWriter(tempFile, true));
		// use the second cli argument as the domain to block	
		if (args.length < 2) {
			help();
			return;
		}
		String toUnblock = "toUnblock";
		// loop through arguments
		for ( int i = 1; i < args.length; i++) {
			toUnblock = args[i];
			// check if not in the list
			if (!checkInBlocklist(blocklist, toUnblock)) {
				// if not, say so
				System.out.println(toUnblock + " not in the blocklist");
			} else {
				// if it is in the list proceed
				// create string for current line
				String currentLine;
				// read the blocklsit line by line
				while((currentLine = rd.readLine()) !=null) {
					// remove whitespaces
					String trimmedLine = currentLine.trim();
					// if the trimed line is the same as the domain to block don't copy it over
					if (trimmedLine.equals(toUnblock)) continue;
					// write the current line to the temp file
					wr.write(currentLine + "\n");
				}
			// inform user
			System.out.println(toUnblock + " has been removed from the blocklist.");
			}
		}
		// close the writer and the writer
		wr.close();
		rd.close();
		// rename the temp file to blocklist.txt
		tempFile.renameTo(blocklist);
	} catch (IOException e) {
	   	// catch exceptions, print them
		System.out.println("Error : " + e.getMessage());
	}
    }
    public static boolean checkInBlocklist(File blocklist, String toBlock){
	// check
	try {
		// create writer
		BufferedReader br = new BufferedReader(new FileReader(blocklist));
		// string for the current line
		String currentLine;
		// loop to read each current line while there are lines
		while ((currentLine = br.readLine()) != null) {
			// if the current line is equal to the domain entered by the user
			if (currentLine.equals(toBlock)) {
				// close reader
				br.close();
				return true;
			}
		}
		// close writer
		br.close();
		return false;
	} catch (IOException e) {
		// catch exeptions, print them
		System.out.println("Error : " + e.getMessage());
	}
	// added just in case (compiler complains otherwise)
	return false;
    }
    public static void help(){
 	// print basic info about usage 
	System.out.println("Usage : project [command] [domain]");
	// print info about the commands
	System.out.println(" --help\t\t\t\t\tPrint this.\n -u, --unblock\t\t\t\tUnblock domains.\n -b, --block\t\t\t\tBlock domains.\n -a, -add\t\t\t\tAdd a domain to block.\n -r, --remove\t\t\t\tRemove a blocked domain.");
   
    }
}
