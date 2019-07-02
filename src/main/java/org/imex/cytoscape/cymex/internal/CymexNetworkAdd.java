package org.imex.cytoscape.cymex.internal;
 
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import javax.xml.namespace.NamespaceContext;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

import org.cytoscape.model.*;
import org.cytoscape.view.model.*;
import org.cytoscape.view.vizmap.*;
import org.cytoscape.view.presentation.property.*;

import org.cytoscape.application.*;
import org.cytoscape.application.swing.*;
import org.cytoscape.work.*;

import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.service.util.AbstractCyActivator;

import org.imex.cytoscape.cymex.internal.model.*;

import java.util.concurrent.TimeUnit;

class CymexNetworkAdd{
    
    final VisualProperty<Double>  xLoc = BasicVisualLexicon.NODE_X_LOCATION;
    final VisualProperty<Double>  yLoc = BasicVisualLexicon.NODE_Y_LOCATION;
    final VisualProperty<Boolean> nSel = BasicVisualLexicon.NODE_SELECTED;
    final VisualProperty<Boolean> eSel = BasicVisualLexicon.EDGE_SELECTED;

    final String VS_NAME ="Cymex Style";
    
    CymexContext context; 
    String filterName;
    BufferedReader in;

    List<CyNode> newNodeList;
    List<CyNode> allNodeList;

    List<CyEdge> newEdgeList;
    List<CyEdge> allEdgeList;
    
    List newEvidList;
    List allEvidList;

    // rid maps (file-wide reference ids)
    //----------------------------------

    private Map<String,Long> ridNodeMap = null;
    private Map<String,Long> ridEdgeMap = null;
    private Map<String,Object> ridEvidMap = null;

    public CymexContext getContext(){
        return context;
    }
    
    List<CyNode> getNewNodeList(){
        return newNodeList;
    }

    List<CyEdge> getNewEdgeList(){
        return newEdgeList;
    }

    List<CyNode> getNewEvidList(){
        return newEvidList;
    }

    public CymexNetworkAdd( CymexContext context, String filterName,
                         BufferedReader in ){    
	this.in = in;
        this.filterName = filterName;
        this.context = context;        

        newNodeList = new ArrayList();
        allNodeList = new ArrayList();

        newEdgeList = new ArrayList();
        allEdgeList = new ArrayList();

        newEvidList = new ArrayList();
        allEvidList = new ArrayList();

        ridNodeMap = new HashMap<String,Long>();
        ridEdgeMap = new HashMap<String,Long>();
        ridEvidMap = new HashMap<String,Object>();
    }

    public void run(){
        run( null );
    }
    
