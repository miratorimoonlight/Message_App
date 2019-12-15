package com.moonlightkim.message_app.itemForRecyclerViews

import com.moonlightkim.message_app.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_of_me_item.view.*

// to show on the right
class ChatOfMeItem (val text: String, val userProfileUrl: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val targetImageView = viewHolder.itemView.image_view_chat_of_me

        viewHolder.itemView.text_of_chat_to.text = text
        Picasso.get().load(userProfileUrl).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_of_me_item
    }
}