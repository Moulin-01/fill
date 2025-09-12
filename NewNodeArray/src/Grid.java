package NewNodeArray.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class Grid {
    private Grid() {}

    /* ============ image -> grid  ============ */
    public static Node2D<Integer> gridFromImage(BufferedImage img) {
        Node2D<Integer> g = new Node2D<Integer>();
        g.buildEmptyGridFromImageSize(img.getWidth(), img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                g.setNode(img.getRGB(x, y), y, x); // row=y, col=x
            }
        }
        return g;
    }

    /* ============ grid -> image ============ */
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

    /*
       Fill WHITE region (black walls) — 4-connected BFS
     */
    public static void floodFillWhite(Node2D<Integer> g, int seedRow, int seedCol, int newColor) {
        Node2D.Node2DGrid<Integer> start = g.getNodeAt(seedRow, seedCol);
        if (start == null || start.value == null) return;
        if (!isWhite(start.value)) return;               // only fill white areas
        if (sameRgb(start.value, newColor)) return;      // already that color

        // Array-based circular queue
        Node2D.Node2DGrid[] q = new Node2D.Node2DGrid[g.size];
        int head = 0, tail = 0;

        // mark visited on enqueue to avoid re-enqueueing
        start.value = newColor;
        q[tail++] = start; if (tail == q.length) tail = 0;

        while (head != tail) {
            Node2D.Node2DGrid<Integer> n = q[head++]; if (head == q.length) head = 0;

            // check neighbors; enqueue whites and mark immediately
            if (n.up != null && n.up.value != null && isWhite(n.up.value)) {
                n.up.value = newColor; q[tail++] = n.up; if (tail == q.length) tail = 0;
            }
            if (n.down != null && n.down.value != null && isWhite(n.down.value)) {
                n.down.value = newColor; q[tail++] = n.down; if (tail == q.length) tail = 0;
            }
            if (n.prev != null && n.prev.value != null && isWhite(n.prev.value)) { // left
                n.prev.value = newColor; q[tail++] = n.prev; if (tail == q.length) tail = 0;
            }
            if (n.next != null && n.next.value != null && isWhite(n.next.value)) { // right
                n.next.value = newColor; q[tail++] = n.next; if (tail == q.length) tail = 0;
            }
        }
    }


    public static void floodFillWhiteWithFrames(
            Node2D<Integer> g, int seedRow, int seedCol, int newColor,
            int captureEvery, File outDir
    ) throws IOException {

        if (captureEvery <= 0) captureEvery = 1;
        if (outDir != null) outDir.mkdirs();

        Node2D.Node2DGrid<Integer> start = g.getNodeAt(seedRow, seedCol);
        if (start == null || start.value == null) return;
        if (!isWhite(start.value)) return;
        if (sameRgb(start.value, newColor)) return;

        Node2D.Node2DGrid[] q = new Node2D.Node2DGrid[g.size];
        int head = 0, tail = 0, painted = 0;

        // enqueue seed
        start.value = newColor; painted++;
        q[tail++] = start; if (tail == q.length) tail = 0;


        if (outDir != null && painted % captureEvery == 0) {
            ImageIO.write(gridToImage(g), "png",
                    new File(outDir, String.format("frame_%06d.png", painted)));
        }

        while (head != tail) {
            Node2D.Node2DGrid<Integer> n = q[head++]; if (head == q.length) head = 0;

            // helper to enqueue a neighbor
            if (n.up != null && n.up.value != null && isWhite(n.up.value)) {
                n.up.value = newColor; painted++;
                q[tail++] = n.up; if (tail == q.length) tail = 0;
                if (outDir != null && painted % captureEvery == 0) {
                    ImageIO.write(gridToImage(g), "png",
                            new File(outDir, String.format("frame_%06d.png", painted)));
                }
            }
            if (n.down != null && n.down.value != null && isWhite(n.down.value)) {
                n.down.value = newColor; painted++;
                q[tail++] = n.down; if (tail == q.length) tail = 0;
                if (outDir != null && painted % captureEvery == 0) {
                    ImageIO.write(gridToImage(g), "png",
                            new File(outDir, String.format("frame_%06d.png", painted)));
                }
            }
            if (n.prev != null && n.prev.value != null && isWhite(n.prev.value)) {
                n.prev.value = newColor; painted++;
                q[tail++] = n.prev; if (tail == q.length) tail = 0;
                if (outDir != null && painted % captureEvery == 0) {
                    ImageIO.write(gridToImage(g), "png",
                            new File(outDir, String.format("frame_%06d.png", painted)));
                }
            }
            if (n.next != null && n.next.value != null && isWhite(n.next.value)) {
                n.next.value = newColor; painted++;
                q[tail++] = n.next; if (tail == q.length) tail = 0;
                if (outDir != null && painted % captureEvery == 0) {
                    ImageIO.write(gridToImage(g), "png",
                            new File(outDir, String.format("frame_%06d.png", painted)));
                }
            }
        }

        // final state
        if (outDir != null) {
            ImageIO.write(gridToImage(g), "png",
                    new File(outDir, String.format("frame_%06d.png", painted)));
        }
    }

    /* helpers */

    // strict white/black
    private static boolean isWhite(int argb) { return (argb & 0x00FFFFFF) == 0x00FFFFFF; }
    @SuppressWarnings("unused")
    private static boolean isBlack(int argb) { return (argb & 0x00FFFFFF) == 0x000000; }

    // compare only RGB
    private static boolean sameRgb(int c1, int c2) {
        return (c1 & 0x00FFFFFF) == (c2 & 0x00FFFFFF);
    }
}
