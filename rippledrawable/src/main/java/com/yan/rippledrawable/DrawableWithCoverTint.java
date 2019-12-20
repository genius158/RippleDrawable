package com.yan.rippledrawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

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
 * to set coverMask
 */
class DrawableWithCoverTint extends AbstractDrawableWithCover {
  private int color;
  private Drawable coverMask;
  private boolean isCoverShow;

  DrawableWithCoverTint(Drawable original, Drawable mask, int color) {
    super(original, mask);
    this.color = color;
  }

  @Override public void draw(@NonNull Canvas canvas) {
    super.draw(canvas);
    if (isCoverShow && coverMask != null) {
      coverMask.draw(canvas);
    }
  }

  @Override void onStateChange(boolean enabled, boolean pressed, boolean focused, boolean hovered) {
    isCoverShow = enabled && (pressed || focused || hovered);
    if (isCoverShow) {
      loadCoverMask();
    } else {
      if (coverMask != null) {
        coverMask.setCallback(null);
        coverMask = null;
      }
    }
    invalidateSelf();
  }

  private void loadCoverMask() {
    if (coverMask != null || original instanceof StateListDrawable) {
      return;
    }
    if (original != null) {
      ConstantState cs = original.getConstantState();
      if (cs != null) {
        coverMask = tintDrawable(cs.newDrawable(), ColorStateList.valueOf(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          coverMask.setLayoutDirection(original.getLayoutDirection());
        }
      }
    }

    if (coverMask == null) {
      coverMask = new ColorDrawable(color);
    }
    coverMask.setBounds(bounds);
    coverMask.setCallback(this);
  }

  private Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
    final Drawable wrappedDrawable = DrawableCompat.wrap(drawable).mutate();
    DrawableCompat.setTintList(wrappedDrawable, colors);
    return wrappedDrawable;
  }
}
