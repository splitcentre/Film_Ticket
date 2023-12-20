package com.example.login_menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.login_menu.database.FilmEntity
import com.example.login_menu.databinding.FilmCardBinding

class FilmCardAdapter(private val onItemClick: (FilmEntity) -> Unit) :
    RecyclerView.Adapter<FilmCardAdapter.FilmCardViewHolder>() {

    private var filmList: List<FilmEntity> = emptyList()

    inner class FilmCardViewHolder(private val binding: FilmCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(filmList[position])
                }
            }
        }

        fun bind(film: FilmEntity) {
            // Bind common properties
            binding.txtFilmName.text = film.filmName
            binding.date.text = film.filmReleaseDate.toString()

            // Load image using Glide
            Glide.with(itemView.context)
                .load(film.filmImage)
                .placeholder(R.drawable.picture) // Optional: Set a placeholder image
                .error(R.drawable.baseline_error_outline_24) // Optional: Set an error image resource
                .into(binding.filmImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmCardViewHolder {
        val binding = FilmCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmCardViewHolder, position: Int) {
        val film = filmList[position]
        holder.bind(film)
    }

    override fun getItemCount(): Int {
        return filmList.size
    }

    fun submitList(list: List<FilmEntity>) {
        filmList = list
        notifyDataSetChanged()
    }
}