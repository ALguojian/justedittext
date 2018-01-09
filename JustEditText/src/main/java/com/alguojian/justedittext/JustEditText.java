package com.alguojian.justedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * 自定义的editText控件
 *
 * @author ALguojian
 * @date 2018/1/9
 */
public class JustEditText extends AppCompatEditText {

    /**
     * 定义画笔--绘制分割线
     */
    private Paint mPaint;

    /**
     * 删除图标--资源ID
     */
    private int ic_deleteResID;


    /**
     * 删除图标对象
     */
    private Drawable ic_delete;

    /**
     * 删除图标起点(X,Y)，删除图标宽高-单位-px
     */
    private int delete_x, delete_y, delete_width, delete_height;

    /**
     * 左侧图标 资源ID（点击 & 无点击）
     */
    private int ic_left_clickResID, ic_left_unclickResID;

    /**
     * 左侧图标（点击 & 未点击）
     */
    private Drawable ic_left_click, ic_left_unclick;

    /**
     * 左侧图标起点（x,y）、左侧图标宽、高（px）
     */
    private int left_x;
    private int left_y, left_width, left_height;

    /**
     * 光标
     */
    private int cursor;

    /**
     * 点击时 & 未点击颜色
     */
    private int lineColor_click, lineColor_unclick;
    private int color;

    private Context mContext;


    public JustEditText(Context context) {
        super(context);
        mContext = context;
    }


    public JustEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        init(context, attrs);

    }

    /**
     * 初始化资源
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {

        // 获取控件资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JustEditText);

        //1.点击状态时左侧图标
        ic_left_clickResID = typedArray.getResourceId(R.styleable.JustEditText_ic_left_click, R.drawable.ic_left_click);
        ic_left_click = ContextCompat.getDrawable(context, ic_left_clickResID);

        //2.设置图标大小
        left_x = dpTopx(typedArray.getDimension(R.styleable.JustEditText_left_x, 0));
        left_y = dpTopx(typedArray.getDimension(R.styleable.JustEditText_left_y, 0));
        left_width = dpTopx(typedArray.getDimension(R.styleable.JustEditText_left_width, 10));
        left_height = dpTopx(typedArray.getDimension(R.styleable.JustEditText_left_height, 10));

        //3.设置图标的位置，左上右下
        ic_left_click.setBounds(left_x, left_y, left_height, left_height);

        //4.未点击状态左侧图标
        ic_left_unclickResID = typedArray.getResourceId(R.styleable.JustEditText_ic_left_unclick, R.drawable.ic_left_unclick);
        ic_left_unclick = ContextCompat.getDrawable(context, ic_left_unclickResID);
        ic_left_unclick.setBounds(left_x, left_y, left_width, left_height);

        //5.初始化删除按钮图标：
        ic_deleteResID = typedArray.getResourceId(R.styleable.JustEditText_ic_delete, R.drawable.delete);
        ic_delete = ContextCompat.getDrawable(context, ic_deleteResID);

        //6.设置图标大小
        delete_x = dpTopx(typedArray.getDimension(R.styleable.JustEditText_delete_x, 0));
        delete_y = dpTopx(typedArray.getDimension(R.styleable.JustEditText_delete_y, 0));
        delete_width = dpTopx(typedArray.getDimension(R.styleable.JustEditText_delete_width, 10));
        delete_height = dpTopx(typedArray.getDimension(R.styleable.JustEditText_delete_height, 10));

        //7.设置删除按钮位置
        ic_delete.setBounds(delete_x, delete_y, delete_width, delete_height);

        //8.设置输入框左侧图片，只有这一个，并且是未点击状态
        /**setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom)介绍
         作用：在EditText上、下、左、右设置图标（相当于android:drawableLeft=""  android:drawableRight=""）
         备注：传入的Drawable对象必须已经setBounds(x,y,width,height)，即必须设置过初始位置、宽和高等信息
         x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点 width:组件的长度 height:组件的高度
         若不想在某个地方显示，则设置为null**/

        // 另外一个相似的方法：setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom)
        // 作用：在EditText上、下、左、右设置图标
        // 与setCompoundDrawables的区别：setCompoundDrawablesWithIntrinsicBounds（）传入的Drawable的宽高=固有宽高（自动通过getIntrinsicWidth（）& getIntrinsicHeight（）获取）
        // 不需要设置setBounds(x,y,width,height)
        setCompoundDrawables(ic_left_unclick, null, null, null);

        //9.初始化光标（颜色，以及粗细）通过 反射机制 动态设置光标
        cursor = typedArray.getResourceId(R.styleable.JustEditText_cursor, R.drawable.cursor);

        try {
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            field.set(this, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //10.初始化画笔，以及颜色
        mPaint = new Paint();
        //分割线粗细
        mPaint.setStrokeWidth(2.0f);
        lineColor_click = typedArray.getColor(R.styleable.JustEditText_lineColor_click, ContextCompat.getColor(context, R.color.lineColor_click));
        lineColor_unclick = typedArray.getColor(R.styleable.JustEditText_lineColor_unclick, ContextCompat.getColor(context, R.color.lineColor_unclick));
        color = lineColor_unclick;
        mPaint.setColor(lineColor_unclick);
        setTextColor(lineColor_unclick);

        setBackground(null);

        //用完之后回收
        typedArray.recycle();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setDeleteViewVisible(hasFocus() && text.length() > 0, hasFocus());
    }

    /**
     * 设置删除按钮动态隐藏显示
     * hasFocus()返回是否获得EditTEXT的焦点，即是否选中
     * setDeleteIconVisible（） = 根据传入的是否选中 & 是否有输入来判断是否显示删除图标->>关注1
     */
    private void setDeleteViewVisible(boolean b, boolean b1) {

        setCompoundDrawables(b1 ? ic_left_click : ic_left_unclick, null, b ? ic_delete : null, null);
        color = b1 ? lineColor_click : lineColor_unclick;
        setTextColor(color);
        //刷新数据
        invalidate();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setDeleteViewVisible(focused && length() > 0, focused);
    }


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

}
