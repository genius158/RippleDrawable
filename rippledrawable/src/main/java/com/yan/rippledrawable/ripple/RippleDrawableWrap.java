package com.yan.rippledrawable.ripple;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

public class RippleDrawableWrap extends Drawable implements Drawable.Callback {
  private static final String TAG = "RippleDrawableWrap";
  private static final long ENTER_DURATION = 1500;
  private static final long DURING_EXIT = 200;

  private Drawable original;
  private Drawable mask;

  private Paint paint;
  private int paintAlpha;
  private PointF point = new PointF();
  private Rect bounds = new Rect();

  @Override public void setHotspot(float x, float y) {
    super.setHotspot(x, y);
    point.set(x, y);
  }

  private final RippleAnim rippleAnim;

  public RippleDrawableWrap(Drawable original, Drawable mask, int color) {
    this.original = original;
    this.mask = mask;
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(color);
    paint.setStyle(Paint.Style.FILL);
    paint.setColorFilter(new PorterDuffColorFilter(
        Color.rgb(Color.red(color), Color.green(color), Color.blue(color)),
        PorterDuff.Mode.DST_IN));
    paintAlpha = paint.getAlpha();

    rippleAnim = new RippleAnim();
    if (original != null) {
      original.setCallback(this);
    }
  }

  private float getMaxRadius() {
    return (float) Math.sqrt(Math.pow(bounds.width(), 2) + Math.pow(bounds.height(), 2));
  }

  @Override protected void onBoundsChange(Rect bounds) {
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
    invalidateSelf();
    Log.e(TAG, "onBoundsChange: " + bounds);
  }

  @Override public boolean isStateful() {
    return true;
  }

  @Override public void draw(@NonNull Canvas canvas) {
    drawRipple(canvas);
  }

  private void drawRipple(Canvas canvas) {
    if (original != null) {
      original.draw(canvas);
    }
    canvas.drawCircle(point.x, point.y, rippleAnim.getRadius(), paint);
  }

  @Override public void setAlpha(int alpha) {
  }

  @Override public void setColorFilter(ColorFilter colorFilter) {
  }

  @Override public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override public boolean setVisible(boolean visible, boolean restart) {
    if (!visible) {
      rippleAnim.applyReleaseAnim();
    }
    return super.setVisible(visible, restart);
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

    if (enabled && pressed) {
      rippleAnim.applyPressAnim();
    }

    boolean coverShow = enabled && (pressed || focused || hovered);
    if (coverShow) {
      loadShader(mask == null ? original : mask);
    } else {
      rippleAnim.applyReleaseAnim();
    }

    Log.e(TAG, "onStateChange: " + enabled + "  " + focused + "  " + pressed + "   " + hovered);

    invalidateSelf();
    return true;
  }

  class RippleAnim {
    private ValueAnimator rippleAnim;
    private float radius = 0;

    void setValue(float radius, int alpha) {
      Log.e(TAG, "setValue: " + radius + "   " + alpha + " " + paint.getAlpha());
      this.radius = radius;
      paint.setAlpha(alpha);
      invalidateSelf();
    }

    float getRadius() {
      return radius;
    }

    void applyPressAnim() {
      cancel();
      reset();
      final float maxRadius = getMaxRadius();
      final float startRadius = radius;
      rippleAnim = ValueAnimator.ofFloat(0F, 1F);
      rippleAnim.setDuration(ENTER_DURATION);
      rippleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
      rippleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          float value = (float) animation.getAnimatedValue();
          float tempRadius = value * (maxRadius - startRadius) + startRadius;
          setValue(tempRadius, paintAlpha);
        }
      });
      rippleAnim.start();
    }

    private void applyReleaseAnim() {
      cancel();
      final float maxRadius = getMaxRadius();
      final float startRadius = radius;
      final float startAlpha = paintAlpha;

      if (startRadius <= maxRadius) {
        rippleAnim = ValueAnimator.ofFloat(0F, 1F);
        rippleAnim.setDuration(DURING_EXIT);
        rippleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        rippleAnim.addListener(new AnimatorListenerAdapter() {
          @Override public void onAnimationEnd(Animator animation) {
            invalidateSelf();
          }
        });
        rippleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            float tempRadius = value * (maxRadius - startRadius) + startRadius;
            setValue(tempRadius, (int) ((1 - value) * startAlpha));
          }
        });
        rippleAnim.start();
      }
    }

    void cancel() {
      if (rippleAnim != null && rippleAnim.isRunning()) {
        rippleAnim.cancel();
      }
    }

    private void reset() {
      this.radius = 0;
    }
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