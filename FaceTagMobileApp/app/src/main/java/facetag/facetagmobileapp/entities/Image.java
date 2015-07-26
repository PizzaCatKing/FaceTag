package facetag.facetagmobileapp.entities;

import android.os.Parcel;
import android.os.Parcelable;


public class Image implements Parcelable {
    String imageID;
    String ownerID;
    String title;
    String base64Image;

    public Image(){}

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageID);
        parcel.writeString(ownerID);
        parcel.writeString(title);
        parcel.writeString(base64Image);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    private Image(Parcel in) {
        imageID = in.readString();
        ownerID = in.readString();
        title = in.readString();
        base64Image = in.readString();
    }
    @Override
    public String toString() {
        return title;
    }
}
