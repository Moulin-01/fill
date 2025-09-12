package NewNodeArray.src;
public class Node2D<T> {

    public static class Node2DGrid<T>{
        T value;
        Node2DGrid<T> next, prev, up, down;
        Node2DGrid(T value){
            this.value = value;}



    }
    public Node2DGrid<T> top;
    public Node2DGrid<T> bottom;
    public Node2DGrid<T> left;
    public Node2DGrid<T> right;

    public int size;
    public int rows;
    public int cols;
    public Node2D(){

    }
    public boolean isEmpty(){
        if (size == 0){
            return true;
        }
        return false;
    }
    public int getSize(){
        return size;
    }
    public void firstAdd(T e1, T e2, T e3, T e4) {
        if (size != 0) {
            throw new IllegalStateException("firstAdd must be called on an empty grid.");
        }

        Node2DGrid<T> a = new Node2DGrid<>(e1);
        Node2DGrid<T> b = new Node2DGrid<>(e2);
        Node2DGrid<T> c = new Node2DGrid<>(e3);
        Node2DGrid<T> d = new Node2DGrid<>(e4);

        a.next = b;  b.prev = a;
        a.down = c;  c.up   = a;
        b.down = d;  d.up   = b;
        c.next = d;  d.prev = c;

        top    = a;
        left   = a;
        right  = b;   // top-right
        bottom = c;   // bottom-left

        size = 4;     // set exact values (not +=)
        rows = 2;
        cols = 2;
    }


    public Node2DGrid<T> goDown(Node2DGrid<T> start, int jumps) {
        Node2DGrid<T> current = start;
        for (int i = 0; i < jumps; i++) {
            if (current == null || current.down == null) {
                throw new IndexOutOfBoundsException("Cannot move down " + jumps + " steps.");
            }
            current = current.down;
        }
        return current;
    }

    public Node2DGrid<T> goRight(Node2DGrid<T> start, int jumps) {
        Node2DGrid<T> current = start;
        for (int i = 0; i < jumps; i++) {
            if (current == null || current.next == null) {
                throw new IndexOutOfBoundsException("Cannot move next " + jumps + " steps.");
            }
            current = current.next;
        }
        return current;
    }
    public Node2DGrid<T> goLeft(Node2DGrid<T> start, int jumps) {
        Node2DGrid<T> current = start;
        for (int i = 0; i < jumps; i++) {
            if (current == null || current.prev == null) {
                throw new IndexOutOfBoundsException("Cannot move next " + jumps + " steps.");
            }
            current = current.prev;
        }
        return current;
    }

    public Node2DGrid<T> goUp(Node2DGrid<T> start, int jumps) {
        Node2DGrid<T> current = start;
        for (int i = 0; i < jumps; i++) {
            if (current == null || current.up == null) {
                throw new IndexOutOfBoundsException("Cannot move next " + jumps + " steps.");
            }
            current = current.up;
        }
        return current;
    }
    public Node2DGrid<T> selectRowHead(Node2DGrid<T> start, int jumps) {
        // If caller passes null, default to top-left
        if (start == null) start = top;

        // Move down `jumps` rows from the chosen starting point (usually top-left).
        Node2DGrid<T> rowHead = goDown(start, jumps);

        // rowHead is the leftmost cell of that row
        return rowHead;
    }

    public Node2DGrid<T> selectColmHead(Node2DGrid<T> start, int jumps) {
        // If caller passes null, default to top-left
        if (start == null) start = top;

        // Move down `jumps` rows from the chosen starting point (usually top-left).
        Node2DGrid<T> ColmHead = goRight(start, jumps);

        // rowHead is the leftmost cell of that row
        return ColmHead;
    }

    public Node2DGrid<T> getNodeAt(int row, int col) {
        if (row < 0 || col < 0 || row >= rows || col >= cols) {
            throw new IndexOutOfBoundsException(
                    "row=" + row + ", col=" + col + ", rows=" + rows + ", cols=" + cols
            );
        }
        if (top == null) {
            throw new IllegalStateException("Grid is empty.");
        }

        Node2DGrid<T> current = top;

        // move DOWN 'row' times
        for (int i = 0; i < row; i++) current = current.down;

        // move RIGHT 'col' times
        for (int j = 0; j < col; j++) current = current.next;

        return current;
    }

    public void setNode(T value, int row, int col) {
        Node2DGrid<T> current = getNodeAt(row,col);
        current.value = value;
    }

    public Node2DGrid<T> getNodeByValue(T value) {
        for (Node2DGrid<T> row = top; row != null; row = row.down) {      // go row by row
            for (Node2DGrid<T> cur = row; cur != null; cur = cur.next) {  // scan across the row
                if (java.util.Objects.equals(cur.value, value)) {
                    return cur;                                           // first match
                }
            }
        }
        return null; // not found
    }


    public java.util.List<Node2DGrid<T>> neighbors4(Node2DGrid<T> node) {
        java.util.ArrayList<Node2DGrid<T>> out = new java.util.ArrayList<>(4);
        if (node.up   != null) out.add(node.up);
        if (node.down != null) out.add(node.down);
        if (node.prev != null) out.add(node.prev); // left
        if (node.next != null) out.add(node.next); // right
        return out;
    }
    // Inside your Node2D<T> class
    public void buildEmptyGridFromImageSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        if (size != 0) {
            throw new IllegalStateException("Grid already initialized. Clear or recreate before building.");
        }

        // ---- first row (y = 0) ----
        Node2DGrid<T> first = new Node2DGrid<>(null); // (row=0, col=0)
        top = first;
        left = first;

        Node2DGrid<T> cur = first;
        for (int x = 1; x < width; x++) {
            Node2DGrid<T> n = new Node2DGrid<>(null);
            cur.next = n;  n.prev = cur;   // link horizontally (→)
            cur = n;
        }
        right = cur; // top-right

        // ---- remaining rows (y = 1..height-1) ----
        Node2DGrid<T> rowAboveHead = first; // head of previous row
        for (int y = 1; y < height; y++) {
            // head of new row directly under rowAboveHead
            Node2DGrid<T> newRowHead = new Node2DGrid<>(null);
            rowAboveHead.down = newRowHead;  newRowHead.up = rowAboveHead;

            // stitch across the row, pairing each new cell with the cell above it
            Node2DGrid<T> above = rowAboveHead;   // (y-1, 0)
            Node2DGrid<T> leftInNew = newRowHead; // (y,   0)
            for (int x = 1; x < width; x++) {
                Node2DGrid<T> n = new Node2DGrid<>(null);

                // horizontal link in the new row
                leftInNew.next = n;  n.prev = leftInNew;

                // move 'above' to (y-1, x) and link vertically
                above = above.next;
                above.down = n;      n.up = above;

                leftInNew = n;
            }

            // advance to next row
            rowAboveHead = newRowHead;

            // record bottom-left when we reach the last row
            if (y == height - 1) {
                bottom = newRowHead;
            }
        }

        // single-row / single-col edges
        if (height == 1) bottom = top;
        if (width  == 1) right  = top;

        // metadata
        this.rows = height;   // row = y
        this.cols = width;    // col = x
        this.size = width * height;
    }







}


