package com.yan.rippledrawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import androidx.annotation.NonNull;

/**
 * @author genius158
 *
 * work on all kinds of drawable
 * use shader to cteate mask
 */
class DrawableWithCover extends AbstractDrawableWithCover {
  private final Paint paint = new Paint();
  private boolean isCoverShow;

  DrawableWithCover(Drawable original, Drawable mask, int color) {
    super(original, mask);
    paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
  }

  @Override public void draw(@NonNull Canvas canvas) {
    super.draw(canvas);
    if (!isCoverShow) {
      return;
    }
    if (paint.getShader() != null) {
      canvas.drawRect(bounds, paint);
    }
  }

  @Override void onStateChange(boolean enabled, boolean pressed, boolean focused, boolean hovered) {
    isCoverShow = enabled && (pressed || focused || hovered);
    if (isCoverShow) {
      loadShader();
    }
    invalidateSelf();
  }

  private void loadShader() {
    if (paint.getShader() != null || bounds.width() == 0 || bounds.height() == 0) {
      return;
    }
    Drawable drawable = getMask();
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

  @Override public boolean setVisible(boolean visible, boolean restart) {
    if (!isVisible()) {
      paint.setShader(null);
    }
    return super.setVisible(visible, restart);
  }
}
