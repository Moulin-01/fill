package NewNodeArray.src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AnimationPanel extends JPanel {

    private BufferedImage[] frames; // Array de imagens
    private int currentFrame = 0;   // índice do frame atual
    private int delay = 100;        // delay entre os frames em ms (100ms = 10fps)

    public AnimationPanel(File framesDir, int delay) throws IOException {
        this.delay = delay;
        loadFrames(framesDir);

        // Timer Swing para atualizar o frame
        Timer timer = new Timer(delay, e -> {
            currentFrame = (currentFrame + 1) % frames.length;
            repaint(); // redesenha o painel
        });
        timer.start();
    }

    private void loadFrames(File framesDir) throws IOException {
        File[] files = framesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null || files.length == 0) {
            throw new IOException("Nenhum frame PNG encontrado em: " + framesDir.getAbsolutePath());
        }

        // Ordena os arquivos pelo nome (frame_000001.png, frame_000002.png, ...)
        java.util.Arrays.sort(files);

        frames = new BufferedImage[files.length];
        for (int i = 0; i < files.length; i++) {
            frames[i] = ImageIO.read(files[i]);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (frames != null && frames.length > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Centralizar a imagem no painel
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

                File framesDir = new File("frames"); // pasta dos frames
                AnimationPanel panel = new AnimationPanel(framesDir, 50); // 50ms = ~20fps

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
