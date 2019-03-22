package com.yan.rippledrawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * @author genius158
 */
abstract class AbstractDrawableWithCover extends Drawable implements Drawable.Callback {
  final Drawable original;
  final Drawable mask;
  final Rect bounds = new Rect();

  AbstractDrawableWithCover(Drawable original, Drawable mask) {
    this.original = original;
    this.mask = mask;

    if (original != null) {
      original.setCallback(this);
    }
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (original != null) {
      original.draw(canvas);
    }
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

    onStateChange(enabled, pressed, focused, hovered);
    return true;
  }

  abstract void onStateChange(boolean enabled, boolean pressed, boolean focused, boolean hovered);

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

  @Override public boolean setVisible(boolean visible, boolean restart) {
    if (original != null) {
      original.setVisible(visible, restart);
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

  Drawable getMask() {
    return mask == null ? original : mask;
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
