package umg.TelegramBot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.UUID;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Constantes para manejar el estado del cliente
    private static final String CLIENT_STATE_PREFIX = "client_state:";
    private static final String CLIENT_NAME_PREFIX = "client:";
    private static final String STATE_WAITING_FOR_NAME = "WAITING_FOR_NAME";
    private static final String STATE_WAITING_FOR_QUESTION = "WAITING_FOR_QUESTION";

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageTextReceived = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Claves para Redis
            String redisClientKey = CLIENT_NAME_PREFIX + chatId;
            String redisStateKey = CLIENT_STATE_PREFIX + chatId;

            // Obtener el estado actual del cliente
            String clientState = (String) redisTemplate.opsForValue().get(redisStateKey);

            // Si se recibe el comando /start o el estado es nulo, preguntar por el nombre
            if (messageTextReceived.equalsIgnoreCase("/start") || clientState == null) {
                redisTemplate.opsForValue().set(redisStateKey, STATE_WAITING_FOR_NAME);
                sendMessage(chatId, "Hola, ¿cuál es tu nombre?");
                return;
            }

            // Manejar el estado de espera del nombre
            if (clientState.equals(STATE_WAITING_FOR_NAME)) {
                // Guardar el nombre del cliente en Redis
                redisTemplate.opsForValue().set(redisClientKey, messageTextReceived);

                // Cambiar el estado a "esperando la pregunta"
                redisTemplate.opsForValue().set(redisStateKey, STATE_WAITING_FOR_QUESTION);

                // Responder al cliente y pedir la pregunta
                sendMessage(chatId, "Hola " + messageTextReceived + ", ¿cuál es tu pregunta?");
                return;
            }

            // Manejar el estado de espera de la pregunta
            if (clientState.equals(STATE_WAITING_FOR_QUESTION)) {
                // Obtener el nombre del cliente de Redis
                String clientName = (String) redisTemplate.opsForValue().get(redisClientKey);

                // Obtener la respuesta de OpenAI
                String botResponse = openAIService.getChatResponse(messageTextReceived);

                // Generar un ID único para la solicitud
                String requestId = UUID.randomUUID().toString();
                String redisRequestKey = "request:" + requestId;

                // Guardar la solicitud y respuesta en Redis
                redisTemplate.opsForHash().put(redisRequestKey, "question", messageTextReceived);
                redisTemplate.opsForHash().put(redisRequestKey, "response", botResponse);
                redisTemplate.opsForHash().put(redisRequestKey, "client", chatId);

                // Responder al cliente con la respuesta de ChatGPT
                sendMessage(chatId, botResponse);
            }
        }
    }

    // Método auxiliar para enviar mensajes al cliente
    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
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