    public void run( TaskMonitor monitor ){

        // create cymex network & view
        
        if( monitor !=null ){
            monitor.setStatusMessage( "Adding Network/View" );
            monitor.setProgress( 0.01 );
        }

        try{
            TimeUnit.SECONDS.sleep(1);
        } catch(Exception ex){}
        
        CyNetwork network 
            = context.getApplicationManager().getCurrentNetwork();        
        CyNetworkView view 
            = context.getApplicationManager().getCurrentNetworkView();
        
        if( monitor !=null ){
            monitor.setProgress( 0.02 );
        }
        
        // get filter configuration

        CymexFilter flt = context.getFilter( filterName );
        
        // node, evidence & edge lists
        //----------------------------
	
        List ndeList = flt.getNodeFilter(); // "ndeList" ); 
        List edgList = flt.getEdgeFilter(); // "edgList" ); 
        List evdList = flt.getEvidFilter(); // "evdList" ); 
        List dtaList = flt.getDataFilter(); // "dtaList" ); 
        
        NamespaceContext nsc = flt.getNamespaceContext(); 
        
	try{
            
	    // setup parser
	    //-------------
            
	    DocumentBuilderFactory docFac 
                = DocumentBuilderFactory.newInstance();

	    docFac.setNamespaceAware( true ); // never forget this!

	    DocumentBuilder builder = docFac.newDocumentBuilder();

            if( monitor !=null ){
                monitor.setStatusMessage( "Parsing Input Data" );
                monitor.setProgress( 0.03 );
            }
            
            try{
                TimeUnit.SECONDS.sleep(1);
            } catch(Exception ex){}
	    Document doc = builder.parse( new InputSource(in) );
            
            in.close();            
            
            XPathFactory xpFac = XPathFactory.newInstance();
            
            if( monitor !=null ){
                monitor.setStatusMessage( "Searching For Nodes" );
                monitor.setProgress( 0.25 );
            }
            try{
                TimeUnit.SECONDS.sleep(1);
            } catch(Exception ex){}
            
            //------------------------------------------------------------------
            // nodes 
            //------
            
            for( Iterator ni=ndeList.iterator(); ni.hasNext(); ){
                
                Map ndeMap = (Map) ni.next();

                
                List<Node> ndl = null;
                try{
                    ndl = XPT.getXPNodeList( doc, nsc,
                                             (String)ndeMap.get("xp") );
                } catch( XPathExpressionException epx) {
                    //
                }
                for( Node nde : ndl){ //int i=0; i< nodesN.getLength(); i++ ){
                    
                    CymexRefNode cn =
                        CymexNodeBuilder.build( context, flt, nde, ndeMap  );
                                       
                    context.addCymexRefNode( cn );                
                }
            }
                        
            if( monitor !=null ){
                monitor.setStatusMessage( "Nodes Parsed" );
                monitor.setProgress( 0.35 );
            }
            
            try{
                TimeUnit.SECONDS.sleep(1);
            } catch(Exception ex){}
            

            //------------------------------------------------------------------
            // evidence
            //---------
            
            if( monitor !=null ){
                monitor.setStatusMessage( "Parsing Evidence" );
                monitor.setProgress( 0.36 );
            }
            
            for( Iterator dti=dtaList.iterator(); dti.hasNext(); ){
                
                Map dtaMap = (Map) dti.next();
                
                List<Node> evl = null;
                try{
                    evl = XPT.getXPNodeList( doc, nsc,
                                             (String)dtaMap.get("xp") );
                } catch( XPathExpressionException epx) {
                    //
                }

                for( Node evd : evl){
                    CymexData ce =
                        CymexDataBuilder.build( context, flt, evd, dtaMap  );
                    
                    // add to cymex evidence list
                    // evdMap - > current node filter definition
                    
                    context.putCymexData( ce );

                    if( context.isDebugOn() ){  
                        System.out.println("  evid id: " + ce.getId());
                        System.out.println("    attributes: " + ce.getAttributeList());
                        System.out.println("\n");
                    }
                }
            }
            
            if( monitor !=null ){
                monitor.setStatusMessage( "Evidence Parsed" );
                monitor.setProgress( 0.50 );
            }
            
            try{
                TimeUnit.SECONDS.sleep(1);
            } catch( Exception ex ){}
            
            //------------------------------------------------------------------
            // edges
            //------

            List newCymexEdges = new ArrayList<CymexEdge>();
            
            if( context.isDebugOn() ){ 
                System.out.println("CymexNetworkAdd: find edges...");
            }
            
            if( monitor !=null ){
                monitor.setStatusMessage( "Parsing Edges" );
                monitor.setProgress( 0.51 );
            }
            
            for ( Iterator edi = edgList.iterator(); edi.hasNext();){

                // go over edge definitions
                //-------------------------
                
                Map edgMap = (Map) edi.next();
                System.out.println("####edgMap: " + edgMap);
                // xpath="//mif:interaction"
                String edgeXP = (String) edgMap.get("xpath");   
                
                // find edges
                //-----------
                List<Node> edl = null;

                try{
                    edl  = XPT.getXPNodeList( doc, nsc, edgeXP );
                } catch( XPathExpressionException epx) {
                }
                
                if( context.isDebugOn() ){
                    System.out.println( "XP (edge): " + edgeXP);
                    System.out.println( " found: " +
                                        edl.size() + " edges" );
                }
                
                // go over edges
                //--------------
                
                if( edl != null ){

                    for( Node edg : edl ){ 

                        CymexEdge ce = CymexEdgeBuilder.build( context, flt,
                                                               edg, edgMap  );
                        
                        System.out.println( "CymexEdge:\n" + ce );

                        context.putCymexEdge( ce );
                        newCymexEdges.add(ce);
                    }
                }
            }
            
            if( context.isDebugOn() ){
                System.out.println("\nGOT NEW EDGES " );
                System.out.println("   N=" + newCymexEdges.size());
            }
            
            List<CymexNode> newNodes = new ArrayList<CymexNode>();
            List<CymexEdge> newEdges = new ArrayList<CymexEdge>();
            
            for(Iterator<CymexEdge> cei = newCymexEdges.iterator();
                cei.hasNext(); ){
                
                List<String> vertexIgnoreList =  new ArrayList<String>();
                
                CymexEdge cce = cei.next();

                boolean edgeSkip = false;
                
                List<String> vids = cce.getVertexList();
                
                //  Homooligomer test (if needed ) 
                //  Note: 1) simplified version - single vertex test
                //           should also test multiple vertices with
                //           equivalent identifiers !!!!
                //
                //        2) not-icluded nodes result in droppped edge.
                //           is it always correct ?
                
                if( (! context.getEdgeSelf().equalsIgnoreCase("include") )
                    && vids.size() <=1 ) break;  
                
                // Test for ignored vertices: 
                
                for( Iterator<String>  cvi = vids.iterator();
                     cvi.hasNext(); ){
                    
                    String cnid = cvi.next(); 
                    
                    CymexNode cn = cce.getVertex( cnid );
                    
                    //  - context-defined ignore list
                         
                    if( context.getActiveNodeIdIgnore()
                        .contains( cn.getRefId() ) ){
                        vertexIgnoreList.add( cnid );
                    }

                    // NOTE: node-ignore,  filter-defined node ignore
                    //       should be defined while parsing file

                    // - ignore selected experimental/biological roles
                    //   (hardwired for now in CymexContext)
                    
                    for( Iterator<String> ai = cn.getAttributeList().iterator();
                         ai.hasNext(); ){
                        String attName = ai.next();
                        
                        if( attName.equalsIgnoreCase( "exproleAc") ){
                            
                            String attValue = cn.getAttribute( attName ) ;
                            if( CymexContext.EXPROLE_IGNORE_LIST
                                .contains( cn.getAttribute( attName) ) ){

                                vertexIgnoreList.add( cnid );                                
                            }                            
                        }
                        if( attName.equalsIgnoreCase( "bioroleAc") ){ 
                            if( CymexContext.BIOROLE_IGNORE_LIST
                                .contains( cn.getAttribute( attName) ) ){
                                vertexIgnoreList.add( cnid );
                            }                                
                        }
                    } 
                }

                // test if edge ok:
                //  -binary or self - drop nodes from multi-protein
                //                    if no expansion

                
                if( vids.size() == 1  &&
                    ! context.getEdgeSelf().equalsIgnoreCase("include") ){

                    // skip self edge

                    edgeSkip = true;
                    
                }
                if( vids.size() == 2 ){
                    if( vertexIgnoreList.size() > 0) {
                        // skip binary with ignored node

                        edgeSkip = true;
                    }
                    
                    String refIdA =
                        cce.getVertex( vids.get( 0 ) ).getRefId(); 
                    String refIdB =
                        cce.getVertex( vids.get( 1 ) ).getRefId(); 

                    if( ! context.getEdgeSelf().equalsIgnoreCase("include") &&
                        refIdA != null && refIdA.length() > 0 &&
                        refIdA.equalsIgnoreCase( refIdB ) ){

                        // skip self edge annotated ad binary

                        edgeSkip = true;
                        
                    }
                    // test for merged isoforms here ?                     
                }

                if( vids.size() > 2 ){
                    
                    if( vids.size() - vertexIgnoreList.size() > 2 ){

                        // test for expansion: ignore unexpanded n-aries

                        String edgeType = cce.getAttribute( "type" );                         
                        String expand = context.getEdgeExpand( edgeType );

                        if( expand == null ||
                            expand.equalsIgnoreCase("drop") ||
                            expand.equalsIgnoreCase("none") ){
                            
                            // skip unexpanded n-ary edge

                            edgeSkip = true;
                        }
                    }
                }

                if( ! edgeSkip ){

                    newEdges.add( cce );
                    
                    for( Iterator<String> vi = vids.iterator();
                         vi.hasNext(); ){
                        String vId = vi.next();
                        
                        if( ! vertexIgnoreList.contains( vId ) ){
                            newNodes.add( cce.getVertex( vId ) ); 
                        }                        
                    }
                }
            }

            //------------------------------------------------------------------
            // add new cymex nodes to cytoscape network
            //-----------------------------------------

            if( monitor !=null ){
                monitor.setStatusMessage( "Adding Nodes" );
                monitor.setProgress( 0.52 );
            }
            
            if( newNodes.size() > 0){

                // got nodes to add
                //------------------

                List<Long> layoutUNID = new ArrayList<Long>();
                
                for( Iterator<CymexNode> nni = newNodes.iterator();
                     nni.hasNext(); ){
                    
                    CymexNode nn = nni.next();
                    Long newUNID =
                        addCymexNode( network, view, nn.getRefNode() );

                    if( newUNID != null){
                        layoutUNID.add( newUNID );
                    }
                }
                
                //  add view
                //----------

                if(view == null){
                    CymexView.add( context );
                }
                
                
                // set layout
                //-----------
                // not yet written ....

                // just in case...
                
                context.getEventHelper().flushPayloadEvents();
            }

            //------------------------------------------------------------------
            // add new cymex edges to cytoscape network            
            //-----------------------------------------
            
            if( monitor !=null ){
                monitor.setStatusMessage( "Adding Edges" );
                monitor.setProgress( 0.75 );
            }
            
            for( CymexEdge ne : newEdges ){
                addCymexEdge( network, view, ne );
            }
            
            // just in case...
            context.getEventHelper().flushPayloadEvents();
            
	    CyTable cet = network.getDefaultEdgeTable();
            
            for( Iterator<CyEdge> ei = allEdgeList.iterator();
                 ei.hasNext(); ){
		
		CyEdge ce =  ei.next();
                               
                View<CyEdge> eView = view.getEdgeView( ce );
                if( eView != null ){
                    
                    // set status
                    //-----------

                    cet.getRow( ce.getSUID() ).set( CyNetwork.SELECTED, true );
                    eView.setVisualProperty( eSel, true );
                }
            }

            if( monitor !=null ){
                monitor.setStatusMessage( "Edges Added" );
                monitor.setProgress( 0.95 );
            }            
            
            VisualStyle style = context.getVisualMappingManager()
                .getCurrentVisualStyle();
            
            //style.apply( view );
            //view.updateView();           
            
            context.getEventHelper().flushPayloadEvents();

            if( monitor !=null ){
                monitor.setStatusMessage( "DONE" );
                monitor.setProgress( 1.00 );
            }            
            
        }catch( SAXException se ){
	    JOptionPane.showMessageDialog( context.getSwingApplication()
                                           .getJFrame(), "se" );
	    se.printStackTrace();
        }catch( ParserConfigurationException pce ){
	    JOptionPane.showMessageDialog( context.getSwingApplication()
                                           .getJFrame(), "pce" );
	    pce.printStackTrace();	    
	}catch( IOException ioe ){
            
            ioe.printStackTrace();
	}  
    }

