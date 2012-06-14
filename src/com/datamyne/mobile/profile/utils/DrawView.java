package com.datamyne.mobile.profile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

public  class DrawView extends View {
    Paint paint = new Paint();

    int height;
    int width;
    int color;
    
    final int borderWidth = 1;
    public DrawView(Context context){
    	this(context, Color.RED);
    }
    public DrawView(Context context, int color) {
        this(context, color, 20, 20);
    }
    
    public DrawView(Context context, int color, int heightDips, int widthDips) {
        super(context);     
        height = heightDips;
        width = widthDips;
        this.color = color;
        setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setStrokeWidth(0);
        paint.setColor(color);
        canvas.drawRect(borderWidth, borderWidth,width-borderWidth, height-borderWidth, paint );
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	setMeasuredDimension(width,height);

    }

    private int convertMetric(int dips){
    	Resources r = getResources();
    	float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.getDisplayMetrics());
    	
    	return Float.valueOf(pix).intValue();
    }
    
}
