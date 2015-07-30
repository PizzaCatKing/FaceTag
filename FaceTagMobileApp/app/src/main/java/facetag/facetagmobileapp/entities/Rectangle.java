package facetag.facetagmobileapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chris_2 on 2015-07-29.
 */
public class Rectangle implements Parcelable {
    private String rectID;
    private String imageID;
    private String userID;
    private int x1;
    private int x2;
    private int y1;
    private int y2;


    public Rectangle() {}
    public Rectangle(Rectangle rectangle) {
        rectID = rectangle.getRectID();
        imageID = rectangle.getImageID();
        userID = rectangle.getUserID();

        x1 = rectangle.getX1();
        y1 = rectangle.getY1();
        x2 = rectangle.getX2();
        y2 = rectangle.getY2();
    }

    public String getRectID() {
        return rectID;
    }

    public boolean isInside(float x, float y){
        return (x > x1 && x < x2 && y > y1 && y < y2);
    }

    public void setRectID(String rectID) {
        this.rectID = rectID;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rectID);
        dest.writeString(imageID);
        dest.writeString(userID);
        dest.writeInt(x1);
        dest.writeInt(x2);
        dest.writeInt(y1);
        dest.writeInt(y2);

    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Rectangle> CREATOR = new Parcelable.Creator<Rectangle>() {
        public Rectangle createFromParcel(Parcel in) {
            return new Rectangle(in);
        }

        public Rectangle[] newArray(int size) {
            return new Rectangle[size];
        }
    };

    private Rectangle(Parcel in) {
        rectID = in.readString();
        imageID = in.readString();
        userID = in.readString();
        x1 = in.readInt();
        x2 = in.readInt();
        y1 = in.readInt();
        y2 = in.readInt();

    }

    @Override
    public String toString() {
        return x1 + "." + y1 + "." +  x2 + "." + y2;
    }
}
