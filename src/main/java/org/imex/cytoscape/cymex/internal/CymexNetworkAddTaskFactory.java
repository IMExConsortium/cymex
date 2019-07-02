package org.imex.cytoscape.cymex.internal;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import java.io.BufferedReader;

public class CymexNetworkAddTaskFactory extends AbstractTaskFactory {

    private final CymexContext context;
    private String name = null;
    BufferedReader reader = null;
    
    public CymexNetworkAddTaskFactory( final CymexContext context, String name,
                                       BufferedReader reader ){
        this.name = name;
        this.context = context;
        this.reader = reader;
    }
    
    public CymexNetworkAddTaskFactory( final CymexContext context, BufferedReader reader ){
        this.context = context;
        this.reader = reader;
    }
    
    public TaskIterator createTaskIterator(){
        if( name == null ){
            return new TaskIterator( new CymexNetworkAddTask ( context, 
                                                               reader ));
        } else {
            return new TaskIterator( new CymexNetworkAddTask ( context, 
                                                               reader, 
                                                               name ));
        }
    }
}
