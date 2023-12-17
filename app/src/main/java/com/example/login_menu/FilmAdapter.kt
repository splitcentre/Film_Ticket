import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_menu.R
import com.example.login_menu.database.FilmEntity
import com.example.login_menu.database.film
import com.squareup.picasso.Picasso

class FilmAdapter(private val onItemClick: (film: FilmEntity) -> Unit) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    private var filmList: List<FilmEntity> = emptyList()

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

        // Load image using Picasso (replace "placeholder_image" with your placeholder image resource)
        Picasso.get()
            .load(film.filmImage)
            .placeholder(R.drawable.picture)
            .error(R.drawable.baseline_error_outline_24) // Optional: Set an error image resource
            .into(holder.filmImage)

        // Additional bindings for film_view
        holder.releaseDateTextView?.text = film.filmReleaseDate.toString()
        holder.synopsisTextView?.text = film.filmSynopsis
    }

    override fun getItemCount(): Int {
        return filmList.size
    }

    fun submitList(list: List<FilmEntity>) {
        filmList = list
        notifyDataSetChanged()
    }
    fun generateDummy(): List<FilmEntity> {
        return listOf(
            FilmEntity(filmImage = "film_ex", filmName = "In The Mouth Of Madness", filmReleaseDate = 1994, filmSynopsis = "When horror novelist Sutter Cane (Jürgen Prochnow) goes missing, insurance investigator John Trent (Sam Neill) scrutinizes the claim made by his publisher, Jackson Harglow (Charlton Heston), and endeavors to retrieve a yet-to-be-released manuscript and ascertain the writer's whereabouts. Accompanied by the novelist's editor, Linda Styles (Julie Carmen), and disturbed by nightmares from reading Cane's other novels, Trent makes an eerie nighttime trek to a supernatural town in New Hampshire."),
            FilmEntity(filmImage = "film_ex2", filmName = "Drive", filmReleaseDate = 2011, filmSynopsis = "A getaway driver falls in love with Irene, a criminal's wife. He gets involved in a robbery attempt and lands himself in trouble with the mob to protect his lover."),
        )
    }

}
