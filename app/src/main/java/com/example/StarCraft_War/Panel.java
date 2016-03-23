package com.example.StarCraft_War;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
	Context context;
	private Long startTime;
	private Handler handler = new Handler();
	
	Long currentMin = (long) 0;
	Long currentSec = (long) 0;
	int score = 0;
	int topScore;
	
	private Bitmap darkmothership;
	private Bitmap ultraliskBip;
	private Bitmap queenBip;
	private Bitmap roachBip;
	private ViewThread mThread;

    float optionLineHeight;   // option line in screen y
    float deadLine;           // the line to cause damage
    float screenWidth, screenHeight;
    public int life = 20;     // public for viewThread and if life become 0, you lose the game

	public Panel(Context context, float height, float width) {
		super(context);
        this.screenHeight = height;
        this.screenWidth = width;
        this.optionLineHeight = height - 100;   // move up 100px
        this.deadLine = (float)(screenHeight * 0.8);
		this.context = context;

        darkmothership = getResizeBitmap(R.drawable.darkmothership, 20);
        ultraliskBip = getResizeBitmap(R.drawable.ultralisk, 10);   // resize to 10%
        queenBip = getResizeBitmap(R.drawable.queen, 10);
        roachBip = getResizeBitmap(R.drawable.roach, 10);
		
		this.getHolder().addCallback(this);		//SurfaceHolder.Callback
		mThread = new ViewThread(this);
		
		// ** control time **
		startTime = System.currentTimeMillis();
        Runnable counterRunable = new Runnable() {    //refresh immediately
            @SuppressLint("NewApi")
            public void run() {
                Long spentTime = System.currentTimeMillis() - startTime;
                Long mins = (spentTime / 1000) / 60;
                Long seconds = (spentTime / 1000) % 60;
                currentMin = mins;                    //set Clock
                currentSec = seconds;
                handler.postDelayed(this, 1000);
            }
        };
        handler.removeCallbacks(counterRunable);
		handler.postDelayed(counterRunable, 1000);
		// ** control time **
		try {
			load();
		} catch (StreamCorruptedException ignored) {
		} catch (FileNotFoundException ignored) {
		} catch (IOException ignored) {
		} catch (ClassNotFoundException ignored) {}
	}

	// roach = 0, queen = 1, ultralisk = 2;
	public void doDraw(Canvas canvas, StateX x){
        int path; // monster move down til reach path limit
		Bitmap xBitmap;
		switch (x.getName()) {
		case 0:
			xBitmap = roachBip;
			break;
		case 1:
			xBitmap = queenBip;
			break;
		default:
			xBitmap = ultraliskBip;
		}
		canvas.drawBitmap(xBitmap, (float)x.getX(), (float)x.getY(), null);
		switch (x.pathType) {
		case 0:
			pathFuncLine(x);
			break;
		case 1:
			pathFuncCos(x);
			break;
		default:
			if(x.slope == 0) x.slope = ((((int)(Math.random()*2) == 1) ? 1 : -1)*2.5) + 1;	//-3.5 < slope < 3.5
			pathFuncSlope(x);
		}
		path = (int)x.getY();
		if( path > deadLine){
            life--;         // reach the deadLine cause 1 damage
			x.setY(1);
		}
	}

    // move down method => pathFuncLine, pathFuncCos, pathFuncSlope
	public void pathFuncLine(StateX x) {
		x.setY(x.getY()+x.getSpeed());
	}
	public void pathFuncCos(StateX x) {
		double X = x.getY();
		double Y = x.getX();
		Y = Y > screenWidth ? 0 : Y;
		Y = Y < 0  ? screenWidth : Y;
		x.setY(X + (Math.random()*15));
		X = x.getY();
		x.setX(Y + Math.cos(X)*25);
	}
	public void pathFuncSlope(StateX x) {
		x.setY(x.getY()+x.getSpeed());
		double Z = x.slope * x.getY() + x.getSpeed()*100;
		Z = Z > screenWidth ? Z % screenWidth : Z;
		Z = Z < 0 ? (Z % screenWidth)+screenWidth : Z;
		x.setX(Z);
	}

	public void drawOption(Canvas canvas) {
		Paint drawPaint = new Paint();
		drawPaint.setTextSize(45);
		drawPaint.setColor(Color.BLUE);
        String text = "Clock:  " + currentMin + ": " + currentSec;
		canvas.drawText(text, 0, optionLineHeight - 100, drawPaint);

		drawPaint.setColor(Color.GREEN );
        text = "Score: " + score;
		canvas.drawText(text, 0, optionLineHeight, drawPaint);

        float textLength = drawPaint.measureText(text, 0, text.length());
		drawPaint.setColor(Color.YELLOW);
		canvas.drawText("No.1 Score: "+topScore, textLength+50, optionLineHeight, drawPaint);

        drawPaint.setColor(Color.WHITE);
        drawPaint.setTextSize(40);
        textLength = drawPaint.measureText("Clear record");
        canvas.drawText("Clear record", screenWidth-textLength, optionLineHeight, drawPaint);
	}
	
	public void drawGameOver(Canvas canvas) {
		Paint centerPaint = new Paint();
        String text;
		centerPaint.setTextSize(60);
		centerPaint.setColor(Color.RED );
        text = life != 0 ? "PROTESS WIN" : "PROTESS LOSE";
        float textLength = centerPaint.measureText(text, 0, text.length());
		canvas.drawText(text, screenWidth/2-(textLength/2), screenHeight/2, centerPaint);
        drawMotherShip(canvas);
		saving();
	}
	
	public void drawMotherShip(Canvas canvas) {
        float fixX = darkmothership.getWidth() / 2;
        float fixY = darkmothership.getHeight() + 100;
		canvas.drawBitmap(darkmothership, screenWidth/2-fixX, screenHeight/2-fixY, null);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!mThread.isAlive()) {
			mThread = new ViewThread(this);
			mThread.setRunning(true);
			mThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mThread.isAlive()) {
			mThread.setRunning(false);
		}
	}

    int clear_record_x = (int)screenWidth;

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
        float mX, mY;
		mX = motionEvent.getX();
		mY = motionEvent.getY();
		StateX[] state = mThread.getStateXs();
		
		for(int i = 0 ; i < mThread.monsterNum ; i++){
			float bX = (float)state[i].getX() + ultraliskBip.getWidth() / 2;
			float bY = (float)state[i].getY() + ultraliskBip.getHeight() / 2;
			double distance = Math.hypot(mX-bX, mY-bY);		// distFunc: count hypotenuse
			if(distance < 50){		// touching
				state[i].setX(0);
				state[i].setY(0);
				state[i].setBeTouch();
                // time is the lower the better
                score = (int)((System.currentTimeMillis()-startTime)/1000);
			}
		}
		
		if( Math.hypot(mX-(clear_record_x+80), mY-optionLineHeight) < 50){
			topScore = 0;
			saving();
		}
		return super.onTouchEvent(motionEvent);
	}

	public void saving(){
		try {
			save();
		}
        catch (IOException ignored) { }
	}

    public void save() throws IOException{
		FileOutputStream out = context.openFileOutput("data.txt", Context.MODE_PRIVATE);
		DataOutputStream output = new DataOutputStream(out);
		TopScore topScore = new TopScore();
		if(score < this.topScore)
			topScore.setTopScore(score);
		output.writeInt(score);
		output.close();
	}
	
	public void load() throws IOException, ClassNotFoundException{
		FileInputStream in = context.openFileInput("data.txt");
		DataInputStream input = new DataInputStream(in);
		this.topScore = input.readInt();
		input.close();
	}

    public Bitmap getResizeBitmap(int resourceInt, int percent){    // percent 1~100%
        Bitmap tmp = BitmapFactory.decodeResource(getResources(), resourceInt);
        return  Bitmap.createScaledBitmap(tmp, (int) optionLineHeight * percent / 100, (int) optionLineHeight * percent / 100, false);
    }
}