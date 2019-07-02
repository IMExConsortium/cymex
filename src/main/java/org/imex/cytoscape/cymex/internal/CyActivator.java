package org.imex.cytoscape.cymex.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;
import org.cytoscape.service.util.AbstractCyActivator;

public class CyActivator implements BundleActivator{

    CymexActivator myCymexActivator;
    
    public CyActivator(){
        myCymexActivator = new CymexActivator();        
    }

    public void start( BundleContext bc ){
        try{
            myCymexActivator.start( bc );
        } catch( Exception x ){
            x.printStackTrace();
        }
    }
    
    public void stop( BundleContext bc ){
        myCymexActivator.serverStop();
        myCymexActivator.stop( bc );
    }

}
