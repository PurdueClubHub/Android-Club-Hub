package edu.purdue.purdueclubhub;

/**
 * Created by Cameron on 2/24/2015.
 */

 import android.graphics.drawable.Drawable;

public class NavigationItem {
    private String mText;
   // private Drawable mDrawable;

    public NavigationItem(String text) {
        mText = text;
        //mDrawable = drawable;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    /*public Drawable getDrawable() {
        return mDrawable;
    }*/

   /* public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }*/
}
