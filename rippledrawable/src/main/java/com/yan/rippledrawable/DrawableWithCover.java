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
 * use shader to cteate mask
 */
class DrawableWithCover extends Drawable implements Drawable.Callback {
  private final Drawable original;
  private final Drawable mask;
  private boolean coverShow;
  private final Rect bounds = new Rect();
  private final Paint paint = new Paint();

  DrawableWithCover(Drawable original, Drawable mask, int color) {
    this.original = original;
    this.mask = mask;
    paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));

    if (original != null) {
      original.setCallback(this);
    }
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (original != null) {
      original.draw(canvas);
    }
    if (!coverShow) {
      return;
    }
    if (paint.getShader() == null) {
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
    if (original != null) {
      original.setState(stateSet);
    }

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
      loadShader(mask == null ? original : mask);
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
    if (paint.getShader() != null) {
      return;
    }
    Bitmap coverBitmap =
        Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
    Shader shader = new BitmapShader(coverBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    Canvas canvas = new Canvas(coverBitmap);
    if (drawable == null) {
      drawable = new ShapeDrawable();
    }
    drawable.draw(canvas);
    paint.setShader(shader);
  }

  @Override public boolean setVisible(boolean visible, boolean restart) {
    if (original != null) {
      original.setVisible(visible, restart);
    }
    if (!isVisible()) {
      paint.setShader(null);
    }
    return super.setVisible(visible, restart);
  }

  @Override public boolean isStateful() {
    return true;
  }

  @Override public int getIntrinsicWidth() {
    if (original != null) {
      return original.getIntrinsicWidth();
    }
    if (mask != null) {
      return mask.getIntrinsicWidth();
    }
    return super.getIntrinsicWidth();
  }

  @Override public int getIntrinsicHeight() {
    if (original != null) {
      return original.getIntrinsicHeight();
    }
    if (mask != null) {
      return mask.getIntrinsicHeight();
    }

    return super.getIntrinsicHeight();
  }

  @Override public void invalidateDrawable(@NonNull Drawable who) {
    this.invalidateSelf();
  }

  @Override public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
    this.scheduleSelf(what, when);
  }

  @Override public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
    this.unscheduleSelf(what);
  }
}
