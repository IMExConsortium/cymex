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

public class CymexNode implements Comparable<CymexNode>{

    // Represents experimental state od a reference node
    // ID and single-value string attributes only
    // to do: integer & float types; multi-valuet attributes
    
    String id = null;
    CymexRefNode refNode; 

    Map<String,String> attribute; 
    Map<String,String> attributeUrl; 

    public CymexNode( String id, CymexRefNode  refNode){
        this.id = id;
        this.refNode = refNode;
        this.attribute = new HashMap<String,String>();
        this.attributeUrl = new HashMap<String,String>();
    }

    public CymexNode( CymexRefNode refNode ){
        this.id = refNode.getId();
        this.refNode = refNode;
        this.attribute = new HashMap<String,String>();
        this.attributeUrl = new HashMap<String,String>();
    }
    
    // reference node access (read only)

    
    public String getId(){

        if( id  == null ){
            return refNode.getId();
        } else {
            return id;
        }
    }

    public CymexRefNode getRefNode(){
        return refNode;
    }
    
    public String getRefId(){
        return refNode.getId();
    }
    
    public String getRefNodeAttribute( String name ){
        return refNode.getAttribute(name);
    }
    
    public String getRefNodeAttributeUurl( String name ){
        return refNode.getAttributeUrl( name );
    }
    
    public Set<String> getRefNodeAttributeList(){
        return refNode.getAttributeList();
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
    
    public Set<String> getAttributeSet(){
        return attribute.keySet();
    }

    public List<String> getAttributeList(){
        ArrayList attrList =
            new ArrayList<String>( attribute.keySet());
        Collections.sort( attrList );
        return attrList;
    }

    public String toString(){
        
        StringBuffer buffer = new StringBuffer("CymexNode\n");
        buffer.append(" id=" + id + "\n");
        buffer.append(" attr=" + this.getAttributeList() + "\n");

        buffer.append(" refid=" + refNode.getId() + "\n");
        buffer.append(" refattr=" + refNode.getAttributeList() + "\n");
        buffer.append("CymexNode: END\n"); 
        return buffer.toString();
    }

    public int compareTo( CymexNode  o){

        if( o == null ){
            return -1;
        }
        if( this.getId().compareTo( o.getId() ) !=  0 ){
            return this.getId().compareTo( o.getId() ) ;
        } else {
            return this.getRefId().compareTo( o.getRefId() ) ;
        }
    }
}