    //--------------------------------------------------------------------------
    
    private void addCymexEdge( CyNetwork network, CyNetworkView view,
                               CymexEdge cymexEdge ){

        // note: there is no need to test for unary/binary self
        //       interactions but need to test for expanded binaries !!!
        
        if(  context.isDebugOn() ){
            System.out.println("  Adding edge: "  + cymexEdge.getId() );
            System.out.println( "vertex count:" +
                                cymexEdge.getVertexList().size() );
        }
        
        Map<String,Map> vertexKeepMap = new HashMap<String,Map>();
        
        boolean insertFlag = true;
        boolean homoFlag = false;
        boolean homoInsertFlag = false;
               
        // ignore node list
        //-----------------
        
        for( Iterator<String> vi = cymexEdge.getVertexList().iterator();
             vi.hasNext(); ){
            CymexNode vertex =  cymexEdge.getVertex( vi.next() );
            String vid = vertex.getRefId();
                
            if( context.getActiveNodeIdIgnore().size() > 0 &&
                context.getActiveNodeIdIgnore().contains( vid ) ){
                if( context.isDebugOn() ){                        
                    System.out.println("dropping: " + vid );
                }
            }  else {
                Map<String,Object> vmap = new HashMap();
                vmap.put( "vertex", vertex );
                vmap.put( "refid", vertex.getRefId() );
                //vmap.put( "id", vertex.getId() );
                    
                vertexKeepMap.put( vertex.getId(), vmap );
            }
        }
        
        // merge isoforms
        //---------------
        
        if( context.getNodeMerge().equalsIgnoreCase("isoform") ){ 

            for( Map.Entry<String,Map> entry  : vertexKeepMap.entrySet() ){
                
                CymexNode vertex = (CymexNode) entry.getValue().get("vertex");
                String refid = (String) entry.getValue().get("refid");
                
                // merge uniprot-?? -> uniprot 
                
                if( !refid.toLowerCase().startsWith("ebi-") ){ // exclude ebi identifiers
                    Pattern isop = Pattern.compile(".*-[0-9]{1,2}");
                    if( isop.matcher( refid ).matches() ){
                        int lastdash = refid.lastIndexOf('-');
                        String newRefid = refid.substring(0,lastdash );
                        
                        entry.getValue().put("refid", newRefid );                        
                    }                    
                }
            }
        }
        
        // generate binary edge list
        //--------------------------
        
        List<Map> binaryList = new ArrayList<Map>(); 
        
        if( cymexEdge.getVertexList().size() == 1){
            
            if( vertexKeepMap.containsKey( cymexEdge.getVertexList().get(0) ) ){
                List cbv = new ArrayList();                           
                cbv.add( cymexEdge.getVertex( cymexEdge.getVertexList().get(0)) );
                cbv.add( cymexEdge.getVertex( cymexEdge.getVertexList().get(0)) );
                        
                Map binary = new HashMap();
                binary.put( "edge", cymexEdge );
                binary.put( "vlist", cbv );
            
                binaryList.add( binary );
            }
        }else if( cymexEdge.getVertexList().size() == 2) {
                       
            if( vertexKeepMap.containsKey( cymexEdge.getVertexList().get(0) ) &&
                vertexKeepMap.containsKey( cymexEdge.getVertexList().get(1) ) ){
                List cbv = new ArrayList();                           
                cbv.add( cymexEdge.getVertex( cymexEdge.getVertexList().get(0)) );
                cbv.add( cymexEdge.getVertex( cymexEdge.getVertexList().get(1)) );
                        
                Map binary = new HashMap();
                binary.put( "edge", cymexEdge );
                binary.put( "vlist", cbv );
            
                binaryList.add( binary );
            }
            
        } else {

            if( cymexEdge.getAttribute("typeAc").equalsIgnoreCase("MI:0915") ){
                
                // expand/ignore physical
                //-----------------------
                
                String expand =
                    context.getEdgeExpand( "physical association" );
                
                if( expand.equalsIgnoreCase("drop") ){
                    insertFlag = false;
                }
            
                if( expand.equalsIgnoreCase("matrix") ){
                    
                    // matrix expand, type stays as is
                    //--------------------------------
                    
                    for( String vAname : cymexEdge.getVertexList() ){
                        CymexNode vA = cymexEdge.getVertex( vAname ); 
                        if( ! vertexKeepMap.containsKey( vA.getId() ) ) continue;
                        
                        String arole = vA.getAttribute( "exproleAc" );
                        
                        if( arole != null && 
                            ! arole.equalsIgnoreCase("MI:0684") ){
                            // skip ancillary

                            for( String vBname : cymexEdge.getVertexList() ){
                                CymexNode vB = cymexEdge.getVertex( vBname ); 
                                if( ! vertexKeepMap.containsKey( vB.getId() ) ) continue; 

                                String brole = vB.getAttribute( "exproleAc" );
                                
                                if( brole != null && 
                                    ! brole.equalsIgnoreCase( "MI:0684" ) ){
                                    // skip ancillary 
                                    
                                    if( vA.compareTo(vB) >= 0 ){
                                        
                                        List cbv = new ArrayList();
                                        cbv.add( vA);
                                        cbv.add( vB );

                                        Map<String,String> inference =
                                            new HashMap<String,String>();
                                        inference.put( "inf-source", cymexEdge.getEvid().getId() );
                                        inference.put( "inf-meth", "matrix expansion" );
                                        inference.put( "type", "physical association" );
                                        inference.put( "typeAc", "MI:0915" );
                                        
                                        Map binary = new HashMap();
                                        binary.put("edge", cymexEdge);                                    
                                        binary.put("vlist", cbv);
                                        binary.put("inference", inference );
                                        
                                        binaryList.add( binary );                                    
                                    } 
                                }
                            }
                        }
                    }                    
                }   
            }

            if( cymexEdge.getAttribute("typeAc").equalsIgnoreCase("MI:0914") ){


                // expand/ignore associations
                //---------------------------
                    
                String expand =
                    context.getEdgeExpand( "association" );

                if( expand.equalsIgnoreCase("drop") ){
                
                    // drop non-binaries
                    //------------------
                        insertFlag = false;                    
                }
                
                if( expand.equalsIgnoreCase("spoke") ){

                    // spoke expand, type switch
                    //---------------------------
                    
                    CymexNode bait = null;
                    List<CymexNode> prey = new ArrayList<CymexNode>();

                    for( String vname : cymexEdge.getVertexList() ){
                        CymexNode v = cymexEdge.getVertex( vname ); 
                        if( ! vertexKeepMap.containsKey( v.getId() ) ) continue;

                        String role = v.getAttribute( "exproleAc" );
                        
                        if( role != null && 
                            role.equalsIgnoreCase( "MI:0498" ) ){ // prey
                            prey.add( v );
                        } else { 
                            if( role != null && 
                                role.equalsIgnoreCase("MI:0496") ){ // bait
                                bait = v;
                            }
                        }                    
                    }    

                    if( bait != null && prey.size() > 0 ){
                        
                        for( Iterator<CymexNode> pit = prey.iterator();
                             pit.hasNext(); ){
                            
                            List cbv = new ArrayList();
                            
                            cbv.add( bait );
                            cbv.add( pit.next() );
                            
                            Map binary = new HashMap();
                            binary.put( "edge", cymexEdge);
                            binary.put( "vlist", cbv);
                            
                            Map<String,String> inference =
                                new HashMap<String,String>();
                            inference.put( "inf-source", cymexEdge.getEvid().getId() );
                            inference.put( "inf-meth", "spoke expansion" );
                            inference.put( "type", "physical association" );
                            inference.put( "typeAc", "MI:0915" );
                            
                            binary.put("inference", inference);
                            binaryList.add( binary );
                        }                   
                    }
                }                
            }                        
        }
        
        for( Iterator<Map> nbi = binaryList.iterator(); nbi.hasNext(); ){           
            addBinaryEdge( network, view, nbi.next() );
        }        
    }
    
