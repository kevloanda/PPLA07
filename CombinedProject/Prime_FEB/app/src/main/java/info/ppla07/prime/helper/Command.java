package info.ppla07.prime.helper;

import android.os.Parcel;
import android.os.Parcelable;

public class Command implements Parcelable {

    private String content;
    public Command (String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
    }

    protected Command(Parcel in) {
        this.content = in.readString();
    }

    public static final Creator<Command> CREATOR = new Creator<Command>() {
        @Override
        public Command createFromParcel(Parcel in) {
            return new Command(in);
        }

        @Override
        public Command[] newArray(int size) {
            return new Command[size];
        }
    };
}