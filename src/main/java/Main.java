import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        String botUsername = "https://t.me/XtremeFitnesBot";
        String botToken = "8059576649:AAGdjcXAPQ8J75MEPDRakkOZUkbZNKlFvmw";
        String url= "https://api.telegram.org/bot"+ botToken +"/deletewebhook";

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new FitnessBot(botUsername, botToken));
            System.out.println(" Bot is running...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
