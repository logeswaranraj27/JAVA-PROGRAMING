import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

public class DrawingPad extends JPanel {
    private final ArrayList<Point> points = new ArrayList<>();
    private final JLabel countLabel;

    public DrawingPad(JLabel countLabel) {
        this.countLabel = countLabel;
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                points.add(event.getPoint());
                countLabel.setText("Clicks: " + points.size());
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        for (Point point : points) {
            g.fillOval(point.x - 3, point.y - 3, 6, 6);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Drawing Pad");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel countLabel = new JLabel("Clicks: 0");
            frame.add(countLabel, BorderLayout.NORTH);
            frame.add(new DrawingPad(countLabel), BorderLayout.CENTER);
            frame.setSize(600, 400);
            frame.setVisible(true);
        });
    }
}
