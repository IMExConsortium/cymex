package org.imex.cytoscape.cymex.internal;


import java.net.*;
import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import javax.xml.namespace.NamespaceContext;

import org.json.*;

public class XPT{
    
    public static List<Node> getXPNodeList( Node root, NamespaceContext nsc,
                                             String xpath )
        throws XPathExpressionException {
        
        XPathFactory xpFactory = XPathFactory.newInstance(); 
        XPath xp = xpFactory.newXPath();
        if( nsc != null ) xp.setNamespaceContext( nsc );
        
        XPathExpression xpe = xp.compile( xpath );

        NodeList nl = (NodeList) xpe.evaluate( root, XPathConstants.NODESET);
        List<Node> res = new ArrayList<Node>();
        for( int i= 0; i < nl.getLength(); i++ ){
            res.add( nl.item( i ) );
        }
        return res;
    }                                

    public static Node getXPNode( Node root, NamespaceContext nsc,
                                  String xpath )
        throws XPathExpressionException {
        
        XPathFactory xpFactory = XPathFactory.newInstance(); 
        XPath xp = xpFactory.newXPath();
        if( nsc != null ) xp.setNamespaceContext( nsc );
        
        XPathExpression xpe = xp.compile( xpath );

        NodeList nl = (NodeList) xpe.evaluate( root, XPathConstants.NODESET);
        if( nl.getLength() > 0) {
            return nl.item( 0 );        
        }
        return null;
    }                                
    
    public static List<String> getXPStringList( Node root, NamespaceContext nsc,
                                                 String xpath )
        throws XPathExpressionException {

        XPathFactory xpFactory = XPathFactory.newInstance(); 
        XPath xp = xpFactory.newXPath();
        if( nsc != null ) xp.setNamespaceContext( nsc );
        
        XPathExpression xpe = xp.compile( xpath );

        NodeList nl = (NodeList) xpe.evaluate( root, XPathConstants.NODESET);
        List<String> res = new ArrayList<String>();
        for( int i= 0; i < nl.getLength(); i++ ){
            res.add( nl.item( i ).getNodeValue() );
        }
        return res;
    }                                
    
    public static String getXPString( Node root, NamespaceContext nsc,
                                       String xpath )
        throws XPathExpressionException {
        
        XPathFactory xpFactory = XPathFactory.newInstance(); 
        XPath xp = xpFactory.newXPath();
        if( nsc != null ) xp.setNamespaceContext( nsc );
        
        XPathExpression xpe = xp.compile( xpath );

        NodeList nl = (NodeList) xpe.evaluate( root, XPathConstants.NODESET);
        if( nl.getLength() > 0) {
            return nl.item( 0 ).getNodeValue();
        }
        return null;
    }
}
