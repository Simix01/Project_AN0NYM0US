package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private var listaUsers: ArrayList<String> = arrayListOf()
    private var listaIDs: ArrayList<String> = arrayListOf()
    private val dbRefUsers =
        FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("InfoUtenti")
    private lateinit var adapter : ArrayAdapter<*>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        overridePendingTransition(0, 0)
        search_field.requestFocus()
        prelevaUtenti()
        dynamicListView()
    }

    private fun prelevaUtenti(){
        dbRefUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listaUsers.clear()
                    listaIDs.clear()
                    for (userSnapshot in snapshot.children) {
                        var Map = userSnapshot.getValue() as HashMap<*, *>
                        var nickname = Map["nickname"] as String
                        var canBeFound = Map["canBeFound"] as Boolean

                        if(canBeFound){
                            listaUsers.add(nickname)
                            listaIDs.add(userSnapshot.key.toString())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun dynamicListView(){
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaUsers)
        listViewUtenti.adapter = adapter
        listViewUtenti.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this@SearchActivity, ProfileActivity::class.java)
            intent.putExtra("username", listaIDs[i])
            startActivity(intent)
        }
    }
}