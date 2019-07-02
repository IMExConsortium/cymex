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

import org.json.*;

public class CymexFilterSet{

    private static final String CONFIG = "cymex.xml";
    
    private static final File configDir
        = new File( System.getProperty( "user.home" ),
                    CyProperty.DEFAULT_PROPS_CONFIG_DIR);
    
    Map filters = new HashMap();
    
    boolean debug = false;
    
    Map getMaps(){
	return filters;
    }

    Map getMap( String fnm ){
	return (Map) filters.get( fnm );
    }
    
    // constructor
    //------------
    
    CymexFilterSet( String fname ){
	this.read( fname );
    }
    
    public boolean isDebugOn(){
	return debug;
    }
    
    public void setDebug( boolean debug ){
        this.debug = debug;
    }
    
    //--------------------------------------------------------------------------
    
    private InputStream openLocalFile( String fileName ){
        
        InputStream is = null;

        try{
            final File configDir 
                = new File( System.getProperty( "user.home" ), 
                            CyProperty.DEFAULT_PROPS_CONFIG_DIR );
            
            final File localPropsFile = new File( configDir, fileName );

            if( localPropsFile.exists() ){
                is = new FileInputStream( fileName );
            } 
        }catch( FileNotFoundException fx ){ }
        return is;
    }

    //--------------------------------------------------------------------------
    
    private InputStream openDefaultJar( String fileName ){
        
        InputStream is = null;
        
        try {
            if ( !fileName.startsWith("/") ){
                fileName = "/" + fileName;
            }
            
            is = getClass().getResourceAsStream( fileName );
            if ( is == null ){

                JOptionPane.showMessageDialog( null, "Cymex Error: Cannot"
                                               + " open configuration file" );
            }
        } finally {}
        
        return is;
    }
    
    //----------------------------------------------------------------------
    // Read-in configuration file
    //---------------------------
    
