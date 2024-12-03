import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



/**
 * Тестовый класс для FitnessBot.
 * Этот класс содержит модульные тесты для проверки функциональности методов класса FitnessBot.
 */
class FitnessBotTest {


    /**
     * Проверяет профиля пользователя и добавление тренировок.
     */
    @Test
    public void testUserProfileCreation() {
        UserProfile userProfile = new UserProfile(123L, "testUser");

        assertEquals(123L, userProfile.getChatId());
        assertEquals("testUser", userProfile.getUsername());


        assertEquals("EN", userProfile.getLanguage());

        assertEquals("Workout history is empty.", userProfile.getWorkoutHistory());
    }

    /**
     * Проверяет добавление тренировки в профиль пользователя.
     * Убеждается, что после добавления упражнения его детали появляются в истории тренировок.
     */

    @Test
    public void testAddWorkout() {
        UserProfile userProfile = new UserProfile(123L, "testUser");

        userProfile.setCurrentExerciseName("Push-ups");
        userProfile.setCurrentExerciseSets(3);
        userProfile.setCurrentExerciseReps(15);

        Workout workout = new Workout(
                userProfile.getCurrentExercise(),
                userProfile.getCurrentExerciseSets(),
                userProfile.getCurrentExerciseReps()
        );
        userProfile.addWorkout(workout);

        String workoutHistory = userProfile.getWorkoutHistory();
        assertTrue(workoutHistory.contains("Push-ups"));
        assertTrue(workoutHistory.contains("3"));
        assertTrue(workoutHistory.contains("15"));
    }
}
