package umg.TelegramBot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import umg.TelegramBot.Model.UserSession;

import java.util.Date;
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

    private static final String CLIENT_STATE_PREFIX = "client_state:";
    private static final String CLIENT_NAME_PREFIX = "client:";
    private static final String STATE_WAITING_FOR_NAME = "WAITING_FOR_NAME";
    private static final String STATE_WAITING_FOR_QUESTION = "WAITING_FOR_QUESTION";

    // Iniciar la sesión del usuario y guardar en Redis
    private void startUserSession(long chatId) {
        String sessionId = UUID.randomUUID().toString();
        UserSession userSession = new UserSession(sessionId, chatId, new Date().toString());

        redisTemplate.opsForHash().put("session:" + sessionId, "chatId", chatId);
        redisTemplate.opsForHash().put("session:" + sessionId, "startTime", userSession.getStartTime());
        redisTemplate.opsForValue().set("current_session:" + chatId, sessionId);
    }

    // Finalizar la sesión del usuario en Redis
    private void endUserSession(long chatId) {
        String sessionId = (String) redisTemplate.opsForValue().get("current_session:" + chatId);
        if (sessionId != null) {
            redisTemplate.opsForHash().put("session:" + sessionId, "endTime", new Date().toString());
            redisTemplate.delete("current_session:" + chatId);
        } else {
            System.out.println("No se encontró una sesión activa para el chatId: " + chatId);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if ("/start".equalsIgnoreCase(messageText)) {
                startUserSession(chatId);
                handleInitialInteraction(chatId);
                return;
            }

            if ("/end".equalsIgnoreCase(messageText)) {
                endUserSession(chatId);
                return;
            }

            handleUserInteraction(chatId, messageText);
        }
    }

    // Manejar la interacción inicial con el usuario
    private void handleInitialInteraction(long chatId) {
        redisTemplate.opsForValue().set(CLIENT_STATE_PREFIX + chatId, STATE_WAITING_FOR_NAME);
        sendMessage(chatId, "Hola, ¿cuál es tu nombre?");
    }

    // Manejar la interacción posterior con el usuario
    private void handleUserInteraction(long chatId, String messageText) {
        String clientState = (String) redisTemplate.opsForValue().get(CLIENT_STATE_PREFIX + chatId);
        String redisClientKey = CLIENT_NAME_PREFIX + chatId;

        if (clientState == null) {
            handleInitialInteraction(chatId);
        } else if (STATE_WAITING_FOR_NAME.equals(clientState)) {
            redisTemplate.opsForValue().set(redisClientKey, messageText);
            redisTemplate.opsForValue().set(CLIENT_STATE_PREFIX + chatId, STATE_WAITING_FOR_QUESTION);
            sendMessage(chatId, "Hola " + messageText + ", ¿cuál es tu pregunta?");
        } else if (STATE_WAITING_FOR_QUESTION.equals(clientState)) {
            String botResponse = openAIService.getChatResponse(messageText);
            saveUserQuery(chatId, messageText, botResponse);
            sendMessage(chatId, botResponse);
        }
    }

    // Guardar la consulta del usuario en Redis
    private void saveUserQuery(long chatId, String question, String response) {
        String requestId = UUID.randomUUID().toString();
        redisTemplate.opsForHash().put("request:" + requestId, "question", question);
        redisTemplate.opsForHash().put("request:" + requestId, "response", response);
        redisTemplate.opsForHash().put("request:" + requestId, "client", chatId);
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
