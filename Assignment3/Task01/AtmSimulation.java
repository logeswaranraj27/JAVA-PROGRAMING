import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.InputMismatchException;
import java.util.Scanner;

class InsufficientBalanceException extends Exception {
    InsufficientBalanceException(String message) { super(message); }
}

public class AtmSimulation {
    private int balance = 5000;

    public void withdraw(int amount) throws InsufficientBalanceException {
        if (amount < 0) throw new IllegalArgumentException("Negative amount is not allowed");
        if (amount > balance) throw new InsufficientBalanceException("Insufficient balance");
        balance -= amount;
        System.out.println("Dispensed " + amount + ". Remaining balance " + balance);
    }

    public static void main(String[] args) {
        AtmSimulation atm = new AtmSimulation();
        Scanner input = new Scanner(System.in);
        try {
            System.out.print("Enter withdrawal amount: ");
            int amount = input.nextInt();
            atm.withdraw(amount);
        } catch (InputMismatchException e) {
            System.out.println("Input must be a whole number");
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } finally {
            input.close();
            System.out.println("Transaction session closed");
        }
    }
}
