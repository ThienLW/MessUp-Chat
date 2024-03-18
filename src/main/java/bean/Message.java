package bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private String content = null;
    private String speaker = null;
    private String speakerFirstName = null;
    private String speakerSurname = null;
    private String timer = null;
    private ArrayList<String> imageList = null;

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getSpeakerFirstname(){
        return speakerFirstName;
    }
    public void setSpeakerFirstname(String speakerFirstName){
        this.speakerFirstName = speakerFirstName;
    }

    public String getSpeakerSurname(){
        return speakerSurname;
    }
    public void setSpeakerSurname(String speakerSurname){
        this.speakerSurname = speakerSurname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
