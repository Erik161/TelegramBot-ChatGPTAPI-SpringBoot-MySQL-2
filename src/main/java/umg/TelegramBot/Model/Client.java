package umg.TelegramBot.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer client;

    @Column(nullable = false)
    private Long chatId; // ID único del usuario en Telegram

    @Column(nullable = false)
    private String name;
}
