package com.moonlightkim.message_app.itemForRecyclerViews

import com.moonlightkim.message_app.Model.User
import com.moonlightkim.message_app.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*

// Some set up from Groupie - It's like setting up each cell (viewHolder and its ITEM) to be added into adapter.
class UserItem(val user : User): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_text.text = user.username

        Picasso.get().load(user.profilePicUrl).into(viewHolder.itemView.image_view_new_message)
    }
}