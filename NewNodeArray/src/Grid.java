package NewNodeArray.src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilitários de grade/imagem + Flood Fill:
 * - FILA (BFS) HORIZONTAL (scanline por LINHAS)
 * - PILHA (DFS)  VERTICAL   (scanline por COLUNAS)
 *
 * Usa Node2D<Integer> (ARGB por célula) como “canvas” de pixels.
 */
public final class Grid {
    private Grid() {}

    /* =========================================================
       imagem -> grade (ARGB por célula)
       ========================================================= */
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

    /* =========================================================
       grade -> imagem
       ========================================================= */
    public static BufferedImage gridToImage(Node2D<Integer> g) {
        BufferedImage out = new BufferedImage(g.getCols(), g.getRows(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < g.getRows(); y++) {
            for (int x = 0; x < g.getCols(); x++) {
                Node2D.Node2DGrid<Integer> n = g.getNodeAt(y, x);
                int argb = (n.value == null) ? 0 : n.value;
                out.setRGB(x, y, argb);
            }
        }
        return out;
    }

    /* =========================================================
       FILA (BFS) HORIZONTAL — scanline por LINHAS (4-conectado)
       - Pinta um segmento HORIZONTAL (esquerda<->direita) por vez
       - Enfileira CIMA/BAIXO dos pixels desse segmento
       ========================================================= */
    public static void floodFillWhiteQueueHorizontalWithFrames(
            Node2D<Integer> g, int seedRow, int seedCol, int newColor,
            int captureEvery, File outDir
    ) throws java.io.IOException {

        if (captureEvery <= 0) captureEvery = 1;
        if (outDir != null) outDir.mkdirs();

        Node2D.Node2DGrid<Integer> start = g.getNodeAt(seedRow, seedCol);
        if (start == null || start.value == null) return;
        if (!isWhite(start.value)) return;                 // só preenche área branca
        if (sameRgb(start.value, newColor)) return;        // já está na cor destino

        final int bg = start.value;                        // “cor de fundo” (branco)
        boolean[][] seen = new boolean[g.getRows()][g.getCols()];
        MyQueue<int[]> q = new MyQueue<>();

        q.enqueue(new int[]{seedRow, seedCol});
        seen[seedRow][seedCol] = true;

        int painted = 0;

        // frame inicial (opcional)
        if (outDir != null) {
            ImageIO.write(gridToImage(g), "png",
                    new File(outDir, String.format("frame_%06d.png", painted)));
        }

        while (!q.isEmpty()) {
            int[] rc = q.dequeue();
            int r = rc[0], c = rc[1];

            Node2D.Node2DGrid<Integer> n = g.getNodeAt(r, c);
            if (n == null || n.value == null || !sameRgb(n.value, bg)) continue;

            // 1) espalha HORIZONTAL: para a ESQUERDA
            int left = c;
            while (left >= 0) {
                Node2D.Node2DGrid<Integer> cell = g.getNodeAt(r, left);
                if (cell == null || cell.value == null || !sameRgb(cell.value, bg)) break;
                cell.value = newColor;
                painted++;
                if (outDir != null && painted % captureEvery == 0) {
                    ImageIO.write(gridToImage(g), "png",
                            new File(outDir, String.format("frame_%06d.png", painted)));
                }
                left--;
            }
            left++; // primeiro pintado do segmento

            // 2) espalha HORIZONTAL: para a DIREITA
            int right = c + 1;
            while (right < g.getCols()) {
                Node2D.Node2DGrid<Integer> cell = g.getNodeAt(r, right);
                if (cell == null || cell.value == null || !sameRgb(cell.value, bg)) break;
                cell.value = newColor;
                painted++;
                if (outDir != null && painted % captureEvery == 0) {
                    ImageIO.write(gridToImage(g), "png",
                            new File(outDir, String.format("frame_%06d.png", painted)));
                }
                right++;
            }
            right--; // último pintado do segmento

            // 3) agenda CIMA/BAIXO para cada coluna do segmento pintado
            for (int col = left; col <= right; col++) {
                int up = r - 1;    // cima
                if (up >= 0 && !seen[up][col]) {
                    Node2D.Node2DGrid<Integer> upCell = g.getNodeAt(up, col);
                    if (upCell != null && upCell.value != null && sameRgb(upCell.value, bg)) {
                        q.enqueue(new int[]{up, col});
                        seen[up][col] = true;
                    }
                }
                int down = r + 1;  // baixo
                if (down < g.getRows() && !seen[down][col]) {
                    Node2D.Node2DGrid<Integer> downCell = g.getNodeAt(down, col);
                    if (downCell != null && downCell.value != null && sameRgb(downCell.value, bg)) {
                        q.enqueue(new int[]{down, col});
                        seen[down][col] = true;
                    }
                }
            }
        }

        // frame final
        if (outDir != null) {
            ImageIO.write(gridToImage(g), "png",
                    new File(outDir, String.format("frame_%06d.png", painted)));
        }
    }

    /* =========================================================
       PILHA (DFS) VERTICAL — scanline por COLUNAS (4-conectado)
       - Pinta um segmento VERTICAL (cima<->baixo) por vez
       - Empilha ESQUERDA/DIREITA dos pixels desse segmento
       ========================================================= */
    public static void floodFillWhiteStackVerticalWithFrames(
            Node2D<Integer> g, int seedRow, int seedCol, int newColor,
            int captureEvery, File outDir
    ) throws java.io.IOException {

        if (captureEvery <= 0) captureEvery = 1;
        if (outDir != null) outDir.mkdirs();

        Node2D.Node2DGrid<Integer> start = g.getNodeAt(seedRow, seedCol);
        if (start == null || start.value == null) return;
        if (!isWhite(start.value)) return;                 // só área branca
        if (sameRgb(start.value, newColor)) return;        // já é a cor destino

        final int bg = start.value;
        boolean[][] seen = new boolean[g.getRows()][g.getCols()];
        MyStack<int[]> st = new MyStack<>();

        st.push(new int[]{seedRow, seedCol});
        seen[seedRow][seedCol] = true;

        int painted = 0;

        // frame inicial (opcional)
        if (outDir != null) {
            ImageIO.write(gridToImage(g), "png",
                    new File(outDir, String.format("frame_%06d.png", painted)));
        }

        while (!st.isEmpty()) {
            int[] rc = st.pop();
            int r = rc[0], c = rc[1];

            Node2D.Node2DGrid<Integer> n = g.getNodeAt(r, c);
            if (n == null || n.value == null || !sameRgb(n.value, bg)) continue;

            // 1) sobe pintando (coluna)
            int up = r;
            while (up >= 0) {
                Node2D.Node2DGrid<Integer> cell = g.getNodeAt(up, c);
                if (cell == null || cell.value == null || !sameRgb(cell.value, bg)) break;
                cell.value = newColor;
                painted++;
                if (outDir != null && painted % captureEvery == 0) {
                    ImageIO.write(gridToImage(g), "png",
                            new File(outDir, String.format("frame_%06d.png", painted)));
                }
                up--;
            }
            up++; // primeiro pintado

            // 2) desce pintando (coluna)
            int down = r + 1;
            while (down < g.getRows()) {
                Node2D.Node2DGrid<Integer> cell = g.getNodeAt(down, c);
                if (cell == null || cell.value == null || !sameRgb(cell.value, bg)) break;
                cell.value = newColor;
                painted++;
                if (outDir != null && painted % captureEvery == 0) {
                    ImageIO.write(gridToImage(g), "png",
                            new File(outDir, String.format("frame_%06d.png", painted)));
                }
                down++;
            }
            down--; // último pintado

            // 3) empilha LADOS (esquerda/direita) do segmento vertical
            for (int row = up; row <= down; row++) {
                int lc = c - 1; // esquerda
                if (lc >= 0 && !seen[row][lc]) {
                    Node2D.Node2DGrid<Integer> left = g.getNodeAt(row, lc);
                    if (left != null && left.value != null && sameRgb(left.value, bg)) {
                        st.push(new int[]{row, lc});
                        seen[row][lc] = true;
                    }
                }
                int rc2 = c + 1; // direita
                if (rc2 < g.getCols() && !seen[row][rc2]) {
                    Node2D.Node2DGrid<Integer> right = g.getNodeAt(row, rc2);
                    if (right != null && right.value != null && sameRgb(right.value, bg)) {
                        st.push(new int[]{row, rc2});
                        seen[row][rc2] = true;
                    }
                }
            }
        }

        // frame final
        if (outDir != null) {
            ImageIO.write(gridToImage(g), "png",
                    new File(outDir, String.format("frame_%06d.png", painted)));
        }
    }

    /* ===================== helpers ===================== */

    // BRANCO estrito (ignora alpha)
    private static boolean isWhite(int argb) {
        return (argb & 0x00FFFFFF) == 0x00FFFFFF;
    }

    // compara só o RGB (ignora alpha)
    private static boolean sameRgb(int c1, int c2) {
        return (c1 & 0x00FFFFFF) == (c2 & 0x00FFFFFF);
    }
}
