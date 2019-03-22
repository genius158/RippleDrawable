# RippleDrawable (ripple with mask|带遮罩的水波纹)
Google商店，应用想要被推荐，必须符合其开发规范，其中按钮效果需要使用水波纹，但是由于系统的阴影的效果很一般，
而且设计的阴影样式多变，所以一般需要阴影由切图自带，这样的图片加上水波纹，会覆盖到图片阴影部分
如果切图带圆角，或者其他什么不规则的形状，那么水波纹就很难看，
基于以上的问题，想给水波纹加上mask，达到比较好的效果

<br/>
低版本由于性能问题，我并不准备让低版本也产生水波纹的效果，如果那是你的期望，RippleDrawableWrap 可以在低版本使用，但是
波纹产生的位置，可能需要你自行传入
## gradle
implementation 'com.yan:rippledrawable:1.0.1'

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
rippleStyle borderless or normal(tip: api >= 28 selectableItemBackground selectableItemBackgroundBorderless animation is the same 
| api >=28 selectableItemBackground selectableItemBackgroundBorderless 效果是一样的 )
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