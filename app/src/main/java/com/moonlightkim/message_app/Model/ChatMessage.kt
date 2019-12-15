package com.moonlightkim.message_app.Model

//fromID - sender of message - currently auth user
//toID - receiver of message

class ChatMessage(val id: String, val text: String, val fromID: String, val toID: String, val timeStamp: Long)
{
    constructor() : this("", "", "", "", -1)
}