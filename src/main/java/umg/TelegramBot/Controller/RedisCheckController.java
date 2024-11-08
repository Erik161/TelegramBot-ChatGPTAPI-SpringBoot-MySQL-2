package umg.TelegramBot.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public class RedisCheckController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/verificarUsuario")
    public String verificarUsuario(@RequestParam String chatId) {
        String redisKey = "user:" + chatId;
        Object userName = redisTemplate.opsForValue().get(redisKey);
        return userName != null ? "Usuario encontrado: " + userName : "No se encontró el usuario.";
    }

    @GetMapping("/verificarSolicitud")
    public Map<Object, Object> verificarSolicitud(@RequestParam String requestId) {
        return redisTemplate.opsForHash().entries("request:" + requestId);
    }

    @GetMapping("/verificarSesion")
    public Map<Object, Object> verificarSesion(@RequestParam String sessionId) {
        return redisTemplate.opsForHash().entries("session:" + sessionId);
    }
}
