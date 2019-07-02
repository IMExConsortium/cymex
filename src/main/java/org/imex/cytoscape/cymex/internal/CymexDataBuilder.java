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

public class CymexDataBuilder{

    private CymexDataBuilder(){}

    public static CymexData
        build( CymexContext context, CymexFilter filter,
               Node node, Map dataMap ){
        
        // dataMap - filter node definition used find node 
        
        // note: is it/should be thread safe ? 
        
        XPathFactory xpathFac = XPathFactory.newInstance(); 
        NamespaceContext nsContext = filter.getNamespaceContext(); 
        
        // get data id
        //------------
        
        XPath xpEvID = xpathFac.newXPath();
        if( nsContext != null )  xpEvID.setNamespaceContext( nsContext );

        Object resultEvID = null;

        try{
            XPathExpression expEvID = xpEvID.compile((String) dataMap.get("id"));
            resultEvID = expEvID.evaluate( node, XPathConstants.NODESET);
        } catch( XPathExpressionException xpex ){            
            // parsing problem
        }
        
        NodeList nodesEvID = (NodeList) resultEvID;
        
        String dataId = null;
        String dataRid = null;
        
        if( nodesEvID != null && nodesEvID.getLength()>0 ){
            dataId = nodesEvID.item(0).getNodeValue();
        }

        if( nodesEvID == null || nodesEvID.getLength() == 0 ) return null;
        
        CymexData newData = new CymexData( dataId ) ;
        
        //if( context.isDebugOn() ){
        //    System.out.println(" new evid id: " + newNode.getId() );	
        //}
             
        
        // set attributes
        //---------------

        Map attMap = (Map) dataMap.get( "at" );
        Map attDVMap = (Map) dataMap.get( "atDV" );
        
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
                        
                    if(! newData.getAttributeList().contains( attName ) ){ 
                        newData.setAttribute( attName, attValue );
                    } else {
                        newData.setAttribute( attName, attValue );
                    }                
                }    
            }
        }  
        return newData;
    }    
}
