package com.rtuitlab.studo.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.rtuitlab.studo.R
import kotlinx.android.synthetic.main.view_profile_list_row.view.*

class ProfileListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ListView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE shr 2,
            MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val icons = context.resources.obtainTypedArray(R.array.profile_icons)
        val titles = resources.getStringArray(R.array.profile_titles)
        val menuList = mutableListOf<Pair<Int, String>>()
        for (i in 0 until 6) {
            menuList.add(Pair(icons.getResourceId(i, -1), titles[i]))
        }
        adapter = ListViewAdapter(context, R.layout.view_profile_list_row, menuList)
        icons.recycle()
        setSelector(android.R.color.transparent)
    }

    class ListViewAdapter(ctx : Context, private var resource : Int, private var items : List<Pair<Int, String>>)
        : ArrayAdapter<Pair<Int, String>>(ctx , resource , items){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view : View = LayoutInflater.from(context).inflate(resource , null )

            val textView = view.title
            val item= items[position]
            textView.setCompoundDrawablesWithIntrinsicBounds(item.first, 0, 0, 0)
            textView.text = item.second

            return view
        }
    }

    enum class MenuItem {
        ADS, RESUMES, BOOKMARKS, ORGANIZATIONS, SETTINGS, ABOUT
    }
}