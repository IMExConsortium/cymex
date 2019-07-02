package org.imex.cytoscape.cymex.internal;

import java.net.*;
import java.io.*;
import java.util.*;

import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskFactory;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

public class CymexImportFile{

    private static String FILTER = "MIF25";
    private static String NETWORK_NAME ="Cymex";
    
    CymexContext context; 
    
    public CymexImportFile( CymexContext context){ 
        this.context = context;
    }

    public void importMif25( File file ){
        readMifFile( file, CymexImportFile.FILTER);        
    }
    
    public void readMifFile( File file, String filterName ){

        try{   
            
            if( filterName == null  || filterName.trim().length() == 0 ){
                filterName = CymexImportFile.FILTER;
            }
            
            //CymexFilterConfig myFilterConfig = context.getFilterConfig();
            
            // file -> bufferred reader

            FileReader fr =  new FileReader( file );
            BufferedReader br = new BufferedReader( fr );
            
            CyNetwork network
                = context.getApplicationManager().getCurrentNetwork();
            CyNetworkView view
                = context.getApplicationManager().getCurrentNetworkView();
            
            if( network == null ){
                
                // Create an empty network

                network = context.getNetworkFactory().createNetwork();
               
                network.getDefaultNetworkTable().getRow( network.getSUID() )
                    .set( "name", context.getNetworkNaming().getSuggestedNetworkTitle( CymexImportFile.NETWORK_NAME ));
                context.getNetworkManager().addNetwork( network );               
                
            }
            
            if( filterName != null && context.getFilter( filterName ) != null ){
                
                TaskFactory naTF 
                    = new CymexNetworkAddTaskFactory( context,  filterName, br );
                
                context.getTaskManager().execute( naTF.createTaskIterator() ); 
		
            } else {
                if( context.isDebugOn() ){
                    String message = "Cymex App: unknown filter: ";
                    JOptionPane
                        .showMessageDialog( context.getSwingApplication()
                                            .getJFrame(),
                                            message + filterName );
                }
            }
            
        } catch(Exception e){
	    
            if( context.isDebugOn() ){
                e.printStackTrace();
            }	
        }
        if( context.isDebugOn() ){
            System.out.println("Cymex Server.run(): done...");
        }        
    }
}
