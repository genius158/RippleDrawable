package com.yan.demo;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.yan.rippledrawable.RippleLayout;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Drawable testDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.test_icon);
    ((ImageView) findViewById(R.id.iv_code)).setImageDrawable(
        RippleLayout.getRippleDrawable(testDrawable, 1, testDrawable, RippleLayout.DEFAULT_COLOR));
  }
}
