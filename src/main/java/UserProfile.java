import java.util.ArrayList;
import java.util.List;

/**
 * Сохраняет данные пользователя, включая выбранный язык
 */
public class UserProfile {
    private Long chatId;
    private String username;
    private String language;
    private String currentExercise;
    private int currentExerciseSets;
    private int currentExerciseReps;
    private List<Workout> workoutHistory;

    public UserProfile(Long chatId, String username) {
        this.chatId = chatId;
        this.username = username;
        this.language = "EN";
        this.workoutHistory = new ArrayList<>();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void addWorkout(Workout workout) {
        workoutHistory.add(workout);
    }

    public String getWorkoutHistory() {
        if (workoutHistory.isEmpty()) {
            return language.equals("RU") ? "История тренировок пуста." : "Workout history is empty.";
        }
        StringBuilder history = new StringBuilder();
        for (Workout workout : workoutHistory) {
            history.append(workout.toString()).append("\n");
        }
        return history.toString();
    }

    public String getCurrentExercise() {
        return currentExercise;
    }

    public void setCurrentExerciseName(String currentExercise) {
        this.currentExercise = currentExercise;
    }

    public int getCurrentExerciseSets() {
        return currentExerciseSets;
    }

    public void setCurrentExerciseSets(int sets) {
        this.currentExerciseSets = sets;
    }

    public int getCurrentExerciseReps() {
        return currentExerciseReps;
    }

    public void setCurrentExerciseReps(int reps) {
        this.currentExerciseReps = reps;
    }


    public void clearCurrentExerciseData() {
        this.currentExercise = null;
        this.currentExerciseSets = 0;
        this.currentExerciseReps = 0;
    }

    public String getUsername() {
        return username;
    }

    public long getChatId() {

        return chatId;
    }
}