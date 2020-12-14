package com.narojczyk;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.narojczyk.ConsoleColors.*;

public class Main {

    public static void main(String[] args) {
        String database = "tasks.csv";
        String menuItems[] = {"add", "remove", "list", "help", "exit", "!exit"};
        String menuSelect;
        boolean dbModified = false;
        int[] taskdim = {0,0};

        inspectDBfromFile(database, taskdim);
        String[][] tasks = new String[taskdim[0]][taskdim[1]];
        readDBfromFile(database, tasks);

        // main program loop
        while (true){
            menuSelect = selectAction(menuItems, dbModified);

            if(menuSelect.equals(menuItems[0])){
                addToDB();
            }

            if(menuSelect.equals(menuItems[1])){
                removeFromDB();
            }
            if(menuSelect.equals(menuItems[2])){
                listDB(tasks);
            }

            if(menuSelect.equals(menuItems[3])){
                printHelp(menuItems);
            }

            // Exit the program if either of last 2 exit options is seleced
            if(menuSelect.equals(menuItems[menuItems.length-1]) ||
               menuSelect.equals(menuItems[menuItems.length-2]) ){
                break;
            }
        }
    }

    public static void addToDB() {
        System.out.println(GREEN + "Add entry to database" + RESET);
        System.out.println(RED_BOLD + "This functionality is not implemented yet" + RESET);
    }

    public static void removeFromDB() {
        System.out.println(GREEN + "Remove entries from data base" + RESET);
        System.out.println(RED_BOLD + "This functionality is not implemented yet" + RESET);
    }

    public static String selectAction(String menu[], boolean anyModyficationsDone) {
        printMainMenu(menu, anyModyficationsDone);

        Scanner scan = new Scanner(System.in);
        String selection = "";
        boolean validSelection = false;

        System.out.print("Type in your selection: ");
        while (!validSelection) {
            selection = scan.next().trim();
            for(int i=0; i< menu.length; i++){
                if(selection.equals(menu[i])){
                    validSelection = true;
                    System.out.println(" <"+selection+"> command accepted");
                    break;
                }
            }
            if(!validSelection){
                System.out.print("Unrecognized option\nType in your selection: ");
            }
        }


        /* else{
                    System.out.println("Nieprawidłowe dane. Podaj jeszcze raz:");
                }*/

        // TODO return user selected (valid) string
        return selection;
    }

    public static void listDB(String array[][]){

        String[] wsbuffer = new String[array.length];
        prepareAligningBuffer(array, wsbuffer);

        System.out.println(GREEN + "Listing database entries:" + RESET);
        for(int i=0; i< array.length; i++){
            System.out.print("["+i+"]\t" + array[i][0] + wsbuffer[i] + "\t");
            for(int l=1; l<array[i].length; l++){
                System.out.print(array[i][l] + "\t");
            }
            System.out.println();
        }
    }

    public static void prepareAligningBuffer(String array[][], String sp_buff[]){
        int maxCol1_width=0;
        // get max with of text from first column of array[][]
        for(int i=0; i< array.length; i++){
            if (array[i][0].length() > maxCol1_width){
                maxCol1_width = array[i][0].length();
            }
        }

        // prepare array of whitespaces for element in collumn 0 that are shorter then maxCol1_width
        for(int i=0; i< array.length; i++){
            StringBuilder sb = new StringBuilder();
            if(array[i][0].length() < maxCol1_width){
                for(int j=0; j<maxCol1_width-array[i][0].length();j++){
                    sb.append(" ");
                }
                sp_buff[i] = sb.toString();
            }else{
                sp_buff[i]="";
            }
        }
    }

    public static void readDBfromFile(String fname, String array[][]){
        File file = new File(fname);
        int i=-1;
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                array[++i] = scan.nextLine().split(",");
                for(int j=0; j<array[i].length; j++){
                    array[i][j] = array[i][j].trim();
                }
            }
        }catch(FileNotFoundException e) {
            System.out.println("Missing file "+fname);
        }
    }

    public static void inspectDBfromFile(String fname, int dim[]){
        int maxElements = 0, currentNumOfElements;
        dim[0] = 0;
        dim[1] = 0;
        File file = new File(fname);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                dim[0]++;
                currentNumOfElements = scan.nextLine().trim().split(",").length;
                if(maxElements < currentNumOfElements){
                    maxElements = currentNumOfElements;
                }
            }
        }catch(FileNotFoundException e) {
            System.out.println("Missing file "+fname);
        }
        dim[1] = maxElements;
    }

    public static void printMainMenu(String menu[], boolean anyModyficationsDone){
        int maxMenuItems = ((anyModyficationsDone) ? 0 : -1) + menu.length;
        System.out.println(BLUE + "Please select an option:" + RESET);
        for(int i=0; i< maxMenuItems; i++){
            System.out.println("* "+ menu[i]);
        }
    }

    public static void printHelp(String menu[]){
         System.out.println(GREEN + "Menu items description:" + RESET);
        for(int i=0; i< menu.length; i++){
            System.out.print(" " + YELLOW_BOLD + menu[i] + RESET + "\t");
            switch (menu[i]) {
                case "add":
                    System.out.println("Add entry to data base"); break;
                case "remove":
                    System.out.println("Delete entry from data base"); break;
                case "list":
                    System.out.println("Print all entries in data base"); break;
                case "help":
                    System.out.println("Print this message"); break;
                case "exit":
                    System.out.println("Save changes and exit"); break;
                case "!exit":
                    System.out.println("Exit without saving changes"); break;
                default:
                    break;
            }
        }
    }
}
