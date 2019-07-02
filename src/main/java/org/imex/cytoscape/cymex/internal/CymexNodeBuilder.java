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


public class CymexNodeBuilder{

    private CymexNodeBuilder(){}

    public static CymexRefNode
        build( CymexContext context, CymexFilter filter,
               Node node, Map nodeMap ){
        
        // nodeMap - fiter node definition used find node 
        
        // note: is it/should be thread safe ? 
        
        XPathFactory xpathFac = XPathFactory.newInstance(); 
        NamespaceContext nsContext = filter.getNamespaceContext(); 
        
        // get node Id
        //------------
        
        XPath xpNid = xpathFac.newXPath();
        if( nsContext != null )  xpNid.setNamespaceContext( nsContext );

        Object resultNid = null;

        try{
            XPathExpression expNid = xpNid.compile((String) nodeMap.get("id"));
            resultNid = expNid.evaluate( node, XPathConstants.NODESET);
        } catch( XPathExpressionException xpex ){            
            // parsing problem
        }
        
        NodeList nodesNid = (NodeList) resultNid;
        
        String nodeId = null;
        String nodeRid = null;
        
        if( nodesNid != null && nodesNid.getLength()>0 ){
            nodeId = nodesNid.item(0).getNodeValue();
        }

        if( nodeId == null || nodeId.length() == 0 ) return null;
        
        CymexRefNode newNode = new CymexRefNode( nodeId ) ;
                     
        // set attributes
        //---------------

        Map attMap = (Map) nodeMap.get( "at" );      // xpath
        Map attDVMap = (Map) nodeMap.get( "atDV" );  // default value
        
        for( Iterator ai = attMap.keySet().iterator(); ai.hasNext(); ){

            String attName = (String) ai.next();            
            if( attName != null ){
                
                String attValue = (String) attDVMap.get( attName );
                String aXP = (String) attMap.get( attName );
                
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
                    
                    if( nodesAtt!=null && nodesAtt.getLength()>0 ){
                        attValue = nodesAtt.item(0).getNodeValue();
                    }
                }
                    
                if( attName!=null && attValue!=null ){
                    
                    if(! newNode.getAttributeList().contains( attName ) ){ 
                        newNode.setAttribute( attName, attValue );
                    } else {
                        newNode.setAttribute( attName, attValue );
                    }
                }
            }
        }  
        return newNode;
    }    
}
