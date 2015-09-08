package com.citrus.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity {

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected abstract int getLayoutResource();

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    public void setSpannableTitle(String title, ActionBar actionBar) {
//        SpannableString s = new SpannableString(title);
//        s.setSpan(new TypefaceSpan(this, "Raleway-SemiBold.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_dark)),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//// Update the action bar title with the TypefaceSpan instance
//        actionBar.setTitle(s);
//    }
}