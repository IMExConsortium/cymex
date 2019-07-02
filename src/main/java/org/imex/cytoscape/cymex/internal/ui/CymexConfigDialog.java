package org.imex.cytoscape.cymex.internal.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.*;

import org.imex.cytoscape.cymex.internal.CymexContext;

public class CymexConfigDialog{
    
    private DCymexConfig aboutFrame;
    private String svr;

    public CymexConfigDialog( CymexContext context ){       	
	aboutFrame = new DCymexConfig( context );
    }
    
    public void setVisible( boolean visible ){
	aboutFrame.setVisible( visible );
    }

    public void setLocationRelativeTo( Component c ){
	aboutFrame.setLocationRelativeTo( c );
    }
        
    private class DCymexConfig extends JDialog {
        
	private CDialogTop dialogTop;
	
	private DCymexConfig( CymexContext context ){
	    
	    dialogTop = new CDialogTop( context );
	    
	    setTitle("Cymex");
	    Container contentPane = getContentPane();
	    contentPane.setLayout(new BorderLayout());
	    contentPane.add(dialogTop, BorderLayout.CENTER);
	    pack();
	    Dimension size= getSize();
	    size.setSize(size.getWidth()+50,size.getHeight());
	    setSize(size);
	    setResizable(false);
	    setModal(true);
	}
    }
    
    private class CDialogTop extends JPanel
        implements ActionListener{

        CymexContext context;
        
	private CLogoPane logoPane;
	private CLabelPane labelPane;

        private JComboBox ignoreBox; 
        
       	private CDialogTop( CymexContext context ){

            this.context = context;
            
	    logoPane = new CLogoPane();
	    labelPane = new CLabelPane( context );

            Box vBox = Box.createVerticalBox();

            Box hBox = Box.createHorizontalBox();
            
            hBox.add( Box.createHorizontalStrut(8) );
            hBox.add( new JLabel("Node Ignore List  ") );

            //String[] ignoreList = { "--- NONE ---", "Ubiquitin" };
            
            ignoreBox = new JComboBox( context.getNodeIdIgnoreKeys() );
            
            ignoreBox.setSelectedIndex( context.getIdIgnoreIndex() );
            
            hBox.add( ignoreBox );
            hBox.add( Box.createHorizontalStrut(8) );

            vBox.add( hBox);
            vBox.add( Box.createVerticalStrut(5) );

            
            CymexExpansion cyExp = new CymexExpansion( context );
            cyExp.setBorder( BorderFactory.createTitledBorder( "Complex Expansion"));            
            vBox.add( cyExp );

            vBox.add( Box.createVerticalStrut(5) );
            
            CymexNodeMerge cyMerge = new CymexNodeMerge( context );
            cyMerge.setBorder(BorderFactory.createTitledBorder( "Isoforms"));                                  
            vBox.add( cyMerge );
            
            vBox.add( Box.createVerticalStrut(5) );
            
            CymexEdgeSelf cySelf = new CymexEdgeSelf( context );
            cySelf.setBorder( BorderFactory.createTitledBorder( "Homo-Oligomers" ));
            vBox.add( cySelf );
            
	    setLayout(new BorderLayout());
	    
	    add( logoPane, BorderLayout.WEST);
	    add( labelPane, BorderLayout.CENTER);
            add( vBox, BorderLayout.SOUTH);

            ignoreBox.addActionListener( this );
                    
	}
        
        // Listen to the combo
        //---------------------
    
        public void actionPerformed(ActionEvent e) {
            System.out.println( e.getActionCommand() );            
            System.out.println( ignoreBox.getSelectedItem() );

            context.setIdIgnoreIndex( ignoreBox.getSelectedIndex() );
            
            System.out.println( context.getActiveNodeIdIgnore() );
        }
    }
    
    private class CLogoPane extends JPanel {
        
	private CLogoBorder logoBorder;
	
	private CLogoPane(){
	    logoBorder = new CLogoBorder();
	    setBorder(new EmptyBorder(7,7,7,7));
	    setLayout(new BorderLayout());
	    add(logoBorder, BorderLayout.WEST);
	}
    }
    
    private class CLogoIcon extends JLabel {
	
	private CLogoIcon() {
	 
            ImageIcon icon = new ImageIcon(getClass().getResource("/CyMEx55.png"));	    
	    setIcon(icon);
	}
    }
    
    private class CLogoBorder extends JPanel {

