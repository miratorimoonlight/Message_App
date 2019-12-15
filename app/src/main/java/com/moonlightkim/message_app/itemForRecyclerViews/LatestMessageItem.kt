package com.moonlightkim.message_app.itemForRecyclerViews

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.moonlightkim.message_app.Model.ChatMessage
import com.moonlightkim.message_app.Model.User
import com.moonlightkim.message_app.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_item.view.*

class LatestMessageItem (val latestChat: ChatMessage, val dataID: String): Item<GroupieViewHolder>(){

    var userInfo: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_view_latest_chat.text = latestChat.text

        //get User INFO
        val chatPartnerID: String = dataID
        val dbRef = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerID")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                userInfo = p0.getValue(User::class.java)
                viewHolder.itemView.text_view_username_latest_chat.text = userInfo?.username

                //load image
                val targetView = viewHolder.itemView.image_view_latest_message
                Picasso.get().load(userInfo?.profilePicUrl).into(targetView)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}