import net.kdt.pojavlaunch.value.OfflineIdentity;

public final class OfflineIdentityTest {
    public static void main(String[] args) {
        for (String valid : new String[]{"abc", "Steve", "Player_123", "1234567890123456"}) {
            require(OfflineIdentity.isValidUsername(valid), "Rejected valid username: " + valid);
        }
        for (String invalid : new String[]{null, "", "ab", "12345678901234567", "a/b", "a b", "a.b", "abc\n", "../Steve"}) {
            require(!OfflineIdentity.isValidUsername(invalid), "Accepted unsafe username: " + invalid);
            try {
                OfflineIdentity.profileId(invalid);
                throw new AssertionError("Generated UUID for invalid username");
            } catch (IllegalArgumentException expected) { }
        }
        // Known vanilla offline UUID for Notch: preserves offline worlds/player data.
        require(OfflineIdentity.profileId("Notch").equals("b50ad385829d3141a2167e7d7539ba7f"), "Offline UUID compatibility failed");
        require(OfflineIdentity.profileId("Steve").equals(OfflineIdentity.profileId("Steve")), "UUID is unstable");
        require(!OfflineIdentity.profileId("Steve").equals(OfflineIdentity.profileId("steve")), "Distinct identities collided");
        System.out.println("Offline identity tests passed");
    }
    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
