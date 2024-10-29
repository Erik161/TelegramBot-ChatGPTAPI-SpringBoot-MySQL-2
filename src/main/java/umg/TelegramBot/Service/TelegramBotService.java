package umg.TelegramBot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import umg.TelegramBot.Model.Client;
import umg.TelegramBot.Model.Request;
import umg.TelegramBot.Repository.ClientRepository;
import umg.TelegramBot.Repository.RequestRepository;
import java.util.Optional;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageTextReceived = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getFirstName();

            // Buscamos si el usuario ya está registrado por su chatId
            Optional<Client> optionalClient = clientRepository.findById(chatId.intValue());

            if (optionalClient.isEmpty()) {
                // Si no está registrado, le pedimos su nombre
                if (messageTextReceived.equalsIgnoreCase("/start")) {
                    sendMessage(chatId, "Hola, ¿cómo te llamas?");
                } else {
                    // Registramos al usuario
                    Client client = new Client();
                    client.setClient(chatId.intValue());
                    client.setName(messageTextReceived);
                    clientRepository.save(client);

                    sendMessage(chatId, "Hola " + messageTextReceived + ", ¿cuál es tu pregunta?");
                }
            } else {
                // Usuario registrado, procedemos con ChatGPT
                Client client = optionalClient.get();

                // Obtenemos la respuesta de OpenAI
                String botResponse = openAIService.getChatResponse(messageTextReceived);

                // Guardamos la interacción en la base de datos
                Request request = new Request();
                request.setQuestion(messageTextReceived);
                request.setResponse(botResponse);
                request.setCliente(client);
                requestRepository.save(request);

                // Enviamos la respuesta al usuario
                sendMessage(chatId, botResponse);
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername; // Define el nombre de usuario de tu bot
    }

    @Override
    public String getBotToken() {
        return botToken; // Define el token de tu bot
    }


}
