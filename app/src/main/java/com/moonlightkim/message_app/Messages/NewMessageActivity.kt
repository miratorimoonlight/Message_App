package com.moonlightkim.message_app.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.moonlightkim.message_app.R
import com.moonlightkim.message_app.Model.User
import com.moonlightkim.message_app.itemForRecyclerViews.UserItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*


class NewMessageActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        //link adapter to the recycler_view
        recycler_view_new_message.adapter = adapter
        supportActionBar?.title = "Select User"
        my_username_newmessage.text = "My Username: " + LatestMessagesActivity.loggedInUser?.username

        fetchUsers()
    }

    //get users data from FireBase DB
    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if(user != null)
                        adapter.add(UserItem(user))
                }

                //set click listener on each ITEM - to go to Chat Log screen when clicked
                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    val userInfo = item as UserItem
                    intent.putExtra("userInfo", userInfo.user)
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}
