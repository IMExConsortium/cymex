package org.imex.cytoscape.cymex.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import java.util.*;

public class CreateCymexNetworkTask extends AbstractTask {
    
    private final String NETWORK_NAME ="Cymex";
    
    private final CymexContext context;

    public CreateCymexNetworkTask( CymexContext context ){
        this.context = context;
    }
	
    public void run( TaskMonitor monitor ){
        
        // Create an empty network
        
        CyNetwork myNet = context.getNetworkFactory().createNetwork();
        
        //myNet.getRow( myNet )
        //    .set( CyNetwork.NAME,
        //          context.getNetworkNaming()
        //          .getSuggestedNetworkTitle( NETWORK_NAME ));

        myNet.getDefaultNetworkTable().getRow( myNet.getSUID() )
            .set( "name", context.getNetworkNaming().getSuggestedNetworkTitle( "Cymex" )); 
        context.getNetworkManager().addNetwork( myNet );
    
    }        
    
}
