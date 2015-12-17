package com.example.maciek.mtmproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MojeView extends View{

    private float[] dane=null;

    public float x;
    public float y;
    public String nazwa;
    public MojeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDane(float[] dane) {
        this.dane = dane;
        j++;
    }

    private int i =0;
    private int j=0;

    public MojeView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Paint p = new Paint();
        p.setARGB(255, 255, 0, 0);

       // canvas.drawText("MojeView.onDraw: i=" + i++, 200, 100, p);



        float sy = canvas.getHeight();
        float sx = canvas.getWidth();

        float drawy = x * sy + sy / 2;
        float drawx =  y * sx + sx / 2;
        canvas.drawCircle(drawx, drawy, 30, p);
        //canvas.drawText(nazwa , drawx , drawy + 20, p);
        //Log.i("x: ", Float.toString(drawx));
        if(dane!=null)
        {
            canvas.drawLine(0, 0, 100*dane[0], 100*dane[0], p);
            p.setStyle(Style.STROKE);
            canvas.drawRect(100, 100, 200, 200, p);

        }
    }



}
