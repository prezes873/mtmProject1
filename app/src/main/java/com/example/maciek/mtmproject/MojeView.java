package com.example.maciek.mtmproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MojeView extends View{

    private float[] dane=null;
    List<float[]> listPoint;
    List<Object> objectList;
    public float x;
    public float y;
    public String[] nazwa;
    public MojeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDane(List<float[]> listPoint) {
        this.listPoint = listPoint;

    }

    public void setOjectList(List<Object> objectList) {
        this.objectList = objectList;

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
        p.setTextSize(40.0f);

        float sy = canvas.getHeight();
        float sx = canvas.getWidth();

        for (int i = 0; i < listPoint.size(); i++)
        {
            float[] temp = listPoint.get(i).clone();
            float drawy = temp[1]*sy+sy/2;
            float drawx =  -temp[0]*sx+sx/2;

            if (temp[2] < 0)
            {
                canvas.drawCircle(drawx, drawy, 30, p);
                canvas.drawText(objectList.get(i).nazwa, drawx , drawy - 70 ,p  );
            }
        }

    }



}
