package org.imex.cytoscape.cymex.internal;

import java.util.Properties;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.security.*;

import org.osgi.framework.BundleContext;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.CyAction;

import org.cytoscape.application.swing.CyEdgeViewContextMenuFactory;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.application.swing.AbstractCyAction;

import org.cytoscape.service.util.AbstractCyActivator;

import org.imex.cytoscape.cymex.internal.ui.*;

public class CymexActivator extends AbstractCyActivator {
    
    CymexContext context;
    CymexServerThread msth;
    BundleContext bc;
    
    public CymexActivator() {
        super();
    }
    
    public BundleContext getBundleContext(){
        return bc;
    }

    public <S> S getService( Class<S> serviceClass ){
        return getService( bc,  serviceClass );
    }

    public <S> S getService( Class<S> serviceClass, String filter ){
        return getService( bc,  serviceClass, filter );
    }

    public void start( BundleContext bc ){
        
        this.bc = bc;
        context = new CymexContext( this );
        
        // read configuration file(s)
        //--------------------------

        // app configuration
        
        CymexConfig mconf = new CymexConfig( "cymex.xml" );
        context.setConfig( mconf );

        // filter configuration
        
        CymexFilterConfig mfconf = new CymexFilterConfig( "cymex.xml" );
        context.setFilterConfig( mfconf );
        
        // start server thread
        //--------------------
        
        try{
            CymexServerThread msth = new CymexServerThread( context );
            msth.start();
        }catch( Exception e ){
            e.printStackTrace();
        }
        
        // Import File Action
        //-------------------
        
        CymexImportFileAction ifAction = new CymexImportFileAction( context );        
        registerService( bc, ifAction, CyAction.class, new Properties() );

        // Import Url Action
        //-------------------
        
        CymexImportUrlAction iuAction = new CymexImportUrlAction( context );        
        registerService( bc, iuAction, CyAction.class, new Properties() );

        // Import IMEx Action
        //-------------------
        
        CymexImportImexAction iiAction = new CymexImportImexAction( context );        
        registerService( bc, iiAction, CyAction.class, new Properties() );
        
        // configuration action
        //---------------------
        
        CymexConfigAction configAction = new CymexConfigAction( context );        
        registerService( bc, configAction, CyAction.class, new Properties() );
        
        // Node Context Menu
        //------------------

        CyNodeViewContextMenuFactory mncmf  
            = new MiNodeContextMenuFactory( context );
        
        Properties mncmfProps = new Properties();
        mncmfProps.put( "preferredMenu", "Apps" );
        registerAllServices( bc, mncmf, mncmfProps );


        // Edge Context Menu
        //------------------

        CyEdgeViewContextMenuFactory mecmf  
            = new MiEdgeContextMenuFactory( context );
        
        Properties mecmfProps = new Properties();
        mecmfProps.put( "preferredMenu", "Apps" );
        registerAllServices( bc, mecmf, mecmfProps );
        
        CymexEdgeTableContextMenuFactory etcmf =
            new CymexEdgeTableContextMenuFactory( context );

        Properties etcmfProps = new Properties();
        etcmfProps.put( "preferredMenu", "Apps" );
        registerAllServices( bc, etcmf, etcmfProps );

        
    }    

    public void serverStop(){
        if( context.getServerThread() != null ){
            context.getServerThread().terminate();
        }
    }
}
