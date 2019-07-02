package org.imex.cytoscape.cymex.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class CymexNodeContextMenuFactory implements CyNodeViewContextMenuFactory, 
                                                    ActionListener{

    CymexContext context;

    public CymexNodeContextMenuFactory( CymexContext context ){
        this.context = context;        
    }
    
    @Override
    public CyMenuItem createMenuItem( CyNetworkView netView,
                                      View<CyNode> nodeView ){

        CymexLinkFactory mlf = new CymexLinkFactory( context );
        CyMenuItem cyMenuItem = mlf.addNodeLinks( nodeView );
        
        if( cyMenuItem == null ){
            JMenuItem menuItem = new JMenuItem( "Cymex" );
            menuItem.setEnabled( false );
            cyMenuItem = new CyMenuItem( menuItem, 0 );
        }
        
        return cyMenuItem;
    }

    public void actionPerformed( ActionEvent e ){}
}



