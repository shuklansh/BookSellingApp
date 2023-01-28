package com.shuklansh.booksellingapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView
import com.shuklansh.booksellingapp.R
import com.shuklansh.booksellingapp.activity.DescriptionActivity
import com.shuklansh.booksellingapp.models.Book
import com.squareup.picasso.Picasso


//adapter has the viewholder inside it. adapter adapts the data that can be used by recyclerview (works with context)
class DashboardRecyclerAdapter(val context: Context?, val itemList: ArrayList<Book>) : RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    class DashboardViewHolder(view: View):RecyclerView.ViewHolder(view){
        val textView:TextView=view.findViewById(R.id.txtBookName) //initializing textview variable to the data in recycler_dashboard_single_row.xml (i.e. book name)

        //initializing all views which will display data for each book
        //we are giving view variables names here
        val txtBookName : TextView=view.findViewById(R.id.txtBookName)
        val txtBookAuth : TextView=view.findViewById(R.id.txtBookAuthor)
        val txtBookPrice : TextView=view.findViewById(R.id.txtBookPrice)
        val txtBookRating : TextView=view.findViewById(R.id.txtBookRating)
        val txtBookImage : ImageView=view.findViewById(R.id.imgBookImage)
        val llcontent: LinearLayout=view.findViewById(R.id.llcontent)//parentLayout of all views in the single row file

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        //this view is storing the layout created for a single row
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)
        return DashboardViewHolder(view) //the viewholder here is given the view for single row, so that it can hold the view and display when called.

    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        //variable is named book, and it corresponds to the itemlist element
        val book=itemList[position] // based on position index, respective data will be stored in text variable (position=0 means Book 1 ...)
        //we are connecting view variable names to the corresponding data from the data class (model) book here
        holder.txtBookName.text=book.bookName
        holder.txtBookAuth.text=book.bookAuthor
        holder.txtBookPrice.text=book.bookPrice
        holder.txtBookRating.text=book.bookRating
        //holder.txtBookImage.setImageResource(book.bookImage)
        // to display image from image link, we will use picasso.
        // load(book.bookImage): hold link of image for the current book. it got this link in the responseListener block (as string from jsonobject from data jsonArray)
        // error(R.drawable...): display default img if picasso fails to get img from link
        // into(holder.txtBookImage): where to show image, txtBookImage holds the id of image view (in recycler_dashboard_single_row which is referred using view)
         Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.txtBookImage);

        //pop a toast when individual row is clicked.
        holder.llcontent.setOnClickListener{
//            Toast.makeText(
//                context,
//                "${holder.txtBookName.text} is ${holder.txtBookPrice.text} rupees", //it is the individual row clicked which is inside the PARENT LAYOUT (i.e. llcontent)
//                Toast.LENGTH_SHORT
//            ).show()
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("book_id" , book.bookId) // we getting this data from "val book = itemList[position]"above
            context?.startActivity(intent) // every method related to current activity is obtained using context in adapter file.

        }
    }

    override fun getItemCount(): Int {
        return itemList.size //the number of views to be created is given as the length of array
    }
}








//
//class DashboardRecyclerAdapter(val context: Context, val itemList: ArrayList<Book>) :
//    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
//    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
//        val txtBookAuthor: TextView = view.findViewById(R.id.txtBookAuthor)
//        val txtBookPrice: TextView = view.findViewById(R.id.txtBookPrice)
//        val txtBookRating: TextView = view.findViewById(R.id.txtBookRating)
//        val imgBookImage: ImageView = view.findViewById(R.id.imgBookImage)
//        val llContent: LinearLayout = view.findViewById(R.id.llcontent)
//    }
//
//    @SuppressLint("ResourceType")
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
//        return DashboardViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
//        val book = itemList[position]
//        holder.txtBookName.text = book.bookName
//        holder.txtBookAuthor.text = book.bookAuthor
//        holder.txtBookPrice.text = book.bookPrice
//        holder.txtBookRating.text = book.bookRating
//        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover)
//            .into(holder.imgBookImage)
//        holder.llContent.setOnClickListener {
//            val intent = Intent(context,DescriptionActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("book_id",book.bookId)
//            context.startActivity(intent)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//}


