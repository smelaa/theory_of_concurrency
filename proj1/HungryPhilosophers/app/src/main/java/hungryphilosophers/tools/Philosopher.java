package hungryphilosophers.tools;

public enum Philosopher {
    P1(0),
    P2(1),
    P3(2),
    P4(3),
    P5(4),
    P6(5);

    private final int order; 

    Philosopher(int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }
}
