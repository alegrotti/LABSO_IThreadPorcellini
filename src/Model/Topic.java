package Model;

import java.util.LinkedList;
import java.util.List;

public class Topic {

    private String name;
    private List<Message> messages;

    public Topic(String name) {
        this.name = name;
        this.messages = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public String toString() {
        String text = "Topic " + name + " : \n";

        for (Message m : messages)
            text+=m.toString()+"\n";
        
        return text;        
    }

}
