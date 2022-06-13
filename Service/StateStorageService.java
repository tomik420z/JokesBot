package Service;

import States.States;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateStorageService {
    private final List<States> jokeRateStorage = new ArrayList<>();

    public int size() {
        return jokeRateStorage.size();
    }

    public void addJokeId(long chatId, long messageId, int jokeId) {
        jokeRateStorage.add(new States(chatId, messageId, jokeId));
    }

    public int extractJokeId(long chatId, long messageId) {
        int result = -1;
        for (int i = 0; i < jokeRateStorage.size(); i++) {
            States states = jokeRateStorage.get(i);
            if (states.chatId == chatId && states.messageId == messageId) {
                result = states.jokeId;
                jokeRateStorage.remove(i);
                break;
            }
        }
        return result;
    }


    private final Map<Long, Boolean> jokeAdditionStateStorage = new HashMap<>();

    public boolean isEmpty() {
        return jokeAdditionStateStorage.isEmpty();
    }

    public boolean getJokeAdditionState(long chatId) {
        return jokeAdditionStateStorage.get(chatId);
    }

    public void setJokeAdditionState(long chatId, boolean state) {
        jokeAdditionStateStorage.put(chatId, state);
    }

    public void removeJokeAdditionState(long chatId) {
        jokeAdditionStateStorage.remove(chatId);
    }
}
