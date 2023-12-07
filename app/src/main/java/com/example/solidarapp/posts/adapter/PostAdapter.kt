package com.example.solidarapp.posts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.solidarapp.R

class PostAdapter(private var PostList : List<Post>) : RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostViewHolder(layoutInflater.inflate(R.layout.posts_structure, parent, false))
    }

    override fun getItemCount(): Int {
        return PostList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = PostList[position]
        holder.render(item)

    }

    fun updateData(newPostList: List<Post>) {
        PostList = newPostList
        notifyDataSetChanged()
    }
}