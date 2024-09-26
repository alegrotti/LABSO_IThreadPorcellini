package Model;

import java.util.LinkedList;
import java.util.List;

public class Topic {

    private String name;
    private List<Message> messages;
    private int ID;

    public Topic(String name) {
        this.name = name;
        this.messages = new LinkedList<>();
        this.ID = 1;
    }

    public String getName() {
        return name;
    }

    public int getCurrentID(){
        return ID;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message addMessage(String message) {
        Message m = new Message(message, ID);
        messages.add(m);
        ID++;
        return m;
    }

}