    private void addBinaryEdge( CyNetwork network, CyNetworkView view,
                                Map binaryEdge){ 
        
        CymexNode va = (CymexNode) ((List) binaryEdge.get("vlist")).get(0);
        CymexNode vb = (CymexNode) ((List) binaryEdge.get("vlist")).get(1);
        
        CyNode cya = network.getNode( context.getNodeSUID( va.getRefId() ) );
        CyNode cyb = network.getNode( context.getNodeSUID( vb.getRefId() ) );
        
        CymexEdge edge =  (CymexEdge) binaryEdge.get("edge");
        
        Map infMap = (Map) binaryEdge.get("inference");
        
        CyTable ct = network.getDefaultEdgeTable();
        
        CyEdge cyedge = null;
        
        List<CyEdge> cel = network
            .getConnectingEdgeList( cya, cyb, CyEdge.Type.UNDIRECTED );
        
        if( cel.size() > 0 ){
            cyedge = cel.get( 0 );
        } else {
                
            // new edge
            //---------
            cyedge = network.addEdge( cya, cyb, false );
            //newEdgeList.add( cyedge );
            
            if( ct.getColumn( "interaction" ) == null ){ 
                ct.createColumn( "interaction", String.class, false, "" );
            }

            ct.getRow( cyedge.getSUID() ).set( "interaction", "pp" );
            ct.getRow( cyedge.getSUID() ).set( "name",
                                               va.getRefId() +
                                               " (pp) " +
                                               vb.getRefId() );
        }

        if( cyedge == null ) return;
        
        // add vertex attributes to nodes
        //-------------------------------

        CyTable vct = network.getDefaultNodeTable();
        
        for(String name :  va.getAttributeList() ){
            String value = va.getAttribute( name );
            
            if( vct.getColumn( "cym:" + name ) == null ){ 
                vct.createListColumn( "cym:" + name, String.class,
                                      false);
            }
            
            List<String> sv= vct.getRow( cya.getSUID() )
                .getList("cym:" + name, String.class );

            if(sv == null ) sv = new ArrayList<String>();
            sv.add(value);
            vct.getRow( cya.getSUID() ).set("cym:" + name, sv);
        }

        for( String name : vb.getAttributeList() ){           
            String value = vb.getAttribute( name );
            
            if( vct.getColumn( "cym:" + name ) == null ){ 
                vct.createListColumn( "cym:" + name, String.class,
                                      false  );
            }

            List<String> sv= vct.getRow( cyb.getSUID() )
                .getList("cym:" + name, String.class );
            
            if(sv == null )  sv = new ArrayList<String>();
            sv.add(value);
            vct.getRow( cyb.getSUID() ).set("cym:" + name, sv);            
        }
        
        // set edge attributes
        //---------------------
        
        for( Iterator<String> ati = edge.getAttributeList().iterator();
             ati.hasNext(); ){
            
            String name = ati.next();
            String value = edge.getAttribute( name );

            if( infMap != null ){
                if( name.equalsIgnoreCase("id") ){
                    name = name + "(inf)";
                }
                
                // type/typeAc ovewrite
                if( name.equalsIgnoreCase("type") ){
                    if( infMap.get( "type" ) != null ){                        
                        value = (String) infMap.get( "type" );
                    }
                }
                if( name.equalsIgnoreCase("typeAc") ){
                    if( infMap.get( "typeAc" ) != null ){                        
                        value = (String) infMap.get( "typeAc" );
                    }
                }
            }
            
            if( value != null ){            
                if( ct.getColumn( "cym:" + name ) == null ){
                    ct.createListColumn( "cym:" + name, String.class,
                                         false );
                }

                List<String> sv =
                    ct.getRow( cyedge.getSUID() ).getList("cym:" + name, String.class );
                if(sv == null ) sv = new ArrayList<String>();
                sv.add( value );
                ct.getRow( cyedge.getSUID() ).set("cym:" + name, sv);            
            }
        }
        
        // set evidence/inference attributes
        //----------------------------------
        
        if( infMap == null ){  // direct evidence

            // evidence attributes
            
            for( String name : edge.getEvid().getAttributeList() ){
                
                String value = edge.getEvid().getAttribute( name );
                if( value != null ){
                    if( ct.getColumn( "cym:" + name ) == null ){
                        ct.createListColumn( "cym:" + name, String.class,
                                             false );
                    }
                    
                    List<String> sv =
                        ct.getRow( cyedge.getSUID() )
                        .getList("cym:" + name, String.class );
                    
                    if(sv == null ) sv = new ArrayList<String>();
                    sv.add(value);
                    ct.getRow( cyedge.getSUID() ).set("cym:" + name, sv);
                }
            }

            // evidence data attributes
            
            for( String dname : edge.getEvid().getDataList() ){
                
                CymexData dta = edge.getEvid().getData( dname );
                for( String datt : dta.getAttributeList() ){
                    
                    String value = dta.getAttribute( datt );
                    if( value != null ){
                        if( ct.getColumn( "cym:" + datt ) == null ){
                            ct.createListColumn( "cym:" + datt, String.class,
                                                 false );
                        }
                    
                        List<String> sv =
                            ct.getRow( cyedge.getSUID() )
                            .getList("cym:" + datt, String.class );
                        
                        if( sv == null ) sv = new ArrayList<String>();
                        sv.add(value);
                        ct.getRow( cyedge.getSUID() ).set("cym:" + datt, sv);
                    }
                }
            }          
            
        } else { // inference

            // inf-meth
            if( infMap.get( "inf-meth" ) != null){
                
                if( ct.getColumn( "cym:inf-meth" ) == null ){ 
                    ct.createListColumn( "cym:inf-meth", String.class,
                                         false  );
                }
                
                List<String> sv =
                    ct.getRow( cyedge.getSUID() ).getList( "cym:inf-meth",
                                                           String.class );
                
                if( sv == null ) sv= new ArrayList<String>();                
                sv.add( (String) infMap.get( "inf-meth" ));
                ct.getRow( cyedge.getSUID() ).set("cym:inf-meth", sv); 
            }
            
            // inf-source
            if( infMap.get( "inf-source" ) != null){
                if( ct.getColumn( "cym:inf-source" ) == null ){ 
                    ct.createListColumn( "cym:inf-source", String.class,
                                         false );
                }
                
                List<String> sv =
                    ct.getRow( cyedge.getSUID() ).getList( "cym:inf-source",
                                                           String.class );
                if( sv == null ) sv = new ArrayList<String>();
                sv.add( (String) infMap.get( "inf-source" ));
                ct.getRow( cyedge.getSUID() ).set("cym:inf-source", sv); 
            }
            
            for( String aname : edge.getEvid().getAttributeList() ){
                
                String value = edge.getEvid().getAttribute( aname );
                
                if( ct.getColumn( "cym:" + aname ) == null ){ 
                    ct.createListColumn( "cym:" + aname, String.class,
                                         false );
                }
                List<String> sv = ct.getRow( cyedge.getSUID() ).getList("cym:" + aname, String.class);
                if( sv == null ) sv = new ArrayList<String>();
                sv.add( value );
                ct.getRow( cyedge.getSUID() ).set("cym:" + aname, sv);                 
            }
        }      
    }
        
