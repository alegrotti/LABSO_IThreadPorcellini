package Model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    
    private int ID;
    private String text;
    private String date;

    public Message(String text, int ID) { 
        this.text = text;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - H:m:s");
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
        String printString = "";
        printString += "  - ID: " + getID() + "\n"
                    + "    Text: " + getText() + "\n"
                    + "    Date: " + getTimestamp() ;
        return printString;
    }
}
