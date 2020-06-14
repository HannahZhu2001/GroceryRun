package com.example.groceryrun

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_search.*

private const val link = "url"

class SearchFragment : BottomSheetDialogFragment() {
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(link)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("Search received url", "Url: " + url)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    companion object {
        fun newInstance(link: String): SearchFragment {    // create a SearchFragment with url argument
            Log.i("Search received url", "Link: " + link)
            return SearchFragment().apply {
                arguments = Bundle().apply {
                    this.putString("url", link)
                }
            }
        }
    }
}