    private Long addCymexNode( CyNetwork network, CyNetworkView view,
                               CymexRefNode cymexNode ){
        
        // returns SUID for new nodes, otherwise null
        
        String nodeId = cymexNode.getId();
        
        // merge isoforms
        //---------------
        
        if( context.getNodeMerge().equalsIgnoreCase("isoform") ){ 

            // merge uniprot-?? -> uniprot 
                
            if( !nodeId.toLowerCase().startsWith("ebi-") ){ // exclude ebi identifiers
                Pattern isop = Pattern.compile(".*-[0-9]{1,2}");
                if( isop.matcher( nodeId ).matches() ){
                    int lastdash = nodeId.lastIndexOf( '-' );
                    nodeId = nodeId.substring( 0, lastdash );                    
                }                    
            }
        }
        
        //  test if maps to SUID ?
        
        Long newNode = context.getNodeSUID( nodeId );
        if( newNode != null ) return null;  // return null if known
                    
        // create new node
        //----------------
        
        CyNode cynode = network.addNode();

        context.addNodeSUID( nodeId, cynode.getSUID() );
            
        // set name and filter name for the new node
        //------------------------------------------
            
        final CyRow nodeRow = network.getRow(cynode);
        nodeRow.set(CyNetwork.NAME, nodeId );
            
        context.getEventHelper().flushPayloadEvents();
            
        CyTable ct = network.getDefaultNodeTable();
            
        if( ct.getColumn( "cym:id" ) == null ){ 
            ct.createColumn("cym:id",String.class, false, "" );
        }
        ct.getRow( cynode.getSUID() ).set( "cym:id", nodeId );
            
        if( ct.getColumn( "cym:filter" ) == null ){
            ct.createColumn( "cym:filter", String.class, false, "" );
        }
        
        ct.getRow( cynode.getSUID() ).set( "cym:filter", filterName );
        context.getEventHelper().flushPayloadEvents();
           
        // allNodeList.add( cynode );
       
        // set attributes
        //---------------

        for( String aname : cymexNode.getAttributeList() ){
            
            String attValue = cymexNode.getAttribute( aname );
            
            if( aname!=null && attValue != null ){               
                
                if( ct.getColumn( aname ) == null ){
                    ct.createColumn( aname, String.class, false, "" );
                }
                ct.getRow( cynode.getSUID() ).set( aname, attValue );
            }
        }
                
        if( context.isDebugOn() ){  		                   
            System.out.println(" New SUID: " + cynode.getSUID().longValue());
        }
        return cynode.getSUID();
    }
}
