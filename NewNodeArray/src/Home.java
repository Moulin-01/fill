package NewNodeArray.src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Home {
    public static void main(String[] args) throws IOException {
        // 1) carregar uma imagem em preto e branco
        BufferedImage img = ImageIO.read(new File(
                "C:\\Users\\gabri\\OneDrive\\Documentos\\FILL\\NewNodeArray\\src\\bw_test_64.png.png"
        ));

        // 2) construir a grade (grid) a partir da imagem
        Node2D<Integer> grid = Grid.gridFromImage(img);

        // 3) escolher a semente (pixel inicial) + cor de pintura
        int seedX = 10, seedY = 10;   // coordenadas do pixel (x, y)
        int newColor = 0xFFFF0077;    // ARGB (rosa opaco)

        // 4) diretório dos frames
        File framesDir = new File("frames");
        int captureEvery = 100; // salva um frame a cada 100 pixels preenchidos

        // 5) executar o flood fill salvando os frames
        Grid.floodFillWhiteWithFrames(grid, seedY, seedX, newColor, captureEvery, framesDir);

        // 6) salvar imagem final
        BufferedImage out = Grid.gridToImage(grid);
        ImageIO.write(out, "png", new File("out.png"));

        // 7) abrir animação Swing
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Flood Fill Animation");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // cria o painel de animação (20 fps = delay de 50ms)
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
