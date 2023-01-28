package com.shuklansh.booksellingapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shuklansh.booksellingapp.R
import com.shuklansh.booksellingapp.adapter.DashboardRecyclerAdapter
import com.shuklansh.booksellingapp.models.Book
import com.shuklansh.booksellingapp.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    //recyclerview needs: layoutmanager,viewholder,adapter
    lateinit var recyclerDashboard: RecyclerView //recyclerDashboard is the name of variable storing the RecyclerView made in the fragment_dashboard.xml
    lateinit var layoutManager: RecyclerView.LayoutManager //layoutManager is the name of variable storing the Layoutmanager value of recyclerView (Linear/grid)
    lateinit var recyclerAdapter: DashboardRecyclerAdapter // recyclerAdapter variable will store the dashboardRecyclerAdapter.kt class so that we can use the adapter to inflate the fragment and display singleRow layout in view (which is inside the ViewHolder())
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar: ProgressBar
    //lateinit var btnCeckInternet: Button

    val bookInfoList = arrayListOf<Book>() // variable type is of type Book, which is the model created in models package.

//        Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
//        Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
//        Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
//        Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
//        Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
//        Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
//        Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
//        Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
//        Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
//        Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
//    )


    //CODE TO SORT BOOKS BASED ON RATING:
    var ratingComparator = Comparator<Book> { book1, book2 ->

        // IF (BOOK RATINGS ARE SAME) {
        //  SORT BY NAME
        // }
        // ELSE {
        //  SORT BY RATING
        // }

        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName,true)

        }else{
            book1.bookRating.compareTo(book2.bookRating,true)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment to display the view
        //val view, here view is the view which the fragment will display when called. this is done by inflating the view inside the fragment
        val view= inflater.inflate(R.layout.fragment_dashboard, container, false) //IMPORTANT : THIS DECLARATION OF VIEW VARIABLE IS USED EVERYWHERE TO WORK ON THE ELEMENTS TO BE DISPLAYED ON THE DASHBOARD SCREEN(FRAGMENT)
        //on clicking diff menu items it will open diff fragments. so attachToRoot is set to false
        //container is a viewgroup provided by activity. it will hold the fragment layout for the activity in which the fragment will be hosted

        setHasOptionsMenu(true) // for a fragment to show menu, we need to add this line, but for activity, compiler automatically adds it


        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout.visibility=View.VISIBLE
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)   //here we connect recyclerDashboard variable to RecyclerView layout element (which is given id as recyclerDashboard inside the dashboardFrag xml file). this will be used across all screens to manipulate data to show on recyclerview layout()
        //we need to add the recyclerview to the view which the fragment will display. (that view is then inflated inside the fragment)
        //thus we do it by view.findviewbyid since view variable is storing the fragment layout which will be displayed when the view is inflated as fragment is called.

         //here "activity" is the context (this is not used as context as we are declaring in the fragment file(not declaring it in an activity rn)) in which the linearlayoutmanager will be used.

