package org.imex.cytoscape.cymex.internal.ui;

import java.io.*;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;

import org.imex.cytoscape.cymex.internal.*;

public class CymexImportFileAction extends AbstractCyAction {

    private static final long serialVersionUID = 1L;
    private CySwingApplication desktopApp;
    
    CymexConfig msc;
    CymexContext context;

    public CymexImportFileAction( CymexContext context ){

        super( "Network from File..." );
        setPreferredMenu( "Apps.Cymex.Import" );
        this.context = context;
    }

    @Override
    public void actionPerformed( ActionEvent ae ){

        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new CymexMif25FileFilter());
        fc.addChoosableFileFilter(new CymexMifFileFilter());
        
        int returnVal = fc.showOpenDialog( context.getSwingApplication().getJFrame() );

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            System.out.println( "Opening: " + file.getName() );
            
            CymexImportFile cif = new CymexImportFile( context );
            cif.importMif25( file );
            
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }
}
