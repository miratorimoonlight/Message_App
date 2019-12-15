package com.moonlightkim.message_app.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.moonlightkim.message_app.R
import com.moonlightkim.message_app.LoginStuff.RegisterActivity
import com.moonlightkim.message_app.Model.ChatMessage
import com.moonlightkim.message_app.Model.User
import com.moonlightkim.message_app.itemForRecyclerViews.LatestMessageItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object{
        var loggedInUser: User? = null
        var loggedInUserID: String? = null
    }
    private val adapter = GroupAdapter<GroupieViewHolder>()

    //used for refreshing recycler view instead of adding item more and more
    val latestChatMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        recycler_view_latest_message.adapter = adapter
        recycler_view_latest_message.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            val row = item as LatestMessageItem
            val intent = Intent(this, ChatLogActivity::class.java)

            intent.putExtra("userInfo", row.userInfo)
            startActivity(intent)
        }

        //no need to relogin if alr logged in once
        verifyUserIsLoggedIn()
        fetchLoggedInUser()

        listenForLatestMessages()
    }

    private fun verifyUserIsLoggedIn() {
        loggedInUserID = FirebaseAuth.getInstance().uid

        if (loggedInUserID == null)
        {
            Log.d("LatestMessagesActivity", "Not logged in yet")
            val intentToRegister = Intent(this, RegisterActivity::class.java)
            intentToRegister.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentToRegister)
        }
    }

    private fun fetchLoggedInUser() {
        if (loggedInUserID != null){
            Log.d("LatestMessagesActivity", "Get logged in user DATA")
            val dbRef = FirebaseDatabase.getInstance().getReference("/users/$loggedInUserID")

            dbRef.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    Log.d("Meme", p0.toString())
                    loggedInUser = p0.getValue(User::class.java)
                    //Display username on screen
                    my_user_name.text = "My Username: " + loggedInUser?.username
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
    }

    private fun listenForLatestMessages(){
        //my, me means logged-in user
        val myID = FirebaseAuth.getInstance().uid
        val dbRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$myID")

        dbRef.addChildEventListener(object : ChildEventListener{

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                //?: return - if latestChat is null, return
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestChatMap[p0.key!!] = chatMessage
                refreshRecyclerView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                //?: return - if latestChat is null, return
                val latestChat = p0.getValue(ChatMessage::class.java) ?: return
                latestChatMap[p0.key!!] = latestChat
                refreshRecyclerView()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
        })
    }

    private fun refreshRecyclerView(){
        adapter.clear()

        //sort the chat Map based on timestamp
        val sortedHashMap = latestChatMap.toList().sortedByDescending { (_, value) -> value.timeStamp}.toMap()

        sortedHashMap.forEach {(dataID, chat) ->
            adapter.add(LatestMessageItem(chat, dataID))
        }
    }



    //to create nav_bar and put on latest_message screen
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                //sign out
                FirebaseAuth.getInstance().signOut()
                //get back to login
                val intentToLogin = Intent(this, RegisterActivity::class.java)
                intentToLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentToLogin)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        Log.d("Destroy Activity", "LatestMessages Activity destroyed")
        super.onDestroy()
    }
}
