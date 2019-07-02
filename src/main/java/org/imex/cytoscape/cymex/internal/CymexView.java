package org.imex.cytoscape.cymex.internal;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import javax.xml.namespace.NamespaceContext;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

import org.cytoscape.model.*;
import org.cytoscape.view.model.*;
import org.cytoscape.view.vizmap.*;
import org.cytoscape.view.presentation.property.*;

import org.cytoscape.application.*;
import org.cytoscape.application.swing.*;
import org.cytoscape.work.*;

import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.service.util.AbstractCyActivator;

class CymexView{
    
    final static String VS_NAME ="Cymex Style";
    
    CymexContext context; 
    
    private CymexView(){}
    
    public static  CyNetworkView add( CymexContext context ){              
        
        CyNetwork network 
            = context.getApplicationManager().getCurrentNetwork();
        CyNetworkView view 
            = context.getApplicationManager().getCurrentNetworkView();

        if( view == null ){

            final Collection<CyNetworkView> views
                = context.getNetworkViewManager().getNetworkViews( network );
                
            if( views.size() != 0){
                view = views.iterator().next();
            }
            if( view == null ){
                view = context.getNetworkViewFactory().createNetworkView( network );
                context.getNetworkViewManager().addNetworkView( view );
            }
                    
            VisualStyle myVisualStyle = null;
                
            for( Iterator<VisualStyle> vsi = context
                     .getVisualMappingManager().getAllVisualStyles()
                     .iterator(); vsi.hasNext(); ){
                    
                VisualStyle vs = vsi.next();
                    
                if( vs.getTitle().equals( VS_NAME ) ){
                    myVisualStyle = vs;
                    break;
                }
            }
                
            if( myVisualStyle == null ){

                // build new visual style
                //-----------------------
                    
                CymexVisualStyle mvs = new CymexVisualStyle( context );
                myVisualStyle = mvs.build( VS_NAME );
                                 
                context.getVisualMappingManager()
                    .addVisualStyle( myVisualStyle );
            }
            
            context.getVisualMappingManager()
                .setCurrentVisualStyle( myVisualStyle );
                
            myVisualStyle.apply( view );
            view.updateView();
                
            context.getEventHelper().flushPayloadEvents();
        }

        return view;
    }    
}
