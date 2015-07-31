package com.markshoe.stashapp.CustomViews;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.markshoe.stashapp.Backpack;
import com.markshoe.stashapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shoe on 15-05-23.
 * Custom view that creates an icon and can put a number of different effects on drawable.
 * Gradient effect
 * Blur w/ coloring
 * Shadow
 * Color masking drawable
 * To use the class , instantiate the view and set the icon res id and setHighlightColour
 */
public class HighlightedDrawable extends View {
    private static final String LOG_TAG = HighlightedDrawable.class.getSimpleName();
    public final static int STANDARD_MODE = 0;
    public final static int LETTER_MODE = 1;
    /* Icon for the item*/
    private Drawable mIcon ;
    /* HighlightColor */
    private int mHighlightColour;
    private int mIconId;
    private String mTitle;
    public long mItemId;
    Context mContext ;
    public boolean mTag = true;
    private float CORNER_RADIUS = 0f;
    private int CIRCLE_TAG_BUTTON_RADIUS;
    private int mCurrentStyle = 0;


    public HighlightedDrawable(Context context) { this(context, null); }
    public HighlightedDrawable(Context context, AttributeSet attrs) { this(context, attrs, 0); }
    public HighlightedDrawable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (defStyle == LETTER_MODE){
            mCurrentStyle = 1;
        }
        mContext = context;
        if (attrs!=null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.ItemInGridView, 0, 0);
            setIconDrawable(a.getDrawable(R.styleable.ItemInGridView_android_drawable));
            mHighlightColour = a.getInteger(R.styleable.ItemInGridView_highlight_color, 0);
            a.recycle();
        }
        CORNER_RADIUS =  context.getResources().getDisplayMetrics().density*7; // 7 dp corner radius
        CIRCLE_TAG_BUTTON_RADIUS = (int) context.getResources().getDisplayMetrics().density*4;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width = widthSize;
        int height = width;
        setMeasuredDimension(width, height);
    }

    protected void onDraw(Canvas canvas){

        //Drawable gd = addGradient(255, mContext.getResources().getColor(R.color.grey_200),255);
        //this.setBackgroundDrawable(gd);

        int background = mContext.getResources().getColor(R.color.primary_dark);
        int foreground = mContext.getResources().getColor(R.color.grey_100);
        addCircularGradient(255,background, background, 255, canvas);
        if (mCurrentStyle == STANDARD_MODE) {
            mIcon.setBounds(0 + 30, 0 + 30, getMeasuredWidth() - 30, getMeasuredHeight() - 30);
            int red = (foreground & 0xFF0000) / 0xFFFF;
            int green = (foreground & 0xFF00) / 0xFF;
            int blue = foreground & 0xFF;
            float[] matrix = new float[]{0, 0, 0, 0, red
                    , 0, 0, 0, 0, green
                    , 0, 0, 0, 0, blue
                    , 0, 0, 0, 1, 0};
            //addSmallTagCircle(canvas);
            ColorFilter colorFilter2 = new ColorMatrixColorFilter(matrix);
            mIcon.setColorFilter(colorFilter2);
            mIcon.setAlpha(255);
            mIcon.draw(canvas);
        } if (mCurrentStyle == LETTER_MODE){
            String firstLetter = mTitle.substring(0,1);
            TextDrawable drawable2 = TextDrawable.builder()
                    .beginConfig()
                        .textColor(foreground)
                        .toUpperCase()
                    .endConfig()
                    .buildRound(firstLetter, Color.argb(0, 255, 255, 255));
            mIcon = drawable2;
            mIcon.setBounds(0 , 0 , getMeasuredWidth() , getMeasuredHeight() );
            mIcon.setAlpha(255);
            mIcon.draw(canvas);
        }
    }

    public void addSmallTagCircle(Canvas canvas){
        GradientDrawable drawable = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.circle);
        int colordissolved = (mHighlightColour & 0x00FFFFFF) | (255<<24);
        mHighlightColour = (mHighlightColour & 0x00FFFFFF) | (255<<24);
        drawable.setColors(new int[]{mHighlightColour, 0xFFFFFF});
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        drawable.setBounds(getMeasuredWidth() - 40, getMeasuredHeight() - 40, getMeasuredWidth(), getMeasuredHeight());
        drawable.draw(canvas);
    }

    public void addBgCircle(Canvas canvas,int color){
        Paint paint = new Paint();
        int myc = mContext.getResources().getColor(R.color.grey_300);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(myc);
        canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/2 , getMeasuredWidth()/2, paint);

    }

    //
    // SETTERS AND GETTERS
    //

    public void setProperties(int resId, int highlightColour){
        setIcon(resId);
        setHighlightColour(highlightColour);
    }

    public void setIcon(int resId){
        setIconDrawable(getResources().getDrawable(resId));
        mIconId = resId;
        return;
    }
    public int getIconResId(){
        return mIconId;
    }

    private void setIconDrawable(Drawable d){
        if (d!=null){
            mIcon=d;
        }
    }

    public void setItem(Backpack.Item i){
        mItemId = i.itemID;
        setIcon(i.resId);
        setHighlightColour(i.color);
        setTitle(i.title);
        //addGradient(55,i.color,230);
    }

    /**
     * Adds a drop shadow of the highlighed color
     * @param offsetLeft
     * @param offsetTop
     * @param icon
     * @return
     */
    public Drawable addDropShadow(int offsetLeft,int offsetTop,Drawable icon){
        int red = (mHighlightColour & 0xFF0000) / 0xFFFF;
        int green = (mHighlightColour & 0xFF00) / 0xFF;
        int blue = mHighlightColour & 0xFF;
        float[] matrix = { 0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0 };
        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        icon.setColorFilter(colorFilter);
        icon.setBounds(0 + offsetLeft, 0 + offsetTop, getMeasuredWidth() + offsetLeft, getMeasuredHeight() + offsetTop);
        return icon;
    }

    /**
     * 0-255 alpha disolve on the background
     * @param alpha
     */
    public Drawable addGradient(int alpha, int color, int alpha2){
        int colordissolved = (color & 0x00FFFFFF) | (alpha<<24);
        color = (color & 0x00FFFFFF) | (alpha2<<24);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {colordissolved, color});
        gd.setAlpha(230);
        gd.setCornerRadius(0f);
        gd.setCornerRadius(CORNER_RADIUS);
        return gd;
    }

    public void addCircularGradient(int alpha, int color, int color2, int alpha2, Canvas canvas){
        GradientDrawable drawable = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.circle_no_border);
        int colorStart = (color & 0x00FFFFFF) | (alpha<<24);
        int colorEnd = (color2 & 0x00FFFFFF) | (alpha2<<24);
        drawable.setColors(new int[]{colorStart, colorEnd});
        drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        drawable.draw(canvas);
    }

    public Drawable addBlur(){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap blurTemplate = BitmapFactory.decodeResource(getResources(), mIconId, options);
        //define this only once if blurring multiple times
        RenderScript rs = RenderScript.create(mContext);
        //this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
        final Allocation input = Allocation.createFromBitmap(rs, blurTemplate); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(0.5f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);
        return new BitmapDrawable(getResources(), blurTemplate);
    }

    public void setItem(int itemId, int resId, int color, String title){
        mItemId = itemId;
        if (resId != 0) {
            setIcon(resId);
        }
        setHighlightColour(color);
        setTitle(title);
    }
    public void setTitle(String title){
        mTitle = title;
    }
    public String getTitle(){
        return mTitle;
    }
    public int getHighlightColour(){
        return mHighlightColour;
    }
    public void setHighlightColour(int color){
        mHighlightColour = color;
    }
    public void setMode(int mode){mCurrentStyle = mode;}



}

