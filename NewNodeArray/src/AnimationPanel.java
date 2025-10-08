package NewNodeArray.src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Painel simples para animar frames PNG na pasta "frames".
 * Use junto com uma variante do Flood Fill que salva frames durante a pintura.
 */
public class AnimationPanel extends JPanel {

    private BufferedImage[] frames;
    private int currentFrame = 0;

    public AnimationPanel(File framesDir, int delayMs) throws IOException {
        loadFrames(framesDir);
        setPreferredSize(new Dimension(frames[0].getWidth(), frames[0].getHeight()));

        Timer timer = new Timer(delayMs, e -> {
            if (frames.length == 0) return;
            currentFrame = (currentFrame + 1) % frames.length;
            repaint();
        });
        timer.start();
    }

    private void loadFrames(File framesDir) throws IOException {
        File[] files = framesDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".png"); }
        });
        if (files == null || files.length == 0) throw new IOException("Nenhum PNG em: " + framesDir.getAbsolutePath());

        // Ordenação simples por nome (nomes zero-padded: frame_000001.png etc.)
        for (int i = 0; i < files.length - 1; i++) {
            for (int j = 0; j < files.length - i - 1; j++) {
                if (files[j].getName().compareTo(files[j + 1].getName()) > 0) {
                    File t = files[j]; files[j] = files[j + 1]; files[j + 1] = t;
                }
            }
        }

        frames = new BufferedImage[files.length];
        for (int i = 0; i < files.length; i++) frames[i] = ImageIO.read(files[i]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (frames == null || frames.length == 0) return;
        Graphics2D g2 = (Graphics2D) g.create();
        int x = (getWidth() - frames[currentFrame].getWidth()) / 2;
        int y = (getHeight() - frames[currentFrame].getHeight()) / 2;
        g2.drawImage(frames[currentFrame], x, y, null);
        g2.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                File framesDir = new File("frames");
                AnimationPanel panel = new AnimationPanel(framesDir, 50);

                JFrame frame = new JFrame("Flood Fill Animation");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
