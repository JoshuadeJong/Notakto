import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Random rand = new Random(System.currentTimeMillis());

        UserInterface ui = new UserInterface(sc, rand);
        ui.start();
    }
}