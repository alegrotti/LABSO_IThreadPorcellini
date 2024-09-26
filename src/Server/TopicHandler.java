package Server;

import java.util.ArrayList;
import java.util.HashMap;
import Model.Topic;
import Model.Message;

public class TopicHandler {

    private HashMap<String, Topic> information;
    private HashMap<String, ArrayList<ClientHandler>> subscribers;

    public TopicHandler() {

        this.information = new HashMap<>();
        this.subscribers = new HashMap<>();

        subscribers = new HashMap<String, ArrayList<ClientHandler>>();

        subscribers.put("sport", new ArrayList<ClientHandler>());
        subscribers.put("travel", new ArrayList<ClientHandler>());
        subscribers.put("cinema", new ArrayList<ClientHandler>());

        information.put("sport",new Topic("sport"));
        information.put("travel",new Topic("travel"));
        information.put("cinema",new Topic("cinema"));
        
        String m1 = "Il mio sport preferito è il cricket";
        String m2 = "Siuuuuuuuuuuuuum";
        String m3 = "È un ciiiiiiiinema";

        information.get("sport").addMessage(m1);
        information.get("sport").addMessage(m2);
        information.get("cinema").addMessage(m3);

    }

    public String getMessagesList(String k) {
        String messagesList = "Messages:";

        for(Message m : information.get(k).getMessages())
            messagesList += "\n" + m.toString();

        return messagesList;
    }

    public String getTopicList() {
        String topicList = "Topics:";

        for(String s : information.keySet())
            topicList += "\n - " + s ;

        return topicList;
    }

    public synchronized void addSubscriber(String key, ClientHandler subscriber){
        this.subscribers.get(key).add(subscriber);
    }

    public synchronized Message addMessage(String m, String k){
        Message message = this.information.get(k).addMessage(m);

        for(ClientHandler ch : subscribers.get(k))
            ch.sendMessage(message);

        return message;
    }

    public synchronized void addTopic(String key){
        this.information.computeIfAbsent(key, Topic::new );
    }

}