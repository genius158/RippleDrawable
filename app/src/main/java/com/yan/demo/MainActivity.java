package com.yan.demo;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.yan.rippledrawable.RippleLayout;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);
    //Drawable testDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.test_icon);
    //((ImageView) findViewById(R.id.iv_code)).setImageDrawable(
    //    RippleLayout.getRippleDrawable(testDrawable, testDrawable, RippleLayout.DEFAULT_COLOR));
    RecyclerView rv = findViewById(R.id.rv);
    rv.setLayoutManager(new LinearLayoutManager(this));
    rv.setAdapter(new RecyclerView.Adapter() {
      @NonNull @Override
      public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecyclerView.ViewHolder(
            getLayoutInflater().inflate(R.layout.activity_item, viewGroup, false)) {
        };
      }

      @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

      }

      @Override public int getItemCount() {
        return 40;
      }
    });
  }
}
