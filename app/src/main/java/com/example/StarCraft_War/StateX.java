package com.example.StarCraft_War;

public class StateX {
	double x, y;
	int speed, name;
	boolean exist = true;
	int pathType;
	double slope = 0;
	
	public StateX(int x, int y, int speed, int name, int pathType){
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.name = name;
		this.pathType = pathType;
	}
	public int getName(){
		return this.name;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public int getSpeed(){
		return speed;
	}
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	public void setBeTouch(){
		this.exist = false;
	}
	public boolean stillExist(){
		return exist;
	}
}
