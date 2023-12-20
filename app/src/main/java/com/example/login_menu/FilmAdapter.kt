package com.example.login_menu

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.login_menu.database.FilmEntity
import com.example.login_menu.databinding.FilmViewBinding

class FilmAdapter(private val onItemClick: (film: FilmEntity) -> Unit) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    private var filmList: List<FilmEntity> = emptyList()

    inner class FilmViewHolder(val binding: FilmViewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(filmList[position])
                }
            }

            binding.editButton.setOnClickListener {
                // Handle edit button click (open AddFilm activity with edit mode)
                val film = filmList[adapterPosition]
                val intent = Intent(itemView.context, AddFilm::class.java)
                intent.putExtra("EDIT_MODE", true)
                intent.putExtra("FILM_ID", film.id)
                itemView.context.startActivity(intent)
            }

            binding.deleteButton.setOnClickListener {
                // Handle delete button click
                val film = filmList[adapterPosition]
                // Implement the logic to delete the film data (e.g., from Firebase or Room)
                // You can use film.id to identify the film to be deleted
                // After deletion, you might want to notify the adapter about the change
                // For example, you can call filmAdapter.submitList(updatedList) after deletion
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = FilmViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = filmList[position]

        // Bind common properties
        holder.binding.txtFilmName.text = film.filmName

        // Load image using Glide
        Glide.with(holder.itemView)
            .load(film.filmImage)
            .placeholder(R.drawable.picture)
            .error(R.drawable.baseline_error_outline_24) // Optional: Set an error image resource
            .into(holder.binding.filmImage)

        // Additional bindings for film_view
        holder.binding.date.text = film.filmReleaseDate.toString()
        holder.binding.synopsis.text = film.filmSynopsis
    }

    override fun getItemCount(): Int {
        return filmList.size
    }

    fun submitList(list: List<FilmEntity>) {
        filmList = list
        notifyDataSetChanged()
    }

    fun updateList(newList: List<FilmEntity>) {
        filmList = newList
        notifyDataSetChanged()
    }

    fun generateDummy(): List<FilmEntity> {
        return listOf(
            FilmEntity(filmImage = "film_ex", filmName = "In The Mouth Of Madness", filmReleaseDate = 1994, filmSynopsis = "When horror novelist Sutter Cane (Jürgen Prochnow) goes missing, insurance investigator John Trent (Sam Neill) scrutinizes the claim made by his publisher, Jackson Harglow (Charlton Heston), and endeavors to retrieve a yet-to-be-released manuscript and ascertain the writer's whereabouts. Accompanied by the novelist's editor, Linda Styles (Julie Carmen), and disturbed by nightmares from reading Cane's other novels, Trent makes an eerie nighttime trek to a supernatural town in New Hampshire."),
            FilmEntity(filmImage = "film_ex2", filmName = "Drive", filmReleaseDate = 2011, filmSynopsis = "A getaway driver falls in love with Irene, a criminal's wife. He gets involved in a robbery attempt and lands himself in trouble with the mob to protect his lover."),
        )
    }
}
