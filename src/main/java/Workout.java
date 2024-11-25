/**
 * Класс Workout представляет собой одну тренировку.
 * Содержит информацию о типе упражнения, количестве подходов и повторений.
 */

public class Workout {
    private String exerciseType;
    private int sets;
    private int reps;

    public Workout(String exerciseType, int sets, int reps) {
        this.exerciseType = exerciseType;
        this.sets = sets;
        this.reps = reps;
    }

    @Override
    public String toString() {
        return String.format("%s: %d sets of %d reps", exerciseType, sets, reps);
    }
}
