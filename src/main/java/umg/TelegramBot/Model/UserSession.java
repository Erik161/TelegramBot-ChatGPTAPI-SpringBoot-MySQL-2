package umg.TelegramBot.Model;

public class UserSession {
    private String sessionId; // UUID para identificar la sesión de forma única
    private long chatId;
    private String startTime;
    private String endTime;


    public UserSession(String sessionId, long chatId, String startTime) {
        this.sessionId = sessionId;
        this.chatId = chatId;
        this.startTime = startTime;
    }

    // Getters y setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
