package com.dogeprince.dogego;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 10350 on 2017/3/10.
 */


//存储和恢复
//自由落子

public class GoPanel extends View{
    private List<Point> matchInfo = new ArrayList<>();

    private int LINE_NUM = 9;
    private int ViewWidth;
    private float minViewWidth;

    private Paint mPaint = new Paint();  //棋盘画笔
    private Paint bpNumPaint = new Paint(); //棋子计数画笔
    private Paint wpNumPaint = new Paint();

    private SoundPool soundPool;
    public boolean philosophy = false;
    public boolean ban_cancel = false;

    private Bitmap bp;
    private Bitmap wp;

    private int count ;
    public boolean IsShow = false;

    private int[][] allPiece = new int[LINE_NUM+2][LINE_NUM+2];//*黑棋，@白棋，%墙，#空白
    private boolean[][] willKill = new boolean[LINE_NUM+2][LINE_NUM+2];//true标记

    private boolean haveKilled = false;

    private float pieceSize = 0.9f;

    public int freePiece = -1;

    public GoPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(0x44ff0000);

        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(8.0f);

        bpNumPaint.setColor(0xffffffff);
        bpNumPaint.setTextAlign(Paint.Align.CENTER);
        wpNumPaint.setColor(0xff000000);
        wpNumPaint.setTextAlign(Paint.Align.CENTER);

        bp = BitmapFactory.decodeResource(getResources() , R.drawable.blackchess);
        wp = BitmapFactory.decodeResource(getResources() , R.drawable.whitechess);

        soundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(getContext(),R.raw.aaaaaa,1);
        soundPool.load(getContext(),R.raw.stand,1);
        soundPool.load(getContext(),R.raw.image,1);

