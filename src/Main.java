// Kareem Mohamed Morsy Ismail       , ID: 20190386, Group: CS-S3, Program: CS
// David Emad Philip Ata-Allah       , ID: 20190191, Group: CS-S3, Program: CS  
// Mostafa Mahmoud Anwar Morsy Sadek , ID: 20190544, Group: CS-S3, Program: CS
// Mohamed Ashraf Mohamed Ali        , ID: 20190424, Group: CS-S3, Program: CS

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static int[] available;     // The actual amount of each resource
    static int[][] maximum;     // The maximum demand of each process
    static int[][] allocation;  // The amount currently allocated to each process
    static int[][] need;        // The remaining needs of each process
    static boolean[] finish;    // Flag containing status of process
    static boolean safe = false;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        System.out.println("Number of resources:");
        int m = Integer.parseInt(scanner.nextLine());

        System.out.println("Number of processes:");
        int n = Integer.parseInt(scanner.nextLine());

        available = new int[m];
        maximum = new int[n][m];
        allocation = new int[n][m];
        need = new int[n][m];
        finish = new boolean[n];
        Arrays.fill(finish, false);

        System.out.println("Available resources separated by space:");
        String[] input = scanner.nextLine().split(" ");
        for (int i = 0; i < m; i++) {
            available[i] = Integer.parseInt(input[i]);

        }

        System.out.println("Maximum matrix:");
        for (int i = 0; i < n; i++) {
            input = scanner.nextLine().split(" ");
            for (int j = 0; j < m; j++) {
                maximum[i][j] = Integer.parseInt(input[j]);
            }
        }

        System.out.println("Allocation matrix:");
        for (int i = 0; i < n; i++) {
            input = scanner.nextLine().split(" ");
            for (int j = 0; j < m; j++) {
                allocation[i][j] = Integer.parseInt(input[j]);
                need[i][j] = maximum[i][j] - allocation[i][j];
                if (need[i][j] < 0)
                    throw new Exception("Allocation is greater than maximum resources");
            }
        }

        safe = isSafe();

        while (true) {
            System.out.println();
            printValues();
            System.out.println();
            System.out.println("Available commands:");
            System.out.println("    RQ <process#> <r1> <r2> <r3>");
            System.out.println("    RL <process#> <r1> <r2> <r3>");
            if (!safe) {
                System.out.println("    Recover");
            }
            System.out.println("    Quit");

            String[] command = scanner.nextLine().split(" ");
            if (command[0].equalsIgnoreCase("RQ") && command.length == 2 + available.length) {
                int[] resources = new int[available.length];
                for (int i = 0; i < resources.length; i++) {
                    resources[i] = Integer.parseInt(command[2 + i]);
                }
                RQ(Integer.parseInt(command[1]), resources);
            } else if (command[0].equalsIgnoreCase("RL") && command.length == 2 + available.length) {
                int[] resources = new int[available.length];
                for (int i = 0; i < resources.length; i++) {
                    resources[i] = Integer.parseInt(command[2 + i]);
                }
                RL(Integer.parseInt(command[1]), resources);
            } else if (command[0].equalsIgnoreCase("Recover")) {
                do {
                    recovery();
                } while (!isSafe());
            } else if (command[0].equalsIgnoreCase("Quit")) {
                break;
            } else {
                System.out.println("Unknown request");
            }
        }

        scanner.close();
    }

    public static int getFinishableProcess() { // finish[i]== false and less than need
        for (int i = 0; i < finish.length; i++) {
            if (!finish[i]) {
                for (int j = 0; j < available.length; j++) {
                    if (need[i][j] <= available[j]) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /*
     * Check whether the program is still in safe zone.
     */
    public static boolean isSafe() {

        int i = 0;
        int processLength = finish.length;
        ArrayList<Integer> processesOrder = new ArrayList<Integer>();
        int resourceLength = need[0].length;
        int[] work = Arrays.copyOf(available,available.length);
        boolean[] finish = Arrays.copyOf(Main.finish, Main.finish.length);
        while (i < processLength) {
            if (finish[i] == true) {
                ++i;
                continue;
            }
            boolean test = true;
            for (int j = 0; j < resourceLength; ++j) {
                if (need[i][j] > work[j]) {
                    test = false;
                    break;
                }
            }
            if (test) {
                finish[i] = true;
                for (int j = 0; j < resourceLength; ++j) {
                    work[j] += need[i][j];
                }
                processesOrder.add(i);
                i = -1;
            }
            ++i;
        }

        boolean ret = true;
        for (int j = 0; j < processLength; ++j) {
            ret &= finish[j];
        }

        if (ret) {
            if (processesOrder.isEmpty())
                System.out.println("System in safe state, all processes did finish");
            else
                System.out.println("System in safe state with sequence:");
            for (int j = 0; j < processesOrder.size(); j++) {
                System.out.println("Process #" + processesOrder.get(j));
            }
        } else {
            System.out.println("System is not in a safe state. Try recovering...");
        }

        return ret;
    }

    public static void recovery() { // Chooses a victim who has the most resources needed
        int max = 0;
        int maxIdx = 0;
        for (int i = 0; i < maximum.length; i++) {
            if (finish[i]) 
                continue;

            int sum = 0;
            for (int j = 0; j < maximum[i].length; j++) {
                sum += maximum[i][j];
            }

            if (sum > max) {
                max = sum;
                maxIdx = i;
            }
        }

        System.out.println("Chose Process #" + maxIdx + " as a victim for recovery");
        finish[maxIdx] = true;
        for (int i = 0; i < available.length; i++) {
            available[i] += allocation[maxIdx][i];
            need[maxIdx][i] += allocation[maxIdx][i];
            allocation[maxIdx][i] = 0;
        }
    }

    public static void RQ(int processNum, int[] newResources){
        for (int index = 0; index < newResources.length; index++) {
            need[processNum][index] += newResources[index];
            maximum[processNum][index] += newResources[index];
        }
        safe = isSafe();
    }

    public static void RL(int processNum, int[] resources){
        for (int i = 0; i < resources.length; i++) {
            if (allocation[processNum][i] < resources[i]) {
                System.out.println("Releasing more than allocated resources, rolling back...");
                return;
            }
        }

        for (int i = 0; i < resources.length; i++){
            allocation[processNum][i] -= resources[i];
            available[i] +=resources[i];
        }
    }

    public static void printValues() {
        System.out.println("Available Resources Vector:");
        for (int i = 0; i < available.length; ++i) {
            System.out.print(available[i] + " ");
        }
        System.out.println();
        System.out.println();

        System.out.println("Max Resources Matrix:");
        for (int i = 0; i < maximum.length; ++i) {
            System.out.print("P" + i + "| ");
            for (int j = 0; j < maximum[0].length; ++j) {
                System.out.print(maximum[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("Allocated Resources Matrix:");
        for (int i = 0; i < allocation.length; ++i) {
            System.out.print("P" + i + "| ");
            for (int j = 0; j < allocation[0].length; ++j) {
                System.out.print(allocation[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("Needed Resources Matrix:");
        for (int i = 0; i < need.length; ++i) {
            System.out.print("P" + i + "| ");
            for (int j = 0; j < need[0].length; ++j) {
                System.out.print(need[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
