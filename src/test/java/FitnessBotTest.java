import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для FitnessBot.
 * Этот класс содержит модульные тесты для проверки функциональности методов класса FitnessBot.
 */
class FitnessBotTest {

    /**
     * Проверяет создание профиля пользователя и установку языка по умолчанию.
     */
    @Test
    public void testUserProfileCreation() {
        UserProfile userProfile = new UserProfile(123L, "testUser");

        assertEquals(123L, userProfile.getChatId(), "ID чата не совпадает");
        assertEquals("testUser", userProfile.getUsername(), "Имя пользователя не совпадает");
        assertEquals("EN", userProfile.getLanguage(), "Язык по умолчанию должен быть EN");
        assertEquals("Workout history is empty.", userProfile.getWorkoutHistory(), "История тренировок должна быть пустой");
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
        assertTrue(workoutHistory.contains("Push-ups"), "История не содержит имя упражнения");
        assertTrue(workoutHistory.contains("3"), "История не содержит количество подходов");
        assertTrue(workoutHistory.contains("15"), "История не содержит количество повторений");
    }

    /**
     * Проверяет удаление тренировки из профиля пользователя.
     * Убеждается, что после удаления тренировки она исчезает из истории.
     */
    @Test
    public void testDeleteWorkout() {
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


        userProfile.deleteWorkout(0);

        String workoutHistory = userProfile.getWorkoutHistory();
        assertFalse(workoutHistory.contains("Push-ups"), "История должна быть без удаленной тренировки");
    }

    /**
     * Проверяет редактирование тренировки.
     * Убеждается, что после редактирования тренировки ее данные обновляются в истории.
     */
    @Test
    public void testEditWorkout() {
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


        userProfile.setCurrentWorkoutIndex(0);
        userProfile.getWorkouts().get(0).setExerciseName("Pull-ups");
        userProfile.getWorkouts().get(0).setSets(4);
        userProfile.getWorkouts().get(0).setReps(10);


        String workoutHistory = userProfile.getWorkoutHistory();
        assertTrue(workoutHistory.contains("Pull-ups"), "История не содержит новое имя упражнения");
        assertTrue(workoutHistory.contains("4"), "История не содержит новое количество подходов");
        assertTrue(workoutHistory.contains("10"), "История не содержит новое количество повторений");
    }
}
