package Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import Model.Topic;
import Model.Message;

public class TopicHandler {

    private HashMap<String, Topic> information;
    private HashMap<String, ArrayList<ClientHandler>> subscribers;
    private Lock inspectLock;
    private String topicInInspect; // topic che è in sessione interattiva

    public TopicHandler() {

        this.information = new HashMap<>();
        this.subscribers = new HashMap<>();
        this.inspectLock = new ReentrantLock();
        this.topicInInspect = null;

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
    
    public synchronized boolean isTopicInInspect(String topic) {
        return topic.equals(this.topicInInspect);
    }

    public synchronized void startInspection(String topic) {
        this.topicInInspect = topic;
        inspectLock.lock(); // blocca il lock per il topic in ispezione
    }

    public synchronized void endInspection() {
        this.topicInInspect = null;
        inspectLock.unlock(); // sblocca il lock per il topic al termine della sessione interattiva
    }

    public String getMessagesList(String key) {
        if (isTopicInInspect(key)) {
        	// il thread resta bloccato in attesa che il lock venga rilasciato dal server al termine della sessione interattiva 
        	// e poi lo acquisisce
            inspectLock.lock(); 
            try {
                return retrieveMessagesList(key);
            } finally {
                inspectLock.unlock(); // sblocca il lock (in ogni caso) dopo che la lista di messaggi è stata recuperata
            }
        } else {
            // Se il topic non è in ispezione, ritorna semplicemente i messaggi
            return retrieveMessagesList(key);
        }
    }

    private String retrieveMessagesList(String key) {
        if (!information.containsKey(key)) {
            return "No topic existing";
        }

        String messagesList = "Messages:";
        int nMess = 0;

        for (Message m : information.get(key).getMessages()) {
            messagesList += "\n" + m.toString();
            nMess++;
        }
        
        if (nMess == 0) {
        	return "Sono stati inviati 0 messaggi in questo topic";
        } else {
        	return "Sono stati inviati " + nMess + " messaggi in questo topic \n" + messagesList;
        }
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

    public Message addMessage(String m, String key) {
        if (isTopicInInspect(key)) {
            inspectLock.lock();  // attende il rilascio del lock
            try {
            	return createAndSendMessage(m, key);
            } finally {
                inspectLock.unlock(); // rilascia il lock dopo l'operazione
            }
        } else {
        	return createAndSendMessage(m, key);
        }
    }
    
    private Message createAndSendMessage(String m, String key) {
    	if (!information.containsKey(key))
            information.put(key, new Topic(key));

        Message message = this.information.get(key).addMessage(m);

        if (subscribers.containsKey(key))
            for (ClientHandler ch : subscribers.get(key))
                ch.sendMessage(message);

        return message;
    }
    
    public String getPublisherMessages(String topic, List<Message> publisherMessages) {
        if (isTopicInInspect(topic)) {
            inspectLock.lock();
            try {
                return retrievePublisherMessages(topic, publisherMessages);
            } finally {
                inspectLock.unlock();
            }
        } else {
            return retrievePublisherMessages(topic, publisherMessages);
        }
    }

    private String retrievePublisherMessages(String topic, List<Message> publisherMessages) {
        if (!information.containsKey(topic)) {
            return "No topic existing";
        }

        if (publisherMessages.size() == 0) {
            return "No messages sent by this publisher";
        }

        StringBuilder stamp = new StringBuilder("Messages of the publisher:");
        for (Message mess : publisherMessages) {
            stamp.append("\n").append(mess.toString());
        }

        return stamp.toString();
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