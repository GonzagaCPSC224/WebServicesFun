import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class View extends JFrame {
    static final int FONT_SIZE = 18;

    Controller controller;
    JLabel titleLabel;
    JLabel dateTakenLabel;
    JLabel imageLabel;
    JProgressBar progressBar;
    JButton nextButton;

    public View(Controller c) {
        super("Flickr Interesting Photos");
        controller = c;

        setPreferredSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        pack();
        setVisible(true);
    }

    private void setupUI() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        titleLabel = new JLabel(" ");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("default", Font.BOLD, FONT_SIZE));
        dateTakenLabel = new JLabel(" ");
        dateTakenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateTakenLabel.setFont(new Font("default", Font.BOLD, FONT_SIZE));
        infoPanel.add(titleLabel);
        infoPanel.add(dateTakenLabel);
        getContentPane().add(infoPanel, BorderLayout.NORTH);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(imageLabel, BorderLayout.CENTER);

        JPanel advancePanel = new JPanel();
        advancePanel.setLayout(new BoxLayout(advancePanel, BoxLayout.Y_AXIS));
        // https://docs.oracle.com/javase/tutorial/uiswing/components/progress.html
        progressBar = new JProgressBar();
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        advancePanel.add(progressBar);

        nextButton = new JButton("Next Photo");
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        advancePanel.add(nextButton);
        getContentPane().add(advancePanel, BorderLayout.SOUTH);
    }

    public ImageIcon getScaledImageIcon(Image srcImg){
        ImageIcon icon = new ImageIcon(srcImg);
        int width = -1; // -1 to preserve aspect ratio
        int height = -1;
        if (icon.getIconWidth() <= icon.getIconHeight()) {
            height = imageLabel.getHeight();
        }
        else {
            width = imageLabel.getWidth();
        }
        // https://docs.oracle.com/javase/7/docs/api/java/awt/Image.html
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image, icon.getDescription());
        return icon;
    }

}
