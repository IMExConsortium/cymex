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

public class CymexFilter{
    
    List nodeFilter;
    Collection<CymexNodeFilter>  nodeIgnoreFilter;
    List dataFilter;
    List evidFilter;
    List edgeFilter;
    String nsPrefix;
    String nsURI;

    public CymexFilter( List nodeFilter,
                        Collection<CymexNodeFilter> nodeIgnoreFilter,
                        List edgeFilter,
                        List dataFilter,
                        List evidFilter,
                        String nsPrefix,
                        String nsURI
                        ){
        this.nodeFilter = nodeFilter;
        this.nodeIgnoreFilter = nodeIgnoreFilter;
        this.edgeFilter = edgeFilter;
        this.dataFilter = dataFilter;
        this.evidFilter = evidFilter;
        this.nsPrefix = nsPrefix;
        this.nsURI = nsURI;
    }

    public List getNodeFilter(){
	return nodeFilter;
    }
    
    public Collection<CymexNodeFilter> getNodeIgnoreFilter(){
	return nodeIgnoreFilter;
    }
    
    public List getEdgeFilter(){
	return edgeFilter;
    }

    public List getDataFilter(){
	return dataFilter;
    }

    public List getEvidFilter(){
	return evidFilter;
    }

    public NamespaceContext getNamespaceContext(){
        
        NamespaceContext nsc = null;
        
        if( nsPrefix != null && nsURI != null ){
            return  new CymexNamespaceContext( nsPrefix, nsURI, true );
        }
        return null;
    }

    public boolean nodeIgnoreTest( CymexNode cnode ){
        
        String cnid = cnode.getId();
        System.out.println(nodeIgnoreFilter);
        return false;
    }
}
