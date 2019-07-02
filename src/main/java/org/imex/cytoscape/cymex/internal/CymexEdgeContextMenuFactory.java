package org.imex.cytoscape.cymex.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyEdgeViewContextMenuFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class CymexEdgeContextMenuFactory implements CyEdgeViewContextMenuFactory, 
                                                    ActionListener{

    CymexContext context;
    
    public CymexEdgeContextMenuFactory( CymexContext context ){
        this.context = context;        
    }
    
    @Override
    public CyMenuItem createMenuItem( CyNetworkView netView,
                                      View<CyEdge> edgeView ){

        CymexLinkFactory mlf = new CymexLinkFactory( context );
        CyMenuItem cyMenuItem = mlf.addEdgeLinks( edgeView );
        
        if( cyMenuItem == null ){
            JMenuItem menuItem = new JMenuItem( "Cymex" );
            menuItem.setEnabled( false );
            cyMenuItem = new CyMenuItem( menuItem, 0 );
        }
        
        return cyMenuItem;
    }

    public void actionPerformed( ActionEvent e ){ }
}



