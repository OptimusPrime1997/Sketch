package com.example.gui;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class BuildDocument {
	
	
	public static Document  getDocument(){	
		Element root = DocumentHelper.createElement("path");  
		Document document = DocumentHelper.createDocument(root); 
		return document;
	}
	
	public static Element addElement(Element rootElement, String tagName){				
		return rootElement.addElement(tagName);
	}
	
	public static void addText(Element element, String text){
		element.addText(text);
	}
	
	public static void addAttribute(Element element, String name,String value){
		element.addAttribute(name, value);
	}
	
	
    
    
}
