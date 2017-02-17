package a6he.android.yzz.com.a5play;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzz on 2017/2/15 0015.
 */
public class FiveGameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Bitmap mMyQiPan;
    private Bitmap mWhite;
    private Bitmap mBlack;
    private List<Point> mQiZiWihte;
    private List<Point> mQiZiBlack;
    private Paint mPaint;
    private Thread mThread;
    private SurfaceHolder holder;
    private Rect mRect;
    private int value;
    private Point currentPoint;
    private boolean isWhite = true;
    private boolean isWin = false;
    private boolean isMoving = false;


    public FiveGameView(Context context) {
        super(context);
        init();
    }

    public FiveGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FiveGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        holder = getHolder();
        mThread = new Thread(this);
        mMyQiPan = BitmapFactory.decodeResource(getResources(), R.mipmap.bg).copy(Config.ARGB_8888, true);
        mWhite = Bitmap.createBitmap(100, 100, Config.ARGB_8888).copy(Config.ARGB_8888, true);
        mBlack = Bitmap.createBitmap(100, 100, Config.ARGB_8888).copy(Config.ARGB_8888, true);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        Canvas ca = new Canvas(mWhite);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        ca.drawCircle(45, 45, 46, mPaint);
        mPaint.setColor(Color.WHITE);
        ca.drawCircle(45, 45, 44, mPaint);


        mPaint.setColor(Color.BLACK);
        Canvas caa = new Canvas(mBlack);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        caa.drawCircle(45, 45, 46, mPaint);
        mPaint.setColor(Color.BLACK);
        caa.drawCircle(45, 45, 44, mPaint);
        mQiZiBlack = new ArrayList<>();
        mQiZiWihte = new ArrayList<>();
        mRect = new Rect(0, 0, getWidth(), getHeight());
        holder.addCallback(this);
    }

    public void drawQiPan() {
        Canvas ca = new Canvas(mMyQiPan);

        int w = (getWidth() - 100) / 100 * 100;
        int h = (getHeight() - 100) / 100 * 100;
        value = Math.min(w, h);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        //起点
        int x = (getWidth() - value) / 2;
        int y = (getHeight() - value) / 2;
        for (int i = 0; i <= value; i = i + 100) {
            ca.drawLine(i + x, 0 + y, i + x, value + y, mPaint);
            ca.drawLine(0 + x, i + y, value + x, i + y, mPaint);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    //线程方法
    @Override
    public void run() {
        int x = (getWidth() - value) / 2;
        int y = (getHeight() - value) / 2;
        drawQiPan();
        Canvas ca = new Canvas(mMyQiPan);

        for (int i = 0; i < mQiZiWihte.size(); i++) {
            ca.drawBitmap(mWhite, mQiZiWihte.get(i).x * 100 + x - 46, mQiZiWihte.get(i).y * 100 + y - 46, mPaint);
        }
        for (int i = 0; i < mQiZiBlack.size(); i++) {
            ca.drawBitmap(mBlack, mQiZiBlack.get(i).x * 100 + x - 46, mQiZiBlack.get(i).y * 100 + y - 46, mPaint);
        }

        Canvas canvas = holder.lockCanvas(mRect);
        canvas.drawBitmap(mMyQiPan, mRect, new Rect(0, 0, getWidth(), getHeight()), mPaint);
        holder.unlockCanvasAndPost(canvas);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //起点
        int x = (getWidth() - value) / 2;
        int y = (getHeight() - value) / 2;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                isMoving = true;
                return false;
            case MotionEvent.ACTION_UP:
                if (isWin||isMoving) {
                    break;
                }
                int xx = (int) event.getX();
                int yy = (int) event.getY();
                currentPoint = new Point((xx - x) / 100, (yy - y) / 100);
                if (!check(currentPoint)) {
                    break;
                }
                isWhite = !isWhite;
                if (isWhite) {
                    mQiZiWihte.add(currentPoint);
                } else {
                    mQiZiBlack.add(currentPoint);
                }
                //判断在那个位置上
                new Thread(this).start();
                //判断
                if (isWhite) {
                    if (mQiZiWihte.size() < 5) {
                        break;
                    }
                } else {
                    if (mQiZiBlack.size() < 5) {
                        break;
                    }
                }

                if (currentPoint != null ) {
                    checkH(currentPoint,true);
                    if (!isWin){
                        checkH(currentPoint,false);
                    }
                    if (!isWin){
                        checkDuijiao();
                    }
                    if (!isWin){
                        chekZhengXianxian();
                    }

                    if (isWin) {
                        Toast.makeText(getContext().getApplicationContext(), "赢了", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    /*
    * 检查
    * */
    public boolean check(Point point) {
        int num = value / 100;
        if (mQiZiWihte.contains(point) || mQiZiBlack.contains(point)) {
            return false;
        }
        if (point.x >= 0 && point.y < num + 1 && point.x < num + 1 && point.y >= 0) {
            return true;
        }
        return false;
    }

    //检查横向的
    public void checkH(Point point,boolean isX) {

        int x = point.x;
        int y = point.y;
        int count = 1;
        int xunhuan;
        if (isX){

            xunhuan = x;
        }else {
            xunhuan = y;
        }
        //想左
        for (int i = xunhuan - 1; i >= 0; i--) {
            int xx;
            int yy;
            if (isX){
               xx = i;
               yy = point.y;
            }else {
                xx = point.x;
                yy = i;
            }
            if (equal(xx, yy)) {
                count++;
                Log.e("==z====","====="+count);
                if (countCheck(count)) {
                    isWin = true;
                    Log.e("==z====","==1==="+count);
                    return;
                }
            } else {
                break;
            }
        }
        //向右
        for (int i = xunhuan + 1; i < value / 100 + 1; i++) {
            int xx;
            int yy;
            if (isX){
                xx = i;
                yy = point.y;
            }else {
                xx = point.x;
                yy = i;
            }
            if (equal(xx, yy)) {
                count++;
                if (countCheck(count)) {
                    isWin = true;
                    Log.e("==z====","==2==="+count);
                    return;
                }
            } else {
                break;
            }
        }

    }

    //检查斜线
    public void checkDuijiao(){
        int count = 1;

        for (int i =1; i <= value/100; i++) {
            if (currentPoint.x+i>value/100||currentPoint.y+i>value/100){
                break;
            }
            if (equal(currentPoint.x+i,currentPoint.y+i)){
                count++;
                countCheck(count);
                if (countCheck(count)){
                    isWin = true;
                    Log.e("==z====","===3=="+count);
                    return;
                }
            }else {
                break;
            }
        }

        for (int i = 1; i<=value/100 ;i++) {
            if (currentPoint.x-i>value/100||currentPoint.y-i>value/100){
                break;
            }
            if (equal(currentPoint.x-i,currentPoint.y-i)){
                count++;
                if (countCheck(count)){
                    isWin = true;
                    Log.e("==z====","==4==="+count);
                    return;
                }
            }else {
                break;
            }
        }
    }

    public void chekZhengXianxian(){
        int count = 1;
        int mine = Math.min(currentPoint.x,currentPoint.y);
        for (int i = 1; i < value/100; i++) {
            if (currentPoint.x+i>value/100||currentPoint.y-i<0){
                break;
            }
            if (equal(currentPoint.x+i,currentPoint.y-i)){
                count++;
                countCheck(count);
                if (countCheck(count)){
                    isWin = true;
                    Log.e("==z====","===5=="+count);
                    return;
                }
            }else {
                break;
            }
        }

        for (int i = 1; i >=0; i++) {
            if (currentPoint.x+i>value/100||currentPoint.y-i<0){
                break;
            }
            if (equal(currentPoint.x-i,currentPoint.y+i)){
                count++;
                if (countCheck(count)){
                    isWin = true;
                    Log.e("==z====","===6=="+count);
                    return;
                }
            }else {
                break;
            }
        }
    }

    public boolean equal(int x, int y) {
        List<Point> list;
        if (isWhite) {
            list = mQiZiWihte;
        } else {
            list = mQiZiBlack;
        }
        for (int i = 0; i <list.size() ; i++) {
            Point p = list.get(i);
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }


    public boolean countCheck(int count) {
        if (count == 5) {
            return true;
        }
        return false;
    }


//    public boolean checkX(Point point) {
//        //起点
//        int x = (getWidth() - value) / 2;
//        if (point.x < x || point.x > x+value+50) {
//            return false;
//        } else
//            return true;
//    }
//
//    public boolean checkY(Point point) {
//        //起点
//        int y = (getHeight() - value) / 2;
//        if (point.y < y || point.y > y+value+50) {
//            Log.e("=========","========"+point.y+"=="+ (y+value));
//            return false;
//        } else
//            return true;
//    }
}
