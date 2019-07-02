package org.imex.cytoscape.cymex.internal;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import java.io.BufferedReader;

public class CymexNetworkCreateTaskFactory extends AbstractTaskFactory {

    private final CymexContext context;
    private String name = null;
    
    public CymexNetworkCreateTaskFactory( final CymexContext context, String name ){
        this.name = name;
        this.context = context;      
    }
     
    public CymexNetworkCreateTaskFactory( final CymexContext context ){
        this.context = context;     
    }
    
    public TaskIterator createTaskIterator(){
        if( name == null ){
            return new TaskIterator( new CymexNetworkCreateTask ( context ) );
        } else {
            return new TaskIterator( new CymexNetworkCreateTask ( context, name ) );
        }
    }
}
