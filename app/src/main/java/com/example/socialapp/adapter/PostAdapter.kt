package com.example.socialapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.Utils
import com.example.socialapp.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter (private val options:FirestoreRecyclerOptions<Post>,private val iPostAdapter: IPostAdapter):FirestoreRecyclerAdapter<Post,PostAdapter.PostViewHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))
        viewHolder.likedButton.setOnClickListener {
            iPostAdapter.onLikedClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.userName.text=model.createdBy.displayName
        holder.createdAtText.text= Utils.getTimeAgo(model.createdAt)
        holder.likedCount.text=model.likedBy.size.toString()
        holder.postText.text=model.text
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)

        val auth=Firebase.auth
        val currentUserId=auth.currentUser!!.uid
        val isLiked=model.likedBy.contains(currentUserId)
        if(isLiked)
            holder.likedButton.setImageDrawable(ContextCompat.getDrawable(holder.likedButton.context,R.drawable.ic_liked))
        else
            holder.likedButton.setImageDrawable(ContextCompat.getDrawable(holder.likedButton.context,R.drawable.ic_unliked))
    }

    inner class PostViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val userImage: ImageView=itemView.findViewById(R.id.userImage)
        val likedButton: ImageView=itemView.findViewById(R.id.likeButton)
        val userName: TextView =itemView.findViewById(R.id.userName)
        val createdAtText:TextView=itemView.findViewById(R.id.createdAt)
        val postText:TextView=itemView.findViewById(R.id.postText)
        val likedCount:TextView=itemView.findViewById(R.id.likeCount)
    }
    interface IPostAdapter{
        fun onLikedClicked(postId:String)
    }
}