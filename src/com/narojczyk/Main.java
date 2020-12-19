package com.narojczyk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import static com.narojczyk.ConsoleColors.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Arrays.copyOf;

public class Main {

    public static void main(String[] args) {
        String database = "tasks.csv";
        String menuItems[] = {"add", "remove", "list", "help", "exit", "!exit"};
        String menuSelect;
        String newTask = null;
        boolean dbModified = false, addToDBvalidData = false, recRemoved=false;
        int[] taskdim = {0,0};

        //TODO: zapytac o sciezke jesli nie znajdzie pliku
        String[][] tasks = readDBfromFileDev(database);

        // main program loop
        while (true && (tasks != null)){
            menuSelect = selectAction(menuItems, dbModified);

            if(menuSelect.equals(menuItems[0])){
                newTask = getDataToBeAddedToDB();
                dbModified = (!dbModified) ? (newTask != null) : dbModified;
                if(newTask != null) {
                    tasks = addTaskToArray(tasks, newTask, whereToAddData(tasks));
                }
            }
            if(menuSelect.equals(menuItems[1])){
                recRemoved = removeFromDB(tasks);
                dbModified = (!dbModified) ? recRemoved : dbModified;
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
                // Save data to file only if "exit" command is issued
                if(menuSelect.equals(menuItems[menuItems.length-2]) && dbModified) {
                    try {
                        saveToDisk(tasks, database);
                    }catch (FileNotFoundException e){
                        System.out.println("Things went south when writing to file");
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    public static void saveToDisk(String tasksDB[][], String fileName) throws FileNotFoundException {
        /* Zapisz do pliku tymczasowego, jeżeli nie będzie błędów przekopiuj tymczasowy na wynikowy */
        int rec_i_length=0;
        String tmpFileName= fileName.replaceFirst("\\.([a-zA-Z]+)", "_tmp\\.$1");
        Path tempFilePth = Paths.get(tmpFileName);
        Path outputFilePth = Paths.get(fileName);

        StringBuilder pushRecord = new StringBuilder();

        if(Files.exists(tempFilePth)){
            try {
                Files.delete(tempFilePth);
            } catch (IOException e) {
                System.out.println("No write premissions on temp file "+tmpFileName);
                e.printStackTrace();
            }
        }

        PrintWriter write = new PrintWriter(tmpFileName);
        for(int i = 0; i < tasksDB.length; i++){
            if(tasksDB[i] != null){
                rec_i_length = tasksDB[i].length;
                for(int j = 0; j < rec_i_length; j++){
                    pushRecord.append(tasksDB[i][j]+ ((j < rec_i_length-1 ) ? "," : "\n"));
                }
            }
            write.print(pushRecord.toString());
            //delete all contents from previous iteration
            pushRecord.delete(0,  pushRecord.length());
        }
        write.close();

        try {
            Files.copy(tempFilePth, outputFilePth, REPLACE_EXISTING );
        } catch (IOException e) {
            System.out.println("Failed to copy temp file "+tmpFileName+" to output file "+fileName);
            e.printStackTrace();
        }finally {
            System.out.println("Saved database to file");
        }
    }

    public static String[][] addTaskToArray(String tasksDB[][], String toAdd, int pos){
        int tablength = tasksDB.length;

        if(pos >= tablength){
            tasksDB = Arrays.copyOf(tasksDB, ++tablength);
        }

        if(pos < tablength) {
            tasksDB[pos] = toAdd.split(",");
            System.out.println("New record added at position [" + pos + "]");
        }
        return tasksDB;
    }

    public static int whereToAddData(String tasksDB[][]){
        int tablength = tasksDB.length;

        for(int i=0; i<tablength; i++){
            if(tasksDB[i] == null){
                return i;
            }
        }
        return tablength;
    }

    public static boolean removeFromDB(String tasksDB[][]) {
        int idToDelete = -1;
        boolean validID = false, removeConfirmed = false, recordRemoved=false;
        String removeConfirmStr="";
        System.out.println(GREEN + "Remove entries from data base" + RESET);
        listDB(tasksDB);

        do{
            System.out.print("Enter record id number to be removed: ");
            idToDelete = getIntegerInput();
            validID = (idToDelete >= 0) && (idToDelete < tasksDB.length) && (tasksDB[idToDelete] != null);
            if(!validID){
                System.out.println("Index out of range. Select id from above list");
            }
        }while(!validID);

        listDBmarked(tasksDB, idToDelete);
        System.out.print("Confirm to delete id " + idToDelete + " [y/N]: ");
        Scanner scan = new Scanner(System.in);
        removeConfirmStr = scan.nextLine().trim();
        if(removeConfirmStr.equals("y") || removeConfirmStr.equals("Y")){
            removeConfirmed = true;
        }

        if(removeConfirmed){
            tasksDB[idToDelete] = null;
            recordRemoved = true;
            System.out.println("Record removed (save changes do disk to re-iterate record id's)");
        }else{
            System.out.println("Removal aborted");        }

        listDB(tasksDB);
        return recordRemoved;
    }

    public static int getIntegerInput(){
        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextInt()) {
            scan.nextLine().trim();
            System.out.print("Input not an INT. Enter valid int:");
        }
        return scan.nextInt();
    }

    public static String getStringInput(){
        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextLine()) {
            scan.nextLine();
        }
        return scan.nextLine().trim();
    }

    public static String getDataToBeAddedToDB() {
        System.out.println(GREEN + "Add entry to database" + RESET);

        Scanner scan = new Scanner(System.in);
        boolean dateFormatOK = false, taskFlag = true, addConfirmation = false;
        String taskDesc, taskDate = null,  addConfirmationStr = "notAsked";
        String[] taskDateTest = new String[3];

        // Enter data for the first filed
        System.out.print("Type in task description: ");
        taskDesc = scan.nextLine().trim().replaceAll(",", " ").replaceAll("\\s+", " ");

        // Enter data for the second filed
        while(!dateFormatOK) {
            // Not the best test but it's a start
            taskDateTest = splitDateForTesting(askForDate());
            dateFormatOK = testInputDateFormat(taskDateTest);
            if(!dateFormatOK){
                System.out.println("Wrong format or values");
                continue;
            }
            taskDate = aditionalDateFormatting(taskDateTest);
        }

        // Enter data for the third filed
        System.out.print("Type in task flag [true/false]: ");
        while(!scan.hasNextBoolean()){
            scan.nextLine();
            System.out.print("Type in task flag [true/false]: ");
        }
        taskFlag = scan.nextBoolean();

        // Display the generated entry and ask for confirmation
        System.out.print("Given entry to store:\n\t"
                + taskDesc +"\t" + taskDate + "\t" + taskFlag
                + "\nConfirm add record to database [Y/n]: ");
        addConfirmationStr = getStringInput();

        if(addConfirmationStr != null &&
                (addConfirmationStr.equalsIgnoreCase("y") || addConfirmationStr.length() == 0)){
            addConfirmation = true;
        }else{
            addConfirmation = false;
        }

        if(addConfirmation){
            System.out.println("Adding data confirmed");
            return taskDesc +"," + taskDate + "," + taskFlag;
        }
        System.out.println("New data discarded");
        return null;
    }

    public static boolean testInputDateFormat(String[] dateElements){
        return (testForInt(dateElements[0]) != null &&
                testForInt(dateElements[1]) != null && testForInt(dateElements[2]) != null) &&
                (Integer.valueOf(dateElements[1]) <= 12 && Integer.valueOf(dateElements[2]) <= 31);
    }

    public static String[] splitDateForTesting(String dateStr){
        String[] dateElements = new String[3];
        if(dateStr.split("-").length == 3){
            dateElements = dateStr.split("-");
        }else{
            dateElements[0]=null;
            dateElements[1]=null;
            dateElements[2]=null;
        }
        return dateElements;
    }

    public static String aditionalDateFormatting(String[] dateElements){
        // Insert '0' if MM < 10 or DD < 10
        if(dateElements[1].length()==1){
            dateElements[1] =  "0" + dateElements[1];
        }
        if(dateElements[2].length()==1){
            dateElements[2] =  "0" + dateElements[2];
        }
        return dateElements[0]+"-"+dateElements[1]+"-"+dateElements[2];
    }

    public static String askForDate(){
        Scanner scan = new Scanner(System.in);
        System.out.print("Type date [YYYY-MM-DD]: ");
        return scan.nextLine().trim();
    }

    public static Integer testForInt(String text) {
      try {
        return Integer.parseInt(text);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    public static String selectAction(String menu[], boolean anyModyficationsDone) {
        printMainMenu(menu, anyModyficationsDone);

        Scanner scan = new Scanner(System.in);
        String selection = "";
        boolean validSelection = false;

        while (!validSelection) {
            System.out.print("Type in your selection: ");
            selection = scan.nextLine().trim();
            for(int i=0; i< menu.length; i++){
                if(selection.equals(menu[i])){
                    validSelection = true;
                    System.out.println(" <"+selection+"> command accepted");
                    break;
                }
            }
            if(!validSelection){
                System.out.println(RED + "Unrecognized option " + RESET + "\"" + selection + "\"");
            }
        }

        return selection;
    }

    public static void listDB(String array[][]){
        listDBmarked(array, -1);
    }

    public static void listDBmarked(String array[][], int mark){

        String[] wsbuffer = new String[array.length];
        prepareAligningBuffer(array, wsbuffer);

        System.out.println(GREEN + "Listing database entries:" + RESET);
        for(int i=0; i< array.length; i++){
            if(array[i] != null) {
                if(i==mark){
                    System.out.print(RED);
                 }
                System.out.print("[" + i + "]\t" + array[i][0] + wsbuffer[i] + "\t");
                for (int l = 1; l < array[i].length; l++) {
                    System.out.print(array[i][l] + "\t");
                }
                System.out.println( ((i==mark) ? RESET : "") );
            }
        }
    }

    public static void prepareAligningBuffer(String array[][], String sp_buff[]){
        int maxCol1_width=0;
        // get max with of text from first column of array[][]
        for(int i=0; i< array.length; i++){
            if (array[i]!=null && array[i][0].length() > maxCol1_width){
                maxCol1_width = array[i][0].length();
            }
        }

        // prepare array of whitespaces for element in collumn 0 that are shorter then maxCol1_width
        for(int i=0; i< array.length; i++){
            StringBuilder sb = new StringBuilder();
            if(array[i]!=null && array[i][0].length() < maxCol1_width){
                for(int j=0; j<maxCol1_width-array[i][0].length();j++){
                    sb.append(" ");
                }
                sp_buff[i] = sb.toString();
            }else{
                sp_buff[i]="";
            }
        }
    }

    public static String[][] readDBfromFileDev(String fname/*, String array[][]*/){
        // TODO add explicit path to a file (does not work now when run from console
        String array[][] = {null};
        String getLine = null;
        File file = new File(fname);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                getLine = scan.nextLine();
                array = addTaskToArray(array, getLine, whereToAddData(array));
            }
        }catch(FileNotFoundException e) {
            // TODO ask for a path to file
            System.out.println("Missing file "+fname);
            return null;
        }
        return array;
    }

    public static void printMainMenu(String menu[], boolean anyModyficationsDone){
        int maxMenuItems = ((anyModyficationsDone) ? 0 : -1) + menu.length;
        System.out.println("\n" + BLUE + "Please select an option:" + RESET);
        for(int i=0; i< maxMenuItems; i++){
            // Colour exit commands when any modyfications to DB performed
            if(anyModyficationsDone && i >= menu.length-2){
                System.out.print("* " + ((i == menu.length-2) ? GREEN : RED) );
                System.out.println(menu[i] + RESET + ((i == menu.length-2) ? "\t(save & exit)" : "\t(discard changes)"));
            }else {
                System.out.println("* " + menu[i]);
            }
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