        newPanel();
    }

    private void newPanel() {
        count = 2;
        haveKilled = false;
        ban_cancel = false;

        for (int i = 1 ; i<LINE_NUM+1 ; i++)
        {
            for (int j = 1 ; j<LINE_NUM+1 ; j++)
            {
                allPiece[i][j] = -2;
                willKill[i][j] = false;
            }
        }

        for (int i = 0 ; i<LINE_NUM+2 ; i++)
        {
            allPiece[0][i] = -1;
            allPiece[LINE_NUM+1][i] = -1;
            allPiece[i][0] = -1;
            allPiece[i][LINE_NUM+1] = -1;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP)
        {
            int x = (int) event.getX();
            int y = (int) event.getY();

            int soundKey = count;

            x = (int) (x / minViewWidth) + 1;
            y = (int) (y / minViewWidth) + 1;

            if (freePiece != -1)
            {
                allPiece[x][y] = freePiece;
                invalidate();
                ban_cancel = true;

                return true;
            }

            if (allPiece[x][y] == -2)
            {
                allPiece[x][y] = count;
                Point p = new Point(x,y);
                matchInfo.add(p);

                count ++;
                refreshAllPiece(x,y,count);
                if (!haveKilled)
                {
                    count--;
                    if (checkPoint(x,y,count))
                    {
                        count++;
                    }else
                    {
                        allPiece[x][y] = -2;
                    }
                    refreshWillKill();
                }

                if (philosophy)
                {
                    if (haveKilled)
                    {
                        soundPool.play(1, 1, 1, 0, 0, 1);
                    }else
                    {
                        if (count != soundKey)
                        {
                            if (count % 2 == 0)
                            {
                                soundPool.play(2, 1, 1, 0, 0, 1);
                            }else
                            {
                                soundPool.play(3, 1, 1, 0, 0, 1);
                            }
                        }
                    }
                }

                haveKilled = false;
                invalidate();
            }else
            {
                return false;
            }


        }

        return true;

        //return super.onTouchEvent(event);
    }



    private void refreshAllPiece(int x , int y , int count) {

        if (checkPoint(x-1,y,count))
        {
            refreshWillKill();
        }else
        {
            mKillPieces();
        }
        if (checkPoint(x+1,y,count))
        {
            refreshWillKill();
        }else
        {
            mKillPieces();
        }
        if (checkPoint(x,y-1,count))
        {
            refreshWillKill();
        }else
        {
            mKillPieces();
        }
        if (checkPoint(x,y+1,count))
        {
            refreshWillKill();
        }else
        {
            mKillPieces();
        }

    }

    private void refreshWillKill() {
        for (int i = 1 ; i<LINE_NUM+1 ; i++)
        {
            for (int j = 1 ; j<LINE_NUM+1 ; j++)
            {
                willKill[i][j] = false;
            }
        }
    }

    private void mKillPieces() {
        for (int i = 1 ; i<LINE_NUM+1 ; i++)
        {
            for (int j = 1 ; j<LINE_NUM+1 ; j++)
            {
                if (willKill[i][j])
                {
                    willKill[i][j] = !willKill[i][j];
                    allPiece[i][j] = -2;
                    haveKilled = true;
                }
            }
        }
    }

    private boolean checkPoint(int x, int y , int count) {
        if (x == 0 || x == LINE_NUM+1 || y == 0 || y == LINE_NUM+1)
        {
            return false;
        }

        if (allPiece[x][y] == -2)
        {
            return true;
        }

        if ((allPiece[x][y] % 2) == (count % 2) && allPiece[x][y] >= 0 && !willKill[x][y] )//
        {
            willKill[x][y] = !willKill[x][y];
            if (checkPoint(x-1 , y ,count))
            {
                willKill[x][y] = !willKill[x][y];
                return true;
            }
            if (checkPoint(x+1 , y ,count))
            {
                willKill[x][y] = !willKill[x][y];
                return true;
            }
            if (checkPoint(x , y-1 ,count))
            {
                willKill[x][y] = !willKill[x][y];
                return true;
            }
            if (checkPoint(x , y+ 1 ,count)) {
                willKill[x ][y] = !willKill[x ][y];
                return true;
            }
        }

        return false;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED)
        {
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED)
        {
            width = widthSize;
        }

        setMeasuredDimension(width,width);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        ViewWidth = w;
        minViewWidth = w *1.0f / LINE_NUM;

        int pieceWidth = (int) (minViewWidth * pieceSize);


        bpNumPaint.setTextSize((int) (0.43f * minViewWidth) );
        wpNumPaint.setTextSize((int) (0.43f * minViewWidth) );


        bp = Bitmap.createScaledBitmap(bp,pieceWidth,pieceWidth,false);
        wp = Bitmap.createScaledBitmap(wp,pieceWidth,pieceWidth,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mDrawLine(canvas);

        mDrawPieces(canvas);


    }

    private void mDrawPieces(Canvas canvas) {
        for (int i = 1 ; i< LINE_NUM+1 ; i++)
        {
            for (int j = 1 ; j<LINE_NUM+1 ; j++)
            {
                if (allPiece[i][j] % 2 == 0 && allPiece[i][j] >= 0)
                {
                    canvas.drawBitmap(bp , (int)((i - 1 + (1 - pieceSize)/2.0f) * minViewWidth) , (int)((j - 1 + (1 - pieceSize)/2.0f) * minViewWidth) , null);

                }
                if (allPiece[i][j] % 2 == 1  && allPiece[i][j] >= 0)
                {
                    canvas.drawBitmap(wp , (int)((i - 1 + (1 - pieceSize)/2.0f) * minViewWidth) , (int)((j - 1 + (1 - pieceSize)/2.0f) * minViewWidth) , null);
                }

                showCount(i,j,IsShow,canvas);

            }
        }
    }

    private void showCount(int i, int j, boolean isShow, Canvas canvas) {
        if (allPiece[i][j] >= 2 && isShow )
        {
            if (allPiece[i][j] % 2 == 0)
            {
                canvas.drawText(String.valueOf(allPiece[i][j] - 1), (i - 0.5f ) * minViewWidth, (j -0.35f ) * minViewWidth , bpNumPaint);
            }else
            {
                canvas.drawText(String.valueOf(allPiece[i][j] - 1), (i - 0.5f ) * minViewWidth, (j -0.35f ) * minViewWidth , wpNumPaint);
            }
        }
    }


    private void mDrawLine(Canvas canvas) {
        float w = minViewWidth;
        int W = ViewWidth;

        for (int i=0 ; i<LINE_NUM ; i++)
        {
            int startX = (int) (w/2);
            int endX = (int) (W - w/2);
            int Y = (int) ((i + 0.5f) * w);

            canvas.drawLine(startX , Y , endX , Y , mPaint);
        }

        for (int i=0 ; i<LINE_NUM ; i++)
        {
            int startX = (int) (w/2);
            int endX = (int) (W - w/2);
            int Y = (int) ((i + 0.5f) * w);

            canvas.drawLine(Y , startX , Y , endX , mPaint);
        }

    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_ALLPIECE = "instance_allpiece";
    private static final String INSTANCE_COUNT = "instance_count";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putInt(INSTANCE_COUNT,count);
        for (int i = 0 ; i < LINE_NUM + 2 ; i ++ )
        {
            bundle.putIntArray(INSTANCE_ALLPIECE + i , allPiece[i]);
        }
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            count = bundle.getInt(INSTANCE_COUNT);
            for (int i = 0 ; i < LINE_NUM + 2 ; i ++ )
            {
                allPiece[i] = bundle.getIntArray(INSTANCE_ALLPIECE + i);
            }
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    public void restart()
    {
        newPanel();
        invalidate();
        matchInfo.clear();
    }

    public void cancelPiece()
    {
        if (matchInfo.size() > 0)
        {
            matchInfo.remove(matchInfo.size() - 1);
            recoveryMatch(matchInfo);
        }else
        {
            Toast.makeText(getContext(),"至少落一子再来撤销(｡・`ω´･)",Toast.LENGTH_SHORT).show();
        }
    }

    private void recoveryMatch(List<Point> matchInfo) {
        newPanel();
        Point p =new Point();
        int i;
        for (i = 0 ; i < matchInfo.size() ; i++)
        {
            p = matchInfo.get(i);

            allPiece[p.x][p.y] = i+2;
            refreshAllPiece(p.x,p.y,i+1);
            refreshWillKill();
            haveKilled = false;
        }
        invalidate();
        count = i+2;
    }

    private void save (String name , int value)
    {
        SharedPreferences gameInfo = getContext().getSharedPreferences("gameInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = gameInfo.edit();
        editor.putInt(name,value);
        editor.commit();
    }
    private int load (String name)
    {
        SharedPreferences gameInfo = getContext().getSharedPreferences("gameInfo",Context.MODE_PRIVATE);
        return gameInfo.getInt(name,-3);
    }
    public void saveGame()
    {

        for (int i = 0 ; i < count - 2 ; i++)
        {
            save("point_x_"+String.valueOf(i),matchInfo.get(i).x);
            save("point_y_"+String.valueOf(i),matchInfo.get(i).y);
        }
        save("count",count);
        Toast.makeText(getContext(),"保存成功",Toast.LENGTH_SHORT).show();
    }
    public void loadGame()
    {
        matchInfo.clear();
        count = load("count");
        for (int i = 0 ; i < count - 2 ; i++)
        {
            int x = load("point_x_"+String.valueOf(i));
            int y = load("point_y_"+String.valueOf(i));
            Point p = new Point(x,y);
            matchInfo.add(p);
        }
        recoveryMatch(matchInfo);
        Toast.makeText(getContext(),"读取成功",Toast.LENGTH_SHORT).show();
    }
}

