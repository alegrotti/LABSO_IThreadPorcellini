package Model;

import java.util.Date;

public class Message {
    
    private int ID;
    private String text;
    private Date date;

    public Message(int ID, String text) {
        this.ID = ID;
        this.text = text;
        this.date = new Date();
    }

    public String getText() {
        return text;
    }

    public int getID(){
        return ID;
    }

    public Date getTimestamp() {
        return date;
    }

    @Override
    public String toString() {
        return "Messaggio " + ID + " - " + date + " : \n"+
                text;
    }
}
