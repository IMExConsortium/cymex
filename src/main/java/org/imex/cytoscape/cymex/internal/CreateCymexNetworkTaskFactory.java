package org.imex.cytoscape.cymex.internal;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CreateCymexNetworkTaskFactory extends AbstractTaskFactory {

    private final CymexContext context;
    
    public CreateCymexNetworkTaskFactory( final CymexContext context ){
        this.context = context;
    }
    
    public TaskIterator createTaskIterator(){
        return new TaskIterator( new CreateCymexNetworkTask ( context ) );
    }
}
