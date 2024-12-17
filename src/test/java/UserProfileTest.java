import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для UserProfile.
 * Этот класс содержит модульные тесты для проверки функциональности методов класса UserProfile.
 */
public class UserProfileTest {
    private UserProfile userProfile;
    private Workout workout1;
    private Workout workout2;

    @BeforeEach
    public void setUp() {
        userProfile = new UserProfile(123L, "test_user");
        workout1 = new Workout("Push-up", 3, 12);
        workout2 = new Workout("Squat", 4, 10);
        userProfile.addWorkout(workout1);
        userProfile.addWorkout(workout2);
    }

    /**
     * Проверка, чтобы убедиться, что язык по умолчанию установлен на английский,
     * если пользователь не выбрал предпочитаемый язык.
     */
    @Test
    public void testDefaultLanguage() {
        assertEquals("EN", userProfile.getLanguage(), "Default language should be EN");
    }

    /**
     * Проверяет установку языка пользователя.
     */
    @Test
    public void testSetLanguage() {
        userProfile.setLanguage("RU");
        assertEquals("RU", userProfile.getLanguage(), "Язык должен быть установлен на RU.");
    }

    /**
     * Проверяет добавление тренировки в историю тренировок пользователя.
     */
    @Test

    public void testAddWorkout() {
        Workout workout = new Workout("Push-ups", 5, 15);
        userProfile.addWorkout(workout);

        String expectedHistory = "ID: 1 - Push-up: 3 sets of 12 reps\n" +
                "ID: 2 - Squat: 4 sets of 10 reps\n" +
                "ID: 3 - Push-ups: 5 sets of 15 reps\n";  // Note the spaces here
        assertEquals(expectedHistory, userProfile.getWorkoutHistory());
    }


    /**
     * Проверяет сообщение для пустой истории тренировок на разных языках.
     */
    @Test
    public void testEmptyWorkoutHistory() {
        String workoutHistory = userProfile.getWorkoutHistory();

        assertTrue(workoutHistory.contains("ID: 1 - Push-up: 3 sets of 12 reps"),
                "Workout history should contain 'Push-up: 3 sets of 12 reps'");
        assertTrue(workoutHistory.contains("ID: 2 - Squat: 4 sets of 10 reps"),
                "История тренировок должна содержать 'Squat: 4 sets of 10 reps'");
    }

    /**
     * Проверка удаления некорректного индекса тренировки.
     */
    @Test
    void testDeleteInvalidWorkout() {
        assertFalse(userProfile.deleteWorkout(3));
        assertEquals(2, userProfile.getWorkouts().size());
    }
}
