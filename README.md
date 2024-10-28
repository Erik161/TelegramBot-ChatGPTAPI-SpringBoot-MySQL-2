<div align="center">
  <h1>
      TelegramBot-ChatGPTAPI "PROYECTO FINAL"
  </h1> 
  "Grupo 4"
</div>



![cover](https://github.com/user-attachments/assets/9e61c141-7162-4856-8e49-e5032c4c1474)

# VER ONLINE: [:arrow_right: PREVIEW :arrow_left:](https://airman-project.vercel.app/) 

  
  
 Nuestro proyecto es una aplicación innovadora que integra la API de ChatGPT con la funcionalidad de un bot de Telegram, utilizando BootFather, MySQL, Lombok, Spring Boot, Swagger y Maven como herramientas clave. Este sistema permite una interacción fluida con el usuario mediante Telegram, brindando respuestas inteligentes y automatizadas a través de ChatGPT. Desarrollado en un stack backend robusto, el proyecto ofrece una experiencia intuitiva y segura, ideal para quienes buscan soluciones de interacción con IA en aplicaciones de mensajería.



<table align="center">
  
  </tr>
    <td>
      <a href="#">
        <img alt="" src="https://github.com/user-attachments/assets/26143db6-a205-43d2-9147-3d71813ad958">
      </a>
    </td>
    <td>
      <a href="#">
        <img alt="" src="https://github.com/user-attachments/assets/986da5b2-2c1d-478d-b88f-668edd319359" width="100">
      </a>
    </td>
     <td>
      <a href="#">
        <img alt="" src="https://github.com/user-attachments/assets/0aff50f5-8d21-46a5-8b02-5a1711cb14c0" width="100">
      </a>
    </td>
    </td>
      
  </tr>
  
  </tr>
    <td>
      <a href="#">
        <img alt="Go" src="https://github.com/user-attachments/assets/b9ac54b2-93bd-4da8-8990-507c1f5d4282" width="100">
      </a>
    </td>
    <td>
      <a href="#">
        <img alt="Bash" src="https://github.com/user-attachments/assets/9ad1aea5-da17-4927-84b7-7155290f0f57" width="100">
      </a>
    </td>
     <td>
      <a href="#">
        <img alt="" src="https://logovtor.com/wp-content/uploads/2020/10/vercel-inc-logo-vector.png" width="100">
      </a>
    </td>
    </td>
       
  </tr>
  
  </tr>
    <td>
      <a href="#">
        <img alt="" src="https://github.com/user-attachments/assets/e1e40d8c-59ef-4807-b7e3-66dce61a2988" width="100">
      </a>
    </td>
    <td>
      <a href="#">
        <img alt="Bash" src="https://github.com/user-attachments/assets/e9456f50-97a7-48bd-8474-da490f8bbd13" width="100">
      </a>
    </td>
     <td>
      <a href="#">
        <img alt="" src="https://github.com/user-attachments/assets/f039df30-2298-429c-b153-a1cd9671095e" width="100">
      </a>
    </td>
    </td>
       
  </tr>
  
</table>




# Nuestro Equipo



<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/c2caed80-a81f-4909-ba01-0e98b1c086eb" width="100" alt="Erick Hernandez ">
      <br>
      <strong>Erick Hernandez</strong>
      <br>
     <a href="https://github.com" target="_blank">GitHub</a>|
      <a href="" target="_blank"
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/fd52886b-245f-40a0-b11b-e49423f13939" width="100" alt="Kimberly Villeda">
      <br>
      <strong>kimberly villeda </strong>
      <br>
      <a href="" target="_blank" |
      <a href="https://github.com" target="_blank">GitHub</a>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/174bf52e-2aa2-4b52-aaca-095ea282ecf5" width="100" alt="Paolo Sanchez">
      <br>
      <strong>Paolo Sanchez </strong>
      <br>
      <a href="https://github.com" target="_blank">GitHub</a>|
      <a href="" target="_blank"
    </td>
  </tr>
</table>




                                                               Guía de Integración del Bot de Telegram

                                                  
1. Seleccionamos la tecnología adecuada
Para integrar un bot en nuestra aplicación, decidimos usar la API de bots de Telegram. Esta opción nos pareció confiable y tiene una documentación detallada. Nos aseguramos de revisarla bien para entender sus capacidades y requisitos técnicos; pueden verla https://core.telegram.org/bots



2 . Agregamos la dependencia de Telegram Bots en nuestro proyecto
Para que nuestra aplicación de Spring Boot se comunique con Telegram, incluimos una dependencia en el archivo pom.xml. Esta dependencia (telegrambots) contiene las herramientas necesarias para que el bot funcione con Telegram. La configuración es la siguiente:

<!-- Dependencia para interactuar con Telegram -->
<dependency>
    <groupId>org.telegram</groupId>
    <artifactId>telegrambots</artifactId>
    <version>6.9.7.1</version>
</dependency>

3. Creamos un paquete llamado service en el proyecto
Para mantener el código organizado, creamos un paquete llamado service, donde incluimos la clase principal de integración del bot. Esta clase gestiona la lógica de interacción con Telegram, y decidimos llamarla TelegramBotService, aunque cualquier otro nombre claro también hubiera funcionado.


4. Extendimos la clase TelegramLongPollingBot
En la clase TelegramBotService, la hicimos extender TelegramLongPollingBot, una clase de la API que se encarga de recibir y procesar los mensajes. Esta herencia simplifica nuestro desarrollo, permitiéndonos enfocarnos en definir las respuestas que queremos que dé el bot.

public class TelegramBotService extends TelegramLongPollingBot {
    // Aquí va la implementación del bot
}

5. Sobrescribimos los métodos clave para configurar el bot
Para que el bot funcionara correctamente, sobrescribimos los métodos getBotUsername y getBotToken. getBotUsername devuelve el nombre del bot, y getBotToken contiene el token de autenticación. Configuramos estos métodos para devolver el nombre y el token específicos de nuestro bot:
@Override
public String getBotUsername() {
    return "nombre_de_nuestro_bot"; // Sustituido con el nombre real del bot
}

@Override
public String getBotToken() {
    return "nuestro_apikey"; // Sustituido con el token proporcionado por BotFather
}
6. Creamos el bot en Telegram usando BotFather
Para obtener el nombre y el token necesarios, utilizamos BotFather, el bot oficial de Telegram para gestionar bots. En Telegram, buscamos @BotFather, iniciamos una conversación y usamos el comando /newbot para crear uno nuevo. Asignamos un nombre que termina en "bot", como lo exige Telegram, y BotFather nos proporcionó un token de API. Luego, pegamos este token en el método getBotToken y añadimos el nombre en getBotUsername.

7. Activamos el bot en Telegram
Finalmente, abrimos el bot en Telegram y presionamos START para activarlo. Con esto, nuestro bot ya quedó listo para interactuar con los usuarios en Telegram.

