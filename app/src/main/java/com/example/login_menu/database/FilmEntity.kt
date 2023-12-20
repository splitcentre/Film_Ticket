package com.example.login_menu.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "films")
data class FilmEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val filmImage: String = "",
    val filmName: String = "",
    val filmReleaseDate: Int = 0,
    val filmSynopsis: String = ""
) : Parcelable {

    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()?:0,
        parcel.readString() ?: "",
    )

    // Override writeToParcel method
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(filmImage)
        parcel.writeString(filmName)
        parcel.writeInt(filmReleaseDate) // This line is correct for writing an Int
        parcel.writeString(filmSynopsis)
    }

    // Parcelable CREATOR
    companion object CREATOR : Parcelable.Creator<FilmEntity> {
        override fun createFromParcel(parcel: Parcel): FilmEntity {
            return FilmEntity(parcel)
        }

        override fun newArray(size: Int): Array<FilmEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}
