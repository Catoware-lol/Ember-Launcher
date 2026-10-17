import net.kdt.pojavlaunch.utils.FrameRateSample;
public class FrameRateSampleTest {
    private static void expect(double actual, double expected) {
        if (Math.abs(actual - expected) > .001) throw new AssertionError(actual + " != " + expected);
    }
    public static void main(String[] args) {
        FrameRateSample sample = new FrameRateSample();
        sample.reset(100, 1_000_000_000L);
        expect(sample.sample(160, 2_000_000_000L), 60);
        expect(sample.sample(220, 4_000_000_000L), 30);
        expect(sample.sample(220, 5_000_000_000L), 0);
        sample.reset(0xfffffff0L, 0);
        expect(sample.sample(44, 1_000_000_000L), 60);
        expect(sample.sample(44, 1_000_000_000L), 0);
        System.out.println("Frame rate sampling tests passed");
    }
}
