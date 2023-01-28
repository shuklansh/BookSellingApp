package com.shuklansh.booksellingapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.shuklansh.booksellingapp.fragments.DashboardFragment
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shuklansh.booksellingapp.R
import com.shuklansh.booksellingapp.R.id.progressBar
import com.shuklansh.booksellingapp.database.BookDatabase
import com.shuklansh.booksellingapp.database.BookEntity
import com.shuklansh.booksellingapp.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject


class DescriptionActivity : AppCompatActivity() {

    //INITIALIZE ALL VIEWS BEING USED IN ACTIVITY_DESCRIPTION
    lateinit var txtBookName : TextView
    lateinit var txtBookAuth : TextView
    lateinit var txtBookPrice : TextView
    lateinit var txtBookRating : TextView
    lateinit var llcontent : LinearLayout
    lateinit var txtBookImage : ImageView
    lateinit var txtBookDesc : TextView
    lateinit var txtBookAddtoFav : Button
    lateinit var progressBar : ProgressBar
    lateinit var progressLayout : RelativeLayout
    lateinit var toolbar : androidx.appcompat.widget.Toolbar

    var bookId:String? = "100" // some random value for bookID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuth = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        txtBookAddtoFav = findViewById(R.id.btnAddFav)
        progressBar = findViewById(R.id.progressBarDesc)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressBarLayoutDesc)
        progressLayout.visibility = View.VISIBLE
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"


        //link for post req: http://13.235.250.119/v1/book/get_book/
        if(intent!=null){
            bookId = intent.getStringExtra("book_id") // storing required book id we get from intent which was fed this info from dashboard fragment adapte //
        }
        else{
            finish()
            Toast.makeText(this@DescriptionActivity, "some error occurred",Toast.LENGTH_SHORT).show()
        }
        if("book_id"=="100" ){
            finish()
            Toast.makeText(this@DescriptionActivity, "some error occurred",Toast.LENGTH_SHORT).show()
        }


        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject() // this will go with url (this is json object holding bookid of the book we want info for)
        jsonParams.put("book_id",bookId) // in variable book_id we store the value of bookID we got from Dashboardfragment as intent.


        if (ConnectionManager().checkConnectivity(context = this@DescriptionActivity)){ //if internet available proceed with getting response and displaying description

            val jsonRequest = object : JsonObjectRequest(
                Method.POST,
                url,
                jsonParams,
                Response.Listener {
                    try{
                        val success = it.getBoolean("success")
                        if(success){
                            progressLayout.visibility = View.GONE
                            val bookjsonObject = it.getJSONObject("book_data") //book_data is the key in which json info is sent by api

                            // bookImg url for using in bookEntity
                            val bookImageUrl = bookjsonObject.getString("image")

                            Picasso.get().load(bookjsonObject.getString("image")).into(txtBookImage)
                            txtBookName.text = bookjsonObject.getString("name")
                            txtBookAuth.text = bookjsonObject.getString("author")
                            txtBookPrice.text = bookjsonObject.getString("price")
                            txtBookRating.text = bookjsonObject.getString("rating")
                            txtBookDesc.text = bookjsonObject.getString("description")

                            //creating bookEntity which is an object of BookEntity class, for working with addtofav.removefromfav.retrievefromfav
                            val bookEntity = BookEntity( // now creating all the properties of BookEntity class which are required in bookEntity object
                                // give the values of all properties of objects, in the same order as recieved from API
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuth.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl

                            )

                            // now creating a variable to check favourites
                            //3 args as told below, context, entity from which to be checked, mode
                            val checkFav = DBAsyncTask(applicationContext,bookEntity,1).execute() // .execute() to start bgProcess

                            // isfav will be boolean and call the get method of checkFav (tells us weather the result of bg process is true /false)
                            val isFav = checkFav.get()

                            // if isfav == true: btn should show remove from fav, else: btn should show add to fav

                            if(isFav){
                                txtBookAddtoFav.text = "Remove from Favourites"
                                val favcolor  = ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                txtBookAddtoFav.setBackgroundColor(favcolor)
                            }
                            else{
                                txtBookAddtoFav.text = "Add to Favourites"
                                val nofavColor = ContextCompat.getColor(applicationContext,R.color.purple_200)
                                txtBookAddtoFav.setBackgroundColor(nofavColor)
                            }

                            // BUTTON FUNCTIONALITY FOR ADDTOFAV/REMOVEFROMFAV BTN
                            txtBookAddtoFav.setOnClickListener{
                                // calling DBAsyncTask class in mode 1, to know if book is added to bookEntity (which stores all books which were added to favs by user) or not
                                if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get()) {
                                    //this block executes when book is NOT in favs (!DBA... means if book is not in db)

                                    val async = DBAsyncTask(
                                        applicationContext,
                                        bookEntity,
                                        2
                                    ).execute() //asynctask to add book to favs
                                    val result =
                                        async.get() // result will have value true if book added to fav, false if error occured
                                    if (result) {
                                        //toast to tell user book added to fav
                                        Toast.makeText(
                                            applicationContext,
                                            "${txtBookName.text} successfully added to favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        txtBookAddtoFav.text = "Remove from Favourites"
                                        val favcolor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorFavourite
                                        )
                                        txtBookAddtoFav.setBackgroundColor(favcolor)
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                else{
                                    //THIS BLOCK EXECUTES IF BOOK IS ALREADY IN FAVS
                                    val async = DBAsyncTask(
                                        applicationContext,
                                        bookEntity,
                                        3
                                    ).execute() //asynctask to add book to favs
                                    val result =
                                        async.get() // result will have value true if book added to fav, false if error occured
                                    if (result) {
                                        //toast to tell user book removed from favs
                                        Toast.makeText(
                                            applicationContext,
                                            "${txtBookName.text} successfully removed from favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        txtBookAddtoFav.text = "Add to Favourites"
                                        val favcolor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.purple_200
                                        )
                                        txtBookAddtoFav.setBackgroundColor(favcolor)
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }


                            }

                        }
                        else{
                            Toast.makeText(this@DescriptionActivity, "Some error occured" , Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: Exception){
                        Toast.makeText(this@DescriptionActivity, "Some error occured" , Toast.LENGTH_SHORT).show()
                    }
                }
                ,Response.ErrorListener {
                    Toast.makeText(this@DescriptionActivity, "Volley error $it" , Toast.LENGTH_SHORT).show()
                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers  = HashMap<String,String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "9657c1fa810e5a"
                    return headers
                }

            }
            queue.add(jsonRequest)

        }
        else {

            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("No Internet")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("Open settings"){text,listener->
                val settingsOpenIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsOpenIntent)
                finish()
            }
            dialog.setNegativeButton("Close App"){text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()

        }

    }


    // since a class can't inherit 2 classes, we create a subclass DBAsyncTask and make it inherit AsyncTask class
    class DBAsyncTask (val context: Context, val bookEntity: BookEntity, val mode:Int): AsyncTask<Void, Void, Boolean>() { // 3 parameters of ASYNCTASK CLASS: PARAMS, PROGRESS, RESULT
        //we need context to perform database operation , and bookentity to add/remove from favs
        /*
        we are using this class to do 3 things, so each action will be done using a MODE through "when"(so we make mode as a parameter):
        modes are
        mode1. check db if book is in fav or not
        mode2. save book in db as fav
        mode3. remove book from fav
        */

        //initializing book database outside all asynctask funcs as this will be used by all the asyncTask funcs
        //3 args needed for db: context, the db class and name of db
        val db = Room.databaseBuilder(context,BookDatabase::class.java,"book-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            //use cases (when we open DescriptionActivity for any book):
            // addtofav button if book not added to favs
            // removeFromFav button if book already added to favs

            when(mode){ //to perform these operations, we can use the bookDao interface.
                /*
                mode1. check db if book is in fav or not
                mode2. save book in db as fav
                mode3. remove book from fav
                */
                1->{ //check db if book is in fav or not
                    val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString() /*id of the book we need to check*/)
                    db.close() //IMPORTANT TO CLOSE DB AFTER DOING OPERATION
                    return book != null // if return value for this statement is null, it will return false

                }
                2->{ //save book in db as fav
                    db.bookDao().insertBook(bookEntity) // in the currently opened context, (i.e. the descriptionactivity of the book selected from dashboard frag) insert this book to bookEntity
                    db.close()
                    return true

                }
                3->{//remove book from fav
                    db.bookDao().deleteBook(bookEntity) // in the currently opened context, (i.e. the descriptionactivity of the book selected from dashboard frag) delete this book from bookEntity
                    db.close()
                    return true

                }
            }


            return false //false for now
        }

    }



}