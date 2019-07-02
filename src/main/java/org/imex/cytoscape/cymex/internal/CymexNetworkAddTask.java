package org.imex.cytoscape.cymex.internal;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import java.util.*;
import java.io.BufferedReader;

public class CymexNetworkAddTask extends AbstractTask {
    
    private final String DEFAULT_NETWORK_NAME ="Cymex";
    
    private final CymexContext context;
    private String name = DEFAULT_NETWORK_NAME;
    BufferedReader reader;

    public CymexNetworkAddTask( CymexContext context, 
                                BufferedReader reader ){
        this.context = context;
        this.reader = reader;
    }

    public CymexNetworkAddTask( CymexContext context, 
                                BufferedReader reader,
                                String name ){
        
        this.context = context;
        this.reader = reader;
        this.name = name;
    }
	
    public void run( TaskMonitor monitor ){
        monitor.setTitle( "Cymex App" );
        monitor.setStatusMessage( "Adding New Data" );
        monitor.setProgress( 0.0 ); 
        CymexNetworkAdd cmn = new CymexNetworkAdd( context, name, reader );
        cmn.run( monitor );
        monitor.setProgress( 1.00 );
    }
}
