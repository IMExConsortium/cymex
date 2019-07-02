package org.imex.cytoscape.cymex.internal;

import java.util.*;
import javax.swing.*;

import java.net.URI;
import java.awt.Desktop;

import java.awt.event.ActionEvent;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;

import org.cytoscape.application.swing.CyMenuItem;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * Generates links to external web pages using urls specified in 
 * cymex.xml configuration file. 
 
*/

public class CymexLinkFactory {
    
    CymexContext context;
    
    public CymexLinkFactory( CymexContext context ){
	this.context = context;
    }

    /**
     * Generates URL links based on node attributes and 
     * places them in CyMenu list
     * @param nodeView the NodeView.
     * @return CyMenuItem
     ***/

    public CyMenuItem addNodeLinks( View<CyNode> nodeView ){

        CyMenuItem topCyMenu = null;
        
	// Cytoscape node & atrributes
	//----------------------------
        
        CyNode cynode = nodeView.getModel(); 
        CyNetwork network = context.getApplicationManager().getCurrentNetwork();
        CyRow cyrow = network.getDefaultNodeTable().getRow( cynode.getSUID() ); 
        
	// Filter to use
	//---------------

	CymexFilter filter = null;
        
	// JMenu to build
	//---------------

	JMenu top_menu = null;

        if( !java.awt.Desktop.isDesktopSupported() ){
            System.out.println( "CymexLinkFactory.addNodeLinks: NO desktop support");
            return topCyMenu;
        }
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ){ 
            System.out.println( "CymexLinkFactory.addNodeLinks: NO browser support");
            return topCyMenu;
        }
        
	// test for filter name
	//---------------------
	
	try{
            String cMapName = cyrow.get( "cym:filter", String.class );
            filter = context.getFilter( cMapName );
	    
	}catch( ClassCastException cce ){
	    // wrong type - ignore
	}
	
        if(context.isDebugOn() ){
            System.out.println( "CymexLinkFactory.addNodeLinks: filter OK");
        }

        if( filter != null ){  // map set by mis:filter field

	    // get attribute list
	    //-------------------
            
            Map<String,Object> attMap = cyrow.getAllValues();
            
	    //String[] an = nAtt.getAttributeNames();
	    
	    top_menu = new JMenu("Cymex");

	    // go over attributes
	    //-------------------

	    for( Iterator<Map.Entry<String,Object>> ai = attMap.entrySet().iterator();
                 ai.hasNext(); ){
                
                Map.Entry<String,Object> ame = ai.next();
                
		try{
		    String cAttName = ame.getKey();
		    String cAttValue = (String) ame.getValue();
                    if(context.isDebugOn() ){
                        System.out.println( "Att: " + cAttName+" -> " + cAttValue );
                    }
		    if( cAttValue!=null && cAttValue.length()>0 ){
			
			List nl= (List) filter.getNodeFilter();  //( "ndeList" );
			
			for( Iterator nli=nl.iterator();nli.hasNext(); ){
			    Map nd = (Map) nli.next();
                            
			    Map atRU= (Map) nd.get("atRU");
			    Map atRI= (Map) nd.get("atRI");

			    if( atRU!=null ){
				for( Iterator atRUi = atRU.keySet().iterator();
                                     atRUi.hasNext(); ){
                                    
				    String sAt = (String) atRUi.next();
				    String sAtRU = (String) atRU.get(sAt);
				    String aStRI = (String) atRI.get(sAt);

				    if( sAt.equals( cAttName ) ){

					String cAttUrl = sAtRU
                                            .replaceFirst( aStRI, cAttValue );

					JMenuItem new_jmi = this
                                            .addMenuItem( cAttName, cAttUrl );
					top_menu.add( new_jmi );
                                        System.out.println( cAttName + " : " + cAttUrl );
				    }

				}
			    }
			}   	       
		    }		
		}catch( ClassCastException cce ){
		    // wrong type - ignore 
		}
	    }
	}
        
        if( top_menu != null ){
            topCyMenu = new CyMenuItem( top_menu, 0 );
        }
        
