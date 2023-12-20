package com.example.login_menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.login_menu.R

class ToolbarAdapter(
    private val toolbarType: Int,
    private val logoutClickListener: () -> Unit,
    private val backArrowClickListener: () -> Unit
) : RecyclerView.Adapter<ToolbarAdapter.ToolbarViewHolder>() {

    companion object {
        const val TOOLBAR_TYPE_WITH_LOGOUT = 1
        const val TOOLBAR_TYPE_WITH_BACK_ARROW = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolbarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = when (toolbarType) {
            TOOLBAR_TYPE_WITH_LOGOUT -> {
                layoutInflater.inflate(R.layout.toolbar, parent, false)
            }
            TOOLBAR_TYPE_WITH_BACK_ARROW -> {
                layoutInflater.inflate(R.layout.toolbar_withback, parent, false)
            }
            else -> throw IllegalArgumentException("Invalid toolbarType")
        }
        return ToolbarViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolbarViewHolder, position: Int) {
        when (toolbarType) {
            TOOLBAR_TYPE_WITH_LOGOUT -> {
                holder.itemView.findViewById<ImageButton>(R.id.logout).setOnClickListener {
                    logoutClickListener.invoke()
                }
            }
            TOOLBAR_TYPE_WITH_BACK_ARROW -> {
                holder.itemView.findViewById<ImageButton>(R.id.backarrow).setOnClickListener {
                    backArrowClickListener.invoke()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    class ToolbarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
