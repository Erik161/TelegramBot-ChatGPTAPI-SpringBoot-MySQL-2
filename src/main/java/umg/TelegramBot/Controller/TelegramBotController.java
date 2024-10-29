package umg.TelegramBot.Controller;

import org.springframework.web.bind.annotation.*;
import umg.TelegramBot.Model.Request;
import umg.TelegramBot.Repository.RequestRepository;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class TelegramBotController {

    private final RequestRepository requestRepository;

    public TelegramBotController(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @GetMapping
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @GetMapping("/{id}")
    public Request getRequestById(@PathVariable Integer id) {
        return requestRepository.findById(id).orElse(null);
    }
}
