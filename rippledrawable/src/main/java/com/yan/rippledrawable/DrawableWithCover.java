package com.yan.rippledrawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;

/**
 * @author genius158
 *
 * work on all kinds of drawable
 * use shader to cteate cover
 */
class DrawableWithCover extends Drawable {
  private Drawable original;
  private int color;
  private boolean coverShow;
  private Rect bounds = new Rect();
  private Shader shader;
  private Paint paint = new Paint();

  DrawableWithCover(Drawable original, int color) {
    this.original = original;
    this.color = color;
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (original != null) {
      original.draw(canvas);
    }
    if (!coverShow) {
      return;
    }
    if (shader == null) {
      return;
    }
    canvas.drawRect(bounds, paint);
  }

  @Override public void setAlpha(int alpha) {
    if (original != null) {
      original.setAlpha(alpha);
    }
  }

  @Override public void setColorFilter(ColorFilter colorFilter) {
    if (original != null) {
      original.setColorFilter(colorFilter);
    }
  }

  @Override public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override protected boolean onStateChange(int[] stateSet) {
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

    coverShow = enabled && (pressed || focused || hovered);
    if (coverShow) {
      loadShader(original);
    }

    invalidateSelf();
    return super.onStateChange(stateSet);
  }

  @Override protected void onBoundsChange(Rect bounds) {
    if (this.bounds.equals(bounds)) {
      return;
    }
    this.bounds.set(bounds);
    if (original != null) {
      original.setBounds(this.bounds);
    }
    invalidateSelf();
  }

  private void loadShader(Drawable drawable) {
    if (shader != null) {
      return;
    }
    Bitmap coverBitmap =
        Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
    shader = new BitmapShader(coverBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    Canvas canvas = new Canvas(coverBitmap);
    if (drawable == null) {
      drawable = new ShapeDrawable();
    }
    drawable.setBounds(bounds);
    drawable.draw(canvas);
    paint.setShader(shader);
    paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
  }

  @Override public boolean isStateful() {
    return true;
  }
}
