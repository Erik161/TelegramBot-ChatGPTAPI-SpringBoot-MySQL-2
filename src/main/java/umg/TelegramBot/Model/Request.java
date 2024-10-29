package umg.TelegramBot.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer request;

    @Column(nullable = false, length = 2000)
    private String question;

    @Lob
    @Column(nullable = false)
    private String response;

    @ManyToOne
    @JoinColumn(name = "cliente", nullable = false)
    private Client cliente;
}
