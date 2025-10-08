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

        // 3) Parâmetros da pintura
        int seedX = 32, seedY = 32;      // coordenadas do pixel (x,y)
        int newColor = 0xFF22CCFF;       // ARGB (opaco)

        // 4) Flood Fill com PILHA (DFS)
        Grid.floodFillWhiteStack(gridDFS, seedY, seedX, newColor);
        ImageIO.write(Grid.gridToImage(gridDFS), "png", new File("out_stack.png"));

        // 5) Flood Fill com FILA (BFS)
        Grid.floodFillWhiteQueue(gridBFS, seedY, seedX, newColor);
        ImageIO.write(Grid.gridToImage(gridBFS), "png", new File("out_queue.png"));

        System.out.println("Gerado: out_stack.png e out_queue.png");
    }
}
