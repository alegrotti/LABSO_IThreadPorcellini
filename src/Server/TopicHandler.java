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
        this.editTopic = new ReentrantReadWriteLock();

        subscribers.put("sport", new ArrayList<>());
        subscribers.put("travel", new ArrayList<>());
        subscribers.put("cinema", new ArrayList<>());

        information.put("sport", new Topic("sport"));
        information.put("travel", new Topic("travel"));
        information.put("cinema", new Topic("cinema"));

        topicLocks.put("sport", new ReentrantReadWriteLock());
        topicLocks.put("travel", new ReentrantReadWriteLock());
        topicLocks.put("cinema", new ReentrantReadWriteLock());

        information.get("sport").addMessage("My favourite sport is cricket");
        information.get("sport").addMessage("Forza Olimpia Castello!");
        information.get("cinema").addMessage("My favourite film is 'Pulp Fiction'");
    }

    public boolean startInspection(String topic) throws InterruptedException{
        if (containsTopic(topic)) {
            topicLocks.get(topic).writeLock().lock();
            return true;
        } else {
            System.out.println("Topic does not exist");
            return false;
        }
    }

    public void endInspection(String topic) {
        if (topicLocks.containsKey(topic)) {
            topicLocks.get(topic).writeLock().unlock();
        }
    }

    public String getMessagesList(String key) throws InterruptedException{
        topicLocks.get(key).readLock().lockInterruptibly();
        try {
            return retrieveMessagesList(key);
        } finally {
            topicLocks.get(key).readLock().unlock();
        }
    }

    private String retrieveMessagesList(String key) {
        StringBuilder messagesList = new StringBuilder("Messages:");
        int nMess = 0;

        for (Message m : information.get(key).getMessages()) {
            messagesList.append("\n").append(m.toString());
            nMess++;
        }

        return nMess == 0 ? "No messages have been sent on this topic" 
                          : nMess + " messages have been sent on this topic \n" + messagesList;
    }

    public String getTopicList() throws InterruptedException{
        editTopic.readLock().lockInterruptibly();
        try {
            StringBuilder topicList = new StringBuilder("Topics:");
            for (String s : information.keySet()) {
                topicList.append("\n - ").append(s);
            }
            return topicList.toString();
        } finally {
            editTopic.readLock().unlock();
        }
    }

    public synchronized void addSubscriber(String key, ClientHandler subscriber) throws InterruptedException{
        addTopic(key);
        this.subscribers.get(key).add(subscriber);
        notifyAll();
    }

    public Message addMessage(String m, String key) throws InterruptedException{
        topicLocks.get(key).writeLock().lockInterruptibly();
        try {
            return createAndSendMessage(m, key);
        } finally {
            topicLocks.get(key).writeLock().unlock();
        }
    }

    private Message createAndSendMessage(String m, String key) {
        Message message = this.information.get(key).addMessage(m);
        System.out.println("Message successfully added to the topic " + key);

        for (ClientHandler ch : subscribers.get(key)) {
            ch.sendMessage(message);
        }

        return message;
    }

    public String getPublisherMessages(String topic, List<Message> publisherMessages) throws InterruptedException{
        topicLocks.get(topic).readLock().lockInterruptibly();
        try {
            return retrievePublisherMessages(topic, publisherMessages);
        } finally {
            topicLocks.get(topic).readLock().unlock();
        }
    }

    private String retrievePublisherMessages(String topic, List<Message> publisherMessages) {
        if (publisherMessages.isEmpty()) {
            return "No messages sent by this publisher";
        }

        StringBuilder stamp = new StringBuilder("Messages of the publisher:");
        for (Message mess : publisherMessages) {
            stamp.append("\n").append(mess.toString());
        }
        return stamp.toString();
    }

    public void deleteMessage(int ID, String key) throws InterruptedException{
        topicLocks.get(key).writeLock().lockInterruptibly();
        try {
            if (information.get(key).removeMessage(ID)) {
                System.out.println("Message with ID " + ID + " successfully deleted from the topic " + key);
            } else {
                System.out.println("Error: message with ID " + ID + " does not exist in the topic " + key);
            }
        } catch (Exception e) {
            System.err.println("[TopicHandler] Error deleting message: " + e.getMessage());
        } finally {
            topicLocks.get(key).writeLock().unlock();
        }
    }

    public void addTopic(String key) throws InterruptedException{
        if (!containsTopic(key)) {
            editTopic.writeLock().lockInterruptibly();
            try {
                if (!information.containsKey(key)) {
                    information.put(key, new Topic(key));
                    subscribers.put(key, new ArrayList<>());
                    topicLocks.put(key, new ReentrantReadWriteLock());
                }
            } finally {
                editTopic.writeLock().unlock();
            }
        }
    }

    public boolean containsTopic(String key) throws InterruptedException{
        editTopic.readLock().lockInterruptibly();
        try {
            return information.containsKey(key);
        } finally {
            editTopic.readLock().unlock();
        }
    }

    /*
    public void closeServer() {
        for (ReentrantReadWriteLock lock : topicLocks.values()) {
            lock.writeLock().lock();
        }
    }
    */
}
