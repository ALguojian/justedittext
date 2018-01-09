## 自定义EditText封装常用功能

> 该代码参考以下完成:[SuperEditText](https://github.com/Carson-Ho/SuperEditText)

#### 自定义步骤如下：

1. 添加自定义属性attrs.xml文件：

> 关于自定义属性参考:  [Android自定义属性](https://www.jianshu.com/p/42396bbadf33)

2.初始化资源ID，以及对应尺寸大小

```
  // 获取控件资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JustEditText);

        //1.点击状态时左侧图标
        ic_left_clickResID = typedArray.getResourceId(R.styleable.JustEditText_ic_left_click, R.drawable.ic_left_click);
        ic_left_click = ContextCompat.getDrawable(context, ic_left_clickResID);

```

3.进行dp转化px单位
```
  /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dpTopx(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int pxTodp(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
```

4.初始化画笔，绘制

```
 @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(color);
        setTextColor(color);
        // 绘制分割线
        // 需要考虑：当输入长度超过输入框时，所画的线需要跟随着延伸
        // 解决方案：线的长度 = 控件长度 + 延伸后的长度
        int x=this.getScrollX(); // 获取延伸后的长度
        int w=this.getMeasuredWidth(); // 获取控件长度

        // 传入参数时，线的长度 = 控件长度 + 延伸后的长度
                canvas.drawLine(0, this.getMeasuredHeight()- linePosition, w+x,
                        this.getMeasuredHeight() - linePosition, mPaint);

    }

```

5.添加点击事件`onTouchEvent`
···
 @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                Drawable drawable = ic_delete;
                if (null != drawable &&
                        event.getX() <= (getWidth() - getPaddingRight()) &&
                        event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {

                    // 判断条件说明
                    // event.getX() ：抬起时的位置坐标
                    // getWidth()：控件的宽度
                    // getPaddingRight():删除图标图标右边缘至EditText控件右边缘的距离
                    // 即：getWidth() - getPaddingRight() = 删除图标的右边缘坐标 = X1
                    // getWidth() - getPaddingRight() - drawable.getBounds().width() = 删除图标左边缘的坐标 = X2
                    // 所以X1与X2之间的区域 = 删除图标的区域
                    // 当手指抬起的位置在删除图标的区域（X2=<event.getX() <=X1），即视为点击了删除图标 = 清空搜索框内容

                    setText("");
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

···

6.自定义属性如下：自定义EditText：属性如下：暂不支持代码动态设置

![095196333906964CCD876CAFFFCDA3C8.jpg](http://upload-images.jianshu.io/upload_images/7426378-3b6bab896f6a2891.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
 android:hint="请输入账号"
        android:textSize="14dp"
        //标识符
        app:cursor="@drawable/cursor"
        //删除按钮高度
        app:delete_height="10dp"
        //删除按钮宽度
        app:delete_width="10dp"
        //删除按钮距离左边位置
        app:delete_x="0dp"
        //删除按钮距离上边位置
        app:delete_y="0dp"
        //删除按钮图片资源
        app:ic_delete="@drawable/delete"
        //左边图片获得焦点资源ID
        app:ic_left_click="@drawable/mine_guide2"
        //左边图片未获得焦点资源ID
        app:ic_left_unclick="@drawable/mine_guide"
        //左边图片高度
        app:left_height="10dp"
        //左边图片宽度
        app:left_width="10dp"
        app:left_x="0dp"
        app:left_y="0dp"

        //输入框内获得焦点时候字体颜色
        app:lineColor_click="@color/lineColor_click"
        app:lineColor_unclick="@color/lineColor_unclick"
```



