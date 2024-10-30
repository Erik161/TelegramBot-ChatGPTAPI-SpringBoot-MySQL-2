package umg.TelegramBot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String getChatResponse(String userMessage) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMessage);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", Collections.singletonList(message));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> messageResponse = (Map<String, Object>) firstChoice.get("message");
                String content = (String) messageResponse.get("content");

                // Guardar la solicitud y la respuesta en Redis
                String requestId = UUID.randomUUID().toString();
                redisTemplate.opsForHash().put("request:" + requestId, "question", userMessage);
                redisTemplate.opsForHash().put("request:" + requestId, "response", content);
                return content.trim();
            } else {
                return "Lo siento, ocurrió un error al procesar la solicitud.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lo siento, ocurrió un error inesperado.";
        }
    }
}
