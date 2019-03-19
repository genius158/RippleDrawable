package com.yan.demo;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.yan.rippledrawable.RippleLayout;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ((ImageView) findViewById(R.id.iv_code)).setImageDrawable(
        RippleLayout.getRippleDrawable(ContextCompat.getDrawable(this, R.drawable.test_icon)));
  }
}
