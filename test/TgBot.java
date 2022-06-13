/*import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TgBot extends TelegramLongPollingBot {

    Storage radixStorage = new Storage();

    public static void main(String[] args) throws TelegramApiException {
        TgBot bot = new TgBot(new DefaultBotOptions());
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }

    public TgBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotUsername() {
        return "@FuzzyLogic_bot";
    }

    @Override
    public String getBotToken() {
        return "5511573720:AAEVWiLXFvjiFdpfqxs_2kus9AsONfaSSsk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                processCallbackQuery(update.getCallbackQuery());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage()) {
            try {
                processUpdateMessage(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void processCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");

        switch (param[0]) {
            case "current" -> radixStorage.setCurrentRadix(message.getChatId(), Integer.parseInt(param[1]));
            case "target" -> radixStorage.setTargetRadix(message.getChatId(), Integer.parseInt(param[1]));
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (Radix radix : Radix.values()) {
            buttons.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text(setCurrentRadix(radix.getRadix(), message))
                            .callbackData("current:" + radix.getRadix())
                            .build(),
                    InlineKeyboardButton.builder()
                            .text(setTargetRadix(radix.getRadix(), message))
                            .callbackData("target:" + radix.getRadix())
                            .build()));
        }
        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }

    private void processUpdateMessage(Message message) throws TelegramApiException {
        if (message.hasText() && message.hasEntities()) { // Entities - command
            Optional<MessageEntity> commandEntity = message.getEntities().stream()
                    .filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText()
                        .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                switch (command) {
                    case "/convert":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        for (Radix radix : Radix.values()) {
                            buttons.add(Arrays.asList(
                                    InlineKeyboardButton.builder()
                                            .text(setCurrentRadix(radix.getRadix(), message))
                                            .callbackData("current:" + radix.getRadix())
                                            .build(),
                                    InlineKeyboardButton.builder()
                                            .text(setTargetRadix(radix.getRadix(), message))
                                            .callbackData("target:" + radix.getRadix())
                                            .build()));
                        }
                        execute(SendMessage.builder()
                                .text("Left - original; Right - target")
                                .chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build());
                        break;

                    case "test asd":
                        break;
                }
            }
        }
        else if (message.hasText()) {
            long dValue;
            String result = "";
            boolean flag = false;
            try {
                dValue = Long.parseLong(message.getText(), radixStorage.getCurrentRadix(message.getChatId()));
                result = Long.toString(dValue, radixStorage.getTargetRadix(message.getChatId()));
            } catch (NumberFormatException e) {
                flag = true;
            }
            if (!flag) {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Your number in " + radixStorage.getTargetRadix(message.getChatId()) + " number system:\n" + result.toUpperCase())
                        .build());
            } else {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("The number " + message.getText()
                                + " is not a number in the " + radixStorage.getCurrentRadix(message.getChatId()) + " number system")
                        .build());
            }
        }
        else if (message.hasPhoto()) {
            List<PhotoSize> photo = message.getPhoto();
            SendPhoto sendPhoto = new SendPhoto();
            InputFile replyPhoto = new InputFile(photo.get(0).getFileId());
            sendPhoto.setChatId(message.getChatId().toString());
            sendPhoto.setPhoto(replyPhoto);
            execute(sendPhoto);
        }
    }

    private String setCurrentRadix(String radix, Message message) {
        if (Integer.parseInt(radix) == radixStorage.getCurrentRadix(message.getChatId()))
            return radixStorage.getCurrentRadix(message.getChatId()) + " ✅";
        else
            return radix;
    }

    private String setTargetRadix(String radix, Message message) {
        if (Integer.parseInt(radix) == radixStorage.getTargetRadix(message.getChatId()))
            return radixStorage.getTargetRadix(message.getChatId()) + " ✅";
        else
            return radix;
    }
}
*/
/*
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText()) {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Test from Java \nYour message: " +
                                "\n[" + message.getText() + "]")
                        .build());
            }

        }

    }

 */
