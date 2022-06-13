package Service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class SendMessageService {
    ButtonsService buttonsService = new ButtonsService();
    public final String greetingMessage = "Вас приветствует нешуточный бот!";
    public final String rateJokeMessage = "Оцените данную шутку \uD83D\uDE03:";
    public final String unidentifiedObjectMessage = "Я не знаю что это такое \uD83E\uDD7A";
    public final String registrationMessage = "Регистрация завершена!";
    public final String registrationCompletedMessage = "Вы уже зарегистрированы!";
    public final String addJokeErrorMessage = "Операция ввода шутки не сработала!\nВведенная команда отменена!";
    public final String addJokeMessage = "Введите Вашу шутку \uD83D\uDE0B:";
    public final String registerFirstMessage = "Эта команда доступна только зарегистрированным пользователям\n" +
            "Для регистрации воспользуйтесь командой \"/reg\"";
    public final String addJokeSuccessMessage = "Ваша шутка успешно добавлена ✅";
    public final String emptyJokeListMessage = "Список ваших шуток пуст.\n" +
            "Для добавления шуток воспользуйтесь командой \"/add\"";
    public final String deletingErrorMessage = "Для того чтобы удалить аккаунт, сначала создайте его.\n" +
            "Зарегистрироваться можно при помощи команды\"/reg\"";
    public final String accountDeletingMessage = "Выберите вариант удаления аккаунта:";
    public final String helpMessage = """
            Для всех пользователей 🙂:
            /help - Получение описания всех доступных команд.
            /joke - Получение случайной шутки.
            /top - Получение шуток с самым высоким рейтингом.
            /reg - Регистрация аккаунта.
            
            Для зарегистрированных пользователей 😎:
            /joke -  Получение шутки на основе предпочтений.
            /add - Добавление пользовательской шутки.
            /mine - Получение всех шуток, добавленных пользователем.
            /delete - Удаление аккаунта.""";

    public SendMessage createTextMessage(Update update, String text) {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(update.getMessage().getChatId().toString());
        responseMessage.setText(text);
        return responseMessage;
    }

    public SendMessage createJokeRateMessage(Update update, String text) {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(update.getMessage().getChatId().toString());
        responseMessage.setReplyMarkup(buttonsService.setInlineButtons(buttonsService.jokeRateButtons()));
        responseMessage.setText(text);
        return responseMessage;
    }

    public EditMessageReplyMarkup createJokeRateResultMessage(CallbackQuery callbackQuery) {
        EditMessageReplyMarkup responseMessage = new EditMessageReplyMarkup();
        responseMessage.setChatId(callbackQuery.getMessage().getChatId().toString());
        responseMessage.setMessageId(callbackQuery.getMessage().getMessageId());
        responseMessage.setReplyMarkup(buttonsService.setInlineButtons(buttonsService.jokeRateResultButton(callbackQuery)));
        return responseMessage;
    }

    public List<SendMessage> createTopJokesMessage(Update update, String[] jokes) {
        List<SendMessage> sendMessageSet = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String[] jokeInf = jokes[i].split(":");
            SendMessage responseMessage = new SendMessage();
            responseMessage.setText(jokeInf[0]);
            responseMessage.setReplyMarkup(buttonsService.setInlineButtons(buttonsService.topJokeButton(jokeInf[1])));
            responseMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessageSet.add(responseMessage);
        }
        return sendMessageSet;
    }

    public List<SendMessage> createAllAuthorJokeMessage(Update update, List<String> jokesAndRating) {
        List<SendMessage> sendMessageSet = new ArrayList<>();
        if (jokesAndRating.size() != 0) {
            for (String s : jokesAndRating) {
                String[] jokeInf = s.split(":");
                SendMessage responseMessage = new SendMessage();
                responseMessage.setText(jokeInf[0]);
                responseMessage.setReplyMarkup(buttonsService.setInlineButtons(buttonsService.topJokeButton(jokeInf[1])));
                responseMessage.setChatId(update.getMessage().getChatId().toString());
                sendMessageSet.add(responseMessage);
            }
        } else {
            SendMessage responseMessage = new SendMessage();
            responseMessage.setText(emptyJokeListMessage);
            responseMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessageSet.add(responseMessage);
        }
        return sendMessageSet;
    }

    public SendMessage createDeletingAccountMessage(Update update) {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(update.getMessage().getChatId().toString());
        responseMessage.setReplyMarkup(buttonsService.setInlineButtons(buttonsService.accountDeletionButtons()));
        responseMessage.setText(accountDeletingMessage);
        return responseMessage;
    }

    public EditMessageReplyMarkup createDeletingAccountResultMessage(CallbackQuery callbackQuery) {
        EditMessageReplyMarkup responseMessage = new EditMessageReplyMarkup();
        responseMessage.setChatId(callbackQuery.getMessage().getChatId().toString());
        responseMessage.setMessageId(callbackQuery.getMessage().getMessageId());
        responseMessage.setReplyMarkup(buttonsService.setInlineButtons(buttonsService.accountDeletionSuccessButton(callbackQuery)));
        return responseMessage;
    }
}
