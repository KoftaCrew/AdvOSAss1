import java.util.Scanner;

public class Main {
    static int [] resources; //the actual amount of each resource
    static int [] available; //the available amount of each resource
    static int [][] maximum; //the maximum demand of each process
    static int [][] allocation; //the amount currently allocated to each process
    static int [][] need; //the remaining needs of each process
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        // TODO get inputs from the user
        System.out.println("Number of resources:");
        int m = Integer.parseInt(scanner.nextLine());

        System.out.println("Number of processes:");
        int n = Integer.parseInt(scanner.nextLine());

        resources = new int[m];
        maximum = new int[n][m];
        allocation = new int[n][m];
        need = new int[n][m];

        System.out.println("Available resources seperated by space:");
        String[] input = scanner.nextLine().split(" ");
        for (int i = 0; i < m; i++) {
            resources[i] = Integer.parseInt(input[i]);
            available[i] = resources[i];
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
                available[j] -= allocation[i][j];
                if (available[j] < 0)
                    throw new Exception("Allocation is greater than available resources");
                need[i][j] = maximum[i][j] - allocation[i][j];
                if (need[i][j] < 0)
                    throw new Exception("Allocation is greater than maximum resources");
            }
        }

        // TODO check initial safe state

        // TODO menu

        scanner.close();
    }
}
