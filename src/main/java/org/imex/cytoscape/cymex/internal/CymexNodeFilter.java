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

public class CymexNodeFilter{
    
    String xpath;
    List<String> values;
    
    public CymexNodeFilter( String xpath,
                            List<String> values){
        this.xpath = xpath;
        this.values = values;
    }
}
