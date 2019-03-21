package com.yan.rippledrawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * @author genius158
 *
 * good work on
 * <br>
 * ShapeDrawable
 * GradientDrawable
 * NinePatchDrawable
 * BitmapDrawable
 * <br/>
 *
 * use drawable setColorFiller change self color
 * to set mask
 */
class DrawableWithCoverTint extends Drawable implements Drawable.Callback {
  private int color;

  private final Drawable original;
  private final Drawable coverOriginal;
  private Drawable mask;
  private boolean coverShow;
  private Rect bounds = new Rect();

  DrawableWithCoverTint(Drawable original, Drawable coverOriginal, int color) {
    this.original = original;
    this.coverOriginal = coverOriginal;
    this.color = color;
    if (original != null) {
      original.setCallback(this);
    }
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (original != null) {
      original.draw(canvas);
    }
    if (!coverShow || mask == null) {
      return;
    }
    mask.draw(canvas);
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
      coverBitmap(coverOriginal == null ? original : coverOriginal, color);
    } else {
      if (mask != null) {
        mask.setCallback(null);
        mask = null;
      }
    }

    invalidateSelf();
    return super.onStateChange(stateSet);
  }

  private void coverBitmap(Drawable original, int color) {
    if (mask != null || original instanceof StateListDrawable) {
      return;
    }
    if (original != null) {
      ConstantState cs = original.getConstantState();
      if (cs != null) {
        mask = tintDrawable(cs.newDrawable(), ColorStateList.valueOf(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          mask.setLayoutDirection(original.getLayoutDirection());
        }
      }
    }

    if (mask == null) {
      mask = new ColorDrawable(color);
    }
    mask.setBounds(bounds);
    mask.setCallback(this);
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

  @Override public boolean isStateful() {
    return true;
  }

  @Override public boolean setVisible(boolean visible, boolean restart) {
    if (original != null) {
      original.setVisible(visible, restart);
    }
    return super.setVisible(visible, restart);
  }

  private Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
    final Drawable wrappedDrawable = DrawableCompat.wrap(drawable).mutate();
    DrawableCompat.setTintList(wrappedDrawable, colors);
    return wrappedDrawable;
  }

  @Override public int getIntrinsicWidth() {
    if (original != null) {
      return original.getIntrinsicWidth();
    }
    if (coverOriginal != null) {
      return coverOriginal.getIntrinsicWidth();
    }
    return super.getIntrinsicWidth();
  }

  @Override public int getIntrinsicHeight() {
    if (original != null) {
      return original.getIntrinsicHeight();
    }
    if (coverOriginal != null) {
      return coverOriginal.getIntrinsicHeight();
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
