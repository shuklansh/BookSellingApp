package com.shuklansh.booksellingapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.shuklansh.booksellingapp.R
import com.shuklansh.booksellingapp.adapter.FavouritesRecyclerAdapter
import com.shuklansh.booksellingapp.database.BookDatabase
import com.shuklansh.booksellingapp.database.BookEntity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

    /**
     * A simple [Fragment] subclass.
     * Use the [favouritesFragment.newInstance] factory method to
     * create an instance of this fragment.
     */
class FavouritesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    //declaring vars for fragment_favourites.xml

    lateinit var progressBarLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerLayoutFav: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouritesRecyclerAdapter
    var dbBookList = listOf<BookEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerLayoutFav = view.findViewById(R.id.recyclerLayoutFav)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        progressBarLayout.visibility = View.VISIBLE
        layoutManager = GridLayoutManager(activity as Context, 2)
        dbBookList = RetrieveFavourites(activity as Context).execute().get()

        if(activity != null)
        {
            progressBarLayout.visibility = View.GONE
            recyclerAdapter = FavouritesRecyclerAdapter(activity as Context,dbBookList)
            recyclerLayoutFav.adapter = recyclerAdapter
            recyclerLayoutFav.layoutManager = layoutManager
        }
        return view
    }


    class RetrieveFavourites(val context: Context) :AsyncTask<Void, Void, List<BookEntity>>() { // since we want a list of tables in book database, return type is <BookEntity>
        override fun doInBackground(vararg params: Void?): List<BookEntity> {
            val db = databaseBuilder(context, BookDatabase::class.java, "book-db").build()
            return db.bookDao().getAllBooks()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */

        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
