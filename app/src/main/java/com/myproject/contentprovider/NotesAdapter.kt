package com.myproject.contentprovider

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myproject.contentprovider.services.NotesDataBaseHelper.Companion.DESCRIPTION_NOTES
import com.myproject.contentprovider.services.NotesDataBaseHelper.Companion.TITLE_NOTES

class NotesAdapter(private val listener: NoteClickedListener) :
    RecyclerView.Adapter<NotesViewHolder>() {

    private var mainCursor: Cursor? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        )

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        mainCursor?.moveToPosition(position)

        holder.noteTitle.text =
            mainCursor?.getString(mainCursor?.getColumnIndex(TITLE_NOTES) as Int)

        holder.noteDescription.text =
            mainCursor?.getString(mainCursor?.getColumnIndex(DESCRIPTION_NOTES) as Int)

        holder.noteButtonRemove.setOnClickListener {
            mainCursor?.moveToPosition(position)
            listener.noteRemoveItem(mainCursor as Cursor)
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener { listener.noteClickedItem(mainCursor as Cursor) }
    }

    override fun getItemCount(): Int = if (mainCursor != null) mainCursor?.count as Int else 0

    fun setCursor(newCursor: Cursor?) {
        mainCursor = newCursor
        notifyDataSetChanged()
    }
}

class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val noteTitle = itemView.findViewById(R.id.note_title) as TextView
    val noteDescription = itemView.findViewById(R.id.note_description) as TextView
    val noteButtonRemove = itemView.findViewById(R.id.note_button_remove) as Button
}