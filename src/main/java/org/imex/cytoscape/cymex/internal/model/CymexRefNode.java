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

public class CymexRefNode implements Comparable<CymexRefNode>{
    
    // Reference/dictionary (eg UniProtKB) state of a Node
    //----------------------------------------------------
    // ID and single-value string attributes only
    // to do: integer & float types; multi-valuet attributes
    
    String id;

    Map<String,String> attribute; 
    Map<String,String> attributeUrl; 

    public CymexRefNode(String id){
        this.id = id;
        this.attribute = new HashMap<String,String>();
        this.attributeUrl = new HashMap<String,String>();
    }

    public void setId( String id ){
        this.id = id;
    }
    
    public String getId(){
        return id;
    }
    
    
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
        } else {
            attributeUrl.put( name, value );
        }
    }
    
    public String getAttributeUrl(String name ){
        return attributeUrl.get( name );
    }
    
    public Set<String> getAttributeList(){
        return attribute.keySet();
    }


    public String toString(){
        
        StringBuffer buffer = new StringBuffer("CymexRefNode\n");
        buffer.append(" id=" + id + "\n");
        buffer.append(" attr=" + this.getAttributeList() + "\n");
        buffer.append("CymexRefNode: END\n");
        return buffer.toString();
    }
    

    public int compareTo( CymexRefNode  o){

        if( o == null ){
            return -1;
        }
        return( this.getId().compareTo( o.getId() ) );
    } 
    
}
