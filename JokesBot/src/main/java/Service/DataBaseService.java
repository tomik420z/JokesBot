package Service;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.*;

import static Commands.Tags.tags;

public class DataBaseService {
    private static final MongoClient mongoClient;
    private static final MongoDatabase db;
    private static final MongoCollection<Document> jokesCollection;
    private static final MongoCollection<Document> usersCollection;

    private int id;

    static {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        db = mongoClient.getDatabase("DataJokes");
        jokesCollection = db.getCollection("DataTextJokes");
        usersCollection = db.getCollection("DataUser");
    }

    public int getJokeId() {
        return id;
    }

    public Document getRandomJoke(long userId) {
        Document user = usersCollection.find(new Document("id", userId)).first();
        if (user == null) {
            id = (int) (Math.random() * jokesCollection.countDocuments()) + 1;
            Document filter = new Document("id", id);
            Document res = jokesCollection.find(filter).first();
            return res;
        } else {
            return getJokeBasedOnFuzzyLogic(user);
        }
    }

    private Document getJokeBasedOnFuzzyLogic(Document user) {
        List<Double> closestTagArray = findClosestTagArray(user);

        List<Document> jokeDocsList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            jokeDocsList.add(jokesCollection.find(new Document("id", (int) (Math.random() * jokesCollection.countDocuments()) + 1)).first());
        }

        id = (int) jokeDocsList.get(0).get("id");

        int count = 0;
        for (Double aDouble : closestTagArray) {
            if (aDouble != 0)
                count++;
        }
        if (count == 0) {
            return jokeDocsList.get(0);
        } else {
            count = 0;
            while (count < 3) {
                count++;
                double max = findMax(closestTagArray);
                String tag = tags.get(closestTagArray.indexOf(max));
                closestTagArray.set(closestTagArray.indexOf(max), 0.0);
                for (Document jokeDoc : jokeDocsList) {
                    for (String jokeTag : (ArrayList<String>) jokeDoc.get("Type")) {
                        if (tag.equals(jokeTag)) {
                            id = (int) jokeDoc.get("id");
                            return jokeDoc;
                        }
                    }
                }
            }
        }

