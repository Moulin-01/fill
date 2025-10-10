package NewNodeArray.src;

/**
 * FILA (FIFO) encadeada.
 * Usada no Flood Fill HORIZONTAL (scanline por LINHAS) — estratégia BFS.
 */
final class MyQueue<T> {
    // Nó interno da fila
    private static final class Node<U> {
        U v;
        Node<U> next;
        Node(U v) { this.v = v; }
    }

    private Node<T> head, tail; // início e fim da fila

    /** Retorna true se a fila está vazia. */
    boolean isEmpty() { return head == null; }

    /** Enfileira (enqueue): adiciona no fim. */
    void enqueue(T v) {
        Node<T> n = new Node<>(v);
        if (tail == null) head = tail = n;
        else { tail.next = n; tail = n; }
    }

    /** Desenfileira (dequeue): remove do início e retorna o valor. */
    T dequeue() {
        T v = head.v;
        head = head.next;
        if (head == null) tail = null; // fila ficou vazia
        return v;
    }
}
