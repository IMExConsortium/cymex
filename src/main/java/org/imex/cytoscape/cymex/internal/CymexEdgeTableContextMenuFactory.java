package org.imex.cytoscape.cymex.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyEdgeViewContextMenuFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyColumn;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import org.cytoscape.task.AbstractTableCellTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CymexEdgeTableContextMenuFactory extends AbstractTableCellTaskFactory
    implements ActionListener{

    CymexContext context;
    
    public CymexEdgeTableContextMenuFactory( CymexContext context ){
        this.context = context;        
    }

    public TaskIterator createTaskIterator(CyColumn column, Object primaryKeyValue){
        return null;
    }


    public boolean isReady( CyColumn column, Object primaryKeyValue){
        return true;
    }

    /*

    @Override
    public CyMenuItem createMenuItem( CyNetworkView netView,
                                      View<CyEdge> edgeView ){

        MiLinkFactory mlf = new MiLinkFactory( context );
        CyMenuItem cyMenuItem = mlf.addEdgeLinks( edgeView );
        
        if( cyMenuItem==null ){
            JMenuItem menuItem = new JMenuItem( "Cymex" );
            menuItem.setEnabled( false );
            cyMenuItem = new CyMenuItem( menuItem, 0 );
        }
        
        return cyMenuItem;
    }
    */



    public void actionPerformed( ActionEvent e ){ }
}



