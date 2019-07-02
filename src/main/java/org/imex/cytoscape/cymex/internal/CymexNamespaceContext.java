package org.imex.cytoscape.cymex.internal;

import java.net.*;
import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class CymexNamespaceContext implements NamespaceContext{
    
    Map<String, List<String>> context;
    String prefix;
    String namespaceURI;

    // simplified version:  single prefix/ns pair defined

    public CymexNamespaceContext( String prefix, String namespaceURI,
                                  boolean defns ){
        
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;

        context = new HashMap<String, List<String>>();
        context.put( namespaceURI,new ArrayList<String>() );
        context.get( namespaceURI ).add( prefix );

        if( defns ){
            context.get( namespaceURI ).add( "" );
        }
    }


    public String getNamespaceURI( String prefix ) 
        throws IllegalArgumentException{
        
        if( prefix == null ) throw new IllegalArgumentException();
        
        if( prefix.equals(this.prefix) 
            || prefix.equals("") ) return namespaceURI;
        
        if( prefix.equals( XMLConstants.XML_NS_PREFIX ) ) 
            return XMLConstants.XML_NS_URI;
        
        if( prefix.equals( XMLConstants.XMLNS_ATTRIBUTE ) )
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        
        return null;
    }
    
    
    public String getPrefix( String namespaceURI ) 
        throws IllegalArgumentException{
        
        if( namespaceURI == null ) throw new IllegalArgumentException();
        
        
        if( namespaceURI.equals( namespaceURI ) ){
            return prefix;
        }

        if( namespaceURI.equals( XMLConstants.XML_NS_URI) ){
            return XMLConstants.XML_NS_PREFIX;
        }
        
        if( namespaceURI.equals( XMLConstants.XMLNS_ATTRIBUTE_NS_URI) ){
            return XMLConstants.XMLNS_ATTRIBUTE ;
        }
        
        return null;
        
    }
     
    public Iterator getPrefixes( String namespaceURI ) 
        throws IllegalArgumentException{
        
        if( namespaceURI == null ) throw new IllegalArgumentException();
                
        if( namespaceURI.equals( namespaceURI ) ){
            return context.get( namespaceURI ).iterator();
        }

        List nl = new ArrayList();

        if( namespaceURI.equals( XMLConstants.XML_NS_URI) ){
            nl.add( XMLConstants.XML_NS_PREFIX );
        }
        
        if( namespaceURI.equals( XMLConstants.XMLNS_ATTRIBUTE_NS_URI) ){
            nl.add( XMLConstants.XMLNS_ATTRIBUTE );
        }

        return nl.iterator();
    }

}