        return topCyMenu;
    }
    
    
    /**
     * Generate URL links based on edge attributes and places them in 
     * CyMenu list
     * @param edgeView edgeView object
     * @return CyMenuItem
     **/

    public CyMenuItem addEdgeLinks( View<CyEdge> edgeView ){
   
        CyMenuItem topCyMenu = null;
        
	// Cytoscape edge & atrributes
	//----------------------------
        
        CyEdge cyedge = edgeView.getModel(); 
        CyNetwork network = context.getApplicationManager().getCurrentNetwork();
        CyRow cyrow = network.getDefaultEdgeTable().getRow( cyedge.getSUID() ); 
        
	// JMenu to build
	//---------------
        
	JMenu top_menu = null;

        if( !java.awt.Desktop.isDesktopSupported() ){
            System.out.println( "CymexLinkFactory.addEdgeLinks: NO desktop support");
            return topCyMenu;
        }
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ){ 
            System.out.println( "CymexLinkFactory.addEdgeLinks: NO browser support");
            return topCyMenu;
        }
        
        // Filter map to use
	//------------------

	CymexFilter filter = null;
        
	// test for filter name
	//---------------------
	
	try{
            String cMapName = cyrow.get( "cym:filter", String.class );
            filter = context.getFilter( cMapName );
	    
	}catch( ClassCastException cce ){
	    // wrong type - ignore
	}
        
        if(context.isDebugOn() ){	
            System.out.println( "CymexLinkFactory.addEdgeLinks: filter OK" );
        }
        
        if( filter!=null ){  // map set by mis:filter field exists
            
            // get attribute list
            //-------------------
            
            Map<String,Object> attMap = cyrow.getAllValues();

            //String[] an = nAtt.getAttributeNames();

            top_menu = new JMenu("Cymex");

            // go over attributes
            //-------------------

            for( Iterator<Map.Entry<String,Object>> ai = attMap.entrySet().iterator();
                 ai.hasNext(); ){

                Map.Entry<String,Object> ame = ai.next();

                try{
                    String cAttName = ame.getKey();
                    System.out.println("Edge Att: "+ cAttName );
                    
                    List vlist = new ArrayList();

                    if( ame.getValue() instanceof List ){
                        vlist = (List) ame.getValue();
                    } else {
                        vlist.add( ame.getValue() );
                    }

                    for( Iterator iv = vlist.iterator(); iv.hasNext(); ){

                        String cAttValue = iv.next().toString();
        
                        if(context.isDebugOn() ){
                            System.out.println("Edge Att: "+cAttName+" -> "+ cAttValue );
                        }
                    
                        if( cAttValue != null && cAttValue.length()>0 ){
			
                            List el= (List) filter.getEdgeFilter(); //("edgList");

                            System.out.println(el);

                            for(Iterator eli=el.iterator();eli.hasNext();){
                                Map ed = (Map) eli.next();

                                Map atRU= (Map) ed.get("atRU");
                                Map atRI= (Map) ed.get("atRI");
			    
                                System.out.println(ed);

                                if(atRU!=null){
				
                                    for(Iterator 
                                            atRUi=atRU.keySet().iterator();
                                        atRUi.hasNext();){
				    
                                        String sAt  = (String) atRUi.next();
                                        String sAtRU= (String) atRU.get(sAt);
                                        String aStRI= (String) atRI.get(sAt);
                                        
                                        if(sAt.equals(cAttName)){
					
                                            String cAttUrl
                                                =sAtRU.replaceFirst(aStRI,
                                                                    cAttValue);
                                            JMenuItem new_jmi
                                                = this.addMenuItem(cAttName,
                                                                   cAttUrl);
                                            top_menu.add(new_jmi);
                                        }
                                    }
                                }
                                
                                // data links
                                //-----------

                                Map dtaRU= (Map) ed.get("dtaRU");
                                Map dtaRI= (Map) ed.get("dtaRI");
			    
                                if( dtaRU != null ){
				
                                    for(Iterator 
                                            dtaRUi=dtaRU.keySet().iterator();
                                        dtaRUi.hasNext();){
				                                

                                        String sDta  = (String) dtaRUi.next();
                                        String sDtaRU= (String) dtaRU.get(sDta);
                                        String aDtaRI= (String) dtaRI.get(sDta);
                                        
                                        
                                        System.out.println(sDta);
                                                                
                                        
                                        if(sDta.equals(cAttName)){
                                            
                                            String cDtaUrl
                                                =sDtaRU.replaceFirst(aDtaRI,
                                                                     cAttValue);
                                            JMenuItem new_jmi
                                                = this.addMenuItem(cAttName,
                                                                   cDtaUrl);
                                            top_menu.add(new_jmi);
                                        }
                                    }
                                }
			    }                            
			}
		    }		   	       
		}catch(Exception cce){
		    // wrong type - ignore 
                    cce.printStackTrace();
		}
	    }
	}

        if( top_menu != null ){
            topCyMenu = new CyMenuItem( top_menu, 0 );
        }
        
        return topCyMenu;
    }

    //--------------------------------------------------------------------------    

    private JMenuItem addMenuItem( String name, String url ){
	
	final String fUrl = url;
	
	JMenuItem new_jmi= new JMenuItem( new AbstractAction( name ){
		  
                public void actionPerformed (ActionEvent e){
                    if(context.isDebugOn() ){
                        System.out.println( "misink menu action: " + fUrl);
                    }
                    SwingUtilities.invokeLater( new Runnable(){
                            public void run(){
                                try {
                                    java.awt.Desktop desktop 
                                        = java.awt.Desktop.getDesktop();
                                    java.net.URI uri = new java.net.URI( fUrl );
                                    if(context.isDebugOn() ){
                                        System.out.println( "MiSink run: " +
                                                            fUrl );
                                    }
                                    desktop.browse( uri );
                                } catch( Exception e ){
                                    System.err.println( e.getMessage() );
                                }
                            }
                        });
                }
            } );
	
        if(context.isDebugOn() ){
            System.out.println( "misink menu item created: " 
                                + name +": " + url );
        }
        return new_jmi;
    }
};
