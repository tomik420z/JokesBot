package States;

public class States implements Comparable<States> {

    public final long chatId;
    public final long messageId;
    public final int jokeId;

    public States(long chatId, long messageId, int jokeId) {
        this.chatId = chatId;
        this.messageId = messageId;
        this.jokeId = jokeId;
    }

    @Override
    public int compareTo(States obj) {
        if (this.chatId < obj.chatId) {
            return -1;
        } else if (this.chatId > obj.chatId) {
            return 1;
        } else {
            if (this.messageId == obj.messageId) {
                return 0;
            } else if (this.messageId < obj.messageId) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}