package Model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    
    private int ID;
    private String text;
    private String date;

    public Message(String text, int ID) { 
        this.text = text;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        this.date = dateFormat.format(new Date());
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public int getID(){
        return ID;
    }

    public String getTimestamp() {
        return date;
    }

    @Override
    public String toString() {
        StringBuilder printString = new StringBuilder();
        printString.append("  - ID: ").append(getID()).append("\n")
                .append("    Text: ").append(getText()).append("\n")
                .append("    Date: ").append(getTimestamp());
        return printString.toString();
    }
}
