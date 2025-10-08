package NewNodeArray.src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utilidades: converter imagem<->grade e implementar Flood Fill
 * em duas versões: DFS (Pilha) e BFS (Fila)
 */
public final class Grid {
    private Grid() {}

    /* ---------- imagem -> grade (copia ARGB por célula) ---------- */
    public static Node2D<Integer> gridFromImage(BufferedImage img) {
        Node2D<Integer> g = new Node2D<>();
        g.buildEmptyGridFromImageSize(img.getWidth(), img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                g.setNode(img.getRGB(x, y), y, x); // row=y, col=x
            }
        }
        return g;
    }

    /* ---------- grade -> imagem ---------- */
    public static BufferedImage gridToImage(Node2D<Integer> g) {
        BufferedImage out = new BufferedImage(g.cols, g.rows, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < g.rows; y++) {
            for (int x = 0; x < g.cols; x++) {
                Node2D.Node2DGrid<Integer> n = g.getNodeAt(y, x);
                int argb = (n.value == null) ? 0 : n.value;
                out.setRGB(x, y, argb);
            }
        }
        return out;
    }

    /* ===========================================================
       DFS (PILHA)
       - Captura cor de fundo (bg) na semente
       - Usa MyStack<int[]> para coordenadas
       - Usa seen[][] para não empilhar repetido
       - PINTA ao DESPILHAR, se a cor atual == bg
       =========================================================== */
    public static void floodFillWhiteStack(Node2D<Integer> g, int seedRow, int seedCol, int newColor) {
        Node2D.Node2DGrid<Integer> start = g.getNodeAt(seedRow, seedCol);
        if (start == null || start.value == null) return;

        final int bg = start.value;                // cor de fundo capturada na semente
        boolean[][] seen = new boolean[g.rows][g.cols];

        MyStack<int[]> st = new MyStack<>();
        st.push(new int[]{seedRow, seedCol});
        seen[seedRow][seedCol] = true;

        while (!st.isEmpty()) {
            int[] rc = st.pop();
            int r = rc[0], c = rc[1];

            Node2D.Node2DGrid<Integer> n = g.getNodeAt(r, c);
            Integer val = n.value;
            if (val == null) continue;
            if (!sameRgb(val, bg)) continue;       // só pinta se corresponde ao bg

            n.value = newColor;                    // pinta aqui

            // vizinhos 4-conectados, checando limites e seen
            if (r-1 >= 0 && !seen[r-1][c]) { st.push(new int[]{r-1, c}); seen[r-1][c] = true; }
            if (r+1 < g.rows && !seen[r+1][c]) { st.push(new int[]{r+1, c}); seen[r+1][c] = true; }
            if (c-1 >= 0 && !seen[r][c-1]) { st.push(new int[]{r, c-1}); seen[r][c-1] = true; }
            if (c+1 < g.cols && !seen[r][c+1]) { st.push(new int[]{r, c+1}); seen[r][c+1] = true; }
        }
    }

    /* ===========================================================
       BFS (FILA)
       - Mesmo raciocínio, agora em ondas (amplitude primeiro)
       =========================================================== */
    public static void floodFillWhiteQueue(Node2D<Integer> g, int seedRow, int seedCol, int newColor) {
        Node2D.Node2DGrid<Integer> start = g.getNodeAt(seedRow, seedCol);
        if (start == null || start.value == null) return;

        final int bg = start.value;
        boolean[][] seen = new boolean[g.rows][g.cols];

        MyQueue<int[]> q = new MyQueue<>();
        q.enqueue(new int[]{seedRow, seedCol});
        seen[seedRow][seedCol] = true;

        while (!q.isEmpty()) {
            int[] rc = q.dequeue();
            int r = rc[0], c = rc[1];

            Node2D.Node2DGrid<Integer> n = g.getNodeAt(r, c);
            Integer val = n.value;
            if (val == null) continue;
            if (!sameRgb(val, bg)) continue;

            n.value = newColor;                    // pinta aqui

            // vizinhos
            if (r-1 >= 0 && !seen[r-1][c]) { q.enqueue(new int[]{r-1, c}); seen[r-1][c] = true; }
            if (r+1 < g.rows && !seen[r+1][c]) { q.enqueue(new int[]{r+1, c}); seen[r+1][c] = true; }
            if (c-1 >= 0 && !seen[r][c-1]) { q.enqueue(new int[]{r, c-1}); seen[r][c-1] = true; }
            if (c+1 < g.cols && !seen[r][c+1]) { q.enqueue(new int[]{r, c+1}); seen[r][c+1] = true; }
        }
    }

    /* Igualdade de RGB ignorando alpha */
    static boolean sameRgb(int c1, int c2) { return (c1 & 0x00FFFFFF) == (c2 & 0x00FFFFFF); }
}
