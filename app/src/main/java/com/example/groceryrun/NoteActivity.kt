package com.example.groceryrun

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_note.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class NoteActivity : AppCompatActivity() {
    private val TAG: String? = "NoteActivity"
    // hashmap storing item, findButton id
    private var map : HashMap<Int, String> = HashMap<Int, String> ()
    private var count = 2000    // start at 2000 just in case some previous ones were already initialized
    private var names: ArrayList<String> = ArrayList()   // item/ category names
    private var urls: ArrayList<String> = ArrayList()   // item/ category urls

    // has the user chosen a store already
    private var storeChosen = true;

    //private  var textSize = 11; //text size of buttons

    fun onFragmentInteraction(uri: Uri?) {
        Log.i("Tag", "onFragmentInteraction called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        // get JSON Data
        try {
            // get JSONObject from JSON file
            var itemArray = JSONArray(loadJSONFromAsset())
            for (i in 0 until itemArray.length()) {
                // create a JSONObject for fetching single user data
                var itemDetail = itemArray.getJSONObject(i)
                var name = itemDetail.getString("name")
                var url = itemDetail.getString("url")

                names.add(name)
                urls.add(url)

                Log.i("Item loaded from json: ", name)
            }
        }
        catch (e: JSONException) {
            Log.i("Couldn't load json data", "Couldn't get json data but did load the file")
            e.printStackTrace()
        }

        val autocomplete = findViewById<View>(R.id.enterItem) as AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        autocomplete.setAdapter(adapter)
    }

    // load data from JSON file for item list
    private fun loadJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = assets.open("item_data.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val charset: Charset = Charsets.UTF_8
            String(buffer, charset)
        }
        catch (ex: IOException) {
            Log.i("Couldn't load json", "Couldn't load json file")
            ex.printStackTrace()
            return null
        }
        Log.i("json file loaded", "json file loaded properly")
        return json
    }

    fun saveItem(view: View) {     // for when a new item is added
        if (!names.contains(enterItem.text.toString())){
            // erase edittext text, not sure if we want to erase it though
            enterItem.text.clear()
            //CAN'T SAY I UNDERSTAND ANY PART OF THE REST OF THIS PART OF THE IF STATEMENT
            //https://stackoverflow.com/questions/5944987/how-to-create-a-popup-window-popupwindow-in-android was super helpful
            // inflate the layout of the popup window
            val inflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView: View = inflater.inflate(R.layout.popup_window, null)
            // create the popup window
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // lets taps outside the popup also dismiss it

            val popupWindow = PopupWindow(popupView, width, height, focusable)

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            // dismiss the popup window when touched
            popupView.setOnTouchListener { v, event ->
                popupWindow.dismiss()
                true
            }
        }
        /*
        else if (map.containsValue(enterItem.text.toString()))
        {
            // erase edittext text, not sure if we want to erase it though
            enterItem.text.clear()
            //CAN'T SAY I UNDERSTAND ANY PART OF THE REST OF THIS PART OF THE IF STATEMENT
            //https://stackoverflow.com/questions/5944987/how-to-create-a-popup-window-popupwindow-in-android was super helpful
            // inflate the layout of the popup window
            val inflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView: View = inflater.inflate(R.layout.popup_window_duplicate, null)
            // create the popup window
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // lets taps outside the popup also dismiss it
            val popupWindow = PopupWindow(popupView, width, height, focusable)
            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
            // dismiss the popup window when touched
            popupView.setOnTouchListener { v, event ->
                popupWindow.dismiss()
                true
            }
        } */  // if we want to only allow 1 of the same thing, we have to remove the entries from map in removeClicked()
        else {     // save item to hashmap and create new spot for entering data, if that slot is filled out properly
            map.put(
                count+1,
                enterItem.text.toString()
            )
            var msg = enterItem.text.toString()
            Log.i("New item saved: ", msg)

            // add new linear layout for edit text
            val parent = findViewById<LinearLayout>(R.id.mainList)
            val ll = LinearLayout(this)
            ll.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            ll.orientation = LinearLayout.HORIZONTAL

            val tv = TextView(this)
            tv.setMaxWidth(350);
            tv.setMinWidth(350);
            tv.text = enterItem.text.toString()

            val removeButton = Button(this)
            removeButton.id = count                                            // id of remove buttons are even
            val icon = resources.getDrawable(R.drawable.ic_del_24, theme)
            removeButton.background = icon
            removeButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    removeClicked(view)
                }
            })

            val findButton = Button(this)
            findButton.text = "Find Item"
            findButton.id = count + 1        // id of find buttons are odd
            findButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    findClicked(view)
                }
            })

            // increment count by 2
            // even ids are removeButtons, odd are findButtons
            count+=2

            ll.addView(tv)
            ll.addView(removeButton)
            ll.addView(findButton)
            parent.addView(ll)

            // erase edittext text
            enterItem.text.clear()
        }
    }

    // deletes an entry
    fun removeClicked (view: View){
        (view.parent.parent as ViewGroup).removeView(view.parent as ViewGroup)
    }

    // opens search fragment to find specific item at the user's preferred store
    fun findClicked (view: View){
        // this is the id of the view that was clicked
        val eyedee = view.id
        // find corresponding TextView text using map
        val text = map[eyedee]

        // find index of first occurence of that item in the names
        // this will be the same index of the corresponding url
        val index = names.indexOf(text);
        val link = urls[index]
        Log.i("Find button was pressed", "Name and url of item: " + text + " " + link)

        // if store is not chosen, take user to maps activity to choose
        if (storeChosen) {
            val bottomSheetFragment = SearchFragment.newInstance(link)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
        else{ // TODO Differentiate which Activity calls Maps so that if Maps is called by Note, it returns to Note and opens SearchFragment
            startActivity(Intent(this@NoteActivity, MapsActivity::class.java))
        }
    }

    companion object {
        private val TAG = "NoteActivity"
    }
}