package com.example.gui;

public enum Actions {
	
	
		
	click("click"),
	longClick("longClick"),
	drag("drag"),
	drawArea("drawArea"),
	delayTime("delayTime"),
	target("traget");
	
	private String name;

	
	private Actions(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

}
