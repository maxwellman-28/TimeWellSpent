package com.example.timewellspent

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DateFormat
import java.text.SimpleDateFormat


class GameAdapter(var gameList: MutableList<GameEntry>) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView
        val textViewDate: TextView
        val textViewMoneySpent: TextView
        val textViewTimeSpent: TextView
        val textViewEmotion: TextView
        val layout: ConstraintLayout

        init {
            textViewName = itemView.findViewById(R.id.textView_gameEntry_name)
            textViewDate = itemView.findViewById(R.id.textView_gameEntry_date)
            textViewMoneySpent = itemView.findViewById(R.id.textView_gameEntry_moneySpent)
            textViewTimeSpent = itemView.findViewById(R.id.textView_gameEntry_timeSpent)
            textViewEmotion = itemView.findViewById(R.id.textView_gameEntry_emotion)
            layout = itemView.findViewById(R.id.layout_gameEntry)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_game_entry, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = gameList[position]
        val context = holder.layout.context
        holder.textViewName.text = game.name
        // TODO: format the date nicely to show just the day month and year
        val format: DateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy")
        val formatted: String = format.format(game.datePlayed)
        holder.textViewDate.text = formatted
        // TODO: format the time to show it in hours and minutes
        holder.textViewTimeSpent.text = "${game.elapsedTime/60} hrs ${game.elapsedTime%60} mins"
        // TODO: format the money nicely to show it like $5.99
        holder.textViewMoneySpent.text = "$${game.moneySpent/100}.${game.moneySpent%100}"
        if(game.moneySpent%100.toString().length < 2)
            holder.textViewMoneySpent.text = "$${game.moneySpent/100}.${game.moneySpent%100}0"
        // TODO: verify this works in displaying the emoji
        holder.textViewEmotion.text = try {
            GameEntry.EMOTION.valueOf(game.emotion).emoji
        } catch (ex: IllegalArgumentException) {
            "¯\\_(ツ)_/¯"
        }

        holder.layout.isLongClickable = true
        holder.layout.setOnLongClickListener {
            // the holder.textViewBorrower is the textView that the PopMenu will be anchored to
            val popMenu = PopupMenu(context, holder.textViewName)
            popMenu.inflate(R.menu.menu_game_list_context)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_game_delete -> {
                        deleteFromBackendless(position)
                        true
                    }
                    else -> true
                }
            }
            popMenu.show()
            true
        }

        holder.layout.setOnClickListener {
            val context = holder.layout.context
            val detailIntent = Intent(context, GameDetailActivity::class.java)
            detailIntent.putExtra(GameDetailActivity.EXTRA_GAME_ENTRY, game)
            context.startActivity(detailIntent)
        }


    }
    private fun deleteFromBackendless(position: Int) {
        Log.d("GameAdapter", "deleteFromBackendless: Trying to delete ${gameList[position]}")
        // put in the code to delete the item using the callback from Backendless
        Backendless.Data.of(GameEntry::class.java).remove(gameList[position],
            object : AsyncCallback<Long?> {
                override fun handleResponse(response: Long?) {
                    // Contact has been deleted. The response is the
                    // time in milliseconds when the object was deleted
                    Log.d("Game Adapter", "handleResponse:${response}")
                    gameList.remove(gameList[position])
                    notifyDataSetChanged()

                }

                override fun handleFault(fault: BackendlessFault) {
                    // an error has occurred, the error code can be
                    // retrieved with fault.getCode()
                    Log.d("Game Adapter", "handleFault:${fault.message}")
                }
            })
        // in the handleResponse, we'll need to also delete the item from the sleepList
        // and make sure that the recyclerview is updated
    }
}