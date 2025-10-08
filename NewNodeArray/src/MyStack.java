package NewNodeArray.src;

/**
 * Pilha encadeada.
 */
final class MyStack<T> {
    // Nó interno da pilha
    private static final class Node<U> {
        U v; Node<U> next;
        Node(U v, Node<U> next) { this.v = v; this.next = next; }
    }
    private Node<T> top; // topo da pilha

    // Pilha vazia?
    boolean isEmpty() { return top == null; }

    // Empilhar: insere no topo
    void push(T v) { top = new Node<>(v, top); }

    // Desempilhar: remove do topo e retorna o valor
    T pop() {
        T v = top.v;
        top = top.next;
        return v;
    }
}