//        btnCeckInternet=view.findViewById(R.id.btnCheckInternet)
//
//        btnCeckInternet.setOnClickListener{
//            // WHEN BTN PRESSED CHECK IF INTERNET CONNECTED. IF NOT AND RETRY PRESSED, RETRY ELSE CLOSE ALERT DIALOG BOX
//            checkConn()
//        }

        //declaring variable queue for storing queue of requests
        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v1/book/fetch_books/" //this is the url of server from where we will get the response. WE ALSO NEED A TOKEN ALONG WITH URL
        //anonymous object syntax. header will be sent with url


        if(ConnectionManager().checkConnectivity(activity as Context)) { // check connectivity to internet and if true, proceed to get response and map data
            val jsonObjectRequest = object :JsonObjectRequest( //JOR method arguments: ReqMethod , url , jsonObject, ResponseListener, ErrorListener
                    com.android.volley.Request.Method.GET
                    , url
                    , null
                    , Response.Listener{ //println("Response is : $it")
                        // THE DATA SENT BY API FROM SEVER IS LISTENED BY RESPONSE LISTENER. ARGS DEPEND ON THE DATA RECEIVED.
                        // HERE WE ARE RECEIVING JSON FILE, AND "IT" (DATA IS ACCESSED USING IT WHICH IS THE JSON FILE RECEIVED) HAS 2 ELEMS (SUCCESS AND DATA) SO 2 ARGS NEEDED HERE OF RESPECTIVE TYPES.
                        // the response JSON has 1 obj "success": boolean (if data sent successfully and recieved by volley)
                        // and "data": JsonArray (containing all JsonObjects for books. these JsonObjects have info about book stored in api)
                        try{
                            progressLayout.visibility = View.GONE
                            val success = it.getBoolean("success")
                            if (success) { // THIS BLOCK WILL EXECUTE ONLY IF IT IS VERIFIED DATA IS SENT SUCCESSFULLY BY API
                                val data = it.getJSONArray("data")
                                for (i in 0 until data.length()) { //iterate through all elems to get individual jsonobjects. (i.e. info of ind. boooks)
                                    val bookJsonObject = data.getJSONObject(i) // initializing variable which is storing all info about current book
                                    val bookObject =
                                        Book(//the variable bookObject (of data type "Book") will be fed the info of books from the bookJsonObject which is storing info from data JsonArray (one jsonObject at a time) which was sent by api
                                            bookJsonObject.getString("book_id"),  // the name of string and order same as data sent. this order of json values should be same as the order of declaration of vars in Book.kt class
                                            bookJsonObject.getString("name"),
                                            bookJsonObject.getString("author"),
                                            bookJsonObject.getString("rating"),
                                            bookJsonObject.getString("price"),
                                            bookJsonObject.getString("image")
                                        )
                                    //iteration over, data stored for one book at a time inside the bookObject of dt Book.
                                    //now time to add this info into bookInfoList
                                    bookInfoList.add(bookObject)

                                    //NOW PASTING CODE FOR DISPLAYING INFO IN FRAGMENT

                                    //initializing variables for recyclerview, its adapter and its layoutmanager
                                    layoutManager = LinearLayoutManager(activity as Context)

                                    recyclerAdapter = DashboardRecyclerAdapter(activity as Context,bookInfoList)//activity is typecasted using as keyword so that it can be used as a context

                                    recyclerDashboard.layoutManager = layoutManager // connecting LinearlayoutManager with RecyclerView in fragment_dashboard.xml file , as the LayoutManager of the RecyclerView

                                    recyclerDashboard.adapter = recyclerAdapter // connecting adapter file DashboardRecyclerAdapter.kt with RecyclerView in fragment_dashboard.xml file , as the adapter of the RecyclerView

                                    // connecting LayoutManager variable (which is declared as LinearLM above) as the LM of RecyclerView in fragment_dashboard.xml file

//                                    recyclerDashboard.addItemDecoration(
//                                        DividerItemDecoration(
//                                            recyclerDashboard.context, //context in which items will be divided. here: in the recyclerview of fragment_dashboard.xml
//                                            (layoutManager as LinearLayoutManager).orientation //linear manner not grid
//                                        )
//                                    )

                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Something went wrong..",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        catch (e:JSONException){
                            Toast.makeText(activity as Context,"Unexpected error occurred from server",Toast.LENGTH_SHORT).show()
                        }


                    }
                    ,Response.ErrorListener {
                    if(activity!=null) { // so app does not crash due to no activity
                        //  VOLLEY ERRORS ARE HANDLED IN THE RESPONSE.ERRORLISTENER BLOCK. SO WE PUT TOAST HERE SAYING VOLLEY ERROR OCCURED
                        Toast.makeText(
                            activity as Context,
                            "Volley error occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    })
            { //inside JsonObjectRequest(args) : we also send headers to validate unique request
                //GETHEADERS METHOD TO SEND HEADER TO API
                override fun getHeaders(): MutableMap<String, String> {
                    //headers are used to ensure each request is UNIQUE
                    //hashmap is another name for dictionary. so data is being received in string:string
                    val headers = HashMap<String, String>() //hashmap is derived from a mutable map
                    headers["Content-type"] ="application/json" //key: content-type, value: application/json
                    headers["token"] = "9657c1fa810e5a" //key: token, value: unique key given
                    return headers //this is sent to API.
                }
            }

            queue.add(jsonObjectRequest)
        }
        else{
            //dialog box with option to open settings
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("Open Settings"){ text, Listener-> //open settings
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS) //implicit intent declared for opening wireless settings (settings.action_wireless_intent is action for the path needed to open settings. known by android os)
                startActivity(settingsIntent) // intent passed to open settings
                activity?.finish()
                //WHEN WE NEEDED TO NAVIGATE FROM ONE ACTIVITY TO ANOTHER, IT WAS EXPLICIT INTENT.
            }
            dialog.setNegativeButton("Cancel"){ text, Listener-> //do nothing
                ActivityCompat.finishAffinity(activity as Activity) //THIS CODE WILL CLOSE APP. ALL ACTIVITIES AND RUNNING INSTANCES ARE CLOSED


            }
            dialog.create()
            dialog.show()
        }
        //this view which will be returned is the parent view of fragment i.e. view which will be displayed when fragment replaces frame.
        return view //we need to return a view in the onCreateView method (fragment lifecycle)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // commenting super... as this is for the default functionality of the function
        // super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_dashboard, menu) // here we give the menu layout which needs to be inflated. menu is the variable which will hold the menu file we created
        // after this we need to put setHasOptionsMenu as true in the oncreateview()
    // of the fragment for this to function

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId // THIS VAR STORES THE ID OF THE MENU THAT WAS CLICKED
        if (id == R.id.action_sort) { // IF THE MENU ITEM PRESSED IS THE SORT BY
            // RATING OPTION THEN EXECUTE THIS BLOCK
            // HERE WE WILL WRITE THE CODE TO SORT THE LIST ITEMS
            Collections.sort(
                bookInfoList,
                ratingComparator
            ) // sorts the items in ascending (low to high)
            bookInfoList.reverse()
            // reverses the sorted list so we get all books from high rating to low

        }
        recyclerAdapter.notifyDataSetChanged() // TO INFORM THE FRAGMENT DATA CHANGED SO DISPLAY DATA IN THE SORTED MANNER
        return super.onOptionsItemSelected(item)
    }







    fun checkConn(){
        if(ConnectionManager().checkConnectivity(activity as Context)){
            //internet available
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Success")
            dialog.setMessage("Successfully connected to Internet")
            dialog.setPositiveButton("Okay"){text,listener-> //do nothing
            }
//                dialog.setNegativeButton("Cancel"){text,listener-> //do nothing
//                }
            dialog.create()
            dialog.show()
        }
        else{
            //no internet
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Conection Failed")
            dialog.setMessage("Could not connect to Internet, Please check your connection and try again")
            dialog.setPositiveButton("Retry"){text,listener-> //do nothing
                checkConn()
            }
            dialog.setNegativeButton("Cancel"){text,listener-> //do nothing
                }
            dialog.create()
            dialog.show()
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

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}