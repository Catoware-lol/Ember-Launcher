package net.kdt.pojavlaunch.utils;

/** Samples the bridge's unsigned 32-bit frame counter using elapsed real time. */
public final class FrameRateSample {
    private long count, nanos;
    public void reset(long frames, long now) { count = frames; nanos = now; }
    public double sample(long frames, long now) {
        long delta = (frames - count) & 0xffffffffL;
        long elapsed = now - nanos;
        reset(frames, now);
        return elapsed <= 0 ? 0 : delta * 1e9 / elapsed;
    }
}
