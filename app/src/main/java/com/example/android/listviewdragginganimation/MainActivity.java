/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Johann Blake
 *
 * https://www.linkedin.com/in/johannblake
 * https://plus.google.com/+JohannBlake
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.example.android.listviewdragginganimation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import com.johannblake.widgets.jbhorizonalswipelib.JBHorizontalSwipe;

public class MainActivity extends ActionBarActivity
{
  private final String TAG_LOG = "MainActivity";
  private final String TAG_BOTTOM_VIEW = "BottomView";

  private ArrayList<Person> persons = new ArrayList<>();
  private PersonAdapter adapterPerson;
  private PersonListViewOrder lvPersons;
  private JBHorizontalSwipe jbHorizontalSwipe;
  private Context context;
  private ViewGroup vgSwiped;

  private boolean removePrevDeleted;
  private Person prevDeletedPerson;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      this.context = this;

      // The main activity needs a JBHorizontalSwipe object to handle swiping listview items.
      this.jbHorizontalSwipe = new JBHorizontalSwipe(ijbHorizontalSwipe);

      // Add some data to the listview.
      this.persons.add(new Person(getNewId(), "Ben", BitmapFactory.decodeResource(getResources(), R.drawable.ic_ben)));
      this.persons.add(new Person(getNewId(), "Brad", BitmapFactory.decodeResource(getResources(), R.drawable.ic_brad)));
      this.persons.add(new Person(getNewId(), "Bradley", BitmapFactory.decodeResource(getResources(), R.drawable.ic_bradley)));
      this.persons.add(new Person(getNewId(), "Bruce", BitmapFactory.decodeResource(getResources(), R.drawable.ic_bruce)));
      this.persons.add(new Person(getNewId(), "Chris", BitmapFactory.decodeResource(getResources(), R.drawable.ic_chris)));
      this.persons.add(new Person(getNewId(), "Christian", BitmapFactory.decodeResource(getResources(), R.drawable.ic_christian)));
      this.persons.add(new Person(getNewId(), "Denzel", BitmapFactory.decodeResource(getResources(), R.drawable.ic_denzel)));
      this.persons.add(new Person(getNewId(), "George", BitmapFactory.decodeResource(getResources(), R.drawable.ic_george)));
      this.persons.add(new Person(getNewId(), "Hugh", BitmapFactory.decodeResource(getResources(), R.drawable.ic_hugh)));
      this.persons.add(new Person(getNewId(), "Johnny", BitmapFactory.decodeResource(getResources(), R.drawable.ic_johnny)));
      this.persons.add(new Person(getNewId(), "Leo", BitmapFactory.decodeResource(getResources(), R.drawable.ic_leo)));
      this.persons.add(new Person(getNewId(), "Liam", BitmapFactory.decodeResource(getResources(), R.drawable.ic_liam)));
      this.persons.add(new Person(getNewId(), "Matt", BitmapFactory.decodeResource(getResources(), R.drawable.ic_matt)));
      this.persons.add(new Person(getNewId(), "Matthew", BitmapFactory.decodeResource(getResources(), R.drawable.ic_matthew)));
      this.persons.add(new Person(getNewId(), "Morgan", BitmapFactory.decodeResource(getResources(), R.drawable.ic_morgan)));
      this.persons.add(new Person(getNewId(), "Russell", BitmapFactory.decodeResource(getResources(), R.drawable.ic_russell)));
      this.persons.add(new Person(getNewId(), "Tom", BitmapFactory.decodeResource(getResources(), R.drawable.ic_tom)));
      this.persons.add(new Person(getNewId(), "Will", BitmapFactory.decodeResource(getResources(), R.drawable.ic_will)));

      this.lvPersons = (PersonListViewOrder) findViewById(R.id.lvPersons);
      this.lvPersons.setPersonList(this.persons);
      this.adapterPerson = new PersonAdapter(this, R.layout.person_item, persons, this.jbHorizontalSwipe, this.lvPersons, new IListItemControls()
      {
        @Override
        public void onUndoClicked(View v)
        {
          // When the Undo button on a list item is pressed, we need to reset the state of deletion.
          removePrevDeleted = false;
          prevDeletedPerson = null;
        }
      });

      this.lvPersons.setAdapter(this.adapterPerson);

