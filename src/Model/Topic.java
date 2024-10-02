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
        return messages;
    }

    public void removeMessage(int ID) {
        int pos = -1;
        
        for(int i = 0; i < messages.size(); i++)
            if(messages.get(i).getID()==ID){
                pos = i;
                break;
            }

        if(pos != -1)
            messages.remove(pos);
    }

    public Message addMessage(String message) {
        Message m = new Message(message, ID);
        messages.add(m);
        ID++;
        return m;
    }

}
