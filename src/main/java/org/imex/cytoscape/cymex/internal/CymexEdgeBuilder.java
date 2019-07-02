package org.imex.cytoscape.cymex.internal;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

import org.cytoscape.property.CyProperty;

import java.net.*;
import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import javax.xml.namespace.NamespaceContext;

import org.json.*;

import org.imex.cytoscape.cymex.internal.model.*;

public class CymexEdgeBuilder{
    
    public static CymexEdge build( CymexContext context, CymexFilter filter,
                                   Node edge, Map edgeMap ){
        
        // note: is it/should be thread safe ? 
        
        XPathFactory xpathFac = XPathFactory.newInstance(); 
        NamespaceContext nsContext = filter.getNamespaceContext(); 
        
        // get edge Id
        //------------
        
        String edgeRid = null;
        
        String edgeId = null;
        try{
            edgeId = XPT.getXPString( edge, nsContext,
                                      (String) edgeMap.get("id") );
        } catch( XPathExpressionException epx) {
            //
        }
            
        if( edgeId == null || edgeId.length() == 0 ) return null;
        
        if( context.isDebugOn() ){
            System.out.println( "   new edge: id= " + edgeId );
        }
        
        CymexEdge newEdge = new CymexEdge( edgeId ) ;
        
        // get edge attributes
        //--------------------
        
        Map attMap = (Map) edgeMap.get( "attribute" );   // edge attributes
        
        for( Iterator ai = attMap.keySet().iterator();
             ai.hasNext(); ){

            // go over attribute patterns
            //---------------------------
            
            String caName = (String) ai.next();

            // NOTE: might vary depending on type ?
                                    
            List valList = new ArrayList();

            // NOTE: or pass type info here to decide later            
            
            Map ca = (Map) attMap.get( caName );
                        
            String caXpath = (String) ca.get("xpath");
            String caRoot = (String) ca.get("xprt");
            String caTgt = (String) ca.get("tgt");
            String caRefUrl = (String) ca.get("refurl");
            String caRefId = (String) ca.get("refid");
            
            // find attribute
            //---------------
            
            try{
                List<String> caVal = 
                    XPT.getXPStringList( edge, nsContext,
                                         caXpath );
                
                for( String cv : caVal ){
                    Map<String,String> val = new HashMap();                                    
                    val.put( "value", cv );
                    if( caRefUrl != null ){
                        String ref = caRefUrl.replace( caRefId, cv );
                        val.put( "refurl", ref );
                    }
                    valList.add( val );
                    newEdge.setAttribute(caName, cv );                    
                }
            } catch( XPathExpressionException xpex){
                // noting parsed                
            }                        
        }
        
        if( context.isDebugOn() ){
            System.out.println( "   new edge: attributes" +
                                newEdge.getAttributeList() ) ;
        }
        
        // get vertices
        //-------------

        List vdefList = (List) edgeMap.get("vdefList");  // vertex defs 
        
        List vertexList = new ArrayList();  // *** list of vertices
        
        if(context.isDebugOn()){
            System.out.println( "   Looking for vertices...\n" );
        }
        
        for ( Iterator vdi = vdefList.iterator(); vdi.hasNext();){                   
            
            // go over vertex definitions
            //---------------------------
            Map vdef = (Map) vdi.next();
            
            String vrtXP = (String) vdef.get("vrtXP");   // xpath=".//mif:participant"
            List vpXPList = (List) vdef.get( "vpXP");    // parent: xpath (mif:iteractor)
            List rvpXPList = (List) vdef.get( "rvpXP");  // parent reference: refpath, xpath(%%REF%%)
            Map vattMap = (Map) vdef.get( "attribute" ); // vertex attributes
            
            List<Node> vertices = null;
            try{
                vertices = XPT.getXPNodeList( edge, nsContext,vrtXP );
            } catch( XPathExpressionException epx) {

            }
            int  j = 0;

            if( vertices != null ){
                for( Node vertex : vertices ){
                
                    if(context.isDebugOn()){
                        System.out.println("   Vertex # " + (j++)  );
                    }
                
                    // new vertex 
                    //-----------
                
                    CymexNode newVertex = null;
                    CymexRefNode parent = null;
                
                    // go over vertex candidates
                    //--------------------------
                
                    // find parent
                
                    Node vparent = null;
                    String parentid = null;
                    
                    if(context.isDebugOn()){
                        System.out.println("   searching for (direct) parent..." );
                    }
                    
                    // search direct parent
                    //---------------------
                    for ( Iterator vpxpi = vpXPList.iterator();
                          vpxpi.hasNext(); ){
                        
                        String vpxp = (String) vpxpi.next();
                        if(context.isDebugOn()){
                            System.out.println( "   XP(direct): " + vpxp );
                        }

                        try{
                            vparent = XPT.getXPNode( vertex, nsContext, vpxp);
                        } catch( XPathExpressionException epx) {
                        }
                        if( vparent != null ) break;
                    }
                    
                    if(context.isDebugOn()){
                        if( vparent != null){
                            System.out.println("   Direct parent: " + vparent );
                        } else {
                            System.out.println("   Direct parent: NONE");
                        }
                    }
                
                    // search reference to parent
                    //---------------------------
                
                    if( vparent == null ){
                    
                        if(context.isDebugOn()){
                            System.out.println("   searching for (reference) parent..." );
                        }
                        
                        for ( Iterator rvpxpi = rvpXPList.iterator();
                              rvpxpi.hasNext(); ){
                            
                            Map rvp = (Map) rvpxpi.next();
                            String refpath = (String) rvp.get("refpath");
                            String xpath = (String) rvp.get("xpath");
                            String idpath = (String) rvp.get("idpath");
                            String ref = (String) rvp.get("ref");
                            if(context.isDebugOn()){
                                System.out.println( "   XP(xpath):" + xpath );
                                System.out.println( "   XP(refpath):" + refpath );
                                System.out.println( "   XP(idpath):" + idpath );
                            }
                            try{
                                String  parentref = XPT.getXPString( vertex, nsContext, refpath);
                                String npath = xpath.replace( ref, parentref );
                                
                                vparent = XPT.getXPNode( vertex, nsContext, npath );
                            } catch( XPathExpressionException epx) {
                            }
                            if( vparent != null){
                                try{   
                                    parentid = XPT.getXPString( vparent, nsContext, idpath );
                                } catch( XPathExpressionException epx) {
                                }
                                if( parentid != null ){
                                    parent = context.getCymexRefNode( parentid );
                                    break;
                                }
                            }
                        }
                    }    
                    
                    if(context.isDebugOn() ){
                        System.out.println("  Parent: id= "  + parentid );
                        System.out.println("  Parent: "  + parent );
                    }
                    
                    /*   move to cymex2cyto conversion  !!!
                
                // test node ignore 
                //-----------------
                
                boolean ignore = false;
                
                List<Map> ndIgn = (List<Map>) filter.getNodeIgnoreFilter();
                
                for(Iterator<Map> nii = ndIgn.iterator(); nii.hasNext();){
                    Map ignMap = nii.next();
                    
                    NodeList fl = null;
                    
                    try{
                        XPath ignE = xpathFac.newXPath();
                        if( nsContext != null )
                            ignE.setNamespaceContext( nsContext );
                        
                        XPathExpression expNI =
                            ignE.compile( (String) ignMap.get("xpath") );
                        
                        Object featureList = expNI
                            .evaluate( vertex, XPathConstants.NODESET);
                        fl = (NodeList) featureList;
                    } catch(Exception ex){}
                    
                    if( fl != null && fl.getLength() > 0 ){                        
                        for (int k=0; k<fl.getLength(); k++){
                            Node feature = fl.item( k );
                            System.out.println("  filter feature value: " +feature.getNodeValue());
                            System.out.println("  filter list: " + ignMap.get("val"));
                            
                            if( ((List) ignMap.get("val")).contains( feature.getNodeValue() ) ){
                                ignore = true;
                                break;
                            }
                        }
                    }
                }

                    */

                    if( parent != null ){
                    
                        newVertex = new CymexNode( parent );

                        // or CymexNode( vertexName, parent ); when name present
                    
                        newEdge.setVertex( newVertex.getId(), newVertex );
                    
                        // find attributes for the vertex
                        //-------------------------------
                    
                        //Map newVAttr = new HashMap();
                        //newVertex.put("attribute", newVAttr );
                    
                        // go over attribute patterns
                        //---------------------------
                    
                        for( Iterator vai = vattMap.keySet().iterator();
                             vai.hasNext(); ){
                            
                            // attribute name
                            //---------------
                            
                            String cvaName = (String) vai.next();
                            
                            // NOTE: might vary depending on type ?
                            
                            //List cvaVal = new ArrayList();
                            
                            // NOTE: or pass type info here to decide later
                            
                            //newVAttr.put(cvaName, cvaVal );
                            
                            Map cva = (Map) vattMap.get( cvaName );
                            
                            String cvaXpath = (String) cva.get("xpath");
                            String cvaRoot = (String) cva.get("xprt");
                            String cvaTgt = (String) cva.get("tgt");
                            String cvaRefUrl = (String) cva.get("refurl");
                            String cvaRefId = (String) cva.get("refid");
                            
                            // find attribute
                            //---------------
                            
                            List<String> cvaList = null;

                            //--------------------------------------------------                            

                            try{
                                cvaList = XPT.getXPStringList( vertex, nsContext,
                                                               cvaXpath );
                            } catch( XPathExpressionException epx) {
                                //
                            } 
                            
                            String cvaValue = null;
                            String cvaValueUrl = null;
                            
                            for( String value : cvaList ){
                                if( cvaRefUrl != null ){
                                    cvaValueUrl =
                                        cvaRefUrl.replace( cvaRefId, value );
                                }
                                
                                newVertex.setAttribute( cvaName, value );
                                if( cvaValueUrl != null ){
                                    newVertex.setAttributeUrl( cvaName,
                                                               cvaValueUrl );
                                }
                            }
                        }
                        
                        if(context.isDebugOn()){
                            System.out.println("  Vertex: " + newVertex.toString() + "\n\n");        
                            System.out.println("  EDGE(so far): " + newEdge.toString() + "\n\n");        
                        }
                    }
                }                        
            }
        }
        //----------------------------------------------------------------------
        // find evidence
        //--------------
        
        List<Map> evidList = (List<Map>) edgeMap.get( "evid" );
        //List revidList = (List) edgeMap.get( "revid" );
        
        Node evid = null;                // *** evidence dom
        
        // go over direct evidence patterns
        //---------------------------------

        if( context.isDebugOn() ){
            System.out.println("   Looking for direct evidence...");
            System.out.println( evidList );
        }
        
        for ( Map evMap : evidList ){
            
            String evIdXP = (String) evMap.get("idXP");
            String evCode = (String) evMap.get("code");
            String evCodeXP = (String) evMap.get("codeXP");

            List<Map<String,String>> evDtaList =
                (List<Map<String,String>>) evMap.get("data");

            List<Map<String,String>> evDtaRefList =
                (List<Map<String,String>>) evMap.get("dataRef");
            
            String evidXP = (String) evMap.get("xpath");
            String evidID = (String) evMap.get("idpath");

            if(context.isDebugOn()){
                System.out.println("    direct evid xpath:" + evIdXP);
                System.out.println("    direct evid code:" + evCode);
                System.out.println("    direct evid codeXP:" + evCodeXP);
                System.out.println("    direct evid dtaList:" + evDtaList);
                System.out.println("    direct evid dtaRefList:" + evDtaRefList);
            }
            
            List<String> dataIdList = new ArrayList<String>();
            
            for( Map<String,String> dtaMap : evDtaList ){
                try{
                    List<Node> nl =
                        XPT.getXPNodeList( edge, nsContext,
                                           dtaMap.get( "xpath" ) );
                    for( Node n : nl ){
                        String id =
                            XPT.getXPString( n, nsContext,
                                             dtaMap.get( "idpath" ));
                        dataIdList.add(id);                        
                    }
                } catch(Exception ex) {}
            }
            
            if(context.isDebugOn()){
                System.out.println("   Direct Evid:  " +  dataIdList );
            }

            for( Map<String,String> dtaRefMap : evDtaRefList ){                
                try{                                       
                    String refId =
                        XPT.getXPString( edge, nsContext,
                                         dtaRefMap.get( "refpath" ) );
                    
                    if( refId != null ){
                        String xpath = dtaRefMap.get( "xpath" );
                        String ref = dtaRefMap.get( "ref" );
                        
                        String rxpath = xpath.replace( ref, refId ); 
                        
                        Node dtaNode =
                            XPT.getXPNode( edge, nsContext,
                                           rxpath);
                        if(context.isDebugOn()){            
                            System.out.println( "ref xpath: " + rxpath);
                            System.out.println( "ref'd node: " + dtaNode );
                        }
                        if( dtaNode != null ){
                            String id = null;
                            try{
                                id = XPT.getXPString( dtaNode, nsContext,
                                                      dtaRefMap.get( "idpath" ));
                            } catch( XPathExpressionException epx) {
                                //
                            }
                            if( id != null){
                                dataIdList.add(id);                        
                            }
                        }
                    }
                } catch( Exception ex ) {
                    System.out.println(ex);
                }
                
                if(context.isDebugOn()){
                    System.out.println("   Direct Evid:  idlist: " +  dataIdList );
                }
                
                if( dataIdList.size() > 0 ){
                    String nid = null;
                    try{
                        nid = XPT.getXPString( edge, nsContext, evIdXP );
                    } catch( XPathExpressionException epx) {
                        //
                    }
                    
                    
                    if( nid == null ){
                        nid = "interaction//evidence";
                    }
                    
                    CymexEvid nev = new CymexEvid(nid,evCode );

                    for( String dataId : dataIdList ){
                        
                        CymexData dta = context.getCymexData( dataId );
                        
                        if( dta != null ){
                            nev.setData( dataId, dta );
                        }
                    }
                    
                    // got ne evidence
                    
                    newEdge.setEvid( nev ); 
                }
            }
        }
        if(context.isDebugOn()){
            System.out.println(" New Edge: " + newEdge);
        }
        
        return newEdge;
    }
}
