package umg.TelegramBot.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public String getChatResponse(String userMessage) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        RestTemplate restTemplate = new RestTemplate();

        // Configurar los encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        // Construir el mensaje del usuario
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMessage);

        // Construir el cuerpo de la solicitud
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", Collections.singletonList(message));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Procesar la respuesta
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> messageResponse = (Map<String, Object>) firstChoice.get("message");
                String content = (String) messageResponse.get("content");
                return content.trim();
            } else {
                // Manejar otros códigos de estado HTTP
                return "Lo siento, ocurrió un error al procesar la solicitud.";
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                // Manejar el error de cuota excedida
                return "Error: Se ha excedido la cuota de uso de la API de OpenAI. Por favor, verificar el plan y detalles de facturación.";
            } else {
                return "Lo siento, ocurrió un error al procesar la solicitud.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lo siento, ocurrió un error inesperado.";
        }
    }
}
