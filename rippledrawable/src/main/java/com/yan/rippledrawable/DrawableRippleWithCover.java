package com.yan.rippledrawable;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 * @author genius158
 *
 * work on all kinds of drawable
 * use shader to cteate cover
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) class DrawableRippleWithCover
    extends RippleDrawable implements Drawable.Callback {
  private final Drawable original;
  private final Drawable mask;
  private final Rect bounds;
  private final RenderInner renderInner;

  DrawableRippleWithCover(Drawable original, Drawable mask, int color) {
    super(ColorStateList.valueOf(Color.BLACK), null, null);
    this.original = original;
    this.mask = mask;
    this.bounds = new Rect();
    this.renderInner = new RenderInner(color);

    if (original != null) {
      original.setCallback(this);
    } else {
      mask.setCallback(this);
    }
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (original != null) {
      original.draw(canvas);
    }
    renderInner.draw(canvas);
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
    if (enabled && (pressed || focused || hovered)) {
      renderInner.loadShader();
    }
    return super.onStateChange(stateSet);
  }

  @Override protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    if (this.bounds.equals(bounds)) {
      return;
    }

    this.bounds.set(bounds);
    if (original != null) {
      original.setBounds(this.bounds);
    }
    if (mask != null) {
      mask.setBounds(this.bounds);
    }
  }

  @Override public boolean setVisible(boolean visible, boolean restart) {
    if (!visible) {
      renderInner.clear();
    }
    return super.setVisible(visible, restart);
  }

  private class RenderInner {
    private Bitmap bitmapRipple;
    private Canvas bitmapCanvas;

    private final Paint paint;

    RenderInner(int color) {
      paint = new Paint();
      paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }

    void loadShader() {
      if (paint.getShader() != null) {
        return;
      }
      Drawable drawable = original == null ? mask : original;
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

    void draw(Canvas canvas) {
      if (!isVisible()) {
        return;
      }
      if (bitmapRipple == null) {
        bitmapRipple = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
      }

      if (bitmapRipple != null) {
        if (bitmapCanvas == null) {
          bitmapCanvas = new Canvas(bitmapRipple);
        }
        bitmapRipple.eraseColor(Color.TRANSPARENT);
        DrawableRippleWithCover.super.draw(bitmapCanvas);
        canvas.drawBitmap(bitmapRipple, null, bounds, paint);
      }
    }

    void clear() {
      paint.setShader(null);
      if (bitmapRipple != null) {
        bitmapRipple.recycle();
        bitmapRipple = null;
      }
      bitmapCanvas = null;
    }
  }
}
