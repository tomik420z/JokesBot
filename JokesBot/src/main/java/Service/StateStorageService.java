package Service;

import States.States;

import java.util.*;


public class StateStorageService {

    private final TreeSet<States> jokeRateStorage = new TreeSet<>();

    public int size() {
        return jokeRateStorage.size();
    }

    public void addJokeId(long chatId, long messageId, int jokeId) {
        jokeRateStorage.add(new States(chatId, messageId, jokeId));
    }

    public int extractJokeId(long chatId, long messageId) {
        States st = jokeRateStorage.floor(new States(chatId, messageId, -1));
        jokeRateStorage.remove(new States(chatId, messageId, -1));
        return st.jokeId;
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