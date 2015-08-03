package facetag.facetagmobileapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class User implements Parcelable, ListItem{
    private String userID;
    private String name;

    public User() {name = "EMPTY"; userID = "";}

    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(name);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel in) {
        userID = in.readString();
        name = in.readString();
    }

    public User(User user) {
        userID = user.getUserID();
        name = user.getName();
    }


    public boolean equalsUser(User u) {
        return (u.getUserID().equals(getUserID()));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    public User clone() {
        return new User(userID,name);
    }

}
