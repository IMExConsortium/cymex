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



class CymexServerThread extends Thread{
    
    CymexContext context = null;
    CymexServer mss = null;
    boolean terminate = false;
    
    CymexServerThread( CymexContext context ){
        this.context = context;
        context.setServerThread( this );
    }
    
    public void run(){
        context.setServerThread( this );
        if( context.isDebugOn() ){
            System.out.println( "Cymex: server started" );
        }
	try{
            ServerSocket ss
                = new ServerSocket( context.getPort() );
            
            while( !terminate ){
                mss = new CymexServer( ss.accept(), context );                
                mss.start();
                if( context.isDebugOn() ){
                    System.out.println( "Cymex: connection ended" );                                
                }
            }
            ss.close();
            mss = null;
            if( context.isDebugOn() ){
                System.out.println( "Cymex: server stopped" );
            }
	}catch( Exception e ){
	    e.printStackTrace();
	}        
        context.setServerThread( null );        
    }

    public void terminate(){
        terminate = true;
        
        try{
            Socket ts = new Socket( InetAddress.getByName("127.0.0.1"),
                                    context.getPort());
            
            PrintWriter out = new PrintWriter( ts.getOutputStream() );
            out.println( "TERMINATE" );
            ts.close();
            ts = null;
        }catch( Exception e ){
            e.printStackTrace();
        }
        context.setServerThread( null );
    }
}


class CymexServer extends Thread{
    
    private Socket client;
    
    private Map<Long,Map<String,Long>> misNode = null;
    private Map<Long,Map<String,Long>> misEdge = null;
    
    CymexContext context; 
    boolean terminate = false; 
    
    CymexServer( Socket client, CymexContext context ) 
        throws SocketException{

	this.client = client;
        this.context = context;
        
        setPriority(NORM_PRIORITY-1);        
    }


    public void terminate(){
        terminate = true;
    }

    public void run(){
        
        if( terminate ) return;

        if( context.isDebugOn() ){
            System.out.println( "Cymex Server: client=" 
                                + client.getInetAddress().getHostAddress() );
        }
        
        if( client.getInetAddress().getHostAddress() != null &&
            !client.getInetAddress().getHostAddress().equals( "127.0.0.1" ) ){
            try{
                OutputStream sos= client.getOutputStream();
                PrintStream pos = new PrintStream( sos, true );
                pos.println( "Remote connections not allowed");
                
                client.close();
            } catch( Exception e ){
                e.printStackTrace();
            }
            return;
        }
        
	try{    
	    InputStreamReader ir 
		= new InputStreamReader( client.getInputStream(), "8859_1" );
	    BufferedReader in = new BufferedReader( ir );
            
            if( terminate ) return; 

	    String request = in.readLine();
            if( context.isDebugOn() ){
                System.out.println( "Request:" + request  + ":" );
            }
	    StringTokenizer st = null;

            try{
                st = new StringTokenizer( request );
            }catch( NullPointerException npe ){
                return;
            }
            
	    if( st.countTokens()==3 ){
		
		String get= st.nextToken();
                String req= st.nextToken();
                String prt= st.nextToken();
		
		OutputStream sos= client.getOutputStream(); 
                PrintStream pos = new PrintStream( sos, true );
                
		Thread.sleep( 100 );
                
                SimpleDateFormat sdf= new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                sdf.setTimeZone( TimeZone.getTimeZone("GMT") );
                Date date=new Date();
                String sDate=sdf.format( date );
                
                String response = "<html><head><title>Cymex Response</title>"
                    + "</head><body>Cymex OK</body></html>\n";

		pos.println( "HTTP/1.1 200 OK");
		pos.println( "Date: " + sDate);
		pos.println( "Content-Type: text/html;");
		pos.println( "Accept-Ranges: bytes");
		pos.println( "Content-Length: "+ response.length());
		pos.println( ""); 
		pos.println( response ); 
                
		client.close();
                client = null;
                
                String ed = context.getEdgeDepth();
                
		if( get.equals("GET") && prt.startsWith("HTTP") ){
                    
		    String dus = "http:/" + req;
                    
                    if( context.isDebugOn() ){
                        System.out.println( dus );
                    }
                    
		    if( ed != null && ed.equals("1.5") ){
                        dus = dus.replaceAll( "%25ED%25", "&EDP=1.5" );
		    } else {
			dus = dus.replaceAll( "%25ED%25", "" );
		    }

		    URL dtaUrl  = new URL(dus);
		    
		    HttpURLConnection huc = 
                        (HttpURLConnection) dtaUrl.openConnection();
		    huc.setRequestMethod("GET");
		    huc.connect();
		    InputStream is = huc.getInputStream();

		    // replace original input stream
		    //------------------------------
                    
		    ir = new InputStreamReader(is,"8859_1");
		    in = new BufferedReader(ir);

		    request = in.readLine();
		    st = new StringTokenizer( request );
                }
                
                if( get.equals("CREATE") && req.equals("NETWORK") ){
                    
                    if( prt == null ) prt = "Cymex";
                    String name = prt;
                    
                    

                    //****
                    //MiNetworkCreate cmn = new MiNetworkCreate( context, name );
                    //cmn.run();
                    

                    //cmn = null;
                    client.close();
                    client = null;
                    return;
                }
            }
            
            if( st.countTokens()==2 ){
                String op = st.nextToken();
		String arg = st.nextToken();    
		/*
                CymexFilterSet mfs = context.getFilterSet();

                if( context.isDebugOn() ){
                    System.out.println( "op:" + op + " arg:" + arg  );                
                    System.out.println( "MiFilterSet:" + mfs );
                }
		
                if( op.equals("ADD") ){
                    
                    String filterName = arg;
                    
                    CyNetwork network
                        = context.getApplicationManager().getCurrentNetwork();
                    CyNetworkView view
                        = context.getApplicationManager().getCurrentNetworkView();

                    if( network == null ){
                        MiNetworkCreate mnc = new MiNetworkCreate( context, "Cymex" );
                        mnc.run();
                    }
                    
		    if( filterName != null && mfs.getMap( filterName ) != null ){
                        

                        TaskFactory naTF 
                            = new MiNetworkAddTaskFactory( context,  filterName, in );
                        
                        context.getTaskManager().execute( naTF.createTaskIterator() ); 
			
                        MiNetworkAdd amn = new MiNetworkAdd( context, filterName, in );
                        amn.run();
                        
		    } else {
                        if( context.isDebugOn() ){
                            String message = "Cymex App: unknown filter: ";
                            JOptionPane
                                .showMessageDialog( context.getSwingApplication()
                                                    .getJFrame(),
                                                    message + filterName );
                        }
                    }
                    
		} else if( op.equals("DEBUG") ){
                    
                    if( arg != null && arg.equalsIgnoreCase( "ON" ) ){                       
                        context.setDebug( true );
                    } else {
                        context.setDebug( false );
                    }

                } else {

                    if( context.isDebugOn() ){
                        String message = "Cymex App: unknown command: ";
                        JOptionPane
                            .showMessageDialog( context.getSwingApplication()
                                                .getJFrame(), 
                                                message + op ); 
                    }
		}
                */
	    } else {
                if( context.isDebugOn() ){
                    String message = "Cymex Plugin: unrecognized input: ";
                    JOptionPane.showMessageDialog( context.getSwingApplication()
                                                   .getJFrame(), 
                                                   message + request );
                }
            }
                
	    client.close();
            client = null;

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
