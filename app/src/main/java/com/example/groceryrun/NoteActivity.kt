package com.example.groceryrun

import android.content.Context
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
    // hashmap storing item, quantity, removeButton name, findButtonName
    private var map : HashMap<String, Int> = HashMap<String, Int> ()
    private var count = 2000    // start at 2000 just in case some previous ones were already initialized
    private var names: ArrayList<String> = ArrayList()   // item/ category names
    private  var textSize = 11; //text size of buttons

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
                var name = itemDetail.getString("name");

                     names.add(name);

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

    // load data from JSON file
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
        if (!names.contains(enterItem.getText().toString())){
            // erase edittext text, not sure if we want to erase it though
            enterItem.getText().clear()
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
        else if (map.containsKey(enterItem.getText().toString()))
        {
            // erase edittext text, not sure if we want to erase it though
            enterItem.getText().clear()
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
        }
        else {     // save item to hashmap and create new spot for entering data, if that slot is filled out properly
            map.put(
                enterItem.getText().toString(),
                1
            )
            var msg = enterItem.getText().toString()
            Log.i("New item saved: ", msg)

            // add new linear layout for edit text
            val parent = findViewById(R.id.mainList) as LinearLayout
            val ll = LinearLayout(this)
            ll.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            ll.orientation = LinearLayout.HORIZONTAL

            val tv = TextView(this)
            tv.setMaxWidth(350);
            tv.setMinWidth(350);
            tv.setText(enterItem.getText().toString())



            val removeButton = Button(this)
            removeButton.minimumWidth = 130;
            removeButton.maxWidth = 130;
            removeButton.textSize = textSize.toFloat();
            removeButton.text = "Remove Item"
            removeButton.id = count
            removeButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    removeClicked(view)
                }
            })

            val findButton = Button(this)
            findButton.minimumWidth = 130;
            findButton.maxWidth = 130;
            findButton.textSize = textSize.toFloat();
            findButton.text = "Find Item"
            findButton.id = count + 1

            // increment count by 2
            // even ids are removeButtons, odd are findButtons
            count+=2

            ll.addView(tv)
            ll.addView(removeButton)
            ll.addView(findButton)
            parent.addView(ll)

            // erase edittext text
            enterItem.getText().clear()
        }
    }

    fun removeClicked (view: View){
        (view.parent.parent as ViewGroup).removeView(view.parent as ViewGroup)
    }

    companion object {
        private val TAG = "NoteActivity"
    }
}