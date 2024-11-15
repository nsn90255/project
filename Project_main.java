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
		File blocklist = new File("/opt/blocklist");
		Scanner sc = new Scanner(System.in);
		// check if blocklist exists
		if (!blocklist.exists()){
			// if not, ask to create it
			System.out.print("Blocklist does not exist. Create?(y/n) : ");
			if (sc.nextLine().equalsIgnoreCase("y")) { 
				createBlocklist();
			} else {
				System.out.println("A blocklist is needed. Create it.");
			}
			return;
		}
		// options to chose
		if (args.length == 0){
			help();
			return;
		} else if (args[0].equals("-u") || args[0].equals("--unblock")){
			unblock(blocklist, args);
		} else if (args[0].equals("-b") || args[0].equals("--block")){
			block(blocklist, args);
		} else if (args[0].equals("-a") || args[0].equals("--add")){
			addBannedDomain(blocklist, sc, args);
		} else if (args[0].equals("-r") || args[0].equals("--remove")){
			removeBannedDomain(blocklist, sc, args);
		} else {
			help();
		}
	}
	public static void unblock(File blocklist, String args[]){
		// stuff
		if (args.length > 1) {
			help();
			return;
		}
		System.out.println("The blocklist is down.");
	}
	public static void block(File blocklist, String args[]){
		if (args.length > 1) {
			help();
			return;
		}  
		File doesBackupExist = new File("/etc/hosts.bkp");
		// if hosts.bkp already exists exit 
		if (doesBackupExist.exists()){
			System.out.println("The blocklist is already up.");
			return;
		}
		// backup file
		File backup = new File("/etc/hosts.bkp");
		try {
			// create new backup file
			backup.createNewFile();
			System.out.println("backup of hosts file created");
		} catch (IOException e) {
			System.out.println("Error : " + e.getMessage());
		}
		// check if hosts exists
		File doesHostsExist = new File("/etc/hosts");
		if (!doesBackupExist.exists()){
			// if not, tell user
			System.out.println("The hosts file doesn't exist, you should fix that.");
			return;
		}
		// hosts file
		File hosts = new File("/etc/hosts");
		// write the hosts file to a backup
		try {
			// Create writer & reader
			BufferedWriter wr = new BufferedWriter(new FileWriter(backup));
			BufferedReader rd = new BufferedReader(new FileReader(hosts));
			// var for the current line
			String currentLine;
			while((currentLine = rd.readLine()) !=null) {
				wr.write(currentLine + "\n");
			}
		wr.close();
		rd.close();
		} catch (IOException e) {
			// catch any and all exeptions, print them
			System.out.println("Error : " + e.getMessage());
		}
		// copy /opt/blocklist to /etc/hosts
		try {
			//create reader & writer
			BufferedWriter wr = new BufferedWriter(new FileWriter(hosts, true));
			BufferedReader rd = new BufferedReader(new FileReader(blocklist));
			// var for the current line
			String currentLine;
			wr.write("127.0.0.1 ");
			while((currentLine = rd.readLine()) !=null) {
				wr.write(currentLine + " ");
			}
		wr.close();
		rd.close();
		} catch (IOException e) {
			// catch any and all exeptions, print them
			System.out.println("Error : " + e.getMessage());
		}
		System.out.println("The blocklist is up.");
	}
// MAKE CHECK IF BLOCKING OR NOT BEFORE REMOVING OR ADDING DOMAINS
	public static void addBannedDomain(File blocklist, Scanner sc, String[] args){
		// if no domain is specified print help and return to main
		if (args.length < 2) {
			help();
			return;
		}
		try{
			// Create writer
			BufferedWriter wr = new BufferedWriter(new FileWriter(blocklist, true));
			// Create reader
			BufferedReader rd = new BufferedReader(new FileReader(blocklist));
			// loop through every argument starting from the second one
			for (int i = 1; i < args.length; i++){
				// assign the argument to toBlock
				String toBlock = args[i];
				// controls whether to write or not
				boolean write = true;
				// variable to store the line read by the buffered reader
				String currentLine;
				while ((currentLine = rd.readLine()) != null) {
					// remove whitespaces
					String trimmedLine = currentLine.trim();
					if (trimmedLine.equals(args[i])) {
						write = false;
						break;
					}
				}
				if (write == true) {
					wr.write(toBlock + "\n");
					System.out.println(toBlock + " has been added to the blocklist.");
				} else {
					System.out.println(toBlock + " is already in the blocklist the blocklist.");
				}
			}
			wr.close();
		} catch (IOException e){
			System.out.println("Error : " + e.getMessage());
		}
	}
	public static void removeBannedDomain(File blocklist, Scanner sc, String[] args){
		// if no domain is specified print help and return to main
		if (args.length < 2) {
			help();
			return;
		}
		// reading and writing goes in the try catch
		try{
			// create an object for the temp file
			File tempFile = new File("blocklist.temp");
			// create reader & writer
			BufferedReader rd = new BufferedReader(new FileReader(blocklist));
			BufferedWriter wr = new BufferedWriter(new FileWriter(tempFile));
			boolean domainRemoved = false;
			// create the string for the current line that the br is reading
			String currentLine;
			// for each line
			while((currentLine = rd.readLine()) !=null) {
				// remove whitespaces
				String trimmedLine = currentLine.trim();
				// boolean value to allow writing gets reset for each line	
				boolean shouldWrite = true;
				// loop through every argument and check if they match the current line
				for (int i = 1;	i < args.length; i++) {
					// give the value of the argument to the string toUnblock
					String toUnblock = args[i].trim();
					// if the line without the whitespace is the same as the argument
					if (trimmedLine.equals(toUnblock)) {
						System.out.println(toUnblock + " has been removed from the blocklist.");
						// do not write it
						shouldWrite = false;
						// make sure the blocklist is swapped out by the temp one if one domain is written
						domainRemoved = true;
						break;
					}
				}	
				// write that line to the temp file unless the line matches an argument
				if (shouldWrite) {
					wr.write(currentLine + "\n");
				}
			}

			// close the writer and the reader
			wr.close();
			rd.close();
			// rename the temp file to blocklist.txt if one domain or more is written
			if (domainRemoved) {
				tempFile.renameTo(blocklist);
				System.out.println("Blocklist updated");
			} else {
			    // If no domain was removed, delete the temp file
			    tempFile.delete();
			    System.out.println("No domain was removed from the blocklist");
			}
		} catch (IOException e) {
			System.out.println("Error : " + e.getMessage());
		}
	}
	// might delete later if unused
	public static boolean checkInBlocklist(File blocklist, String toBlock){
		try {
			// create writer
			BufferedReader br = new BufferedReader(new FileReader(blocklist));
			// string for the current line
			String currentLine;
			// loop to read each current line while there are lines
			while ((currentLine = br.readLine()) != null) {
				// if the current line is equal to the domain entered by the user
				if (currentLine.equals(toBlock)) {
					br.close();
					return true;
				}
			}
			br.close();
			return false;
		} catch (IOException e) {
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
	public static void createBlocklist() {
		try {
			// create the blocklist
			File blocklist = new File("/opt/blocklist");
			// Create writer
			BufferedWriter wr = new BufferedWriter(new FileWriter(blocklist));
			wr.write("www.youtube.com\n" + "www.instagram.com\n" + "www.tiktok.com\n" + "www.facebook.com\n");
			wr.close();
		} catch (IOException e) {
			System.out.println("Error : " + e.getMessage());
		}
		// inform user
		System.out.println("Blocklist created with default blocked domains.");
		return;
	}
}