      this.lvPersons.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
          try
          {
            // This is where you put your code to handle the user tapping on a list item, i.e.
            // when the item's top view is being displayed.
            Person person = (Person) view.getTag();
            Toast.makeText(context, person.name, Toast.LENGTH_SHORT).show();
          }
          catch (Exception ex)
          {
            Log.e(TAG_LOG, "onItemClick: " + ex.getMessage());
          }
        }
      });

      // Handles removing a deleted item if the user scrolls the listview.
      // NOTE: Don't use a ScrollListener on the listview as this will cause bad
      // side effects. Motion events for the listview must be handled by the
      // onTouchEvent method in PersonListViewOrder in order for this kind of listview
      // to function properly.
      this.lvPersons.setVerticalScrollCallback(new PersonListViewOrder.IVerticalScrollCallback()
      {
        @Override
        public void onVerticalScroll()
        {
          try
          {
            // This method gets called when the user scrolls the listview vertically.
            if (prevDeletedPerson != null)
            {
              int pos = adapterPerson.getPosition(prevDeletedPerson);
              View vPrevDeleted = lvPersons.getChildAt(pos - lvPersons.getFirstVisiblePosition());
              adapterPerson.animateRemoval(vPrevDeleted);
              removePrevDeleted = false;
              prevDeletedPerson = null;
            }
          }
          catch (Exception ex)
          {
            Log.e(TAG_LOG, "onVerticalScroll: " + ex.getMessage());
          }
        }
      });
    }
    catch (Exception ex)
    {
      Log.e(TAG_LOG, "onCreate: " + ex.getMessage());
    }
  }


  /**
   * Used to handle callbacks when the user swipes list items.
   */
  private JBHorizontalSwipe.IJBHorizontalSwipe ijbHorizontalSwipe = new JBHorizontalSwipe.IJBHorizontalSwipe()
  {
    @Override
    public void onReposition(float x, boolean scrollingRight, float scrollDelta)
    {
      // Currently not used. You can use this callback to do something while the user is swiping a list item.
    }

    @Override
    public void onTopViewVisibilityChange(View vTop, boolean visible)
    {
      try
      {
        // This callback gets called when the list item's top view changes from fully visible to
        // fully invisible.

        vgSwiped = (ViewGroup) vTop.getParent();
        final Person person = (Person) vgSwiped.getTag();
        person.deleted = !visible;
        removePrevDeleted = false;

        // Using setPressed is necessary in various places throughout the app in order
        // to restore the background color of the top view. This is required because list
        // items don't receive the ACTION_UP event which would normally restore the background
        // color. The ACTION_UP is not received because code in PersonListViewOrder as well
        // JBHorizontalSwipe and CustomListItem intercept the motion events and take over
        // control when a ACTION_DOWN is received.

        vTop.setPressed(false);
        lvPersons.setPressed(false);

        if ((person == prevDeletedPerson) && !person.deleted)
          prevDeletedPerson = null;

        if ((person.deleted) && (prevDeletedPerson != null) && (person != prevDeletedPerson))
          removePrevDeleted = true;

        View vBottom = vgSwiped.findViewWithTag(TAG_BOTTOM_VIEW);
        PropertyValuesHolder pvhAlphaCurrent;

        ButtonBottomView btnUndo = (ButtonBottomView) vBottom.findViewById(R.id.btnUndo);
        adapterPerson.onItemSwiped(person, btnUndo);

        // If the top view is swiped out of view, we want to animate the bottom view's
        // visibility to gradually show, which is done by changing its alpha.

        if (person.deleted)
          pvhAlphaCurrent = PropertyValuesHolder.ofFloat("alpha", 0, 1);
        else
          pvhAlphaCurrent = PropertyValuesHolder.ofFloat("alpha", 1, 0);

        ObjectAnimator animatorView = ObjectAnimator.ofPropertyValuesHolder(vBottom, pvhAlphaCurrent);
        animatorView.setInterpolator(new LinearInterpolator());
        animatorView.setDuration(300);

        animatorView.addListener(new Animator.AnimatorListener()
        {
          @Override
          public void onAnimationStart(Animator animation)
          {

          }

          @Override
          public void onAnimationEnd(Animator animation)
          {
            try
            {
              // If a previous item has been deleted but is still visible, we
              // need to remove it from the list using some animation.

              if (removePrevDeleted)
              {
                int pos = adapterPerson.getPosition(prevDeletedPerson);

                if ((pos >= lvPersons.getFirstVisiblePosition()) && (pos <= lvPersons.getLastVisiblePosition()))
                {
                  View vPrevDeleted = lvPersons.getChildAt(pos - lvPersons.getFirstVisiblePosition());
                  adapterPerson.animateRemoval(vPrevDeleted);
                }
                else
                {
                  adapterPerson.remove(prevDeletedPerson);
                }
              }

              if (person.deleted)
                prevDeletedPerson = person;
            }
            catch (Exception ex)
            {
              Log.e(TAG_LOG, "onTopViewVisibilityChange.onAnimationEnd: " + ex.getMessage());
            }
          }

          @Override
          public void onAnimationCancel(Animator animation)
          {

          }

          @Override
          public void onAnimationRepeat(Animator animation)
          {

          }
        });

        animatorView.start();
      }
      catch (Exception ex)
      {
        Log.e(TAG_LOG, "onTopViewVisibilityChange: " + ex.getMessage());
      }
    }
  };


  /**
   * Used to intercept touch events.
   */
  public boolean dispatchTouchEvent(MotionEvent ev)
  {
    try
    {
      if (this.jbHorizontalSwipe != null)
        this.jbHorizontalSwipe.onRootDispatchTouchEventListener(ev);
    }
    catch (Exception ex)
    {
      Log.e(TAG_LOG, "dispatchTouchEvent: " + ex.getMessage());
    }

    return super.dispatchTouchEvent(ev);
  }


  /**
   * Generates a unique ID.
   *
   * @return Returns a random number.
   */
  private long getNewId()
  {
    Random r = new Random();
    return r.nextLong();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings)
    {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  interface IListItemControls
  {
    /**
     * A callback that gets called when the user taps on the Undo button.
     *
     * @param v The view that represents the Undo button.
     */
    void onUndoClicked(View v);
  }
}
