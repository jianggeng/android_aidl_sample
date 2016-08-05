package com.jgeng.aidlserver;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by jgeng on 8/3/16.
 */

public class Data implements Parcelable {
    private String name;

    public Data(int id){
      name = new String("data " + id);
    }

    public Data(Parcel parcel){
      name = parcel.readString();
    }

    public String getName() {
      return name;
    }

    public int describeContents() {
      return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
      parcel.writeString(name);
    }

    public static final Parcelable.Creator<Data> CREATOR = new Creator<Data>() {
      public Data createFromParcel(Parcel source) {

        return new Data(source);
      }
      public Data[] newArray(int size) {
        return new Data[size];
      }
    };
  }
