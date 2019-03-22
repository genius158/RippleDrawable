package com.yan.rippledrawable;

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
import android.view.animation.DecelerateInterpolator;

class RippleDrawableWrap extends Drawable implements Drawable.Callback {
  private final int DURING_ALPHA = 350;

  private final Drawable original;
  private final Drawable mask;

  private Paint paint;
  private int paintAlpha;
  private final PointF touchPoint = new PointF();
  private final Rect bounds = new Rect();

  private final RippleAnim rippleAnim;

  RippleDrawableWrap(Drawable original, Drawable mask, int color) {
    this.original = original;
    this.mask = mask;
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(color);
    paint.setStyle(Paint.Style.FILL);
    paint.setColorFilter(new PorterDuffColorFilter(
        Color.argb(185, Color.red(color), Color.green(color), Color.blue(color)),
        PorterDuff.Mode.DST_IN));
    paintAlpha = paint.getAlpha();

    rippleAnim = new RippleAnim();
    if (original != null) {
      original.setCallback(this);
    }
  }

  @Override public void draw(@NonNull Canvas canvas) {
    if (original != null) {
      original.draw(canvas);
    }
    if (rippleAnim.rippleAnim != null) {
      canvas.drawCircle(touchPoint.x, touchPoint.y, rippleAnim.getRadius(), paint);
    }
  }

  private float getMaxRadius(boolean withPoint) {
    float h = bounds.width();
    float v = bounds.height();
    if (withPoint) {
      h = Math.max(touchPoint.x, bounds.width() - touchPoint.x);
      v = Math.max(touchPoint.y, bounds.height() - touchPoint.y);
    }
    return (float) Math.sqrt(Math.pow(h, 2) + Math.pow(v, 2));
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
  }

  @Override public boolean isStateful() {
    return true;
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
      rippleAnim.end();
      rippleAnim.rippleAnim = null;
      paint.setShader(null);
    }
    return super.setVisible(visible, restart);
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

    if (enabled && pressed) {
      rippleAnim.tapDown();
    }

    boolean coverShow = enabled && (pressed || focused || hovered);
    if (coverShow) {
      loadShader(mask == null ? original : mask);
    } else {
      rippleAnim.tapUp();
    }

    if (rippleAnim.rippleAnim != null) {
      invalidateSelf();
    }
    return true;
  }

  class RippleAnim {
    private ValueAnimator rippleAnim;
    private float radius = 0;

    float getRadius() {
      return radius;
    }

    private ValueAnimator getAnim(int during, ValueAnimator.AnimatorUpdateListener updateListener) {
      ValueAnimator rippleAnim = ValueAnimator.ofInt(0, during);
      rippleAnim.setDuration(during);
      rippleAnim.addUpdateListener(updateListener);
      return rippleAnim;
    }

    void tapDown() {
      final float maxRadius = getMaxRadius(false);
      final float touchRadius = getMaxRadius(true);
      final float startRadius = Math.min(touchRadius / 4, 50);
      final int startAlpha = paintAlpha;
      cancel();

      final int enterDuring = (int) (maxRadius * (maxRadius / touchRadius));

      rippleAnim = getAnim(enterDuring, new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          int value = (int) animation.getAnimatedValue();
          if (value <= DURING_ALPHA) {
            float offset = value / (float) DURING_ALPHA;
            paint.setAlpha((int) (offset * startAlpha));
          } else if (paint.getAlpha() != startAlpha) {
            paint.setAlpha(startAlpha);
          }

          float offset = value / (float) enterDuring;
          radius = offset * (maxRadius - startRadius) + startRadius;

          invalidateSelf();
        }
      });
      rippleAnim.setInterpolator(new DecelerateInterpolator(0.8F));
      rippleAnim.start();
    }

    private void tapUp() {
      if (rippleAnim == null) {
        return;
      }
      cancel();

      final float maxRadius = getMaxRadius(true);
      final float startRadius = Math.max(maxRadius / 3, radius);
      final float startAlpha = paintAlpha;

      rippleAnim = getAnim(DURING_ALPHA, new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          int value = (int) animation.getAnimatedValue();
          float offset = value / (float) DURING_ALPHA;
          radius = offset * (maxRadius - startRadius) + startRadius;
          radius = Math.min(radius, maxRadius);
          paint.setAlpha((int) ((1 - offset) * startAlpha));
          invalidateSelf();
        }
      });
      rippleAnim.setInterpolator(new DecelerateInterpolator());
      rippleAnim.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          rippleAnim = null;
        }
      });
      rippleAnim.start();
    }

    void cancel() {
      if (rippleAnim != null && rippleAnim.isRunning()) {
        rippleAnim.cancel();
      }
    }

    void end() {
      if (rippleAnim != null && rippleAnim.isRunning()) {
        rippleAnim.end();
      }
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
      drawable.setBounds(bounds);
    }
    drawable.draw(canvas);
    paint.setShader(shader);
  }

  @Override public void setHotspot(float x, float y) {
    super.setHotspot(x, y);
    touchPoint.set(x, y);
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