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

    public String getMessagesList(String k){
        
        if(!information.containsKey(k))
            return "No topic existing";
        
        String messagesList = "Messages:";

        int nMess = 0;
        for(Message m : information.get(k).getMessages()){
            messagesList += "\n" + m.toString();
            nMess++;
        }

        if(nMess==0)
            return "No messages sent";
        else
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

    public synchronized Message addMessage(String m, String key){

        if(!information.containsKey(key))
            information.put(key, new Topic(key));

        Message message = this.information.get(key).addMessage(m);

        if(subscribers.containsKey(key))
            for(ClientHandler ch : subscribers.get(key))
                ch.sendMessage(message);

        return message;
    }

    public synchronized void deleteMessage(int ID, String key){

        information.get(key).removeMessage(ID);

    }

    public synchronized void addTopic(String key){
        if(!information.containsKey(key)){
            information.put(key, new Topic(key));
            subscribers.put(key, new ArrayList<ClientHandler>());
        }
    }

    public boolean containsTopic(String key){
        return information.containsKey(key);
    }

}