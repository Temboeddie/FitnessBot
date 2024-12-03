import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Тестовый класс для класса Workout.
 */
public class WorkoutTest {

    /**
     * Тестирует метод, что он возвращает правильный формат строки.
     */
    @Test
    public void testWorkoutToString() {
        Workout workout = new Workout("Squats", 4, 12);
        String expectedOutput = "Squats: 4 sets of 12 reps";
        assertEquals(expectedOutput, workout.toString());
    }
}
