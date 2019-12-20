package com.yan.demo;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.yan.rippledrawable.RippleLayout;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.e("MainActivity", "onCreate: "+    SecurityCheckUtil.getInstance().isRoot()
        +"   "+SecurityCheckUtil.getInstance().isSUExist()
    );
    Log.e("MainActivity",
        "onCreate: " + (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)

    +"   \n"+ android.os.Debug.isDebuggerConnected());

    setContentView(R.layout.activity_main2);
    ImageView ivTest = findViewById(R.id.iv_code);
    if (ivTest != null) {
      Drawable testDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.test_icon);
      ((ImageView) findViewById(R.id.iv_code)).setImageDrawable(
          RippleLayout.getRippleDrawable(testDrawable, testDrawable, RippleLayout.DEFAULT_COLOR));
    }
    RecyclerView rv = findViewById(R.id.rv);
    if (rv != null) {
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
}
