package com.example.StarCraft_War;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

class ViewThread extends Thread {
	int monsterNum = 10;		// create how many monster
	int monsterType = 3;		// the monster type amount
	private Panel[] mPanel = new Panel[monsterNum];
	private StateX[] state = new StateX[monsterNum];
	private SurfaceHolder mHolder;
	private boolean mRun = false;
	public int roach = 0, queen = 1, ultralisk = 2;
	
	public ViewThread(Panel panel) {
		for(int i = 0 ; i < monsterNum ; i++){	// set all mPanel to be panel
			mPanel[i] = panel;
			int speed = (int)((Math.random()*5)+2);	//: 2 <= speed <= 6
			int name;
			switch((int)(Math.random()*monsterType)){
			case 0 :
				name = roach;
				break;
			case 1 :
				name = queen;
				break;
			default:
				name = ultralisk;
			}
			int pathType = (int)(Math.random()*3);			//according to Panel's pathFunc_ account
			int x = (int)(Math.random()*7) * 100, y=0;
			state[i] = new StateX(x, y, speed, name, pathType);		//initial state
		}
		mHolder = mPanel[0].getHolder();
	}

	public void setRunning(boolean run) {
		mRun = run;
	}

    Canvas canvas = null;
    Boolean fixedGameOverMsg = true;
	@Override
	public void run() {
		while (mRun) {
			canvas = mHolder.lockCanvas();
			if (canvas != null && fixedGameOverMsg) {
                canvas.drawColor(Color.BLACK);
				mPanel[0].drawOption(canvas);
				boolean gameOver = true;
				for(int i = 0 ; i < monsterNum ; i++){
					if(state[i].stillExist()){
						mPanel[i].doDraw(canvas, state[i]);
						gameOver = false;   // not destroyed all target
					}
                    if(mPanel[0].life == 0) gameOver = true;    // life become zero
				}
				if(gameOver){
					mPanel[0].drawGameOver(canvas);		// All targets has been destroyed.
                    fixedGameOverMsg = false;  // never enter again
                }
                mHolder.unlockCanvasAndPost(canvas);
			}
            else break;
		}
	}
	
	public StateX[] getStateXs(){
		return state;
	}
}