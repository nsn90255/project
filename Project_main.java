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

import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Project_main {
    
	public static void main(String[] args) {
		File blocklist = new File("/etc/blocklist.conf");
		// check if blocklist exists
		if (!blocklist.exists()){
			createBlocklist();
		}
		Scanner sc = new Scanner(System.in);
		if (args.length == 0){
			help();
			return;
		} else if (args[0].equals("-u")) {
			if (args.length > 1) {
				help();
				return;
			} else {
				unblock(blocklist, args);
			}
		} else if (args[0].equals("-b")) {
			if (args.length > 1) {
				help();
				return;
			} else {
				block(blocklist, args);
			}
		} else if (args[0].equals("-a")) {
			addBannedDomain(blocklist, sc, args);
		} else if (args[0].equals("-r")) {
			removeBannedDomain(blocklist, sc, args);
		} else if (args[0].equals("-s")) {
			checkStatus();
		} else {
			help();
		}
	}
	public static void unblock(File blocklist, String args[]){
		if (!checkBlocking()) {
			System.out.println("Not blocking right now. Nothing to do.");
			return;
		}
		File hosts = new File("/etc/hosts");
		File backup = new File("/etc/hosts.bkp");
		hosts.delete();
		backup.renameTo(hosts);
		System.out.println("The blocklist is down.");
	}
	public static void block(File blocklist, String args[]){
		// if already blocking stop
		if (checkBlocking()) {
			System.out.println("Already blocking.");
			return;
		}
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
		if (!doesHostsExist.exists()){
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
			System.out.println("Error : " + e.getMessage());
		}
		// copy /etc/blocklist.conf to /etc/hosts
		try {
			//create reader & writer
			BufferedWriter wr = new BufferedWriter(new FileWriter(hosts, true));
			BufferedReader rd = new BufferedReader(new FileReader(blocklist));
			// var for the current line
			String currentLine;
			wr.write("127.0.0.1 ");
			while((currentLine = rd.readLine()) !=null) {
				if (currentLine.equals("[domains]") || currentLine.equals("[days]") || currentLine.charAt(0) == '#') {
					continue;
				}
				String notWrite[] = {"1=", "2=", "3=", "4=", "5=", "6=", "7="};
				for (int j = 0; j < 7; j++) {
					if (currentLine == notWrite[j]) {
						continue;
					}
				}
				wr.write(currentLine + " ");
			}
		wr.close();
		rd.close();
		} catch (IOException e) {
			System.out.println("Error : " + e.getMessage());
		}
		System.out.println("The blocklist is up.");
	}
	public static void addBannedDomain(File blocklist, Scanner sc, String[] args){
		// if blocking prevent user from editing config
		if (checkBlocking()) {
			System.out.println("Blocking right now. Unblock and try again.");
			return;
		}
		// if no domain is specified print help and return to main
		if (args.length < 2) {
			help();
			return;
		}
		try{
			BufferedWriter wr = new BufferedWriter(new FileWriter(blocklist, true));
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
			rd.close();
		} catch (IOException e){
			System.out.println("Error : " + e.getMessage());
		}
	}
	public static void removeBannedDomain(File blocklist, Scanner sc, String[] args){
		// if blocking prevent user from editing config
		if (checkBlocking()) {
			System.out.println("Blocking right now. Unblock and try again.");
			return;
		}
		// if no arguments stop here
		if (args.length < 2) {
			help();
			return;
		}
		try{
			File tempFile = new File("blocklist.temp");
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
	public static boolean checkBlocking() {
		File doesBackupExist = new File("/etc/hosts.bkp");
		if (doesBackupExist.exists()) {
			return true;
		} else {
			return false;
		}
	}
	public static void help(){
		System.out.println("Usage : project [command] [domain]");
		System.out.println("  -u\t\t\t\tUnblock domains.");
		System.out.println("  -b\t\t\t\tBlock domains.");
		System.out.println("  -a\t\t\t\tAdd a domain to block.");
		System.out.println("  -r\t\t\t\tRemove a blocked domain.");
		System.out.println("  -s\t\t\t\tCheck if running or not.");
	}
	public static void createBlocklist() {
		try {
			// create the blocklist
			File blocklist = new File("/etc/blocklist.conf");
			BufferedWriter wr = new BufferedWriter(new FileWriter(blocklist));
			wr.write("# write your websites to block here\n");
			wr.write("[websites]\n");
			wr.write("www.youtube.com\n");
			wr.write("# www.instagram.com\n");
			wr.write("# www.tiktok.com\n");
			wr.write("# www.facebook.com\n");
			wr.write("# write your days to block here\n");
			wr.write("[days]\n");
			
			wr.close();
		} catch (IOException e) {
			System.out.println("Error : " + e.getMessage());
		}
		System.out.println("Blocklist created with default blocked domains.");
		return;
	}
	public static void blockedDays() {
	}	
	public static void checkStatus() {
		String not = "";
		if (!checkBlocking()) {
			not = "Not ";
		}	
		System.out.println("Status : [" + not + "Blocking]");
	}
}
