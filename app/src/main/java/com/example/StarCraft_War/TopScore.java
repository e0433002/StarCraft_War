package com.example.StarCraft_War;

import java.io.Serializable;

public class TopScore  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int topScore;
	
	public void setTopScore(int topScore){
		this.topScore = topScore;
	}
	
	public int getTopScore(){
		return this.topScore;
	}
}
