package org.imex.cytoscape.cymex.internal;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;

import org.imex.cytoscape.cymex.internal.CymexContext;
import org.imex.cytoscape.cymex.internal.ui.CymexConfigDialog;

public class CymexConfigAction extends AbstractCyAction {

    private static final long serialVersionUID = 1L;
    private CySwingApplication desktopApp;
    
    CymexConfig msc;
    CymexContext context;

    public CymexConfigAction( CymexContext context ){

        super( "Configure" );
        setPreferredMenu( "Apps.Cymex" );
        this.context = context;
    }

    @Override
    public void actionPerformed( ActionEvent ae ){
        
        CymexConfigDialog msc = new CymexConfigDialog( context );
        msc.setLocationRelativeTo( context.getSwingApplication().getJFrame() );
        msc.setVisible( true );
    }
}
