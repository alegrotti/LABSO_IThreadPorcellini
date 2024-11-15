package Model;

import java.util.ArrayList;
import java.util.List;

public class Topic {

    private String name;
    private List<Message> messages;
    private int ID;

    public Topic(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
        this.ID = 1;
    }

    public String getName() {
        return name;
    }

    public int getCurrentID(){
        return ID;
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public boolean removeMessage(int ID) {
        return messages.removeIf(m -> m.getID() == ID);
    }

    public Message addMessage(String message) {
        Message m = new Message(message, ID);
        messages.add(m);
        ID++;
        return m;
    }
}
