package umg.TelegramBot.Repository;

import org.springframework.stereotype.Repository;
import umg.TelegramBot.Model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    // Métodos personalizados si son necesarios
}

