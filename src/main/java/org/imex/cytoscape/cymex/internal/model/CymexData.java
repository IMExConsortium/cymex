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

public class CymexData {
    
    // Represents experimental or computational observation
    
    String id = null;
    
    Map<String,String> attribute; 
    Map<String,String> attributeUrl; 
    
    public CymexData( String id ){
        this.id = id;
        this.attribute = new HashMap<String,String>(); 
        this.attributeUrl = new HashMap<String,String>();
    }
    
    public String getId(){
        return id; 
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

    public String toString(){
        
        StringBuffer buffer = new StringBuffer("CymexData: START\n");
        buffer.append(" id=" + id + "\n");
        buffer.append(" attr=" + this.getAttributeList() + "\n");
        
        for(Iterator<String> ai = this.getAttributeList().iterator();
            ai.hasNext(); ){

            String n = ai.next();
            buffer.append( " " + n + " :: " + this.getAttribute(n) + "\n" );
        }
        buffer.append("CymexData: END\n");
        return buffer.toString();
    }
        
}
