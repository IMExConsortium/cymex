package org.imex.cytoscape.cymex.internal;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;


public class CymexImportImexAction extends AbstractCyAction {

    private static final long serialVersionUID = 1L;
    private CySwingApplication desktopApp;
    
    CymexConfig msc;
    CymexContext context;

    public CymexImportImexAction( CymexContext context ){

        super( "Network from IMEx..." );
        setPreferredMenu( "Apps.Cymex.Import" );
        this.context = context;
    }

    @Override
    public void actionPerformed( ActionEvent ae ){
        
        CymexImport msc = new CymexImport( context );
        msc.setLocationRelativeTo( context.getSwingApplication().getJFrame() );
        msc.setVisible( true );
    }
}
