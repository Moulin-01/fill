package NewNodeArray.src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AnimationPanel extends JPanel {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private int delay = 100;

    public AnimationPanel(File framesDir, int delay) throws IOException {
        this.delay = delay;
        loadFrames(framesDir);

        Timer timer = new Timer(delay, e -> {
            currentFrame = (currentFrame + 1) % frames.length;
            repaint();
        });
        timer.start();
    }

    private void loadFrames(File framesDir) throws IOException {
        File[] files = framesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null || files.length == 0) {
            throw new IOException("Nenhum frame PNG encontrado em: " + framesDir.getAbsolutePath());
        }

        // 🔹 Substitui Arrays.sort() por um Bubble Sort simples
        bubbleSort(files);

        frames = new BufferedImage[files.length];
        for (int i = 0; i < files.length; i++) {
            frames[i] = ImageIO.read(files[i]);
        }
    }

    // Implementação simples de ordenação (Bubble Sort)
    private void bubbleSort(File[] files) {
        int n = files.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (files[j].getName().compareTo(files[j + 1].getName()) > 0) {
                    File temp = files[j];
                    files[j] = files[j + 1];
                    files[j + 1] = temp;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (frames != null && frames.length > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            int x = (getWidth() - frames[currentFrame].getWidth()) / 2;
            int y = (getHeight() - frames[currentFrame].getHeight()) / 2;

            g2d.drawImage(frames[currentFrame], x, y, null);
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Flood Fill Animation");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                File framesDir = new File("frames");
                AnimationPanel panel = new AnimationPanel(framesDir, 50);

                frame.add(panel);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}