package Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Model.Topic;
import Model.Message;

public class TopicHandler {

    private HashMap<String, Topic> information;
    private HashMap<String, ArrayList<ClientHandler>> subscribers;
    private HashMap<String, ReentrantReadWriteLock> topicLocks;
    private ReentrantReadWriteLock editTopic;

    public TopicHandler() {

        this.information = new HashMap<>();
        this.subscribers = new HashMap<>();
        this.topicLocks = new HashMap<>();

        subscribers = new HashMap<String, ArrayList<ClientHandler>>();

        editTopic = new ReentrantReadWriteLock();

        subscribers.put("sport", new ArrayList<ClientHandler>());
        subscribers.put("travel", new ArrayList<ClientHandler>());
        subscribers.put("cinema", new ArrayList<ClientHandler>());

        information.put("sport",new Topic("sport"));
        information.put("travel",new Topic("travel"));
        information.put("cinema",new Topic("cinema"));
        
        topicLocks.put("sport", new ReentrantReadWriteLock());
        topicLocks.put("travel", new ReentrantReadWriteLock());
        topicLocks.put("cinema", new ReentrantReadWriteLock());

        String m1 = "Il mio sport preferito è il cricket";
        String m2 = "Siuuuuuuuuuuuuum";
        String m3 = "È un ciiiiiiiinema";

        information.get("sport").addMessage(m1);
        information.get("sport").addMessage(m2);
        information.get("cinema").addMessage(m3);

    }
    
    public boolean startInspection(String topic) {
        if (containsTopic(topic)) {
            topicLocks.get(topic).writeLock().lock();
            return true;
        } else {
            System.out.println("Topic does not exist");
            return false;
        }
    }

    public void endInspection(String topic) {
        topicLocks.get(topic).writeLock().unlock();
    }

    public String getMessagesList(String key) {
        topicLocks.get(key).readLock().lock();
        try {
            return retrieveMessagesList(key);
        } finally {
            topicLocks.get(key).readLock().unlock();
        }
    }

    private String retrieveMessagesList(String key) {
        String messagesList = "Messages:";
        int nMess = 0;

        for (Message m : information.get(key).getMessages()) {
            messagesList += "\n" + m.toString();
            nMess++;
        }
        
        if (nMess == 0) {
        	return "No messages have been sent on this topic";
        } else {
        	return nMess + " messages have been sent on this topic \n" + messagesList;
        }
    }

    public String getTopicList() {
        editTopic.readLock().lock();
        
        String topicList = "Topics:";
        for(String s : information.keySet())
            topicList += "\n - " + s ;
        
        editTopic.readLock().unlock();
        
        return topicList;
    }

    public synchronized void addSubscriber(String key, ClientHandler subscriber){
        addTopic(key);

        this.subscribers.get(key).add(subscriber);
    }

    public Message addMessage(String m, String key) {
        topicLocks.get(key).writeLock().lock();
        try {
            return createAndSendMessage(m, key);
        } finally {
            topicLocks.get(key).writeLock().unlock();
        }
    }
    
    private Message createAndSendMessage(String m, String key) {
    	Message message = this.information.get(key).addMessage(m);
        System.out.println("Message successfully added to the topic " + key);

        for (ClientHandler ch : subscribers.get(key))
            ch.sendMessage(message);
           
        return message;
    }
    
    public String getPublisherMessages(String topic, List<Message> publisherMessages) {
        topicLocks.get(topic).readLock().lock();
        try {
            return retrievePublisherMessages(topic, publisherMessages);
        } finally {
            topicLocks.get(topic).readLock().unlock();
        }
    }

    private String retrievePublisherMessages(String topic, List<Message> publisherMessages) {
        if (publisherMessages.size() == 0) {
        return "No messages sent by this publisher";
        }

        String stamp = "Messages of the publisher:";
        for (Message mess : publisherMessages) {
            stamp += "\n" + mess.toString();
        }

        return stamp.toString();
    }

    public void deleteMessage(int ID, String key){
    	topicLocks.get(key).writeLock().lock();
        try {
            if(information.get(key).removeMessage(ID))
                System.out.println("Message with ID " + ID + " successfully deleted from the topic " + key);
            else    
                System.out.println("Error: message with ID " + ID + " does not exist in the topic " + key);
        } finally {
            topicLocks.get(key).writeLock().unlock();
        }  
    }

    public void addTopic(String key){
        if(!containsTopic(key)){
            editTopic.writeLock().lock();
            if(!information.containsKey(key)){
                information.put(key, new Topic(key));
                subscribers.put(key, new ArrayList<ClientHandler>());
                topicLocks.put(key, new ReentrantReadWriteLock());
            }
            editTopic.writeLock().unlock();
        }
    }

    public boolean containsTopic(String key){
        editTopic.readLock().lock();
        boolean info = information.containsKey(key);
        editTopic.readLock().unlock();
        return info;
    }

    public void closeServer(){
        for (String topic : topicLocks.keySet())
            topicLocks.get(topic).writeLock().lock();
    }
}