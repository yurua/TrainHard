package com.yurua.trainhard.ui.groups

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.yurua.trainhard.R

class CustomListAdapter(
  private val context: Context,
  private val titleList: List<String>,
  private val dataList: MutableMap<String, List<String>>
) : BaseExpandableListAdapter() {

  override fun getChild(listPos: Int, expListPos: Int) = dataList[titleList[listPos]]!![expListPos]

  override fun getChildId(listPos: Int, expListPos: Int) = expListPos.toLong()

  override fun getChildView(
    listPosition: Int,
    expandedListPosition: Int,
    isLastChild: Boolean,
    view: View?,
    parent: ViewGroup
  ): View {
    var convertView = view
    val expListText = getChild(listPosition, expandedListPosition)
    if (convertView == null) {
      val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
      convertView = layoutInflater.inflate(R.layout.list_item, null)
    }
    val expandedListTextView = convertView!!.findViewById<TextView>(R.id.expandedListItem)
    expandedListTextView.text = expListText
    return convertView
  }

  override fun getChildrenCount(listPosition: Int) = dataList[titleList[listPosition]]!!.size

  override fun getGroup(listPosition: Int) = titleList[listPosition]

  override fun getGroupCount() = titleList.size

  override fun getGroupId(listPosition: Int) = listPosition.toLong()

  override fun getGroupView(
    listPosition: Int,
    isExpanded: Boolean,
    view: View?,
    parent: ViewGroup
  ): View {
    var convertView = view
    val listTitle = getGroup(listPosition)
    if (convertView == null) {
      val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
      convertView = layoutInflater.inflate(R.layout.list_group, null)
    }
    val listTitleTextView = convertView!!.findViewById<TextView>(R.id.listTitle)
    listTitleTextView.text = listTitle
    return convertView
  }

  override fun hasStableIds() = false

  override fun isChildSelectable(listPosition: Int, expandedListPosition: Int) = true
}