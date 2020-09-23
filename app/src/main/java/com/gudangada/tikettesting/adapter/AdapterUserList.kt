package com.gudangada.tikettesting.adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import com.squareup.picasso.Picasso

import com.gudangada.tikettesting.R
import com.gudangada.tikettesting.model.User
import com.gudangada.tikettesting.view.EdittextConfig

import kotlinx.android.synthetic.main.user_list.view.*


class AdapterUserList(var userList: MutableList<User>, var userListFiltered: MutableList<User>) : RecyclerView.Adapter<AdapterUserList.ViewHolder>(), Filterable {

    private fun loadIntent() {

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullName: AppCompatTextView = itemView.textFullName
        val photo: ImageView = itemView.imagePhoto
        val layoutParent: ConstraintLayout = itemView.parentLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userListFiltered[position]

        Picasso.get().load(user.avatarUrl)
            .resize(40, 40).centerCrop()
            .into(holder.photo)

        holder.fullName.text = user.login
        holder.layoutParent.setOnClickListener{
            loadIntent()
        }
    }

    override fun getItemCount(): Int {
        return userListFiltered.size
    }

    fun clear() {
        userList.clear()
        userListFiltered.clear()
        notifyDataSetChanged()
    }

    fun addAll(list: List<User>) {
        userList.addAll(list)
        userListFiltered.addAll(list)
        notifyDataSetChanged()
    }

    fun add(user: User) {
        userList.add(user)
        userListFiltered.add(user)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()

                userListFiltered = if (charString.isEmpty()) {
                    userList
                } else {
                    val filteredList : MutableList<User> = arrayListOf()
                    for (row in userList) {
                        if (row.login!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }

                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = userListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                userListFiltered = filterResults.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }


}