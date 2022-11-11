package com.example.an0nym0us

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import org.w3c.dom.Comment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [comment_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class comment_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

     private lateinit var commentRecyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var commentAdapter: CommentRecyclerAdapter
    private lateinit var commentList: ArrayList<Commento>

    var nameActivity:String = "com.example.an0nym0us."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                var javaClass=Class.forName(nameActivity)
                val intent = Intent(context, javaClass)
                startActivity(intent)
            }

        })
        arguments?.let {

            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_comment, container, false)

        val bundle = arguments
        val user:String?=bundle?.getString("userPost")
        val data:String?=bundle?.getString("datePost")
        nameActivity+= bundle?.getString("nameActivity").toString()
        commentList= arrayListOf<Commento>()

        commentRecyclerView=view.findViewById<RecyclerView>(R.id.commentRecyclerView)
        commentRecyclerView.layoutManager = LinearLayoutManager(activity)
        commentRecyclerView.setHasFixedSize(true)

        if (data != null && user != null) {
            dbRef =
                FirebaseDatabase.getInstance("https://an0nym0usapp-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Utenti").child(user).child(data).child("comments")
        }

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (commentSnapshot in snapshot.children) {

                        var commentApp = commentSnapshot.getValue() as HashMap<*, *>
                        var user = commentApp["user"].toString()
                        var content = commentApp["content"].toString()

                        commentList.add(Commento(user,content))
                    }
                    commentAdapter= CommentRecyclerAdapter(commentList)
                    commentRecyclerView.adapter=commentAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return view
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment comment_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            comment_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}