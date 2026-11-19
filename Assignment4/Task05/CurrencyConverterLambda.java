import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CurrencyConverterLambda extends JFrame {
    private final JTextField amountField = new JTextField(12);
    private final JComboBox<String> from = new JComboBox<>(new String[]{"USD", "EUR", "INR"});
    private final JComboBox<String> to = new JComboBox<>(new String[]{"USD", "EUR", "INR"});
    private final JLabel result = new JLabel("Result");

    public CurrencyConverterLambda() {
        super("Currency Converter");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                dispose();
            }
        });

        setLayout(new GridLayout(4, 2, 8, 8));
        add(new JLabel("Amount")); add(amountField);
        add(new JLabel("From")); add(from);
        add(new JLabel("To")); add(to);
        JButton convert = new JButton("Convert");
        add(convert); add(result);

        convert.addActionListener(e -> convert());
        amountField.addActionListener(e -> convert());
        pack();
        setLocationRelativeTo(null);
    }

    private void convert() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount < 0) throw new NumberFormatException();
            double inr = amount * rate(from.getSelectedItem().toString());
            double converted = inr / rate(to.getSelectedItem().toString());
            String message = String.format("Result: %.2f %s", converted, to.getSelectedItem());
            result.setText(message);
            JOptionPane.showMessageDialog(this, message);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid non-negative amount",
                    "Invalid amount", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double rate(String currency) {
        switch (currency) {
            case "USD": return 83.0;
            case "EUR": return 90.0;
            default: return 1.0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CurrencyConverterLambda().setVisible(true));
    }
}
