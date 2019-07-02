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

public class CymexEdge{

    String id;
    Map<String,String> attribute; 
    Map<String,String> attributeUrl;
    Map<String,CymexNode> vertex;

    CymexEvid evidence;
    
    // ID: string
    // single-value string attributes only
    //    to do: integer & float types; 
    //           multi-valued attributes
    //
    // map of vertices
    // map of evidences

    public CymexEdge( String id ){
        this.id = id;
        this.attribute = new HashMap<String,String>();
        this.attributeUrl = new HashMap<String,String>();
        this.vertex = new HashMap<String,CymexNode>();
        this.evidence = null;
    }

    public String getId(){
        return id;
    }

    // attributes
    //-----------
    
    public void setAttribute( String name, String value ){
        if( attribute.containsKey( name )){
            attribute.put( name, value );
        } else {
            attribute.put( name, value );
        }      
    }

    public String getAttribute( String name ){
        if( attribute.containsKey( name )){
            return attribute.get( name );
        } 
        return "";
    }

    
    public void setAttributeUrl(String name, String url ){
        if( attribute.containsKey( name ) ){
            attributeUrl.put( name, url );
        } 
    }

    public String getAttributeUrl(String name ){
        if( attribute.containsKey( name )){
            return attributeUrl.get( name );
        } else {
            return "";
        }
    }

    public Set<String> getAttributeSet(){
        return attribute.keySet();
    }

    public List<String> getAttributeList(){
        ArrayList<String> attList =
            new ArrayList<String>( attribute.keySet() );
        
        Collections.sort( attList );
        return attList;
    }
    
    // vertices
    //----------
    
    public void setVertex( String name, CymexNode vertex ){
        if( this.vertex.containsKey( name )){
            this.vertex.put( name, vertex );
        } else {
            this.vertex.put( name, vertex );
        }      
    }
    
    public CymexNode getVertex( String name ){
        if( this.vertex.containsKey( name )){
            return this.vertex.get( name );
        } 
        return null;
    }

    public Set<String> getVertexSet(){
        return vertex.keySet();
    }

    public List<String> getVertexList(){
        ArrayList<String> vertList =
            new ArrayList<String>( vertex.keySet() );
        Collections.sort(vertList );
        return vertList;
    }

    // evidence
    //---------
    
    public void setEvid( CymexEvid evid ){
        this.evidence = evid;
    }
    
    public CymexEvid getEvid(){
        return evidence;
    }
    
    public String toString(){

        StringBuffer buffer = new StringBuffer("CymexEdge\n");
        buffer.append( " Id=" +  id + "\n");
        buffer.append( " Vertex count=" +  vertex.keySet().size() + "\n" );
        for( Iterator<String> vi = vertex.keySet().iterator(); vi.hasNext(); ){
            buffer.append( "   Vertex id=" + vertex.get(vi.next()).getId() + "\n" );
        }
        buffer.append( " Evid=" +  evidence + "\n");
        
        return buffer.toString();
    }
        
}
