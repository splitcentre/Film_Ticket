package com.example.login_menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_menu.database.film

class FilmAdapter(private val onItemClick: (film: film) -> Unit) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    private var filmList: List<film> = emptyList()

    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filmImage: ImageView = itemView.findViewById(R.id.film_image)
        val filmNameTextView: TextView = itemView.findViewById(R.id.txt_film_name)

        // Additional views for film_view
        val releaseDateTextView: TextView? = itemView.findViewById(R.id.date)
        val synopsisTextView: TextView? = itemView.findViewById(R.id.synopsis)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(filmList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.film_card, parent, false)
        return FilmViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = filmList[position]

        // Bind common properties
        holder.filmNameTextView.text = film.filmName
        // Set the image (you need to load the actual image using a library like Picasso or Glide)
        // holder.filmImage.setImageResource(R.drawable.film_ex)

        // Additional bindings for film_view
        holder.releaseDateTextView?.text = film.filmReleaseDate.toString()
        holder.synopsisTextView?.text = film.filmSynopsis
    }

    override fun getItemCount(): Int {
        return filmList.size
    }

    fun submitList(list: List<film>) {
        filmList = list
        notifyDataSetChanged()
    }
}
