import java.awt.*;
import javax.swing.*;

public class LibrarySearch extends JFrame {
    public LibrarySearch() {
        super("Campus Library Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLayout(new BorderLayout(8, 8));

        JPanel title = new JPanel(new FlowLayout(FlowLayout.CENTER));
        title.add(new JLabel("Library Search"));

        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(28);
        JComboBox<String> category = new JComboBox<>(new String[]{"All", "Title", "Author", "ISBN"});
        search.add(new JLabel("Search"));
        search.add(searchField);
        search.add(category);
        search.add(new JButton("Search"));

        JTextArea results = new JTextArea("Results appear here");
        results.setEditable(false);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(new JButton("Prev"));
        bottom.add(new JButton("Next"));
        bottom.add(new JButton("Reserve"));
        bottom.add(new JLabel("Status: Ready"));

        JPanel north = new JPanel(new BorderLayout());
        north.add(title, BorderLayout.NORTH);
        north.add(search, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(results), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearch().setVisible(true));
    }
}
