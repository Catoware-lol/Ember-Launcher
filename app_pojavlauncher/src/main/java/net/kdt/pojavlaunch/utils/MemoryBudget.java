package net.kdt.pojavlaunch.utils;

/** Heap budget in MiB. Available memory already accounts for other running apps. */
public final class MemoryBudget {
    private MemoryBudget() {}
    public static int recommend(long total, long available, long lowMemoryThreshold, boolean bits32) {
        total = Math.max(256, total);
        available = Math.max(0, Math.min(total, available));
        // Keep room for Android, the renderer, native libraries and memory fluctuations.
        long reserve = Math.max(512, Math.max(total / 8, lowMemoryThreshold + 256));
        long target = Math.min(total / 4, 2048);
        long budget = Math.min(target, available - reserve);
        if (bits32) budget = Math.min(budget, 768);
        return (int) Math.max(256, budget / 64 * 64);
    }
    public static int initialHeap(int maximum) {
        return Math.min(maximum, 128);
    }
}
