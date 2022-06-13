package TelegramBot;

import Service.DataBaseService;
import Service.SendMessageService;
import Service.StateStorageService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static Commands.Callbacks.ACCOUNT_DELETION;
import static Commands.Callbacks.JOKE_RATE;
import static Commands.Commands.*;

public class TelegramBot extends TelegramLongPollingBot {
    SendMessageService sendMessageService = new SendMessageService();
    DataBaseService dataBaseService = new DataBaseService();
    StateStorageService stateStorageService = new StateStorageService();

    public TelegramBot(DefaultBotOptions options) {
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();

            if (isCommand(message.getText())) {
                if (!stateStorageService.isEmpty() && stateStorageService.getJokeAdditionState(message.getChatId())) {
                    stateStorageService.setJokeAdditionState(message.getChatId(), false);
                    message.setText(ADD_ERROR);
                }
            }

            switch (message.getText()) {
                case START:
                    try {
                        execute(sendMessageService.createTextMessage(update, sendMessageService.greetingMessage));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("CASE: START");
                    }
                    break;
                case HELP:
                    try {
                        execute(sendMessageService.createTextMessage(update, sendMessageService.helpMessage));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("CASE: HELP");
                    }
                    break;
                case JOKE:
                    try {
                        Message storageMessage = execute(sendMessageService.createJokeRateMessage(update,
                                dataBaseService.getRandomJoke(message.getFrom().getId()) + "\n\n" + sendMessageService.rateJokeMessage));
                        stateStorageService.addJokeId(storageMessage.getChatId(), storageMessage.getMessageId(),
                                dataBaseService.getJokeId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("CASE: JOKE");
                    }
                    break;
                case TOP:
                    for (SendMessage sendMessage :
                            sendMessageService.createTopJokesMessage(update, dataBaseService.getTopJokes())) {
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: TOP");
                        }
                    }
                    break;
                case REG:
                    try {
                        if (dataBaseService.registerUser(message.getFrom().getId(), message.getFrom().getFirstName()))
                            execute(sendMessageService.createTextMessage(update, sendMessageService.registrationCompletedMessage));
                        else
                            execute(sendMessageService.createTextMessage(update, sendMessageService.registrationMessage));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("CASE: REG");
                    }
                    break;
                case ADD:
                    if (dataBaseService.isUserRegistered(update.getMessage().getFrom().getId())) {
                        stateStorageService.setJokeAdditionState(update.getMessage().getChatId(), true);
                        try {
                            execute(sendMessageService.createTextMessage(update, sendMessageService.addJokeMessage));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: ADD");
                        }
                    } else {
                        try {
                            execute(sendMessageService.createTextMessage(update, sendMessageService.registerFirstMessage));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: ADD");
                        }
                    }
                    break;
                case ADD_ERROR:
                    try {
                        execute(sendMessageService.createTextMessage(update, sendMessageService.addJokeErrorMessage));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("CASE: ADD_ERROR");
                    }
                    break;
                case MINE:
                    if (dataBaseService.isUserRegistered(update.getMessage().getFrom().getId())) {
                        for (SendMessage allAuthorJokeMessage
                                : sendMessageService.createAllAuthorJokeMessage(update,
                                dataBaseService.getAllAuthorJokes(message.getChatId()))) {
                            try {
                                execute(allAuthorJokeMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                                System.out.println("CASE: MINE");
                            }
                        }
                    } else {
                        try {
                            execute(sendMessageService.createTextMessage(update, sendMessageService.registerFirstMessage));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: MINE");
                        }
                    }
                    break;
                case DELETE:
                    if (dataBaseService.isUserRegistered(update.getMessage().getFrom().getId())) {
                        try {
                            execute(sendMessageService.createDeletingAccountMessage(update));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: DELETE");
                        }
                    } else {
                        try {
                            execute(sendMessageService.createTextMessage(update, sendMessageService.deletingErrorMessage));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: DELETE");
                        }
                    }
                    break;
                default:
                    if (!stateStorageService.isEmpty() && stateStorageService.getJokeAdditionState(update.getMessage().getChatId())) {
                        dataBaseService.addTextJoke(message.getFrom().getId(),
                                message.getFrom().getFirstName(),
                                message.getText());
                        stateStorageService.removeJokeAdditionState(update.getMessage().getChatId());
                        try {
                            execute(sendMessageService.createTextMessage(update, sendMessageService.addJokeSuccessMessage));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: default");
                        }
                    } else {
                        try {
                            execute(sendMessageService.createTextMessage(update, "\uD83D\uDE01"));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: default");
                        }
                    }
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String[] callbackData = callbackQuery.getData().split(":");
            switch (callbackData[0]) {
                case JOKE_RATE:
                    try {
                        execute(sendMessageService.createJokeRateResultMessage(callbackQuery));
                        dataBaseService.changeJokeRating(stateStorageService.extractJokeId(
                                        callbackQuery.getMessage().getChatId(),
                                        callbackQuery.getMessage().getMessageId()),
                                callbackData[1],
                                callbackQuery.getFrom().getId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("CASE: JOKE_RATE");
                    }
                    break;
                case ACCOUNT_DELETION:
                    if (callbackData[1].equals("0"))
                        dataBaseService.deleteUserWithoutJokes(callbackQuery.getFrom().getId());
                    else
                        dataBaseService.deleteUserWithJokes(callbackQuery.getFrom().getId());
                    try {
                        execute(sendMessageService.createDeletingAccountResultMessage(callbackQuery));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("CASE: ACCOUNT_DELETION");
                    }
                    break;
                default:
                    break;
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {

            SendPhoto msg = new SendPhoto();
            msg.setChatId(update.getMessage().getChatId().toString());
            msg.setPhoto(new InputFile("AgACAgIAAxkBAAIBLGKiOpCsIiipX1Z_XbEiZyPdIMPvAAL8wDEbOTQZSQPeEzCNuxwHAQADAgADeAADJAQ"));
            msg.setCaption("Photo regex");

            try {
                execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            try {
                execute(sendMessageService.createTextMessage(update, sendMessageService.unidentifiedObjectMessage));
            } catch (TelegramApiException e) {
                e.printStackTrace();
                System.out.println("CASE: else");
            }
        }
    }
}
