import java.util.Scanner;

class StringReverser {

    public String reverseString(String input) {
        StringBuilder reversed = new StringBuilder(input);
        return reversed.reverse().toString();
    }
}

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringReverser reverser = new StringReverser();

        while (true) {
            System.out.println("Enter a string to reverse (or type 'exit' to quit):");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String reversed = reverser.reverseString(input);
            System.out.println("Reversed string: " + reversed);
        }

        scanner.close();
    }
}

