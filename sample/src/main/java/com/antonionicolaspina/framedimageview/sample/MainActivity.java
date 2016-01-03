package com.antonionicolaspina.framedimageview.sample;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.antonionicolaspina.framedimageview.FramedImageView;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);

    MenuItem menuItem = menu.findItem(R.id.ok);
    menuItem.setOnMenuItemClickListener(this);

    return true;
  }

  @Override
  public boolean onMenuItemClick(MenuItem menuItem) {
    final FramedImageView framedImageView = (FramedImageView) findViewById(R.id.framed_image_view);

    final float scale   = framedImageView.getScale();
    final PointF offset = framedImageView.getOffset();
    final String text   = String.format("Scale: %.2f%%, offset: (%.2f%%, %.2f%%)", scale*100f, offset.x*100f, offset.y*100f);

    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    return true;
  }
}
