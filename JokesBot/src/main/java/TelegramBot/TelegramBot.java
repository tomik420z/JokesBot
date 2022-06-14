package TelegramBot;

import Service.DataBaseService;
import Service.SendMessageService;
import Service.StateStorageService;
import org.bson.Document;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;

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
        return "@Hot123JokesBot";
    }

    @Override
    public String getBotToken() {
        return "5543219339:AAFNXIM9veQxJUGFjhaoo9vHGPCfr6AaZOU";
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
                        Message storageMessage;
                        Document jokeDoc = dataBaseService.getRandomJoke(message.getFrom().getId());
                        if ((Boolean) jokeDoc.get("isPhoto")) {
                            storageMessage = execute(sendMessageService.createPhotoJokeRateMessage(update,
                                    jokeDoc.getString("Joke")));
                        } else {
                            storageMessage = execute(sendMessageService.createTextJokeRateMessage(update,
                                    jokeDoc.getString("Joke")));
                        }
                        stateStorageService.addJokeId(storageMessage.getChatId(), storageMessage.getMessageId(),
                                dataBaseService.getJokeId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("CASE: JOKE");
                    }
                    break;
                case TOP:
                    for (List<String> stringList : dataBaseService.getTopJokes()) {
                        try {
                            if (Boolean.parseBoolean(stringList.get(0))) {
                                execute(sendMessageService.createTopPhotoJokesMessage(update, stringList));
                            } else {
                                execute(sendMessageService.createTopTextJokesMessage(update, stringList));
                            }
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
                                System.out.println("CASE: MINE_1");
                            }
                        }
                    } else {
                        try {
                            execute(sendMessageService.createTextMessage(update, sendMessageService.registerFirstMessage));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: MINE_2");
                        }
                    }
                    break;
                case DELETE:
                    if (dataBaseService.isUserRegistered(update.getMessage().getFrom().getId())) {
                        try {
                            execute(sendMessageService.createDeletingAccountMessage(update));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: DELETE_1");
                        }
                    } else {
                        try {
                            execute(sendMessageService.createTextMessage(update, sendMessageService.deletingErrorMessage));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: DELETE_2");
                        }
                    }
                    break;
                default:
                    if (!stateStorageService.isEmpty() && stateStorageService.getJokeAdditionState(update.getMessage().getChatId())) {
                        if (!dataBaseService.addTextJoke(message.getFrom().getId(),
                                message.getFrom().getFirstName(),
                                message.getText())) {
                            try {
                                execute(sendMessageService.createTextMessage(update, sendMessageService.addJokePlagiarizedMessage));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                                System.out.println("CASE: default_1");
                            }
                        } else {
                            try {
                                execute(sendMessageService.createTextMessage(update, sendMessageService.addJokeSuccessMessage));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                                System.out.println("CASE: default_2");
                            }
                        }
                        stateStorageService.removeJokeAdditionState(update.getMessage().getChatId());
                    } else {
                        try {
                            execute(sendMessageService.createTextMessage(update, "\uD83D\uDE01"));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            System.out.println("CASE: default_3");
                        }
                    }
                    break;
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            String f_id = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElseThrow().getFileId();
            System.out.println(f_id);
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
