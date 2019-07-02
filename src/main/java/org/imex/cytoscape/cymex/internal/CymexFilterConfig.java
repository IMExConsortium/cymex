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

public class CymexFilterConfig{

    private static final String CONFIG = "cymex.xml";
    
    private static final File configDir
        = new File( System.getProperty( "user.home" ),
                    CyProperty.DEFAULT_PROPS_CONFIG_DIR);
    
    Map filters = new HashMap();
    
    boolean debug = false;
    
    Map getMaps(){
	return filters;
    }

    //Map getMap( String fnm ){
    //	return (Map) filters.get( fnm );
    //}

    public CymexFilter getFilter( String name ){

        Map filter = (Map) filters.get( name );

        Map<String,CymexNodeFilter> cnifMap
            = (Map<String,CymexNodeFilter>) filter.get("nodeIgnoreMap");
        
        return new CymexFilter( (List) filter.get("ndeList"),
                                cnifMap.values(),
                                (List) filter.get("edgList"),
                                (List) filter.get("dtaList"),
                                (List) filter.get("evdList"),
                                (String) filter.get("nsPrefix"),
                                (String) filter.get("nsURI") );        
    }
    
    
    // constructor
    //------------
    
    CymexFilterConfig( String fname ){
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
            fileName = CymexFilterConfig.CONFIG;
        }

        InputStream is = openLocalFile( fileName );

        if( is == null ){
            is = openDefaultJar( fileName );
        }
        if( is == null ) return;
        
        System.out.println("Cymex Filter Config file: " + fileName );
        
