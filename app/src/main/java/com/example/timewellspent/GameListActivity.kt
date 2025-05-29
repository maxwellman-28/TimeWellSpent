package com.example.timewellspent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.example.timewellspent.databinding.ActivityGameListBinding


class GameListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameListBinding
    private lateinit var adapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // make backendless call to retrieve all data

        // want the userid of the logged in user to match the ownerId of the object
//        val userId = Backendless.UserService.CurrentUser().userId
//        val userId = intent.getStringExtra(LoginActivity.EXTRA_USER_ID)
//
//        // ownerId = 'userId'
//        val whereClause = "ownerId = '$userId'"
//        val queryBuilder = DataQueryBuilder.create()
//        queryBuilder.setWhereClause(whereClause)

//        Backendless.Data.of(GameEntry::class.java).find(queryBuilder, object : AsyncCallback<MutableList<GameEntry>> {
//            override fun handleResponse(foundGameEntries: MutableList<GameEntry>) {
//                // all GameEntry instances have been found
//                Log.d("GameListActivity","handleResponse: $foundGameEntries")
//                adapter = GameAdapter(foundGameEntries)
//                binding.recyclerViewGameListActivityList.adapter = adapter
//                binding.recyclerViewGameListActivityList.layoutManager = LinearLayoutManager(this@GameListActivity)
//            }
//
//            override fun handleFault(fault: BackendlessFault) {
//                // an error has occurred, the error code can be retrieved with fault.getCode()
//                Log.d("GameListActivity","handleFault: ${fault.message}")
//            }
//        })

//        binding.fabGameListNewEntry.setOnClickListener {
//            val context = binding.fabGameListNewEntry.context
//            val detailIntent = Intent(context, GameDetailActivity::class.java)
//            context.startActivity(detailIntent)
//        }
        // in the handleResponse, get the list of data and constructor the adapter & apply to the reyclerview

    }

    override fun onStart() {
        super.onStart()
        val userId = intent.getStringExtra(LoginActivity.EXTRA_USER_ID)

        // ownerId = 'userId'
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.setWhereClause(whereClause)

        Backendless.Data.of(GameEntry::class.java).find(queryBuilder, object : AsyncCallback<MutableList<GameEntry>> {
            override fun handleResponse(foundGameEntries: MutableList<GameEntry>) {
                // all GameEntry instances have been found
                Log.d("icinGameListActivity","handleResponse: $foundGameEntries")
                adapter = GameAdapter(foundGameEntries)
                binding.recyclerViewGameListActivityList.adapter = adapter
                binding.recyclerViewGameListActivityList.layoutManager = LinearLayoutManager(this@GameListActivity)
            }

            override fun handleFault(fault: BackendlessFault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                Log.d("GameListActivity","handleFault: ${fault.message}")
            }
        })

        binding.fabGameListNewEntry.setOnClickListener {
            val context = binding.fabGameListNewEntry.context
            val detailIntent = Intent(context, GameDetailActivity::class.java)
            context.startActivity(detailIntent)
        }
    }
    
}