/**
 * Класс Workout представляет собой одну тренировку.
 * Содержит информацию о типе упражнения, количестве подходов и повторений.
 */

/**
 * Класс Workout представляет собой одну тренировку.
 * Содержит информацию о типе упражнения, количестве подходов и повторений.
 */
public class Workout {
    private String exerciseName; // Correct data type
    private int sets;
    private int reps;

    public Workout(String exerciseName, int sets, int reps) {
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
    }

    @Override
    public String toString() {
        return String.format("%s: %d sets of %d reps", exerciseName, sets, reps);
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public String getExerciseName() { return exerciseName; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
}


