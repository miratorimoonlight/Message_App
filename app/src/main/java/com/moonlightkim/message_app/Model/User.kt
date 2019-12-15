package com.moonlightkim.message_app.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Model/data class
@Parcelize
class User (val uid:String, val username:String, val profilePicUrl:String) : Parcelable{
    constructor() : this("", "", "")
}