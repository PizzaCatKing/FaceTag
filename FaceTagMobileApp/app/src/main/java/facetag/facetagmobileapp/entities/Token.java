package facetag.facetagmobileapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chris-ubuntu on 22/07/15.
 */
public class Token implements Parcelable {
    String token;
    String userID;


    public Token() {}
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeString(userID);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() {
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    private Token(Parcel in) {
        token = in.readString();
        userID = in.readString();
    }
}
