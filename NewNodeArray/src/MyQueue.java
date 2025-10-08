package NewNodeArray.src;

/**
 * Fila encadeada
 */
final class MyQueue<T> {
    // Nó interno da fila
    private static final class Node<U> {
        U v; Node<U> next;
        Node(U v) { this.v = v; }
    }
    private Node<T> head, tail; // início e fim da fila

    // Fila vazia?
    boolean isEmpty() { return head == null; }

    // Enfileirar: adiciona no fim
    void enqueue(T v) {
        Node<T> n = new Node<>(v);
        if (tail == null) head = tail = n;
        else { tail.next = n; tail = n; }
    }

    // Desenfileirar: remove do início e retorna o valor
    T dequeue() {
        T v = head.v;
        head = head.next;
        if (head == null) tail = null; // fila ficou vazia
        return v;
    }
}