    public void read( String fileName ){
        
        if( fileName == null || fileName.trim().length() == 0){
            fileName = CymexFilterSet.CONFIG;
        }

        InputStream is = openLocalFile( fileName );

        if( is == null ){
            is = openDefaultJar( fileName );
        }
        if( is == null ) return;
        
        System.out.println("Cymex FilterSet Config file: " + fileName );
        
  	try{
            
	    DocumentBuilderFactory docFac = DocumentBuilderFactory
                .newInstance();
            
            docFac.setNamespaceAware( true ); 
            
	    DocumentBuilder builder = docFac.newDocumentBuilder();
	    Document doc = builder.parse( is );
            
	    XPathFactory xpFac = XPathFactory.newInstance();

	    // version
	    //--------
	    /*
	    XPath xVer = xpFac.newXPath();
            XPathExpression eVer = xVer.compile( "//misink/@version" );
            
            Object rVer = eVer.evaluate( doc, XPathConstants.NODESET );
            NodeList verList = (NodeList) rVer;
	    
	    if( verList.getLength()>0 ){
		String ver = verList.item(0).getNodeValue();
		if( ver!=null ){
                    version = ver;
                }
                System.out.println( "MiSink Version:" + ver );
	    }            
            */
            // port setting
	    //-------------
	    /*
	    XPath xPrt = xpFac.newXPath();
            XPathExpression ePrt = xPrt.compile( "//misink/@port" );
            
            Object rPrt = ePrt.evaluate(doc, XPathConstants.NODESET);
            NodeList prtList = (NodeList) rPrt;

	    if( prtList.getLength()>0 ){
		String prt = prtList.item(0).getNodeValue();
		if( prt != null ){
                    
		    try{
			int p = Integer.parseInt( prt );
			if( p>5000 && p<49152 ){
			    port = p;
			}
		    }catch( NumberFormatException ne ){ }
		}
                System.out.println( "Port:" + port );
	    }
            */
	    // port announce
	    //--------------
            /*
	    try{
                final File localPortFile = new File( configDir, 
                                                     CymexFilterSet.PORTFN );

		FileWriter pfw = new FileWriter( localPortFile );
		pfw.write( Integer.toString( port ) );
		pfw.flush();
		pfw.close();
	    } catch( IOException ioe ){
		ioe.printStackTrace();
	    }
            */
            // configuration
            //--------------
            /*
            XPath xConf = xpFac.newXPath();
            XPathExpression eConf = xConf.compile( "//cymex/config/*" );
            Object rConf = eConf.evaluate( doc, XPathConstants.NODESET );
            NodeList confList = (NodeList) rConf;
            
            if( confList != null ){
                for( int j=0; j<confList.getLength(); j++ ){
                    Node confItem = confList.item(j);
                    String iname = confItem.getLocalName();
                    String ivalue = "";
                    Node valNode = confItem.getFirstChild();
                    if( valNode != null 
                        && valNode.getNodeType() == Node.TEXT_NODE ){
                        ivalue = ((Text) valNode).getWholeText();
                    }
                    
                    if( iname != null && iname.equals("edge-add-depth") ){
                        edgedepth = ivalue;
                    }

                    if( iname != null && iname.equals("debug") ){
                        if( ivalue != null ){
                            if( ivalue.equalsIgnoreCase( "true" ) ){
                                debug = true;
                            }
                            if( ivalue.equalsIgnoreCase( "false" ) ){
                                debug = false;
                            }
                            System.out.println("Debug:" + debug );
                        }
                    }
                    
                    if( iname != null && iname.equals("node-merge") ){
                        //<node-merge>distinct</node-merge>
                        nodeMerge = ivalue;
                    }
                    
                    
                    if( iname != null && iname.equals("edge-self") ){
                        //<edge-self>all</edge-self>
                        edgeSelf = ivalue;
                    }
                    
                    if( iname != null && iname.equals("expand-association") ){
                        //<expand-association>spoke</expand-association>
                        edgeExpand.put("association", ivalue);
                    }

                    if( iname != null && iname.equals("expand-physical") ){
                        //<expand-physical>matrix</expand-physical>
                        edgeExpand.put("physical association", ivalue);
                    }
                    
                    if( iname != null && iname.equals("node-id-ignore") ){
                        
                        if( ivalue.equals("none") ){
                            
                            //<node-ignore>none</node-ignore>
                            addNodeIdIgnore( "-- NONE --", new ArrayList<String>() );
                            
                        } else {

                            // json-formatted collection of identifier lists
                            //
                            //<node-ignore>{"-- NONE --":[]}</node-ignore>
                            //<node-ignore>{"Ubiquitin":["","","",""]}</node-ignore>
                            //...
                            
                            try{
                                JSONObject expandList = new JSONObject( ivalue );

                                for( Iterator<String> ik = expandList.keys(); ik.hasNext(); ){
                                    
                                    String key = ik.next();                                   
                                    JSONArray acArrray = expandList.getJSONArray( key );
                                    
                                    List<String> acList = new ArrayList<String>();
                                    
                                    for( Iterator iac = acArrray.iterator(); iac.hasNext(); ){
                                        
                                        Object nac = iac.next();

                                        if( nac instanceof String ){
                                            acList.add( (String) nac );
                                        }                                        
                                    }                                    
                                    addNodeIdIgnore( key, acList );                                    
                                }
                            } catch( JSONException jex ){
                                System.out.println(" JSON format error: " + ivalue );
                            }
                        }
                    }
                }                       
	    }
            */
            //------------------------------------------------------------------
	    // filters
	    //--------
	    
	    XPath xFlt = xpFac.newXPath();
	    XPathExpression eFlt = xFlt.compile( "/cymex//filter" );
	    Object rFlt = eFlt.evaluate( doc, XPathConstants.NODESET );
	    NodeList fltrList = (NodeList) rFlt;
            
	    // go over filters
	    //----------------
	    
	    for( int i=0; i<fltrList.getLength(); i++ ){
		Node flt = fltrList.item(i);
                
		// get filter name
		//----------------

		String fltName = flt.getAttributes()
                    .getNamedItem( "name" ).getNodeValue();
                
		Map fltMap = new HashMap();
		
		filters.put( fltName, fltMap );

                System.out.println( "Cymex Filter: " + fltName );
                
                // get filter namaspace
                //---------------------

                if( flt.getAttributes().getNamedItem( "nsURI" ) != null ){
                    
                    String fltNs = flt.getAttributes()
                        .getNamedItem("nsURI").getNodeValue();
                    
                    fltMap.put( "nsURI", fltNs );
                } else {
                    fltMap.put( "nsURI", "" );
                }
                
                // get filter prefix
                //------------------

                if( flt.getAttributes().getNamedItem("nsPrefix") != null ){
                    
                    String fltNs = flt.getAttributes()
                        .getNamedItem( "nsPrefix" ).getNodeValue();
                    
                    fltMap.put( "nsPrefix", fltNs );
                } else {
                    fltMap.put( "nsPrefix", "" );
                }

                System.out.println( " nsPrefix: " +  fltMap.get( "nsPrefix") +
                                    "    nsURI: " + fltMap.get( "nsURI") );
                                    
                // find node defs
		//---------------
		
		List ndeList = new ArrayList();
		fltMap.put( "ndeList", ndeList );	
	 
		XPath xNde = xpFac.newXPath();
		XPathExpression eNde = xNde.compile( "./node" );
                Object rNde = eNde.evaluate( flt, XPathConstants.NODESET );
                
		NodeList nodeList = (NodeList) rNde;
		
		for( int nl=0; nl<nodeList.getLength(); nl++ ){
		    Node nde = nodeList.item( nl );
		    
		    Map nodeMap = new HashMap();
                    ndeList.add( nodeMap );

                    NamedNodeMap ndeAtt = nde.getAttributes();

		    // node Id & xpath
		    //----------------

		    String ndeId = ndeAtt.getNamedItem("id").getNodeValue();
                    
                    String ndeXP = ndeAtt.getNamedItem("xpath").getNodeValue();

                    nodeMap.put( "id", ndeId );
		    nodeMap.put( "xp", ndeXP );

                    if( ndeAtt.getNamedItem("rid") != null ){
                        String ndeRID = ndeAtt.getNamedItem("rid").getNodeValue();
                        nodeMap.put( "rid", ndeRID );
                    }
                    
		    // find attributes
		    //----------------

		    XPath xAtt = xpFac.newXPath();
		    XPathExpression eAtt = xAtt.compile( "./attribute" );

		    Object rAtt = eAtt.evaluate(nde, XPathConstants.NODESET);
		    NodeList attList = (NodeList) rAtt;   
		    
		    Map attMap = new HashMap();
		    Map attRU  = new HashMap();
		    Map attRI  = new HashMap();

		    nodeMap.put( "at", attMap );
		    nodeMap.put( "atRU", attRU );
		    nodeMap.put("atRI",attRI);
		    
		    for( int al=0; al<attList.getLength(); al++){
			NamedNodeMap att = attList.item(al).getAttributes();
			
			String attName = att.getNamedItem("name")
                            .getNodeValue();
                        String attXP = att.getNamedItem("xpath").getNodeValue();
                        
			attMap.put( attName, attXP );
                        
			String attXRU = "";
			String attXRI = "%ID%";
			
			Node ndeXRU = att.getNamedItem( "refURL" );
			
			if( ndeXRU!=null ){
			    attXRU = ndeXRU.getNodeValue();
			    
			    Node ndeXRI = att.getNamedItem( "refIDS" );
			    if( ndeXRI!=null ){
				attXRI = ndeXRI.getNodeValue();
			    }
			    
			    if( attXRU.length()>0 ){
				attRU.put( attName, attXRU );
				attRI.put( attName, attXRI );
			    }
			}
		    }
                    
                    XPath xDta = xpFac.newXPath();
                    XPathExpression eDta = xDta.compile( "./node-data" );
                    
                    Object rDta = eDta.evaluate(nde, XPathConstants.NODESET);
                    NodeList dtaList = (NodeList) rDta;   
                
                    Map dtaRU  = new HashMap();
                    Map dtaRI  = new HashMap();

		    nodeMap.put( "dtaRU", dtaRU );
		    nodeMap.put( "dtaRI", dtaRI);

                    for( int al=0; al<dtaList.getLength(); al++){
			NamedNodeMap dta = dtaList.item(al).getAttributes();
			
			String dtaName = dta.getNamedItem("name")
                            .getNodeValue();
                        
                        String dtaXRU = "";
			String dtaXRI = "%ID%";
			
			Node ndtaXRU = dta.getNamedItem( "refURL" );
			
			if( ndtaXRU!=null ){
			    dtaXRU = ndtaXRU.getNodeValue();
			    
			    Node ndtaXRI = dta.getNamedItem( "refIDS" );
			    if( ndtaXRI!=null ){
				dtaXRI = ndtaXRI.getNodeValue();
			    }
			    
			    if( dtaXRU.length()>0 ){
				dtaRU.put( dtaName, dtaXRU );
				dtaRI.put( dtaName, dtaXRI );
			    }
			}
		    }                                
                }

                // find field ignores
                //-------------------
                
		XPath xIgn = xpFac.newXPath();
		XPathExpression eIgn = xIgn.compile( "./node-ignore" );
                Object rIgn = eIgn.evaluate( flt, XPathConstants.NODESET );
                
                NodeList ignList = (NodeList) rIgn;
            
                if( ignList != null ){
                    for( int j=0; j<ignList.getLength(); j++ ){
                        Node ignItem = ignList.item(j);
                        String iname = ignItem.getLocalName();
                        String ivalue = "";
                        Node valNode = ignItem.getFirstChild();
                        if( valNode != null 
                            && valNode.getNodeType() == Node.TEXT_NODE ){
                            ivalue = ((Text) valNode).getWholeText();
                        }
                        
                        // json-formatted list of  xpath & values to ignore
                        //
                        //<node-ignore>
                        //  {"feature-list":[
                        //        {"name":"<name>","xpath":"<xpath>", "value:["",""...]}, 
                        //        {}, ...]
                        //  }
                        //</node-ignore>
                        /*    
                        try{
                            JSONObject fields = new JSONObject( ivalue );
                            JSONArray flist = fields.getJSONArray( "field-list" );
                            
                            for( Iterator ifi = flist.iterator(); ifi.hasNext(); ){
                                JSONObject field = (JSONObject) ifi.next();
                                
                                String name = field.getString( "name" );
                                String xpath = field.getString( "xpath" );
                                JSONArray vals = field.getJSONArray( "vlist" );
                                
                                List<String> vlist = new ArrayList<String>();
                                
                                for( Iterator iv = vals.iterator(); iv.hasNext(); ){
                                        
                                    Object nval = iv.next();

                                    if( nval instanceof String ){
                                        vlist.add( (String) nval );
                                    }
                                }
                                addNodeIgnore( fltMap, name, xpath, vlist );                                    
                            }
                        } catch( JSONException jex ){
                            System.out.println(" JSON format error: " + ivalue );
                            jex.printStackTrace();
                        }
                        */
                    }                    
                }

                // find edge defs
		//---------------
		
		List edgList = new ArrayList();
		fltMap.put( "edgList", edgList );	
                
		XPath xEdg = xpFac.newXPath();
		XPathExpression eEdg = xEdg.compile("./edge");
		
		Object rEdg = eEdg.evaluate( flt, XPathConstants.NODESET );
		NodeList edgeList = (NodeList) rEdg;

		for( int el=0; el<edgeList.getLength(); el++ ){
		    
                    Node edg = edgeList.item( el );

                    System.out.println("   " + el);
                    
                    Map edgeMap = new HashMap();
                    edgList.add( edgeMap );
    
		    NamedNodeMap edgAtt = edg.getAttributes();
                    
		    // edge Id
		    //---------

		    String edgId = edgAtt.getNamedItem("id").getNodeValue();                    
                    edgeMap.put( "id", edgId );

                    System.out.println("  edge id: " + edgId);

                    // edge xpath
                    //----------- 

                    String edgXP = edgAtt.getNamedItem("xpath").getNodeValue();                    
                    edgeMap.put( "xpath", edgXP );

                    System.out.println("  edge xpath: " + edgXP);
                    
                    // find evidence node
                    //-------------------
                    
                    List  evList = new ArrayList();
                    edgeMap.put( "evid", evList );
                    
                    XPath xEvid = xpFac.newXPath();
		    XPathExpression eEvid = xEvid.compile( "./evid" );
		    
		    Object rEvid = eEvid.evaluate(edg, XPathConstants.NODESET);
		    NodeList evidList = (NodeList) rEvid;   
                    
                    for( int evl=0; evl<evidList.getLength(); evl++ ){ 

                        NamedNodeMap evid = evidList.item(evl).getAttributes();
                        String evidXP = evid.getNamedItem( "xpath" )
                            .getNodeValue();
                        
                        evList.add(evidXP);
                        System.out.println("   evidXP: " + evidXP);
                    }
                    
                    // find evidence reference node
                    //-----------------------------
                    
                    List  revList = new ArrayList();
                    edgeMap.put( "revid", revList );

                    XPath rexEvid = xpFac.newXPath();
		    XPathExpression reeEvid = rexEvid.compile( "./evid-ref" );
		    
		    Object reerEvid = reeEvid.evaluate(edg, XPathConstants.NODESET);
		    NodeList reevidList = (NodeList) reerEvid;   
                    
                    for( int evl=0; evl<reevidList.getLength(); evl++ ){ 

                        NamedNodeMap evid = reevidList.item(evl).getAttributes();
                        String refpathXP = evid.getNamedItem( "refpath" )
                            .getNodeValue();
                        String xpathXP = evid.getNamedItem( "xpath" )
                            .getNodeValue();

                        String refXP = evid.getNamedItem( "ref" )
                            .getNodeValue();

                        Map revd = new HashMap();
                        revd.put("refpath",refpathXP);
                        revd.put("xpath",xpathXP);
                        revd.put("ref",refXP);
                        revList.add(revd);
                        System.out.println("   revid: " + revd);
                    }
                    
                    
                    // find attributes
		    //----------------

		    XPath xAtt = xpFac.newXPath();
		    XPathExpression eAtt = xAtt.compile( "./attribute" );
		    
		    Object rAtt = eAtt.evaluate( edg, XPathConstants.NODESET );
		    NodeList attList = (NodeList) rAtt;   
		    
		    Map attributeMap = new HashMap();
		    
		    edgeMap.put( "attribute", attributeMap );

		    for( int al=0; al<attList.getLength(); al++ ){

			NamedNodeMap att = attList.item(al).getAttributes();
			String attName = att.getNamedItem( "name" ).getNodeValue();
                        
                        Map cattMap = new HashMap();
                        attributeMap.put( attName, cattMap );
                        
			cattMap.put( "xpath", att.getNamedItem( "xpath" ).getNodeValue() );
                        cattMap.put( "xprt", att.getNamedItem( "xprt" ).getNodeValue() );
                        cattMap.put( "tgt", att.getNamedItem( "tgt" ).getNodeValue() );
                        
			String attXRU = "";
			String attXRI = "%ID%";
			
			Node ndeXRU = att.getNamedItem( "refURL" );
			
			if( ndeXRU!=null ){
			    attXRU = ndeXRU.getNodeValue();
                            
			    Node ndeXRI = att.getNamedItem( "refIDS" );
			    if( ndeXRI!=null ){
				attXRI = ndeXRI.getNodeValue();
			    }
			    
			    if( attXRU.length()>0 ){                                
                                cattMap.put( "refurl",  attXRU );
				cattMap.put( "refid", attXRI );
			    }
			}			
		    }

                    // find data
                    //----------
                    
                    XPath xDta = xpFac.newXPath();
                    XPathExpression eDta = xDta.compile( "./edge-data" );
                    
                    Object rDta = eDta.evaluate( edg, XPathConstants.NODESET);
                    NodeList dtaList = (NodeList) rDta;   
                
                    Map dtaRU  = new HashMap();
                    Map dtaRI  = new HashMap();
                    
		    edgeMap.put( "dtaRU", dtaRU );
		    edgeMap.put( "dtaRI", dtaRI );

                    for( int al=0; al<dtaList.getLength(); al++){
			NamedNodeMap dta = dtaList.item(al).getAttributes();
			
			String dtaName = dta.getNamedItem("name")
                            .getNodeValue();
                        
                        String dtaXRU = "";
			String dtaXRI = "%ID%";
			
			Node ndtaXRU = dta.getNamedItem( "refURL" );
			
			if( ndtaXRU!=null ){
			    dtaXRU = ndtaXRU.getNodeValue();
			    
			    Node ndtaXRI = dta.getNamedItem( "refIDS" );
			    if( ndtaXRI!=null ){
				dtaXRI = ndtaXRI.getNodeValue();
			    }
                            
			    if( dtaXRU.length()>0 ){
				dtaRU.put( dtaName, dtaXRU );
				dtaRI.put( dtaName, dtaXRI );
			    }
			}
		    }

		    // find vertices
                    //---------------
		    
                    XPath xVrt = xpFac.newXPath();
                    XPathExpression eVrt = xVrt.compile( "./vertex" );

                    Object rVrt = eVrt.evaluate( edg, XPathConstants.NODESET );
                    NodeList vrtList = (NodeList) rVrt;

                    List vIdList = new ArrayList();
                    List vXpList = new ArrayList();

                    // vXpList.add( vXP );

                    List vdefList = new ArrayList();
                    edgeMap.put("vdefList", vdefList); 
                    
                    edgeMap.put( "vrtXP", vXpList );
                    edgeMap.put( "vrtId", vIdList );
		    
		    for( int vl=0; vl<vrtList.getLength(); vl++ ){
                        
                        Node vertex = vrtList.item( vl );

                        System.out.println( "   v: " + vl); 
                        
                        Map vrtDef = new HashMap();
                        vdefList.add( vrtDef );

                        NamedNodeMap vrt = vertex.getAttributes();

                        // vertex xpath
                        //-------------
                        
                        String vXP = vrt.getNamedItem( "xpath" ).getNodeValue();
                        vrtDef.put( "vrtXP", vXP );

                        
                        //vertex node
                        //-----------

                        XPath xVParent = xpFac.newXPath();
                        XPathExpression eVParent = xVParent.compile( "./vertex-node" );

                        Object rVParent = eVParent.evaluate( vertex, XPathConstants.NODESET );
                        NodeList vparentList = (NodeList) rVParent;

                        List  vpXPList = new ArrayList();
                        vrtDef.put( "vpXP", vpXPList);
                         
                        for( int vpl=0; vpl<vparentList.getLength(); vpl++ ){  
                            
                            NamedNodeMap vp = vparentList.item( vpl ).getAttributes();

                            // vertex parent xpath
                            //--------------------
                        
                            String vpXP = vp.getNamedItem( "xpath" ).getNodeValue();
                            vpXPList.add(  vpXP );

                            System.out.println( "    v parent: " + vpXP);
                        }
                        
                        //vertex node reference
                        //---------------------

                        XPath rxVParent = xpFac.newXPath();
                        XPathExpression reVParent = rxVParent.compile( "./vertex-node-ref" );

                        Object rrVParent = reVParent.evaluate( vertex, XPathConstants.NODESET );
                        NodeList rvparentList = (NodeList) rrVParent;

                        List  rvpXPList = new ArrayList();
                        vrtDef.put( "rvpXP", rvpXPList);
                         
                        for( int vpl=0; vpl<rvparentList.getLength(); vpl++ ){  
                            
                            NamedNodeMap vp = rvparentList.item( vpl ).getAttributes();

                            // vertex reference xpath and xpath to node (given reference) 
                            //-----------------------------------------------------------
                        
                            String refpathXP = vp.getNamedItem( "refpath" ).getNodeValue();                        
                            String xpathXP = vp.getNamedItem( "xpath" ).getNodeValue();
                            String refXP = vp.getNamedItem( "ref" ).getNodeValue();

                            Map rvp = new HashMap();
                            rvp.put("refpath", refpathXP);
                            rvp.put("xpath", xpathXP);
                            rvp.put("ref", refXP);
                            
                            rvpXPList.add( rvp );
                            
                            System.out.println( "    v parent ref: " + rvp);
                        }

                        // vertex attributes
                        //------------------
                        
                        XPath xVAtt = xpFac.newXPath();
                        XPathExpression eVAtt = xVAtt.compile( "./attribute" );
                        
                        Object rVAtt = eVAtt.evaluate( vertex, XPathConstants.NODESET );
                        NodeList vAttList = (NodeList) rVAtt;
               	    
                        Map vattMap = new HashMap();
              
                        vrtDef.put( "attribute", vattMap );
                        
                        for( int val=0; val < vAttList.getLength(); val++ ){

                            NamedNodeMap vatt = vAttList.item(val).getAttributes();

                            Map cvatt = new HashMap();
                            String vattName = vatt.getNamedItem( "name" ).getNodeValue();

                            vattMap.put( vattName, cvatt);
                            
                            cvatt.put( "xpath", vatt.getNamedItem( "xpath" ).getNodeValue() );
                            cvatt.put( "xprt", vatt.getNamedItem( "xprt" ).getNodeValue() );
                            cvatt.put( "tgt", vatt.getNamedItem( "tgt" ).getNodeValue() );
                            
                            String vattXRU = "";
                            String vattXRI = "%ID%";
			
                            Node ndXRU = vatt.getNamedItem( "refURL" );
                            
                            if( ndXRU!=null ){
                                vattXRU = ndXRU.getNodeValue();
                                
                                Node ndXRI = vatt.getNamedItem( "refIDS" );
                                if( ndXRI!=null ){
                                    vattXRI = ndXRI.getNodeValue();
                                }
                                
                                if( vattXRU.length()>0 ){
                                    cvatt.put( "refurl", vattXRU );
                                    cvatt.put( "refid", vattXRI );
                                }
                            }                                                      
                        }
                        
                        if( vrt.getNamedItem( "id" ) != null ){
                            String vId = vrt.getNamedItem( "id" ).getNodeValue();
                            
                            vIdList.add( vId );
                            vrtDef.put("vrtID",vId);
                        }
                        
                        if( vrt.getNamedItem( "rid" ) != null ){
                            String vRid = vrt.getNamedItem( "rid" ).getNodeValue();
                            
                            vIdList.add( vRid );
                            vrtDef.put("vrtRID",vRid);
                        }                                                                     
                    }                                        
		}
	    }
            
	} catch(Exception e){
            
	    JOptionPane.showMessageDialog( null, "MiSink Error: Cannot"
                                           + "parse configuration file" );	    
	    e.printStackTrace();
	}
    } 
}
