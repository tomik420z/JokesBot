package Service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static Commands.Callbacks.*;

public class ButtonsService {
    private final ArrayList<String> emojiSet = new ArrayList<>(List.of(
            "☹️", "\uD83D\uDE10", "\uD83D\uDE03", "\uD83D\uDE01", "\uD83D\uDE02"));

    public InlineKeyboardMarkup setInlineButtons(List<InlineKeyboardButton> inlineButtons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(inlineButtons));
        return inlineKeyboardMarkup;
    }

    public List<InlineKeyboardButton> jokeRateButtons() {
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(emojiSet.get(i));
            button.setCallbackData(JOKE_RATE + ":" + (i + 1));
            buttonList.add(button);
        }
        return buttonList;
    }

    public List<InlineKeyboardButton> jokeRateResultButton(CallbackQuery callbackQuery) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        switch (callbackQuery.getData().split(":")[1]) {
            case "1" -> button.setText("Вы дали этой шутке " + "1️⃣" + " балл " + emojiSet.get(0));
            case "2" -> button.setText("Вы дали этой шутке " + "2️⃣" + " балла " + emojiSet.get(1));
            case "3" -> button.setText("Вы дали этой шутке " + "3️⃣" + " балла " + emojiSet.get(2));
            case "4" -> button.setText("Вы дали этой шутке " + "4️⃣" + " балла " + emojiSet.get(3));
            case "5" -> button.setText("Вы дали этой шутке " + "5️⃣" + " баллов " + emojiSet.get(4));
        }
        button.setCallbackData(DEFAULT + ":");
        return List.of(button);
    }

    public List<InlineKeyboardButton> topJokeButton(String rating) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Рейтинг этой шутки: " + rating + " " + "\uD83D\uDC8E");
        button.setCallbackData(DEFAULT + ":");
        return List.of(button);
    }

    public List<InlineKeyboardButton> accountDeletionButtons() {
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Только аккаунт");
        button.setCallbackData(ACCOUNT_DELETION + ":" + 0);
        buttonList.add(button);
        button = new InlineKeyboardButton();
        button.setText("Аккаунт и шутки");
        button.setCallbackData(ACCOUNT_DELETION + ":" + 1);
        buttonList.add(button);
        return buttonList;
    }

    public List<InlineKeyboardButton> accountDeletionSuccessButton(CallbackQuery callbackQuery) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        if(callbackQuery.getData().split(":")[1].equals("0"))
            button.setText("Аккаут успешно удален " + "✅");
        else
            button.setText("Аккаут и шутки успешно удалены " + "✅");
        button.setCallbackData(DEFAULT + ":");
        return List.of(button);
    }
}