  	try{
            
	    DocumentBuilderFactory docFac = DocumentBuilderFactory
                .newInstance();
            
            docFac.setNamespaceAware( true ); 
            
	    DocumentBuilder builder = docFac.newDocumentBuilder();
	    Document doc = builder.parse( is );
            
	    XPathFactory xpFac = XPathFactory.newInstance();
            
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

                //-------------------------------------------------------------
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
                    nodeMap.put( "id", ndeId );
                    
                    
                    String ndeXP = ndeAtt.getNamedItem("xpath").getNodeValue();
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
		    Map attDV = new HashMap();

		    nodeMap.put( "at", attMap );
		    nodeMap.put( "atRU", attRU );
		    nodeMap.put( "atRI", attRI);
		    nodeMap.put( "atDV", attDV);  // default value
		    
		    for( int al=0; al<attList.getLength(); al++){
			NamedNodeMap att = attList.item(al).getAttributes();
			
			String attName = att.getNamedItem("name")
                            .getNodeValue();

                        if( att.getNamedItem("xpath") != null ){
                            String attXP = att.getNamedItem("xpath").getNodeValue();
                            attMap.put( attName, attXP );
                        } else {
                            attMap.put( attName, "" );
                        }

                        String attDef = "";
                        
                        if( att.getNamedItem("value") != null ){
                            attDef = att.getNamedItem("value").getNodeValue();                            
                        }

                        attDV.put( attName, attDef );
                        
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

                //--------------------------------------------------------------
                // find node ignores
                //------------------
                
		XPath xIgn = xpFac.newXPath();
		XPathExpression eIgn = xIgn.compile( "./node-ignore" );
                Object rIgn = eIgn.evaluate( flt, XPathConstants.NODESET );
                
                NodeList ignList = (NodeList) rIgn;
                
                Map ignoreNodeMap = new HashMap();
                fltMap.put( "nodeIgnoreMap", ignoreNodeMap );
                
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

                        System.out.println("  cvalue:  " + ivalue);
                        
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
                                // ************
                                //addNodeIgnore( fltMap, name, xpath, vlist );

                                CymexNodeFilter cnif
                                    = new CymexNodeFilter( xpath, vlist); 
                                ignoreNodeMap.put( name, cnif );
                            }
                        } catch( JSONException jex ){
                            System.out.println(" JSON format error: " + ivalue );
                            jex.printStackTrace();
                        }
                    }                    
                }
                
                
                //-------------------------------------------------------------
                // find data defs
		//---------------
		
		List dtaList = new ArrayList();
		fltMap.put( "dtaList", dtaList );	
	 
		XPath xDta = xpFac.newXPath();
		XPathExpression eDta = xDta.compile( "./data" );
                Object rDta = eDta.evaluate( flt, XPathConstants.NODESET );
                
		NodeList rDtaList = (NodeList) rDta;
		
		for( int iev=0; iev<rDtaList.getLength(); iev++ ){
		    Node nde = rDtaList.item( iev );
		    
		    Map dtaMap = new HashMap();
                    dtaList.add( dtaMap );
                    
                    NamedNodeMap dtaAtt = nde.getAttributes();

		    // data ID & xpath
		    //-----------------

		    String dtaId = dtaAtt.getNamedItem("id").getNodeValue();
                    String dtaXP = dtaAtt.getNamedItem("xpath").getNodeValue();
                    
                    dtaMap.put( "id", dtaId );
		    dtaMap.put( "xp", dtaXP );
                    
                    if( dtaAtt.getNamedItem("rid") != null ){
                        String dtaRID = dtaAtt.getNamedItem("rid").getNodeValue();
                        dtaMap.put( "rid", dtaRID );
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
		    Map attDV  = new HashMap();  // default value

		    dtaMap.put( "at", attMap );
		    dtaMap.put( "atRU", attRU );
		    dtaMap.put( "atRI", attRI);
		    dtaMap.put( "atDV", attDV);
		    
		    for( int al=0; al<attList.getLength(); al++){
			NamedNodeMap att = attList.item(al).getAttributes();
			
			String attName = att.getNamedItem("name")
                            .getNodeValue();

                        if(att.getNamedItem("xpath") != null){
                            String attXP = att.getNamedItem("xpath").getNodeValue();
                            attMap.put( attName, attXP );
                        } else {
                            attMap.put( attName, "" );
                        }
                        
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

                        Node ndeDef = att.getNamedItem( "value" );
                        String attDef = "";
                        
                        if( ndeDef != null ){
                            attDef = ndeDef.getNodeValue();
                        }
                        attDV.put( attName, attDef );                        
                        
		    }
                }
                
                //-------------------------------------------------------------
                // find evid defs
		//---------------
		
		List evdList = new ArrayList();
		fltMap.put( "evdList", evdList );	
	 
		XPath xEvd = xpFac.newXPath();
		XPathExpression eEvd = xNde.compile( "./evid" );
                Object rEvd = eEvd.evaluate( flt, XPathConstants.NODESET );
                
		NodeList rEvdList = (NodeList) rEvd;
		
		for( int iev=0; iev<rEvdList.getLength(); iev++ ){
		    Node nde = rEvdList.item( iev );
		    
		    Map evdMap = new HashMap();
                    evdList.add( evdMap );
                    
                    NamedNodeMap evdAtt = nde.getAttributes();

		    // evidence ID & xpath
		    //---------------------

		    String evdId = evdAtt.getNamedItem("id").getNodeValue();
                    String evdXP = evdAtt.getNamedItem("xpath").getNodeValue();
                    String evdCD = evdAtt.getNamedItem("evcode").getNodeValue();

                    evdMap.put( "id", evdId );
		    evdMap.put( "xp", evdXP );
		    evdMap.put( "evcode", evdCD );

                    if( evdAtt.getNamedItem("rid") != null ){
                        String evdRID = evdAtt.getNamedItem("rid").getNodeValue();
                        evdMap.put( "rid", evdRID );
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
		    Map attDV  = new HashMap();  // default value

		    evdMap.put( "at", attMap );
		    evdMap.put( "atRU", attRU );
		    evdMap.put( "atRI", attRI);
		    evdMap.put( "atDV", attDV);
		    
		    for( int al=0; al<attList.getLength(); al++){
			NamedNodeMap att = attList.item(al).getAttributes();
			
			String attName = att.getNamedItem("name")
                            .getNodeValue();

                        if(att.getNamedItem("xpath") != null){
                            String attXP = att.getNamedItem("xpath").getNodeValue();
                            attMap.put( attName, attXP );
                        } else {
                            attMap.put( attName, "" );
                        }
                        
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

                        Node ndeDef = att.getNamedItem( "value" );
                        String attDef = "";
                        
                        if( ndeDef != null ){
                            attDef = ndeDef.getNodeValue();
                        }
                        attDV.put( attName, attDef );                        
                        
		    }
                }

                //--------------------------------------------------------------                
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

                        Node eNode = evidList.item(evl);
                                                    
                        // <evid> attributes
                            
                        NamedNodeMap evid = eNode.getAttributes();
                        
                        String idXP =
                            evid.getNamedItem( "idXP" ).getNodeValue();
                        
                        String code =
                            evid.getNamedItem( "code" ).getNodeValue();

                        //String codeXP =
                        //    evid.getNamedItem( "codeXP" ).getNodeValue();

                        Map evdMap = new HashMap();
                        
                        evdMap.put( "idXP", idXP);
                        evdMap.put( "code", code);                    
                        //evdMap.put( "code", codeXP);                    
                        
                        // <data-ref> and <data> identifiers

                        List  dList = new ArrayList();
                        evdMap.put( "data", dList );

                        List  dRefList = new ArrayList();
                        evdMap.put( "dataRef", dRefList );

                        XPath rexDtaRef = xpFac.newXPath();
                        XPathExpression reeDtaRef = rexDtaRef.compile( "./data-ref" );
                        
                        Object reerDtaRef = reeDtaRef.evaluate(eNode, XPathConstants.NODESET);
                        NodeList reeDtaRefList = (NodeList) reerDtaRef;   
                    
                        for( int drl=0; drl<reeDtaRefList.getLength(); drl++ ){ 
                            
                            NamedNodeMap dref =
                            reeDtaRefList.item(drl).getAttributes();

                            String refpathXP =
                                dref.getNamedItem( "refpath" ).getNodeValue();
                            
                            String xpathXP =
                                dref.getNamedItem( "xpath" ).getNodeValue();

                            String refXP =
                                dref.getNamedItem( "ref" ).getNodeValue();

                            String idpathXP =
                                dref.getNamedItem( "idpath" ).getNodeValue();
                        
                            Map rdta = new HashMap();
                            rdta.put("refpath",refpathXP);
                            rdta.put("xpath",xpathXP);
                            rdta.put("ref",refXP);
                            rdta.put("idpath", idpathXP);                    
                            dRefList.add(rdta);
                            System.out.println("   revid: " + rdta);                            
                        }

                        XPath rexDta = xpFac.newXPath();
                        XPathExpression reeDta = rexDta.compile( "./data" );
                        
                        Object reerDta = reeDta.evaluate(eNode, XPathConstants.NODESET);
                        NodeList reeDtaList = (NodeList) reerDta;                                           
                        
                        for( int dl=0; dl<reeDtaList.getLength(); dl++ ){ 

                            NamedNodeMap cdta = reeDtaList.item(dl).getAttributes();
                            
                            String dtaXP =
                                cdta.getNamedItem( "xpath" ).getNodeValue();
                            
                            String dtaID =
                                cdta.getNamedItem( "idpath" ).getNodeValue();
                            
                            Map<String,String> dtaMap = new HashMap();
                            
                            dtaMap.put( "xpath", dtaXP);
                            dtaMap.put( "idpath", dtaID);                    
                        
                            dList.add(dtaMap);
                            System.out.println("   dtaXP: " + dtaXP);
                        }
                        evList.add(evdMap);
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
                        
                        if(att.getNamedItem( "xpath" ) != null){
                            cattMap.put( "xpath", att.getNamedItem( "xpath" ).getNodeValue() );
                        } else{
                            cattMap.put( "xpath", "");
                        }
                        
                        if( att.getNamedItem( "xprt" ) != null ){
                            cattMap.put( "xprt", att.getNamedItem( "xprt" ).getNodeValue() );
                        }
                        
                        if(att.getNamedItem( "tgt" ) != null){
                            cattMap.put( "tgt", att.getNamedItem( "tgt" ).getNodeValue() );
                        }
                        
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
                    
                    XPath xxDta = xpFac.newXPath();
                    XPathExpression eeDta = xxDta.compile( "./edge-data" );
                    
                    Object rrDta = eeDta.evaluate( edg, XPathConstants.NODESET);
                    NodeList ddtaList = (NodeList) rrDta;   
                
                    Map dtaRU  = new HashMap();
                    Map dtaRI  = new HashMap();
                    
		    edgeMap.put( "dtaRU", dtaRU );
		    edgeMap.put( "dtaRI", dtaRI );

                    for( int al=0; al<ddtaList.getLength(); al++){
			NamedNodeMap dta = ddtaList.item(al).getAttributes();
			
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
                            String idpathXP = vp.getNamedItem( "idpath" ).getNodeValue();
                            String refXP = vp.getNamedItem( "ref" ).getNodeValue();

                            Map rvp = new HashMap();
                            rvp.put("refpath", refpathXP);
                            rvp.put("xpath", xpathXP);
                            rvp.put("idpath", idpathXP);
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
            
	    JOptionPane.showMessageDialog( null, "Cymex Error: Cannot"
                                           + "parse filter configuration file" );	    
	    e.printStackTrace();
	}

        System.out.println("filters:\n"+filters);
    } 
}
