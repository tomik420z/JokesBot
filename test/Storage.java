package test;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private final Map<Long, Integer> currentRadix = new HashMap<>();
    private final Map<Long, Integer> targetRadix = new HashMap<>();

    public Integer getCurrentRadix(long chatId) {
        return currentRadix.getOrDefault(chatId, 10);
    }

    public Integer getTargetRadix(long chatId) {
        return targetRadix.getOrDefault(chatId, 10);
    }

    public void setCurrentRadix(long chatId, Integer radix) {
        currentRadix.put(chatId, radix);
    }

    public void setTargetRadix(long chatId, Integer radix) {
        targetRadix.put(chatId, radix);
    }
}

