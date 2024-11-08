# Documentación de la Mejora 9: Historial de Conexiones de Usuarios

## Descripción de la Mejora
Se implementó una funcionalidad en el proyecto de bot de Telegram para registrar el inicio y fin de las sesiones de los usuarios. Esta funcionalidad permite llevar un historial de conexiones en Redis, lo que facilita el análisis de uso del bot y proporciona trazabilidad de las interacciones de los usuarios.

## Detalles de Implementación

### 1. Creación de la Clase `UserSession`
Se creó la clase `UserSession` para representar las sesiones de usuario. Esta clase contiene los atributos necesarios para almacenar los detalles de la sesión, como `sessionId`, `chatId`, `startTime` y `endTime`.

```java
package umg.TelegramBot.Model;

public class UserSession {
    private String sessionId;
    private long chatId;
    private String startTime;
    private String endTime;

    // Constructor sin argumentos
    public UserSession() {}

    // Constructor con argumentos
    public UserSession(String sessionId, long chatId, String startTime) {
        this.sessionId = sessionId;
        this.chatId = chatId;
        this.startTime = startTime;
    }

    // Getters y setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public long getChatId() { return chatId; }
    public void setChatId(long chatId) { this.chatId = chatId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
```

## 2. Modificaciones en `TelegramBotService`
Se actualizaron los métodos de `TelegramBotService` para incluir la lógica de inicio y fin de sesión. Se almacenan en Redis los detalles de las sesiones, asociando cada `chatId` con un `sessionId` único.

### Código de `TelegramBotService` para el Inicio y Fin de Sesión:

```java
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

    // Otros métodos omitidos por brevedad...
}
```
## 3. Verificación del Registro en Redis
Redis se utiliza para almacenar tanto el inicio como el fin de las sesiones de usuario. Puedes verificar las claves en Redis que representan las sesiones activas y finalizadas.


![image](https://github.com/user-attachments/assets/096dfb35-c748-4a1d-a6e3-735e0998c914)



## 4. Pruebas de la Funcionalidad en el Chat del Bot
Se probaron los comandos `/start` y `/end` en el bot para confirmar que las sesiones se registran correctamente en Redis.


https://github.com/user-attachments/assets/4beaac34-1d08-42bb-8fc4-d1841841f176

