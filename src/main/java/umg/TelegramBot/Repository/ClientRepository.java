package umg.TelegramBot.Repository;

import org.springframework.stereotype.Repository;
import umg.TelegramBot.Model.Client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByName(String name);
}
