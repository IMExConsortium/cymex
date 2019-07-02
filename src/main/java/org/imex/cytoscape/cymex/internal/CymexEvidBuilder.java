package org.imex.cytoscape.cymex.internal;

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

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.json.*;

import org.imex.cytoscape.cymex.internal.model.*;

public class CymexEvidBuilder{

    private CymexEvidBuilder(){}

    public static CymexEvid
        build( CymexContext context, CymexFilter filter,
               Node node, Map evidMap ){
        
        // evidMap - fiter node definition used find node 
        
        // note: is it/should be thread safe ? 
        
        XPathFactory xpathFac = XPathFactory.newInstance(); 
        NamespaceContext nsContext = filter.getNamespaceContext(); 
        
        // get evid id
        //------------
        
        XPath xpEvID = xpathFac.newXPath();
        if( nsContext != null )  xpEvID.setNamespaceContext( nsContext );

        Object resultEvID = null;

        try{
            XPathExpression expEvID = xpEvID.compile((String) evidMap.get("id"));
            resultEvID = expEvID.evaluate( node, XPathConstants.NODESET);
        } catch( XPathExpressionException xpex ){            
            // parsing problem
        }
        
        NodeList nodesEvID = (NodeList) resultEvID;
        
        String evidId = null;
        String evidRid = null;
        
        if( nodesEvID != null && nodesEvID.getLength()>0 ){
            evidId = nodesEvID.item(0).getNodeValue();
        }

        if( nodesEvID == null || nodesEvID.getLength() == 0 ) return null;
        
        // get evidence code
        //------------------

        String evcode = (String) evidMap.get("evcode");
        
        if( evidMap.containsKey("evcode-xpath") ){
            // look for non-default value here            
        }

        CymexEvid newEvid = new CymexEvid( evidId, evcode ) ;
        
        // set attributes
        //---------------

        Map attMap = (Map) evidMap.get( "at" );
        Map attDVMap = (Map) evidMap.get( "atDV" );
        
        for( Iterator ai = attMap.keySet().iterator(); ai.hasNext(); ){

            String attName = (String) ai.next();

            if( attName != null ){
                                
                String attValue = (String) attDVMap.get( attName );  // default
                String aXP = (String) attMap.get( attName );         // xpath
                
                if( aXP != null && aXP.length() > 0 ){
                
                    Object resultAtt = null;

                    try{
                        XPath xpAtt = xpathFac.newXPath();
                        if( nsContext != null ) xpAtt.setNamespaceContext(nsContext);
                        XPathExpression expAtt = xpAtt.compile( aXP );
                        resultAtt = expAtt.evaluate( node, XPathConstants.NODESET);
                    } catch( XPathExpressionException xpex ){
                        // parsing problem
                    }
                    NodeList nodesAtt = (NodeList) resultAtt;
                    
                    if( nodesAtt != null && nodesAtt.getLength()>0 ){
                        attValue = nodesAtt.item(0).getNodeValue();
                    }
                }
                if( attName != null && attValue != null ){
                        
                    if(! newEvid.getAttributeList().contains( attName ) ){ 
                        newEvid.setAttribute( attName, attValue );
                    } else {
                        newEvid.setAttribute( attName, attValue );
                    }                
                }    
            }
        }  
        return newEvid;
    }    
}
