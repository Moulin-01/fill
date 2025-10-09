package NewNodeArray.src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Exemplo de uso: carrega imagem, roda Flood Fill (PILHA e FILA) e salva saídas.
 */
public class Home {
    public static void main(String[] args) throws Exception {
        // 1) Carrega imagem de entrada (ajuste o caminho se necessário)
        BufferedImage img = ImageIO.read(new File("bw_test_64.png"));

        // 2) Constrói a grade (duas cópias para comparar pilha vs fila)
        Node2D<Integer> gridDFS = Grid.gridFromImage(img);
        Node2D<Integer> gridBFS = Grid.gridFromImage(img);

        // 3) parâmetros
        int seedX = 10, seedY = 10;
        int newColor = 0xFF22CCFF;
        File framesDFS = new File("frames_FILA");
        File framesBFS = new File("frames_PILHA");
        int captureEvery = 10;

        // 4) FILA "horizontal"
        Grid.floodFillWhiteQueueHorizontalWithFrames(gridBFS, seedY, seedX, newColor, captureEvery, framesDFS);

        javax.imageio.ImageIO.write(Grid.gridToImage(gridDFS), "png", new File("out_queue.png"));

        // 5) PILHA "vertical"
        Grid.floodFillWhiteStackVerticalWithFrames(gridDFS, seedY, seedX, newColor, captureEvery, framesBFS);

        javax.imageio.ImageIO.write(Grid.gridToImage(gridBFS), "png", new File("out_stack_vertical.png"));




    }
}
