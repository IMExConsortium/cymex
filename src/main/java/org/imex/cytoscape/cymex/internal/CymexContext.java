package org.imex.cytoscape.cymex.internal;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.osgi.framework.BundleContext;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;

import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.work.TaskManager;

import org.cytoscape.session.CyNetworkNaming;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;

import org.imex.cytoscape.cymex.internal.model.*;


public class CymexContext {

    
    public final static List<String> EXPROLE_IGNORE_LIST =        
        Arrays.asList( "MI:0684", "MI:0581", "MI:0582" ); 
    //                 ancillary, suppressed gene, supressor gene
    
    public final static List<String> BIOROLE_IGNORE_LIST =
        Arrays.asList( "MI:0684", "MI:0941" ); 
    //                 ancillary, competitor
    
    // suid maps
    //----------

    private Map<Long,Map<String,Long>> miNode =
        new HashMap<Long,Map<String,Long>>();  // netSUID, nodeID -> nodeSUID 

    private Map<Long,Map<String,Long>> miEdge =
        new HashMap<Long,Map<String,Long>>(); //  netSUID, edgeID -> edgeSUID 
    
    private CymexConfig myConfig;
    private CymexFilterConfig myFilterConfig;

    private CymexServerThread msth;
    private CymexActivator mca;

    // cymexnode id to cymexNode map
    private Map<String, CymexRefNode> refNodeMap = new HashMap<String,CymexRefNode>();

    // nodeSUID to cymexNode id map 
    private Map<String, Long> nodeSuidMap = new HashMap<String,Long>();
    
    // cymexedge id to cymexedge map
    private Map<String,CymexEdge> ceMap = new HashMap<String,CymexEdge>();  

    // cymexdata id to cymexdata id map
    private Map<String,CymexData> cdtMap = new HashMap<String,CymexData>();  

    // cymexevid id to cymexevid map
    private Map<String,CymexEvid> cevMap = new HashMap<String,CymexEvid>();  
    

    // type -> edge expansion mode status map
    //---------------------------------------

    private Map<String,String> edgeExpand = null;

    // mode merge status
    //------------------

    private String nodeMerge = "none";
    
    // node ignore selection status
    //-----------------------------

    int idIgnoreIndex = 0;

    // constructor
    //------------

    public CymexContext( CymexActivator mca ){   
        this.mca = mca;
    }
    
    public void setServerThread( CymexServerThread msth ){
        this.msth = msth;
    }

    public CymexServerThread getServerThread(){
        return msth;
    }

    public void setConfig( CymexConfig config ){
        this.myConfig = config;
    }

    public CymexConfig getConfig(){
        return myConfig;
    }

    public final void setFilterConfig( CymexFilterConfig filterConfig ){
        this.myFilterConfig = filterConfig;
    }
    
    public final CymexFilter getFilter( String name ){
        return myFilterConfig.getFilter( name );
    }
    
    public final Map<String,Long> getMiNodeMap( Long suid ){

        synchronized( miNode ){
            if( miNode.get( suid ) == null ){
                miNode.put( suid, new HashMap<String,Long>() );
            }
        }
        return miNode.get( suid );
    }
    
    public final Map<String,Long> getMiEdgeMap( Long suid ){
        
        synchronized( miEdge ){
            if( miEdge.get( suid ) == null ){
                miEdge.put( suid, new HashMap<String,Long>() );
            }
        }
        return miEdge.get( suid );
    }

    // node map
    //---------
    
    public final Map<String,CymexRefNode> getCymexRefNodeMap(){
        return refNodeMap;
    }
    
    public final void addCymexRefNode( CymexRefNode node ){
        if( ! refNodeMap.keySet().contains( node.getId() ) ){
            refNodeMap.put( node.getId(), node );
        } 
    }
    
    public final CymexRefNode getCymexRefNode( String id ){
        if( refNodeMap.keySet().contains( id ) ){
            return refNodeMap.get( id );
        } else {
            return null;
        }
    }

    public final Long getNodeSUID( String id ){
        if( nodeSuidMap.keySet().contains( id ) ){
            return nodeSuidMap.get( id );
        } else {
            return null;
        }
    
    }
    
    public final void addNodeSUID( String id, Long suid ){
        nodeSuidMap.put( id, suid );          
    }

    /*
    public final String getCymexRefNodeId( Long suid ){
        if( cyNodeId.keySet().contains( suid ) ){
            return cyNodeId.get( suid );
        } else{ 
            return null;
        }
    }
    */
        
    // data map
    //------------
    
    public final Map<String,CymexData> getCymexDataMap(){
        return cdtMap;
    }
    
    public final void putCymexData( CymexData  cdt ){
        if( ! cdtMap.keySet().contains( cdt.getId() ) ){
            cdtMap.put( cdt.getId(), cdt );
        }
    }
    
    public final CymexData getCymexData( String id ){
        if( cdtMap.keySet().contains( id ) ){
            return cdtMap.get( id );
        }
        return null;
    }
        
    // evidence map
    //------------
    
    public final Map<String,CymexEvid> getCymexEvidMap(){
        return cevMap;
    }
    
