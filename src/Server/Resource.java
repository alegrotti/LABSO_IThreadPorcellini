package Server;

import java.util.HashMap;
import Model.Topic;
import Model.Message;

public class Resource {

    private HashMap<String, Topic> information;

    public Resource() {
        this.information = new HashMap<>();
        information.put("sport",new Topic("sport"));
        information.put("travel",new Topic("travel"));
        information.put("cinema",new Topic("cinema"));
        
        Message m1 = new Message("Il mio sport preferito è il cricket");
        Message m2 = new Message("Siuuuuuuuuuuuuum");
        Message m3 = new Message("è un ciiiiiiiinema");

        information.get("sport").addMessage(m1);
        information.get("sport").addMessage(m2);
        information.get("cinema").addMessage(m3);

    }

    public String getMessagesList(String k) {
        String messagesList = "Messages: \n";

        for(Message m : information.get(k).getMessages())
            messagesList += "  - ID: " + m.getID() + "\n"
                +"    Text: " + m.getText() + "\n"
                +"    Date: " + m.getTimestamp() + "\n" ;

        return messagesList;
    }

    public String getTopicList() {
        String topicList = "";

        for(String s : information.keySet())
            topicList += " - " + s + "\n";

        return topicList;
    }

    public synchronized void addMessage(Message m, String k) throws InterruptedException {
        while (this.information.get(k) != null) {
            wait();
        }

        this.information.get(k).addMessage(m);
        notifyAll();
    }

    public synchronized void addTopic(String key) throws InterruptedException {
        while (this.information.get(key) != null) {
            wait();
        }

        this.information.computeIfAbsent(key, Topic::new );
        notifyAll();
    }

}