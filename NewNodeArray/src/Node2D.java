package NewNodeArray.src;

/**
 * Grade 2D ligada: cada célula tem valor (ARGB ou genérico T) e ponteiros
 * para cima/baixo/esquerda/direita. Usada como “matriz” para Flood Fill.
 */
public class Node2D<T> {

    /** Nó de uma célula da grade. */
    public static class Node2DGrid<T> {
        public T value; // valor armazenado (ARGB, por ex.)
        public Node2DGrid<T> next, prev, up, down;
        Node2DGrid(T value) { this.value = value; }
    }

    // Âncoras e metadados
    private Node2DGrid<T> top, bottom, left, right;
    private int rows, cols, size;

    public Node2D() {}

    /** Getters */
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getSize() { return size; }
    public Node2DGrid<T> getTop() { return top; } // se precisar

    /**
     * Constrói grade vazia (height x width) com todos os valores = null,
     * e "costura" os ponteiros em 2D.
     */
    public void buildEmptyGridFromImageSize(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("width/height must be > 0");
        if (size != 0) throw new IllegalStateException("Grid already initialized.");

        // Primeira linha
        Node2DGrid<T> first = new Node2DGrid<>(null);
        top = left = first;

        Node2DGrid<T> cur = first;
        for (int x = 1; x < width; x++) {
            Node2DGrid<T> n = new Node2DGrid<>(null);
            cur.next = n; n.prev = cur;
            cur = n;
        }
        right = cur;

        // Linhas restantes
        Node2DGrid<T> rowAboveHead = first;
        for (int y = 1; y < height; y++) {
            Node2DGrid<T> newRowHead = new Node2DGrid<>(null);
            rowAboveHead.down = newRowHead; newRowHead.up = rowAboveHead;

            Node2DGrid<T> above = rowAboveHead;
            Node2DGrid<T> leftInNew = newRowHead;
            for (int x = 1; x < width; x++) {
                Node2DGrid<T> n = new Node2DGrid<>(null);
                leftInNew.next = n; n.prev = leftInNew;   // liga horizontal
                above = above.next;
                above.down = n; n.up = above;             // liga vertical
                leftInNew = n;
            }
            rowAboveHead = newRowHead;
            if (y == height - 1) bottom = newRowHead;
        }
        if (height == 1) bottom = top;
        if (width  == 1) right  = top;

        this.rows = height;
        this.cols  = width;
        this.size  = width * height;
    }

    /** Acesso por coordenada: navega a partir do topo */
    public Node2DGrid<T> getNodeAt(int row, int col) {
        if (row < 0 || col < 0 || row >= rows || col >= cols)
            throw new IndexOutOfBoundsException("row=" + row + " col=" + col);
        if (top == null) throw new IllegalStateException("Grid is empty.");
        Node2DGrid<T> current = top;
        for (int i = 0; i < row; i++) current = current.down;
        for (int j = 0; j < col; j++) current = current.next;
        return current;
    }

    /** Define valor da célula (row,col). */
    public void setNode(T value, int row, int col) { getNodeAt(row, col).value = value; }
}