    public final void putCymexEvid( CymexEvid  evid ){
        if( ! cevMap.keySet().contains( evid.getId() ) ){
            cevMap.put( evid.getId(), evid );
        }
    }
    
    public final CymexEvid getCymexEvid( String id ){
        if( cevMap.keySet().contains( id ) ){
            return cevMap.get( id );
        }
        return null;
    }
    
    // edge map
    //---------
    
    public final Map<String,CymexEdge> getCymexEdgeMap(){
        return ceMap;
    }

    public final void putCymexEdge( CymexEdge  edge ){
        if( ! ceMap.keySet().contains( edge.getId() ) ){
            ceMap.put( edge.getId(), edge );
        }
    }
    
    public final CymexEdge getCymexEdge( String id ){
        if( ceMap.keySet().contains( id ) ){
            return ceMap.get( id );
        }
        return null;
    }

   
    public int getPort(){
        return  myConfig.getPort();
    }

    public final String getVersion(){
        return myConfig.getVersion();
    }

    public boolean isDebugOn(){
        return myConfig.isDebugOn();
    }

    public void setDebug( boolean debug ){
        myConfig.setDebug( debug );
    }
    
    public final String getEdgeDepth(){
        return myConfig.getEdgeDepth();
    }

    public final void setEdgeDepth( String depth ){
        myConfig.setEdgeDepth( depth );
    }

    // -- self edge  --

    public final String getEdgeSelf(){       
        return myConfig.getEdgeSelf();
    }
    
    public final void setEdgeSelf( String stat ){
        myConfig.setEdgeSelf( stat );
    }

    // -- edge expand --

    public final String getEdgeExpand( String etype ){       
        return myConfig.getEdgeExpand( etype );
    }
    
    public final void setEdgeExpand( String etype, String op ){
        myConfig.setEdgeExpand( etype, op );
    }

    //-- node ignore by id --------
    
    public final List<String> getNodeIdIgnore( String key ){
        return myConfig.getNodeIdIgnore( key );
    }
    
    public final void addNodeIdIgnore( String key, List<String> list ){
        myConfig.addNodeIdIgnore( key, list );
    }
    
    public final List<String> getActiveNodeIdIgnore(){
        return myConfig.getNodeIdIgnore( idIgnoreIndex );
    }

    public final String[] getNodeIdIgnoreKeys(){
        return myConfig.getNodeIdIgnoreKeys();
    }
    
    // -- node ignore by id index ------
    
    public final int getIdIgnoreIndex(){       
        return idIgnoreIndex;
    }
    
    public final void setIdIgnoreIndex( int index ){
        idIgnoreIndex = index ;
    }
    
    //-- node merge ----------

    public final String getNodeMerge( ){
        return myConfig.getNodeMerge( );
    }
    
    public final void setNodeMerge( String merge ){
        myConfig.setNodeMerge( merge );
    }
    
    // ----------------------
        
    public boolean isServerOn(){
        if( msth == null ) return false;
        return true;
    }

    public void setServer( boolean start ){
        if( start && msth == null ){
            msth = new CymexServerThread( this );
            msth.start();
            return;
        }
        if(!start  && msth != null ){
            msth.terminate();
            return;
        }
    }
    
    public final CyApplicationManager getApplicationManager(){
        return  (CyApplicationManager) mca
            .getService( CyApplicationManager.class );
    }

    public final CySwingApplication getSwingApplication(){
        return (CySwingApplication) mca
            .getService( CySwingApplication.class );
    }
    
    public final CyNetworkFactory getNetworkFactory(){
        return (CyNetworkFactory) mca
            .getService( CyNetworkFactory.class );
    }
    
    public final CyNetworkNaming getNetworkNaming(){
        return (CyNetworkNaming) mca
            .getService( CyNetworkNaming.class );
    }

    public final CyNetworkManager getNetworkManager(){
        return (CyNetworkManager) mca
            .getService( CyNetworkManager.class );
    }
    
    public final CyNetworkViewManager getNetworkViewManager(){
        return (CyNetworkViewManager) mca
            .getService( CyNetworkViewManager.class );
    }
    
    public final CyNetworkViewFactory getNetworkViewFactory(){
        return (CyNetworkViewFactory) mca
            .getService( CyNetworkViewFactory.class );
    }

    public final VisualMappingManager getVisualMappingManager(){
        return (VisualMappingManager) mca
            .getService( VisualMappingManager.class );
    }

    public final VisualStyleFactory getVisualStyleFactory(){
        return (VisualStyleFactory) mca
            .getService( VisualStyleFactory.class );
    }

    public final VisualMappingFunctionFactory getPassthroughMappingFactory(){
        return (VisualMappingFunctionFactory) mca
            .getService( VisualMappingFunctionFactory.class, "(mapping.type=passthrough)" );
    }

    public final CyEventHelper getEventHelper(){
        return (CyEventHelper) mca
            .getService( CyEventHelper.class );
    }
    
    public final TaskManager getTaskManager(){
        return (TaskManager) mca
            .getService( TaskManager.class );
    }

}
