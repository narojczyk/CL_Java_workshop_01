package com.narojczyk;

import static com.narojczyk.ConsoleColors.*;

public class Main {

    public static void main(String[] args) {
        String menuItems[] = {"add", "remove", "list", "help", "exit", "!exit"};
        String menuSelect;
        // String[][] tasks;

        // main program loop
        while (true){
            // TODO: wstawic zmienna za true
            menuSelect = selectAction(menuItems, true);

            printHelp(menuItems);

            // Exit the program if either of last 2 exit options is seleced
            if(menuSelect.equals(menuItems[menuItems.length-1]) ||
               menuSelect.equals(menuItems[menuItems.length-2]) ){
                break;
            }
        }
    }

    public static String selectAction(String menu[], boolean anyModyficationsDone) {
        printMainMenu(menu, anyModyficationsDone);

        // TODO return user selected (valid) string
        return "exit";
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
