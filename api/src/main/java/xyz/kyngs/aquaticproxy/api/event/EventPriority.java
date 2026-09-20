package xyz.kyngs.aquaticproxy.api.event;

public record EventPriority(int priority) implements Comparable<EventPriority> {

    public static final EventPriority FIRST = new EventPriority(0);
    public static final EventPriority EARLY = new EventPriority(10000);
    public static final EventPriority NORMAL = new EventPriority(20000);
    public static final EventPriority LATE = new EventPriority(30000);
    public static final EventPriority LAST = new EventPriority(40000);

    @Override
    public int compareTo(EventPriority o) {
        return Integer.compare(this.priority, o.priority);
    }
}
