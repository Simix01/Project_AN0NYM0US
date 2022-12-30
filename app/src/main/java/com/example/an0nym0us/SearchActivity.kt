package com.example.an0nym0us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_search.*
import kotlin.math.absoluteValue

class SearchActivity : AppCompatActivity() {

    private var listaUsers: ArrayList<String> = arrayListOf()
    private var listaIDs: ArrayList<String> = arrayListOf()
    val cUser = FirebaseAuth.getInstance().currentUser!!.uid
    val valoreHash = cUser.hashCode().absoluteValue
    val uId = "anonym$valoreHash"
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

                        if(userSnapshot.key != uId){
                            if(canBeFound){
                                listaUsers.add(nickname)
                                listaIDs.add(userSnapshot.key.toString())
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun dynamicListView(){
        adapter = ArrayAdapter(this, R.layout.items_style, listaUsers)
        listViewUtenti.adapter = adapter
        search_field.setOnQueryTextListener(object:
                SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(listaUsers.contains(query)){
                    adapter.filter.filter(query)
                }else{
                    Toast.makeText(this@SearchActivity,"Utente non trovato",Toast.LENGTH_SHORT).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })
        listViewUtenti.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this@SearchActivity, ProfileActivity::class.java)
            intent.putExtra("username", listaIDs[i])
            startActivity(intent)
        }
    }
}