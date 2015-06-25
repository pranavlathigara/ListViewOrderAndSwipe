package com.example.android.listviewdragginganimation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class PersonAdapter extends ArrayAdapter<Person>
{
  private final String TAG = "PersonAdapter";
  final int INVALID_ID = -1;

  private List<Person> items;
  private Context context;
  private PersonListViewOrder listview;

  HashMap<String, Integer> idMap = new HashMap<>();

  public PersonAdapter(Context context, int textViewResourceId, List<Person> items)
  {
    super(context, textViewResourceId, items);

    this.items = items;
    this.context = context;

    for (int i = 0; i < items.size(); ++i)
    {
      this.idMap.put(items.get(i).toString(), i);
    }
  }

  public void setListView(PersonListViewOrder listview)
  {
    this.listview = listview;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    try
    {
      View v = convertView;

      if (v == null)
      {
        LayoutInflater vi = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.person_item, null);
      }

      Person person = this.items.get(position);
      v.setTag(person);

      ImageView ivIcon = (ImageView) v.findViewById(R.id.ivIcon);

      ivIcon.setImageBitmap(person.bitmap);

      TextView tvName = (TextView) v.findViewById(R.id.tvName);
      tvName.setText(person.name);

      return v;
    }
    catch (Exception ex)
    {
      Log.e(TAG, "onCreate: " + ex.getMessage());
      return convertView;
    }
  }


  @Override
  public long getItemId(int position)
  {
    if (position < 0 || position >= this.items.size())
    {
      return INVALID_ID;
    }

    Person person = this.items.get(position);

    long id = this.idMap.get(person.toString());

    return id;
  }

  @Override
  public boolean hasStableIds()
  {
    return true;
  }
}