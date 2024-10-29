package umg.TelegramBot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private ClienteService clienteService;

    @Override
    public void onUpdateReceived(Update update) {
        // Verificar que el mensaje sea de texto
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageTextReceived = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            System.out.println("Mensaje recibido: " + messageTextReceived);

            // Obtener el nombre del usuario
            String nombreUsuario = update.getMessage().getFrom().getFirstName();

            // Guardar el nombre del usuario en la base de datos
            clienteService.guardarCliente(nombreUsuario);

            // Obtener la respuesta de OpenAI
            String botResponse = openAIService.getChatResponse(messageTextReceived);

            // Enviar la respuesta al usuario
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(botResponse);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
