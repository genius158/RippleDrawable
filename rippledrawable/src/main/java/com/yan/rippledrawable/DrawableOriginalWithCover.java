package com.yan.rippledrawable;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 * @author genius158
 *
 * work on all kinds of drawable
 * use shader to cteate cover
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) class DrawableOriginalWithCover
    extends RippleDrawable implements Drawable.Callback {
  private final Drawable original;
  private final Paint paint = new Paint();
  private Shader shader;

  DrawableOriginalWithCover(Drawable original, int color) {
    super(ColorStateList.valueOf(color), null, null);
    this.original = original;
    if (original != null) {
      original.setCallback(this);
    }
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (original != null) {
      original.draw(canvas);
    }
    canvas.clipRect(getBounds());
    super.draw(canvas);
  }

  @Override protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    if (original != null) {
      original.setBounds(bounds);
    }
  }

  @Override protected boolean onStateChange(int[] stateSet) {
    super.onStateChange(stateSet);
    boolean enabled = false;
    boolean pressed = false;
    boolean focused = false;
    boolean hovered = false;

    for (int state : stateSet) {
      if (state == android.R.attr.state_enabled) {
        enabled = true;
      } else if (state == android.R.attr.state_focused) {
        focused = true;
      } else if (state == android.R.attr.state_pressed) {
        pressed = true;
      } else if (state == android.R.attr.state_hovered) {
        hovered = true;
      }
    }

    boolean coverShow = enabled && (pressed || focused || hovered);
    if (coverShow) {
      loadShader(original);
    }

    invalidateSelf();
    return super.onStateChange(stateSet);
  }

  private void loadShader(Drawable drawable) {
    if (shader != null) {
      return;
    }
    Rect bounds = getBounds();
    Bitmap coverBitmap =
        Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
    shader = new BitmapShader(coverBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    Canvas canvas = new Canvas(coverBitmap);
    drawable.draw(canvas);
    paint.setShader(shader);
  }
}
