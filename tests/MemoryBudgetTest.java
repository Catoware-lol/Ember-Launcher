import net.kdt.pojavlaunch.utils.MemoryBudget;

public class MemoryBudgetTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        check(MemoryBudget.recommend(8192, 6000, 256, false) == 2048, "Conservative high-end cap");
        check(MemoryBudget.recommend(4096, 3000, 256, false) == 1024, "4 GiB device");
        check(MemoryBudget.recommend(8192, 1800, 256, false) == 768, "Background apps reduce budget");
        check(MemoryBudget.recommend(8192, 6000, 256, true) == 768, "32-bit address space cap");
        check(MemoryBudget.recommend(4096, 256, 256, false) == 256, "Low-memory floor");
        check(MemoryBudget.recommend(0, -1, 0, false) == 256, "Invalid readings remain bounded");
        check(MemoryBudget.initialHeap(64) == 64, "Initial heap never exceeds maximum");
        check(MemoryBudget.initialHeap(2048) == 128, "No full heap preallocation");
        for (int total = 1024; total <= 16384; total += 1024) {
            int previous = 0;
            for (int available = 0; available <= total; available += 64) {
                int result = MemoryBudget.recommend(total, available, 256, false);
                check(result >= previous && result >= 256 && result <= 2048, "Bounds and monotonicity");
                check(result % 64 == 0, "64 MiB increments");
                previous = result;
            }
        }
        System.out.println("Memory budget tests passed");
    }
}
