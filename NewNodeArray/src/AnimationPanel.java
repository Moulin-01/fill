package NewNodeArray.src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Painel simples para animar frames PNG de UMA pasta.
 * Use com frames gerados (ex.: frames_PILHA" ou "frames_FILA"/).
 */
public class AnimationPanel extends JPanel {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private boolean playing = true;

    public AnimationPanel(File framesDir, int delayMs) throws IOException {
        if (delayMs <= 0) delayMs = 50;
        loadFrames(framesDir);
        setPreferredSize(new Dimension(frames[0].getWidth(), frames[0].getHeight()));

        // Timer para avançar os frames
        Timer timer = new Timer(delayMs, e -> {
            if (!playing || frames.length == 0) return;
            currentFrame = (currentFrame + 1) % frames.length;
            repaint();
        });
        timer.start();

        // Tecla ESPAÇO pausa/continua
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    playing = !playing;
                }
            }
        });
    }

    private void loadFrames(File framesDir) throws IOException {
        File[] files = framesDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".png"); }
        });
        if (files == null || files.length == 0) {
            throw new IOException("Nenhum PNG em: " + framesDir.getAbsolutePath());
        }

        // Ordena por nome (funciona com zero-padding: frame_000001.png)
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
        // Centraliza
        int x = (getWidth() - frames[currentFrame].getWidth()) / 2;
        int y = (getHeight() - frames[currentFrame].getHeight()) / 2;
        g2.drawImage(frames[currentFrame], x, y, null);
        g2.dispose();
    }

    /** Executa o player para UMA pasta (altere o path). */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                //  ajuste aqui a pasta (ex.: "frames_PILHA" ou "frames_FILA")
                File framesDir = new File("frames_PILHA");

                AnimationPanel panel = new AnimationPanel(framesDir, 50);

                JFrame frame = new JFrame("Flood Fill Animation (single)");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                // foca para receber a tecla espaço
                panel.requestFocusInWindow();

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
