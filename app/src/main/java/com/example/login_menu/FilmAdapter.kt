package com.example.login_menu

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.login_menu.database.FilmEntity
import com.example.login_menu.databinding.FilmViewBinding
import kotlinx.coroutines.launch

class FilmAdapter(private val onItemClick: (FilmEntity) -> Unit) :
    RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

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
                // Handle edit button click (open EditActivity with edit mode)
                val film = filmList[adapterPosition]
                val intent = Intent(itemView.context, EditActivity::class.java)
                intent.putExtra("EDIT_MODE", true)
                intent.putExtra("FILM_ID", film.id)
                itemView.context.startActivity(intent)
            }

            binding.deleteButton.setOnClickListener {
                // Handle delete button click
                val film = filmList[adapterPosition]

                // Create an instance of FilmViewModel
                val filmViewModel = FilmViewModel()

                // Call the deleteFilm function on the FilmViewModel instance
                filmViewModel.deleteFilm(film)

                // Optional: Remove the film from the list to update the UI
                filmList = filmList.filterNot { it.id == film.id }
                notifyDataSetChanged()
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
            .placeholder(R.drawable.picture) // Optional: Set a placeholder image
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

    inner class FilmViewModel : ViewModel() {
        fun deleteFilm(film: FilmEntity) {
            viewModelScope.launch {
                // Perform the deletion operation (replace with your actual implementation)
                // firestore.collection("films").document(film.id).delete().await()
            }
        }
    }
}
