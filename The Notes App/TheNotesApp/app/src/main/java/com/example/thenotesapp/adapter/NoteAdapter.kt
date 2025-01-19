package com.example.thenotesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thenotesapp.R
import com.example.thenotesapp.databinding.NoteLayoutBinding
import com.example.thenotesapp.model.Note

class NoteAdapter(private val onNoteClick: (Note) -> Unit, private val onFavoriteClick: (Note) -> Unit) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding: NoteLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemBinding.noteTitle.text = currentNote.noteTitle
        holder.itemBinding.noteDesc.text = currentNote.noteDesc
        holder.itemBinding.noteDate.text = currentNote.date

        if (currentNote.imageUris.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentNote.imageUris[0])
                .into(holder.itemBinding.noteImageView)
            holder.itemBinding.noteImageView.visibility = View.VISIBLE
        } else {
            holder.itemBinding.noteImageView.visibility = View.GONE
        }

        holder.itemBinding.favoriteIcon.setImageResource(if (currentNote.isFavorite) R.drawable.ic_star_2 else R.drawable.ic_star)

        holder.itemView.setOnClickListener {
            onNoteClick(currentNote)
        }

        holder.itemBinding.favoriteIcon.setOnClickListener {
            onFavoriteClick(currentNote)
        }
    }
}
