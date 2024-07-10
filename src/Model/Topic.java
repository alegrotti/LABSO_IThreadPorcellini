package Model;

import java.util.ArrayList;
import java.util.List;

public class Topic {

    private String nome;
    private List<Message> messaggi;

    public Topic(String nome) {
        this.nome = nome;
        this.messaggi = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public List<Message> getMessaggi() {
        return messaggi;
    }

    public void addMessaggio(Message messaggio) {
        messaggi.add(messaggio);
    }

    @Override
    public String toString() {
        String text = "Topic " + nome + " : \n";

        for (Message m : messaggi)
            text+=m.toString()+"\n";
        
        return text;        
    }

}
