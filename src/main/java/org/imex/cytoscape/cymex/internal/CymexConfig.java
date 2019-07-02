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

public class CymexConfig{

    private static final String CONFIG = "cymex.xml";
    private static final String PORTFN = "cymex.port";

    private static final File configDir
        = new File( System.getProperty( "user.home" ),
                    CyProperty.DEFAULT_PROPS_CONFIG_DIR);
    
    boolean debug = false;
    int     port = 30403;

    String version = "";
    String edgedepth = "1";

    String nodeMerge ="none";
    
    List<Map> nodeIgnore = new ArrayList<Map>();
    List<Map> nodeIdIgnore = new ArrayList<Map>();

    String edgeSelf ="all";
    Map<String,String> edgeExpand = new HashMap<String,String>();

    /*
    Map getMaps(){
	return filters;
    }

    Map getMap( String fnm ){
	return (Map) filters.get( fnm );
    }
    */
    // constructor

    CymexConfig( String fname ){
	this.read( fname );
    }
    
    public boolean isDebugOn(){
	return debug;
    }
    
    public void setDebug( boolean debug ){
        this.debug = debug;
    }
    
    public int getPort(){
	return port;
    }
    
    public String getVersion(){
	return version;
    }

    
    public String getEdgeDepth(){
	return edgedepth;
    }

    public void setEdgeDepth( String depth ){
	edgedepth = depth;
    }
        
    public String getEdgeSelf(){
	return edgeSelf;
    }
    
    public void setEdgeSelf( String stat ){
	edgeSelf = stat;
    }

    public String getNodeMerge(){
	return nodeMerge;
    }
    
    public void setNodeMerge( String merge ){
	nodeMerge = merge;
    }


    //--------------------------------------------------------------------------
    // skip nodes by id
    //------------------
    
    public final List<String> getNodeIdIgnore( String key ){

        if( nodeIdIgnore == null ){
            nodeIdIgnore = new ArrayList<Map>();
        }       
        
        for( Iterator<Map> i = nodeIdIgnore.iterator(); i.hasNext(); ){
            Map ii = i.next();
            if( ii.get("key").equals( key ) ){
                return (List<String>) ii.get("list");
            }
        }
        
        return new ArrayList<String>();
    }
    
    public final List<String> getNodeIdIgnore( int index ){

        if( nodeIdIgnore == null ){
            nodeIdIgnore = new ArrayList<Map>();
        }       
    
        if(  nodeIdIgnore.size() > index ){
            return (List<String>) nodeIdIgnore.get( index ).get("list");
        }
        return new ArrayList<String>();
    }
    
    public final void addNodeIdIgnore( String key, List<String> list ){
        
        if( nodeIdIgnore == null ){
            nodeIdIgnore = new ArrayList<Map>();
        }
        
        Map newIdIgnore = new HashMap();
        newIdIgnore.put( "key", key );
        newIdIgnore.put( "list", list );
        
        nodeIdIgnore.add( newIdIgnore );
        System.out.println( "idIgnore: " + newIdIgnore );
    }
    
    public final String[] getNodeIdIgnoreKeys(){ 
        
        if( nodeIdIgnore  == null ){
            nodeIdIgnore =  new ArrayList<Map>();
        }       
        
        if( nodeIdIgnore.size() > 0 ){
            String[] res = new String[ nodeIdIgnore.size() ];        
            
            int ind = 0;
            for( Iterator<Map> i = nodeIdIgnore.iterator(); i.hasNext(); ){
                Map ii = i.next();
                String ckey = (String) ii.get("key");
                res[ind++]=ckey;
            }
            return res;
        }        
        return null;
    }

    //--------------------------------------------------------------------------
    // skip nodes by feature match
    //----------------------------

    public final void addNodeIgnore( Map flt, String name, 
                                     String xpath, List<String> list ){
        
        if( flt.get( "node-ignore" ) == null ){
            flt.put( "node-ignore",  new ArrayList<Map>() );
        }       
        
        Map item = new HashMap();
        item.put( "name", name );
        item.put( "xpath", xpath );
        item.put( "val", list );
        
        ((List<Map>) flt.get( "node-ignore" )).add( item );
        System.out.println("ignore: " + item );
    }

    public final Map getNodeIgnore( Map flt, String name ){

        if( flt.get( "node-ignore" ) == null ){
            flt.put( "node-ignore",  new ArrayList<Map>() );
            return new HashMap();
        }       
        
        ArrayList<Map> nodeIgnore = (ArrayList<Map>) flt.get( "node-ignore" );

        for( Iterator<Map> i = nodeIgnore.iterator(); i.hasNext(); ){
            Map ii = i.next();
            if( ii.get("name").equals( name ) ){
                return (Map) ii;
            }
        }
        
        return new HashMap();
    }

    public final List<Map> getNodeIgnore( Map flt ){

        if( flt.get( "node-ignore" ) == null ){
            flt.put( "node-ignore",  new ArrayList<Map>() );           
        }       
        
        return (ArrayList<Map>) flt.get( "node-ignore" );
    }
    
    //--------------------------------------------------------------------------

    public final String getEdgeExpand( String etype ){
        if( edgeExpand != null ){
            return edgeExpand.get( etype );
        }
        return "none";
    }
    
    public final void setEdgeExpand( String etype, String op ){
        
        if( edgeExpand == null ){
            edgeExpand = new HashMap<String,String>();
        }
        edgeExpand.put( etype, op );
    }
   
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
            fileName = CymexConfig.CONFIG;
        }
        
        InputStream is = openLocalFile( fileName );
        if( is == null ){
            is = openDefaultJar( fileName );
        }       
        if( is == null ) return;

        System.out.println("Cymex Config file: " + fileName );
        
  	try{
            
	    DocumentBuilderFactory docFac = DocumentBuilderFactory
                .newInstance();

            docFac.setNamespaceAware( true ); 
            
	    DocumentBuilder builder = docFac.newDocumentBuilder();
	    Document doc = builder.parse( is );
            
	    XPathFactory xpFac = XPathFactory.newInstance();

	    // version
	    //--------
	    
	    XPath xVer = xpFac.newXPath();
            XPathExpression eVer = xVer.compile( "//cymex/@version" );
            
            Object rVer = eVer.evaluate( doc, XPathConstants.NODESET );
            NodeList verList = (NodeList) rVer;
	    
	    if( verList.getLength()>0 ){
		String ver = verList.item(0).getNodeValue();
		if( ver!=null ){
                    version = ver;
                }
                System.out.println( "Cymex Version:" + ver );
	    }            

            // port setting
	    //-------------
	    
	    XPath xPrt = xpFac.newXPath();
            XPathExpression ePrt = xPrt.compile( "//cymex/@port" );
            
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

	    // port announce
	    //--------------

	    try{
                final File localPortFile = new File( configDir, 
                                                     CymexConfig.PORTFN );

		FileWriter pfw = new FileWriter( localPortFile );
		pfw.write( Integer.toString( port ) );
		pfw.flush();
		pfw.close();
	    } catch( IOException ioe ){
		ioe.printStackTrace();
	    }

            // configuration
            //--------------
            
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
            
	} catch(Exception e){
            
	    JOptionPane.showMessageDialog( null, "Cymex Error: Cannot"
                                           + "parse configuration file" );	    
	    e.printStackTrace();
	}
    } 
}
