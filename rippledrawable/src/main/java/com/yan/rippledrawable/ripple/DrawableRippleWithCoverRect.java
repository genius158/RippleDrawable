package com.yan.rippledrawable.ripple;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Rect;
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

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) class DrawableRippleWithCoverRect
    extends RippleDrawable implements Drawable.Callback {
  private final Drawable original;

  DrawableRippleWithCoverRect(Drawable original, int color) {
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

  @Override public void setHotspot(float x, float y) {
    super.setHotspot(x, y);
  }


  @Override protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    if (original != null) {
      original.setBounds(bounds);
    }
  }

  @Override public int getIntrinsicWidth() {
    if (original != null) {
      return original.getIntrinsicWidth();
    }
    return super.getIntrinsicWidth();
  }

  @Override public int getIntrinsicHeight() {
    if (original != null) {
      return original.getIntrinsicHeight();
    }
    return super.getIntrinsicHeight();
  }

  @Override protected boolean onStateChange(int[] stateSet) {
    return super.onStateChange(stateSet);
  }
}
