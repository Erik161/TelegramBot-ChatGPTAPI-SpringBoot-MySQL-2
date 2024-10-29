package umg.TelegramBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import umg.TelegramBot.Service.TelegramBotService;

@SpringBootApplication

public class TelegramBotApplication implements CommandLineRunner {

	@Autowired
	private TelegramBotService telegramBotService;

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		try {
			botsApi.registerBot(telegramBotService);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
