package ui;

import java.util.Scanner;

// Program Entry
public class Main {
    @SuppressWarnings("methodlength")
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        StationNetworkManagerConsole manager;

        System.out.println("Choose a user interface:");
        System.out.print("[0] Console  [1] Graphical\n");

        // UI selection
        int modeUI;
        while (true) {
            System.out.print(">");
            if (input.hasNextInt()) {
                modeUI = input.nextInt();
                break;
            }
            // skip non-int input word
            input.next();
        }

        // Manager instantiation
        switch (modeUI) {
            case 0: {
                System.out.println();
                manager = new StationNetworkManagerConsole();
                break;
            }
            case 1: {
                System.out.println();
                manager = new StationNetworkManagerGUI();
                break;
            }
            default: {
                System.err.println("Invalid Selection -> Switch to Console mode by default.\n");
                manager = new StationNetworkManagerConsole();
                break;
            }
        }

        // Run manager
        manager.runNetworkManager();
    }
}
