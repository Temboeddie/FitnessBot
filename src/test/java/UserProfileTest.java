import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для UserProfile.
 * Этот класс содержит модульные тесты для проверки функциональности методов класса UserProfile.
 */
public class UserProfileTest {
    private UserProfile userProfile;

    @BeforeEach
    public void setUp() {
        userProfile = new UserProfile(123L, "test_user");
    }


    /**
     *  Проверка, чтобы убедиться, что язык по умолчанию установлен на английский,
     *  если пользователь не выбрал предпочитаемый язык
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
        Workout workout = new Workout("Push-ups", 3, 10);
        userProfile.addWorkout(workout);

        String expectedHistory = "Push-ups: 3 sets of 10 reps\n";
        assertEquals(expectedHistory, userProfile.getWorkoutHistory());
    }

    /**
     * Проверяет сообщение для пустой истории тренировок на разных языках.
     */
    @Test
    public void testEmptyWorkoutHistory() {
        assertEquals("Workout history is empty.", userProfile.getWorkoutHistory());

        userProfile.setLanguage("RU");
        assertEquals("История тренировок пуста.", userProfile.getWorkoutHistory());
    }
}
