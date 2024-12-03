import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

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
                response = "Welcome to XtremeFitness! Choose your language/Добро пожаловать в XtremeFitness! Выберите язык:\nEN - English\nRU - Русский";
                sendMessage(chatId, response);
            }
            case "en" -> {
                userProfile.setLanguage("EN");
                response = "Language set to English. Use /addworkout to add a workout or /viewhistory to check your history.";
                sendMessage(chatId, response);
            }
            case "ru" -> {
                userProfile.setLanguage("RU");
                response = "Язык установлен на русский. Используйте /addworkout для добавления тренировки или /viewhistory для просмотра истории.";
                sendMessage(chatId, response);
            }
            case "/addworkout" -> {
                userStates.put(chatId, "adding_workout");
                response = language.equals("RU") ? "Введите название упражнения:" : "Please enter the name of the exercise:";
                sendMessage(chatId, response);
            }


            case "/viewhistory" -> {
                response = userProfile.getWorkoutHistory();
                sendMessage(chatId, response);
            }
            default -> {
                response = language.equals("RU") ? "Неизвестная команда." : "Unknown command.";
                sendMessage(chatId, response);
            }
        }
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
                sendMessage(chatId, response);
            }
            case "adding_sets" -> {
                try {
                    int sets = Integer.parseInt(messageText);
                    userProfile.setCurrentExerciseSets(sets);
                    userStates.put(chatId, "adding_reps");
                    response = language.equals("RU") ? "Введите количество повторений:" : "Enter the number of reps:";
                    sendMessage(chatId, response);
                } catch (NumberFormatException e) {
                    response = language.equals("RU") ? "Пожалуйста, введите целое число для подходов." : "Please enter a valid integer for sets.";
                    sendMessage(chatId, response);
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
                    sendMessage(chatId, response);

                    // **Reset state and clear exercise data**:
                    userStates.remove(chatId);
                    userProfile.clearCurrentExerciseData();

                    // **Provide next steps**:
                    response = language.equals("RU") ?
                            "Тренировка успешно добавлена. Используйте /addworkout или /viewhistory." :
                            "Workout successfully added. Use /addworkout or /viewhistory.";
                    sendMessage(chatId, response);

                } catch (NumberFormatException e) {
                    response = language.equals("RU") ? "Пожалуйста, введите целое число для повторений." : "Please enter a valid integer for reps.";
                    sendMessage(chatId, response);
                }
            }
            default -> {
                // **If unknown state, reset to avoid StackOverflow**:
                userStates.remove(chatId);
                sendMessage(chatId, "An error occurred. Please try again or start a new session with /addworkout.");
            }
        }
    }


    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
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
