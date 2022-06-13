package States;

public class States {
    public final long chatId;
    public final long messageId;
    public final int jokeId;

    public States(long chatId, long messageId, int jokeId) {
        this.chatId = chatId;
        this.messageId = messageId;
        this.jokeId = jokeId;
    }
}
