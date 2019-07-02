package org.imex.cytoscape.cymex.internal;

import java.awt.event.ActionEvent;
import java.util.List;

//import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.*;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;


public class CymexImportUrlAction extends AbstractCyAction {

    private static final long serialVersionUID = 1L;
    private CySwingApplication desktopApp;
    
    CymexConfig msc;
    CymexContext context;

    public CymexImportUrlAction( CymexContext context ){

        super( "Network from URL..." );
        setPreferredMenu( "Apps.Cymex.Import" );
        this.context = context;
    }

    @Override
    public void actionPerformed( ActionEvent ae ){
        
        CymexImportUrl msc = new CymexImportUrl( context );
        msc.setLocationRelativeTo( context.getSwingApplication().getJFrame() );
        msc.setVisible( true );
        
    }
}
