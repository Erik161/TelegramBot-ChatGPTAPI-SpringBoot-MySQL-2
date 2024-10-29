package umg.TelegramBot.Repository;

import umg.TelegramBot.Model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    // Métodos personalizados si son necesarios
}

