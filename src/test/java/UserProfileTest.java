import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileTest {
    private UserProfile userProfile;

    @BeforeEach
    public void setUp() {
        userProfile = new UserProfile(123L, "test_user");
    }

    @Test
    public void testDefaultLanguage() {
        assertEquals("EN", userProfile.getLanguage(), "Default language should be EN");
    }

    @Test
    public void testSetLanguage() {
        userProfile.setLanguage("RU");
        assertEquals("RU", userProfile.getLanguage(), "Language should be set to RU");
    }

    @Test
    public void testAddWorkout() {
        Workout workout = new Workout("Push-ups", 3, 10);
        userProfile.addWorkout(workout);

        String expectedHistory = "Push-ups: 3 sets of 10 reps\n";
        assertEquals(expectedHistory, userProfile.getWorkoutHistory());
    }

    @Test
    public void testEmptyWorkoutHistory() {
        assertEquals("Workout history is empty.", userProfile.getWorkoutHistory());

        userProfile.setLanguage("RU");
        assertEquals("История тренировок пуста.", userProfile.getWorkoutHistory());
    }
}
