package org.imex.cytoscape.cymex.internal.model;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

import org.cytoscape.property.CyProperty;

import java.net.*;
import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.json.*;

public class CymexEvid {
    
    // Represents evidence based on single or more experiments pieces
    // of data
    //  
    
    String id = null;
    String ecode = null; 
    
    Map<String,String> attribute; 
    Map<String,String> attributeUrl; 

    Map<String, CymexData> data;   // data for complex evidence 
    
    public CymexEvid( String id, String code){
        this.id = id;
        this.ecode = code;
        this.attribute = new HashMap<String,String>(); 
        this.attributeUrl = new HashMap<String,String>();
        this.data = new HashMap<String,CymexData>();
    }
    
    public String getId(){
        return id; 
    }

    public String getEvidCode(){
        return ecode; 
    }
    
    // attributes

    public String getAttribute( String name ){
        if( attribute.containsKey( name )){
            return attribute.get( name );
        } 
        return "";
    }

    
    public void setAttribute( String name, String value ){
        if( attribute.containsKey( name )){
            attribute.put( name, value );
        }  else {
            attribute.put( name, value );
        }        
    }
    
    public void setAttributeUrl( String name, String value ){
        if( attribute.containsKey( name )){
            attributeUrl.put( name, value );
        }  
    }
    
    public String getAttributeUrl(String name, String value ){
        return "";
    }
    
    public Set<String> getAttributeList(){
        return attribute.keySet();
    }


    // data
    
    public CymexData getData( String name ){
        if( data.containsKey( name ) ){
            return data.get( name );
        } 
        return null;
    }

    public void setData( String name, CymexData value ){
        if( data.containsKey( name )){
            data.put( name, value );
        }  else {
            data.put( name, value );
        }        
    }
    
    public Set<String> getDataList(){
        return data.keySet();
    }
    
    public String toString(){
        
        StringBuffer buffer = new StringBuffer("CymexEvid: START\n");
        buffer.append(" id=" + id + "\n");
        buffer.append(" evcode=" + ecode + "\n");
        buffer.append(" attr=" + this.getAttributeList() + "\n");
                
        for(Iterator<String> ai = this.getAttributeList().iterator();
            ai.hasNext(); ){

            String n = ai.next();
            buffer.append( " " + n + " :: " + this.getAttribute(n) + "\n" );
        }

        if( data != null){
            buffer.append(" data=" + data.keySet() + "\n");
        }
        buffer.append("CymexEvid: END\n");
        return buffer.toString();
    }
        
}
