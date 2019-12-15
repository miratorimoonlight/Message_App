package com.moonlightkim.message_app.Messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.moonlightkim.message_app.Model.ChatMessage
import com.moonlightkim.message_app.Model.User
import com.moonlightkim.message_app.R
import com.moonlightkim.message_app.itemForRecyclerViews.ChatOfMeItem
import com.moonlightkim.message_app.itemForRecyclerViews.ChatOfThemItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {
    companion object{
        val TAG = "ChatLogActivity"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        chat_log_recycler_view.adapter = adapter


        //set actionbar title with username from NewMessage
        val userThemInfo = intent.getParcelableExtra<User>("userInfo")
        supportActionBar?.title = userThemInfo.username

        send_message_button.setOnClickListener {
            Log.d(TAG, "attempt to send message")
            performSendingMessageTo(userThemInfo)
        }

        listenForMessageFrom(userThemInfo)
    }

    //save message to Firebase
    private fun performSendingMessageTo(userThemInfo: User){
        val messageToSend = enter_message_field.text.toString()
        if (messageToSend == "") return
        enter_message_field.text.clear()
        val fromID = FirebaseAuth.getInstance().uid
        val toID = userThemInfo.uid

        if (fromID == null) return

        val dbRef = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage = ChatMessage(dbRef.key!!, messageToSend, fromID, toID, System.currentTimeMillis()/1000)

        //me, my means logged_in user
        val myID = fromID
        val theirID = toID
        val refForLatestMessage = FirebaseDatabase.getInstance().getReference("/latest-messages/$myID/$theirID")
        val refForlatestMessageOpposite = FirebaseDatabase.getInstance().getReference("/latest-messages/$theirID/$myID")

        dbRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "message saved in DB")
                //to see the last message after it is sent from ME
                chat_log_recycler_view.scrollToPosition(adapter.itemCount - 1)
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }

        refForLatestMessage.setValue(chatMessage)
        refForlatestMessageOpposite.setValue(chatMessage)

    }
    private fun listenForMessageFrom(userInfo: User)
    {
        val dbRef = FirebaseDatabase.getInstance().getReference("/messages")

        //reference: https://kotlinlang.org/docs/reference/object-declarations.html
        dbRef.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            //is called when NEW DATA is added under /messages or when THERE is DATA under /messages
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessageObj = p0.getValue(ChatMessage::class.java)
                Log.d("onChildAdded called", "$adapter.itemCount")
                if (chatMessageObj != null){
                    //if  fromID == logged-in user and toID == userThem
                    if (chatMessageObj.fromID == LatestMessagesActivity.loggedInUserID && chatMessageObj.toID == userInfo.uid){
                        val senderProfilePicUrl = LatestMessagesActivity.loggedInUser?.profilePicUrl
                        adapter.add(ChatOfMeItem(chatMessageObj.text, senderProfilePicUrl!!))
                    }
                    else if (chatMessageObj.fromID == userInfo.uid && chatMessageObj.toID == LatestMessagesActivity.loggedInUserID)
                        adapter.add(ChatOfThemItem(chatMessageObj.text, userInfo.profilePicUrl))
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }
}



