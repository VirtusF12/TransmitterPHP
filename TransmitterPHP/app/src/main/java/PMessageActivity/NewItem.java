package PMessageActivity;

import android.widget.ImageView;

public final class NewItem {

    private String name;
    private String time;
    private String message;
    private ImageView photo;

    public NewItem (String name, String time, String message){
        this.name = name;
        this.time = time;
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public ImageView getPhoto() {
        return photo;
    }
}
