package umg.TelegramBot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void guardarCliente(String nombre) {
        String sql = "INSERT INTO clientes (nombre) VALUES (?)";
        jdbcTemplate.update(sql, nombre);
    }
}