        return jokeDocsList.get(0);
    }

    private double findMax(List<Double> list) {
        double maxValue = -1.0;
        for (Double aDouble : list) {
            if (aDouble > maxValue)
                maxValue = aDouble;
        }
        return maxValue;
    }

    private double calculateDistance(ArrayList<Double> v1, ArrayList<Double> v2) {
        double sum = 0;
        for (int i = 0; i < v1.size(); i++) {
            sum += Math.pow(Math.abs(v1.get(i) - v2.get(i)), 2);
        }
        return Math.sqrt(sum);
    }

    private ArrayList<Double> findClosestTagArray(Document user) {
        ArrayList<Double> v1 = (ArrayList<Double>) user.get("Tags");
        double minDistance = Double.MAX_VALUE;
        ArrayList<Double> vecMinDistance = null;
        for (Document doc : usersCollection.find()) {
            if ((long) user.get("id") != (long) doc.get("id")) {
                ArrayList<Double> v2 = (ArrayList<Double>) doc.get("Tags");
                double tmp = calculateDistance(v1, v2);
                if (tmp < minDistance) {
                    minDistance = tmp;
                    vecMinDistance = v2;
                }
            }
        }
        return vecMinDistance == null ? v1 : vecMinDistance;
    }

    private void adjustUserTagValues(long userId, String userJokeRate, ArrayList<String> jokeTags) {
        double userTegRate = 0.0;
        switch (userJokeRate) {
            case "1" -> userTegRate = -2.0;
            case "2" -> userTegRate = -1.0;
            case "3" -> userTegRate = 0.5;
            case "4" -> userTegRate = 1.0;
            case "5" -> userTegRate = 2.0;
        }
        Document user = usersCollection.find(new Document("id", userId)).first();
        if (user != null) {
            List<Double> userTagArray = new ArrayList<>((ArrayList<Double>) user.get("Tags"));
            for (String tag : jokeTags) {
                if (userTagArray.get(tags.indexOf(tag)) > 0.0)
                    userTagArray.set(tags.indexOf(tag),
                            userTagArray.get(tags.indexOf(tag)) + userTegRate); // /2
                else
                    userTagArray.set(tags.indexOf(tag), userTegRate);
            }
            for (int i = 0; i < userTagArray.size(); i++) {
                if (userTagArray.get(i) < 0.0)
                    userTagArray.set(i, 0.0);
            }
            usersCollection.updateOne(user, new Document("$set", new Document("Tags", userTagArray)));
        }
    }

    public void changeJokeRating(int jokeId, String userJokeRate, long userId) {
        Document filter = new Document("id", jokeId);
        Document res = jokesCollection.find(filter).first();
        int prev = (int) res.get("Rating");
        adjustUserTagValues(userId, userJokeRate, (ArrayList<String>) res.get("Type")); // - rating
        jokesCollection.updateOne(res,
                new Document("$set", new Document("Rating", prev + Integer.parseInt(userJokeRate))));
    }

    private Document initializeJokesDoc(long userId, String userName, String text) {
        int jokesAmount = (int) jokesCollection.countDocuments() + 1;
        Document doc = new Document();
        doc.append("id", jokesAmount);
        List<String> initTag = new ArrayList<>(List.of(
                "??????????????????"
        ));
        doc.append("Type", initTag);
        doc.append("Joke", text);
        doc.append("Rating", 0);
        doc.append("Author", userName);
        doc.append("AuthorId", userId);
        doc.append("isPhoto", false);
        return doc;
    }

    public boolean addTextJoke(long userId, String userName, String text) {
        Document filter = jokesCollection.find(new Document("Joke", text)).first();
        if (filter == null) {
            jokesCollection.insertOne(initializeJokesDoc(userId, userName, text));
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<String> ParseToString(FindIterable<Document> listDoc) {
        ArrayList<String> list = new ArrayList<>();
        for (Document el : listDoc) {
            list.add(el.get("Joke") + "\uD83D\uDCA0" + el.get("Rating"));
        }
        return list;
    }

    public ArrayList<String> getAllAuthorJokes(long userId) {
        return ParseToString(jokesCollection.find(new Document("AuthorId", userId)));
    }

    public boolean isUserRegistered(long userId) {
        return usersCollection.find(new Document("id", userId)).first() != null;
    }

    private ArrayList<ArrayList<String>> convertToArrayString(ArrayList<Document> documentList) {
        ArrayList<ArrayList<String>> arrayString = new ArrayList<>(3);
        for (Document doc : documentList) {
            arrayString.add(new ArrayList<>(
                    List.of(doc.get("isPhoto").toString(), doc.get("Joke").toString(), doc.get("Rating").toString())));
        }
        return arrayString;
    }

    public ArrayList<ArrayList<String>> getTopJokes() {
        return convertToArrayString(jokesCollection.find()
                .sort(new Document("Rating", -1)).limit(3).into(new ArrayList<>()));
    }

    private Document initializeDocument(long userId, String userName) {
        Document doc = new Document();
        doc.append("id", userId);
        doc.append("Name", userName);
        List<Double> listTag = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            listTag.add(0.0);
        }
        doc.append("Tags", listTag);
        return doc;
    }

    public boolean registerUser(long userId, String userName) {
        Document res = usersCollection.find(new Document("id", userId)).first();
        if (res == null) {
            Document doc = initializeDocument(userId, userName);
            usersCollection.insertOne(doc);
            return false;
        }
        return true;
    }

    private void correctIndex() {
        int i = 1;
        for (Document doc : jokesCollection.find()) {
            jokesCollection.updateOne(doc, new Document("$set", new Document("id", i)));
            i++;
        }
    }

    public void deleteUserWithJokes(long userId) {
        jokesCollection.deleteMany(new Document("AuthorId", userId));
        correctIndex();
        usersCollection.deleteOne(new Document("id", userId));
    }

    public void deleteUserWithoutJokes(long userId) {
        usersCollection.deleteOne(new Document("id", userId));
    }
}