	private CLogoIcon logoIcon;
	private CLogoBorder(){
	    logoIcon = new CLogoIcon();
            
            //setBorder(new LineBorder(Color.GRAY));
	    //setLayout(new BorderLayout());
	    add(logoIcon, BorderLayout.CENTER);
	}
    }
    
    private class CRow0 extends JPanel {
		
	private JLabel appName =new JLabel();
	private JLabel verLabel=new JLabel();

	private CRow0( CymexContext context ){
	    
	    appName.setText( "Cymex App" );
	    appName.setHorizontalAlignment(SwingConstants.LEFT);
	    verLabel.setText("Ver "+ context.getVersion() );
	    verLabel.setHorizontalAlignment(SwingConstants.RIGHT);
	    
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	    add( appName );
	    add( Box.createHorizontalGlue() ); 
	    add( verLabel );
	}
    }

    private class CRow1 extends JPanel {
	
	private JLabel depthLabel =new JLabel();
	private CRadioButtons rbs = null;
	
	private CRow1( CymexContext context ){
	    
	    rbs = new CRadioButtons(context);
	    depthLabel.setText("Interactive Edge Import Depth: ");
	    depthLabel.setHorizontalAlignment(SwingConstants.CENTER);	    
    
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	    add(depthLabel);
	    add(Box.createHorizontalGlue()); 
	    add(rbs);
	}
    }

    private class CRadioButtons extends JPanel
	implements ActionListener {
	
	CymexContext context = null;

	public CRadioButtons( CymexContext context ){
	    super( new BorderLayout() );
	    
	    this.context = context;

    
	    //Create the radio buttons
	    //------------------------

	    JRadioButton oneButton = new JRadioButton("1");
	    if(context.getEdgeDepth().equals("1")){
		oneButton.setSelected(true);
	    }
	    oneButton.setActionCommand("1");	    
	    
	    JRadioButton halfButton = new JRadioButton("1.5");
	    if(context.getEdgeDepth().equals("1.5")){
		halfButton.setSelected( true );
            }
	    halfButton.setActionCommand( "1.5" );
	    

	    //Group the radio buttons
	    //-----------------------

	    ButtonGroup group = new ButtonGroup();
	    group.add(oneButton);
	    group.add(halfButton);
	    
	    //Register a listener for the radio buttons
	    //-----------------------------------------

	    oneButton.addActionListener(this);
	    halfButton.addActionListener(this);

	    //Put the radio buttons in a row in a panel
	    //-----------------------------------------
	    JPanel radioPanel = new JPanel(new GridLayout(1, 0));
	    radioPanel.add(oneButton);
	    radioPanel.add(halfButton);

	    add(radioPanel, BorderLayout.LINE_START);
	    setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	}

	// Listen to the radio buttons
	//----------------------------

	public void actionPerformed(ActionEvent e) {
	    
	    // action here...
	    //---------------

	    if(e.getActionCommand().equals("1")){
		context.setEdgeDepth("1");
	    }
	    
	    if(e.getActionCommand().equals("1.5")){
		context.setEdgeDepth("1.5");
	    }
	}
    }



    private class CRowServer extends JPanel {
               
	private CRowServer( CymexContext context ){
	    
	    ServerCheckbox sc = new ServerCheckbox( context );
            
            JLabel serverLabel =new JLabel();
            serverLabel.setText("LocalServer Enabled ");
	    serverLabel.setHorizontalAlignment(SwingConstants.CENTER);	    
    
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	    add(serverLabel);
	    add(Box.createHorizontalGlue()); 
	    add(sc);
	}
    }


    private class ServerCheckbox extends JCheckBox implements ActionListener{
        
        CymexContext context;
       
        private ServerCheckbox( CymexContext context ){
            this.context = context;
            setSelected( context.isServerOn() );
            this.addActionListener( this );
        } 
        
        public void actionPerformed( ActionEvent e ){
            context.setServer( ! context.isServerOn());
        }
        
    }
    
    private class CLabelRows extends JPanel {
	
	private CLabelRows( CymexContext context ){

	    CRow0 row0 = new CRow0( context );
	    CRow1 row1 = new CRow1( context );
	    CRowServer row2 = new CRowServer( context );
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    
	    add(row0);
	    //add(Box.createVerticalGlue()); 
	    add(row2);
	    add(row1);
        }
    }
 
    private class CLabelPane extends JPanel {
	
	private CLabelRows labelRows;
	
	private CLabelPane( CymexContext context ){
	    labelRows = new CLabelRows( context );
	    setBorder(new EmptyBorder(7,7,7,7));
	    setLayout(new BorderLayout());
	    add( labelRows, BorderLayout.CENTER );
	}
    }

}
