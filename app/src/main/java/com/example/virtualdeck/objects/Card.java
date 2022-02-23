package com.example.virtualdeck.objects;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import kotlin.Metadata;

public class Card implements Parcelable {

    protected Card(Parcel in) {
        m_CardName = in.readString();
        m_Picture = in.readParcelable(Bitmap.class.getClassLoader());
        m_CardUUID = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(m_CardName);
        parcel.writeParcelable(m_Picture, i);
        parcel.writeString(m_CardUUID);
    }

    public static class CardMetadata implements Parcelable{
        private String m_Game;
        private String m_GameCollection;
        private String m_CardDescription;
        private String m_PrintSeries;
        private String m_ExtraInfo;

        public CardMetadata(String m_Game, String m_GameCollection, String m_CardDescription, String m_PrintSeries, String m_ExtraInfo) {
            this.m_Game = m_Game;
            this.m_GameCollection = m_GameCollection;
            this.m_CardDescription = m_CardDescription;
            this.m_PrintSeries = m_PrintSeries;
            this.m_ExtraInfo = m_ExtraInfo;
        }

        protected CardMetadata(Parcel in) {
            m_Game = in.readString();
            m_GameCollection = in.readString();
            m_CardDescription = in.readString();
            m_PrintSeries = in.readString();
            m_ExtraInfo = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(m_Game);
            dest.writeString(m_GameCollection);
            dest.writeString(m_CardDescription);
            dest.writeString(m_PrintSeries);
            dest.writeString(m_ExtraInfo);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<CardMetadata> CREATOR = new Creator<CardMetadata>() {
            @Override
            public CardMetadata createFromParcel(Parcel in) {
                return new CardMetadata(in);
            }

            @Override
            public CardMetadata[] newArray(int size) {
                return new CardMetadata[size];
            }
        };

        public String getGame() {
            return m_Game;
        }

        public String getGameCollection() {
            return m_GameCollection;
        }

        public String getCardDescription() {
            return m_CardDescription;
        }

        public String getPrintSeries() {
            return m_PrintSeries;
        }

        public String getExtraInfo() {
            return m_ExtraInfo;
        }
    }

    private CardMetadata m_Metadata;
    private String m_CardName;
    private Bitmap m_Picture;
    private String m_CardUUID;

    public Card(CardMetadata m_Metadata, String m_CardName, Bitmap m_Picture, String m_CardUUID) {
        this.m_Metadata = m_Metadata;
        this.m_CardName = m_CardName;
        this.m_Picture = m_Picture;
        this.m_CardUUID = m_CardUUID;
    }

    public CardMetadata getMetadata() {
        return m_Metadata;
    }

    public String getCardName() {
        return m_CardName;
    }

    public Bitmap getPicture() {
        return m_Picture;
    }

    public String getCardUUID() {
        return m_CardUUID;
    }

    public void setCardMetadata(CardMetadata metadata) { m_Metadata = metadata; }
}
