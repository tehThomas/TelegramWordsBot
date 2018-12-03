import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*created by Gaisin Artemiy & Dubrovina Yulia

 */

public class WordsBot extends TelegramLongPollingBot {

    private static ArrayList<String> allWords = new ArrayList<String>();
    private String prevWord = "";

    public String getBotUsername() {
        return "TWordsBot";
    }

    public String getBotToken() {
        return "633385226:AAFT9rj_lZABR4OkCbFnlcZjOMJWWain2aQ";
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            WordsBot bot = new WordsBot();
            telegramBotsApi.registerBot(bot);

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("resources/Words")));
            String line;
            while ((line = reader.readLine()) != null) {
                allWords.add(line);
                System.out.println(line);
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e ) {
            e.printStackTrace();
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }
    public void onUpdateReceived (Update update) {
        Message message = update.getMessage();
        String txt = message.getText();

        if (txt.equals("/start")) {

            File fl = new File("ChatID.txt");

            try {
                FileWriter writer = new FileWriter("resources/ChatID.txt", true);
                writer.append(message.getChatId().toString());
                writer.append('\r');
                writer.append('\n');
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            message.getChatId().toString();
            sendMsg(message, "Привет! Это бот для игры в слова.\n Добро пожаловать!\nВведи любое слово:");

        }
        else if (txt.equals("Я наигрался")) {
            sendMsg(message, "Спасибо,пока!");
            System.exit(-1);    //Доработать. Выяснить, как стопать бота не через System.exit.
        }

        else {
            if (checkWords(txt)) {
                sendMsg(message);
            }
            else {
                sendMsg(message, "Неверное слово. Слово должно начинаться с последней буквы введеного мной слова и существовать в русском языке!");
            }

        }

    }

    private boolean checkWords (String txt) {
        if (prevWord.equals("")) {
            return true;
        }
        String letterPrevWord = prevWord.substring(prevWord.length() - 1);
        String letter2PrevWord = prevWord.substring(prevWord.length() - 2, prevWord.length() - 1);

        if (letterPrevWord.equals(txt.substring(0, 1))) {
            return true;
        }
        else if (letter2PrevWord.equals(txt.substring(0, 1))) {
            return true;
        }
        else
            return false;
    }




    public void sendMsg (Message message, String txt) {
        SendMessage s = new SendMessage();
        s.setChatId(message.getChatId());
        s.setText(txt);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String chatID, String txt) {
        SendMessage s = new SendMessage();
        s.setChatId(chatID);
        s.setText(txt);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg (Message message) {
        SendMessage s = new SendMessage();
        String str = message.getText();

        s.setChatId(message.getChatId());

        String txt = findWord(str);

        if (!allWords.contains(txt)) {
            s.setText("Такого слова нет!");
        }
//        for (int i = 0; i < allWords.size(); i++) {
//            if (!allWords.contains(str.substring(str.length() - 1))) {
//                if (allWords.get(i).contains(str.substring(str.length() - 2))) {
//                    continue;
//                }
//                else
//                    s.setText("Введите слово на последнюю букву того, что написал я!");
//            }
//        }

        s.setText(txt);
        prevWord = txt;

        try {
            execute(s);
            allWords.remove(txt);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String findWord(String txt) {
//        char[] letters = txt.toCharArray();
        ArrayList<String> equelsWords = new ArrayList<String>();
        String letter = txt.substring(txt.length() - 1);
        String letter2 = txt.substring(txt.length() - 2, txt.length() - 1);
        for (int i = 0; i < allWords.size(); i++) {
            if (allWords.get(i).startsWith(letter)) {
                equelsWords.add(allWords.get(i));
            }
        }
        if (equelsWords.isEmpty()) {
            for (int i = 0; i < allWords.size(); i++) {
                if (allWords.get(i).startsWith(letter2)) {
                    equelsWords.add(allWords.get(i));
                }
            }
        }

        Random rnd = new Random();
        return equelsWords.get(rnd.nextInt(equelsWords.size()));
    }

}
