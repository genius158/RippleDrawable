# RippleDrawable (ripple with mask)

## gradle
implementation 'com.yan:rippledrawable:1.0.0'

### screenshot
the bottom bg with shadow
<br/>

Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
<br/>
![Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP](https://raw.githubusercontent.com/genius158/RippleDrawable/master/screenshot/upApi21.gif)
<br/>
Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
<br/>
![Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP](https://raw.githubusercontent.com/genius158/RippleDrawable/master/screenshot/downApi21.gif)

### use
properties
<br/>
rippleColor 波纹颜色
<br/>
rippleStyle borderless or normal(tip: api >= 28 selectableItemBackground selectableItemBackgroundBorderless animation is the same )
<br/>
rippleMask 波纹遮罩
    
```
<com.yan.rippledrawable.RippleLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    >
  <ImageView
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:background="@drawable/btn_bg_shenhe"
      android:clickable="true"
      android:focusable="true"
      android:text="Hello World!"
      />
</com.yan.rippledrawable.RippleLayout>
```

use in java code: RippleLayout.getRippleDrawable(testDrawable, testDrawable, RippleLayout.DEFAULT_COLOR)