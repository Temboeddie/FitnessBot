import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

/**
 * Основной класс Telegram-бота для управления тренировками.
 * Этот бот позволяет пользователям добавлять тренировки, просматривать историю и отменять сеансы.
 */

public class FitnessBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final Map<Long, UserProfile> userProfiles = new HashMap<>();
    private final Map<Long, String> userStates = new HashMap<>();

    public FitnessBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }


    /**
     * Обрабатывает обновления, полученные от Telegram.
     *
     * @param update Объект обновления, содержащий сообщение от пользователя.
     */

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText().trim();
            String username = update.getMessage().getFrom().getUserName();

            UserProfile userProfile = userProfiles.computeIfAbsent(chatId, id -> new UserProfile(chatId, username));

            
            if (userStates.containsKey(chatId)) {
                handleUserState(chatId, messageText, userProfile);
            } else {
                handleMessage(chatId, messageText, userProfile);
            }
        }
    }

    /**
     * Обрабатывает команды, которые не зависят от текущего состояния пользователя.
     *
     * @param chatId      ID чата пользователя.
     * @param messageText Текст сообщения.
     * @param userProfile Профиль пользователя.
     */

    private void handleMessage(Long chatId, String messageText, UserProfile userProfile) {
        String response;
        String language = userProfile.getLanguage();




        switch (messageText.toLowerCase()) {
            case "/start" -> {
                response = "Welcome to XtremeFitness! Choose your language/Добро пожаловать в XtremeFitness! Выберите язык:";
                sendMessage(chatId, response,getLanguageButtons());
            }
            case "en" -> {
                userProfile.setLanguage("EN");
                response = "Language set to English. Use addworkout to add a workout or viewhistory to check your history.";
                sendMessage(chatId, response,getMainButtons("EN"));
            }
            case "ru" -> {
                userProfile.setLanguage("RU");
                response = "Язык установлен на русский. Используйте addworkout для добавления тренировки или viewhistory для просмотра истории.";
                sendMessage(chatId, response,getMainButtons("RU"));
            }
            case "addworkout" -> {
                userStates.put(chatId, "adding_workout");
                response = language.equals("RU") ? "Введите название упражнения:" : "Please enter the name of the exercise:";
                sendMessage(chatId, response,getMainButtons(language));
            }

            //Добавленная команда(editworkout)
            case "editworkout" -> {
                userProfile.populateWorkoutsForEditing();
                if (userProfile.getWorkouts().isEmpty()) {
                    response = language.equals("RU")
                            ? "У вас нет тренировок для редактирования."
                            : "You have no workouts to edit.";
                } else {
                    userStates.put(chatId, "editing_workout_id");
                    StringBuilder workoutsList = new StringBuilder(language.equals("RU")
                            ? "Введите ID тренировки, которую вы хотите изменить:\n"
                            : "Enter the ID of the workout you want to edit:\n");

                    int id = 1;
                    for (Workout workout : userProfile.getWorkouts()) {
                        workoutsList.append(id++).append(". ").append(workout).append("\n");
                    }
                    response = workoutsList.toString();
                }
                sendMessage(chatId, response,null);
            }

            //новый Case для удаления тренировок
            case"deleteworkout" ->{
                if(userProfile.getWorkouts().isEmpty()){
                    response=language.equals("RU")
                            ?"У вас нет тренировок для удаления."
                            : "You have no workouts to delete.";
                }
                else {
                    userStates.put(chatId,"deleting_workout_id");
                    StringBuilder workoutsList=new StringBuilder(language.equals("RU")
                            ? "Введите ID тренировки, которую вы хотите удалить:\n"
                            : "Enter the ID of the workout you want to delete:\n"
                            );
                    int id=1;
                    for(Workout workout:userProfile.getWorkouts()){
                        workoutsList.append(id++).append(".").append(workout.toString()).append("\n");
                    }
                    response=workoutsList.toString();
                }
                sendMessage(chatId,response,getMainButtons(language));
            }



            case "viewhistory" -> {
                response = userProfile.getWorkoutHistory();
                sendMessage(chatId, response,getMainButtons(language));
            }


            default -> {
                response = language.equals("RU") ? "Неизвестная команда." : "Unknown command.";
                sendMessage(chatId, response,getMainButtons(language));
            }
            case "viewstats" -> {
                response = getUserStats(language,userProfile);
                sendMessage(chatId,response,getMainButtons(language));


            }
        }
    }
    private String getUserStats(String language, UserProfile userProfile){

        List<Workout> workouts = userProfile.getWorkouts();
        if(workouts.isEmpty()){
            return language.equals("RU")? "У вас пока нет статистики." : "You have no Stats yet.";
        }
        int totalWorkouts = workouts.size();
        int totalReps = workouts.stream().mapToInt(w->w.getSets() * w.getReps()).sum();

        String favoriteExercise = workouts.stream()
                .collect(Collectors.groupingBy(Workout::getExerciseName, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
        StringBuilder stats = new StringBuilder(language.equals("RU") ? "Ваша статистика:\n" : "Your stats:\n");
        stats.append(language.equals("RU") ? "Всего тренировок: " : "Total workouts: ").append(totalWorkouts).append("\n");
        stats.append(language.equals("RU") ? "Всего повторений: " : "Total reps: ").append(totalReps).append("\n");
        stats.append(language.equals("RU") ? "Любимое упражнение: " : "Favorite exercise: ").append(favoriteExercise).append("\n");
        return stats.toString();
    }

    private ReplyKeyboardMarkup getLanguageButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true); // Fit buttons to screen
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("EN");
        row.add("RU");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }


    private ReplyKeyboardMarkup getMainButtons(String language) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();

        if (language.equals("RU")) {
            row1.add("addWorkout");
            row1.add("viewHistory");
            row2.add("editWorkout");
            row2.add("deleteWorkout");
            row3.add("viewstats");

        } else {
            row1.add("addWorkout");
            row1.add("viewHistory");
            row2.add("editWorkout");
            row2.add("deleteWorkout");
            row3.add("viewstats");

        }

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    /**
     * Обрабатывает пользовательский ввод в зависимости от текущего состояния.
     *
     * @param chatId      ID чата пользователя.
     * @param messageText Текст сообщения.
     * @param userProfile Профиль пользователя.
     */

    private void handleUserState(Long chatId, String messageText, UserProfile userProfile) {
        String state = userStates.get(chatId);
        String response;
        String language = userProfile.getLanguage();

        switch (state) {
            case "adding_workout" -> {
                userProfile.setCurrentExerciseName(messageText);
                userStates.put(chatId, "adding_sets");
                response = language.equals("RU") ? "Введите количество подходов:" : "Enter the number of sets:";
                sendMessage(chatId, response,null);
            }
            case "adding_sets" -> {
                try {
                    int sets = Integer.parseInt(messageText);
                    userProfile.setCurrentExerciseSets(sets);
                    userStates.put(chatId, "adding_reps");
                    response = language.equals("RU") ? "Введите количество повторений:" : "Enter the number of reps:";
                    sendMessage(chatId, response,null);
                } catch (NumberFormatException e) {
                    response = language.equals("RU") ? "Пожалуйста, введите целое число для подходов." : "Please enter a valid integer for sets.";
                    sendMessage(chatId, response,null);
                }
            }
            case "adding_reps" -> {
                try {
                    int reps = Integer.parseInt(messageText);
                    userProfile.setCurrentExerciseReps(reps);

                    Workout workout = new Workout(
                            userProfile.getCurrentExercise(),
                            userProfile.getCurrentExerciseSets(),
                            userProfile.getCurrentExerciseReps()
                    );

                    userProfile.addWorkout(workout);

                    response = language.equals("RU") ?
                            "Упражнение добавлено: " + workout.toString() :
                            "Exercise added: " + workout.toString();
                    sendMessage(chatId, response,getLanguageButtons());


                    userStates.remove(chatId);
                    userProfile.clearCurrentExerciseData();


                    response = language.equals("RU") ?
                            "Тренировка успешно добавлена. Используйте addworkout или viewhistory или editworkout или deleteWorkout." :
                            "Workout successfully added. Use addworkout or viewhistory or editworkout or deleteWorkout.";
                    sendMessage(chatId, response,getLanguageButtons());

                } catch (NumberFormatException e) {
                    response = language.equals("RU") ? "Пожалуйста, введите целое число для повторений." : "Please enter a valid integer for reps.";
                    sendMessage(chatId, response,null);
                }
            }
            default -> {

                userStates.remove(chatId);
                sendMessage(chatId, "An error occurred. Please try again or start a new session with /addworkout.",null);
            }

            //Добавлен Case для редактирования тренировок
            case "editing_workout_id" -> {
                try {
                    int workoutId = Integer.parseInt(messageText) - 1;


                    if (workoutId < 0 || workoutId >= userProfile.getWorkouts().size()) {
                        response = language.equals("RU")
                                ? "Неверный ID тренировки. Попробуйте снова."
                                : "Invalid workout ID. Please try again.";
                    } else {
                        userProfile.setCurrentWorkoutIndex(workoutId);
                        userStates.put(chatId, "editing_workout_details");
                        response = language.equals("RU")
                                ? "Введите новое название упражнения, количество подходов и повторений через запятую:\nПример: Жим лежа, 4, 12"
                                : "Enter the new exercise name, sets, and reps separated by commas:\nExample: Bench Press, 4, 12";
                    }
                } catch (NumberFormatException e) {
                    response = language.equals("RU")
                            ? "Пожалуйста, введите корректный ID."
                            : "Please enter a valid ID.";
                }
                sendMessage(chatId, response,null);
            }

            case "editing_workout_details" -> {
                String[] details = messageText.split(",");
                if (details.length != 3) {
                    response = language.equals("RU")
                            ? "Введите корректные данные (название, подходы, повторения):\nПример: Жим лежа, 4, 12"
                            : "Please enter valid data (name, sets, reps):\nExample: Bench Press, 4, 12";
                    sendMessage(chatId,response,null);
                } else {
                    try {
                        String newName = details[0].trim();
                        String setsString = details[1].trim();
                        String repsString = details[2].trim();

                        int newSets = Integer.parseInt(setsString);
                        int newReps = Integer.parseInt(repsString);

                        int currentIndex = userProfile.getCurrentWorkoutIndex();


                        if (currentIndex < 0 || currentIndex >= userProfile.getWorkouts().size()) {
                            System.out.println("Invalid workout index.");
                            response = language.equals("RU")
                                    ? "Ошибка: неверный индекс тренировки."
                                    : "Error: Invalid workout index.";
                        } else {
                            Workout workout = userProfile.getWorkouts().get(currentIndex);

                            workout.setExerciseName(newName);
                            workout.setSets(newSets);
                            workout.setReps(newReps);

                            userStates.remove(chatId);
                            userProfile.clearCurrentExerciseData();

                            response = language.equals("RU")
                                    ? "Тренировка успешно обновлена."
                                    : "Workout successfully updated.";
                            sendMessage(chatId,response,getMainButtons(language));
                        }
                    } catch (NumberFormatException e) {
                        response = language.equals("RU")
                                ? "Введите корректное количество подходов и повторений (целые числа)."
                                : "Please enter valid numbers for sets and reps.";
                    }
                }
                sendMessage(chatId, response,null);
            }
            // для удаления тренировок
            case "deleting_workout_id" -> {
                try {
                    int workoutId = Integer.parseInt(messageText) - 1;

                    if (workoutId>=0 && workoutId<userProfile.getWorkouts().size()) {
                        userProfile.deleteWorkout(workoutId);
                        response = language.equals("RU")
                                ? "Тренировка успешно удалена."
                                : "Workout successfully deleted.";
                    } else {
                        response = language.equals("RU")
                                ? "Неверный ID тренировки. Попробуйте снова."
                                : "Invalid workout ID. Please try again.";
                    }
                } catch (NumberFormatException e) {
                    response = language.equals("RU")
                            ? "Введите корректный ID (число)."
                            : "Please enter a valid ID (number).";
                }

                userStates.remove(chatId);
                sendMessage(chatId, response,null);
            }




        }
    }


    private void sendMessage(Long chatId, String text,ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (keyboard!=null)
        {
        message.setReplyMarkup(keyboard);}
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
