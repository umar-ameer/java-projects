import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

class BankAccount {
    private final String accountHolder;
    private BigDecimal balance;
    private final List<String> history = new ArrayList<>();
    private final NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public BankAccount(String accountHolder, BigDecimal openingBalance) {
        if (openingBalance == null || openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.accountHolder = accountHolder;
        this.balance = openingBalance.setScale(2, RoundingMode.HALF_UP);
        history.add("Account opened with " + money.format(this.balance));
    }

    public String getAccountHolder() { return accountHolder; }

    public BigDecimal getBalance() { return balance; }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
        history.add("Deposit: " + money.format(amount) + " | Balance: " + money.format(balance));
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        history.add("Withdraw: " + money.format(amount) + " | Balance: " + money.format(balance));
    }

    public List<String> getHistory() { return history; }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }
    }
}

public class BankingApp {
    private static final NumberFormat MONEY = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter account holder name: ");
            String name = sc.nextLine().trim();
            BigDecimal opening = promptAmount(sc, "Enter opening balance: ");

            BankAccount account = new BankAccount(name.isEmpty() ? "User" : name, opening);

            boolean running = true;
            while (running) {
                System.out.println("\n--- Simple Banking Application ---");
                System.out.println("1) Deposit");
                System.out.println("2) Withdraw");
                System.out.println("3) Check Balance");
                System.out.println("4) View Transactions");
                System.out.println("5) Exit");
                System.out.print("Choose an option (1-5): ");

                int choice = promptInt(sc);
                switch (choice) {
                    case 1 -> {
                        BigDecimal amt = promptAmount(sc, "Amount to deposit: ");
                        try {
                            account.deposit(amt);
                            System.out.println("Deposited. Current balance: " + MONEY.format(account.getBalance()));
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                    case 2 -> {
                        BigDecimal amt = promptAmount(sc, "Amount to withdraw: ");
                        try {
                            account.withdraw(amt);
                            System.out.println("Withdrawn. Current balance: " + MONEY.format(account.getBalance()));
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                    case 3 -> System.out.println("Current balance: " + MONEY.format(account.getBalance()));
                    case 4 -> {
                        System.out.println("Transaction history:");
                        account.getHistory().forEach(System.out::println);
                    }
                    case 5 -> {
                        System.out.println("Goodbye.");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please choose 1-5.");
                }
            }
        }
    }
    

    private static BigDecimal promptAmount(Scanner sc, String label) {
        while (true) {
            System.out.print(label);
            String token = sc.nextLine().trim();
            try {
                // Allow values like 100, 100.0, 100.50
                BigDecimal amt = new BigDecimal(token).setScale(2, RoundingMode.HALF_UP);
                if (amt.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException("Amount must be positive.");
                }
                return amt;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a positive number (e.g., 250 or 250.50).");
            }
        }
    }

    private static int promptInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.println("Please enter a number.");
            sc.nextLine();
        }
        int value = sc.nextInt();
        sc.nextLine(); // consume newline
        return value;
    }
}