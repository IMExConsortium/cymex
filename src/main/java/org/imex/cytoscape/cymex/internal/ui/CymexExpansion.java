package org.imex.cytoscape.cymex.internal.ui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JRadioButton;

import javax.swing.ButtonGroup;

import java.awt.*; 
import java.awt.event.*; 

import org.imex.cytoscape.cymex.internal.CymexContext;

public class CymexExpansion extends JPanel
    implements ActionListener{

    CymexContext context;
    
    public CymexExpansion( CymexContext context ){

        this.context = context;
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[]{0.25, 0.0, 1.0, 0.0, 0.0, 0.25, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
	        
        JLabel lblAssembly = new JLabel("Association");
        lblAssembly.setFont( new Font( lblAssembly.getFont().getName(),
                                       Font.BOLD,
                                       lblAssembly.getFont().getSize()));
        
        GridBagConstraints gbc_lblAssembly = new GridBagConstraints();
        gbc_lblAssembly.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssembly.gridx = 1;
        gbc_lblAssembly.gridy = 1;
        add(lblAssembly, gbc_lblAssembly);
                
        JRadioButton rdbtnAssSpoke = new JRadioButton("Spoke");
        rdbtnAssSpoke.setActionCommand("AssSpoke");
        GridBagConstraints gbc_rdbtnAssSpoke = new GridBagConstraints();
        gbc_rdbtnAssSpoke.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnAssSpoke.gridx = 3;
        gbc_rdbtnAssSpoke.gridy = 1;
        add(rdbtnAssSpoke, gbc_rdbtnAssSpoke);
	
        JRadioButton rdbtnAssNone = new JRadioButton("None");
        rdbtnAssNone.setEnabled( false );
        rdbtnAssNone.setActionCommand("AssNone");
        GridBagConstraints gbc_rdbtnAssNone = new GridBagConstraints();
        gbc_rdbtnAssNone.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnAssNone.gridx = 4;
        gbc_rdbtnAssNone.gridy = 1;
        add(rdbtnAssNone, gbc_rdbtnAssNone);
	
        JRadioButton rdbtnAssIgnore = new JRadioButton("Drop");
        rdbtnAssIgnore.setActionCommand("AssDrop");
        GridBagConstraints gbc_rdbtnAssIgnore = new GridBagConstraints();
        gbc_rdbtnAssIgnore.insets = new Insets(0, 0, 5, 0);
        gbc_rdbtnAssIgnore.gridx = 5;
        gbc_rdbtnAssIgnore.gridy = 1;
        add(rdbtnAssIgnore, gbc_rdbtnAssIgnore);
        
        // Assembly Button Group 
        //----------------------
        
        ButtonGroup assGroup = new ButtonGroup();
        assGroup.add( rdbtnAssSpoke );
        assGroup.add( rdbtnAssNone );
        assGroup.add( rdbtnAssIgnore );

        // set default from context
        //-------------------------
        
        String assDef = context.getEdgeExpand("association");
        if( assDef.toLowerCase().equals("spoke") ) rdbtnAssSpoke.setSelected( true );
        if( assDef.toLowerCase().equals("none") ) rdbtnAssNone.setSelected( true );
        if( assDef.toLowerCase().equals("drop") ) rdbtnAssIgnore.setSelected( true );


        
        // Physical Assembly
        //------------------
        
        JLabel lblPhysical = new JLabel("Physical Association");
        lblPhysical.setFont( new Font( lblPhysical.getFont().getName(),
                                       Font.BOLD,
                                       lblPhysical.getFont().getSize()));
                
        GridBagConstraints gbc_lblPhysical = new GridBagConstraints();
        gbc_lblPhysical.insets = new Insets(0, 0, 5, 5);
        gbc_lblPhysical.gridx = 1;
        gbc_lblPhysical.gridy = 2;
        add(lblPhysical, gbc_lblPhysical);
	
        JRadioButton rdbtnPhyMatrix = new JRadioButton("Matrix");
        rdbtnPhyMatrix.setActionCommand("PhyMatrix");
        GridBagConstraints gbc_rdbtnPhyMatrix = new GridBagConstraints();
        gbc_rdbtnPhyMatrix.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnPhyMatrix.gridx = 3;
        gbc_rdbtnPhyMatrix.gridy = 2;
        add(rdbtnPhyMatrix, gbc_rdbtnPhyMatrix);
	
        JRadioButton rdbtnPhyNone = new JRadioButton("None");
        rdbtnPhyNone.setEnabled( false );
        rdbtnPhyNone.setActionCommand("PhyNone");
        GridBagConstraints gbc_rdbtnPhyNone = new GridBagConstraints();
        gbc_rdbtnPhyNone.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnPhyNone.gridx = 4;
        gbc_rdbtnPhyNone.gridy = 2;
        add(rdbtnPhyNone, gbc_rdbtnPhyNone);
	
        JRadioButton rdbtnPhyIgnore = new JRadioButton("Drop");
        rdbtnPhyIgnore.setActionCommand("PhyDrop");
        GridBagConstraints gbc_rdbtnPhyIgnore = new GridBagConstraints();
        gbc_rdbtnPhyIgnore.insets = new Insets(0, 0, 5, 0);
        gbc_rdbtnPhyIgnore.gridx = 5;
        gbc_rdbtnPhyIgnore.gridy = 2;
        add(rdbtnPhyIgnore, gbc_rdbtnPhyIgnore);

        // Physical Button Group 
        //----------------------
        
        ButtonGroup phyGroup = new ButtonGroup();
        phyGroup.add( rdbtnPhyMatrix );
        phyGroup.add( rdbtnPhyNone );
        phyGroup.add( rdbtnPhyIgnore );

        // set default from context
        //-------------------------
        
        String phyDef = context.getEdgeExpand("physical association");
        if( phyDef.toLowerCase().equals("matrix") ) rdbtnPhyMatrix.setSelected( true );
        if( phyDef.toLowerCase().equals("none") ) rdbtnPhyNone.setSelected( true );
        if( phyDef.toLowerCase().equals("drop") ) rdbtnPhyIgnore.setSelected( true );

           
        //Register a listener for the radio buttons
        //-----------------------------------------
        
        rdbtnAssSpoke.addActionListener( this );
        rdbtnAssNone.addActionListener( this );
        rdbtnAssIgnore.addActionListener( this );
        rdbtnPhyMatrix.addActionListener( this );
        rdbtnPhyNone.addActionListener( this );
        rdbtnPhyIgnore.addActionListener( this );
        
    }

    // Listen to the radio buttons
    //----------------------------
    
    public void actionPerformed(ActionEvent e) {
        
        // action here...
        //---------------
        
        if(e.getActionCommand().equals("AssSpoke")){
            System.out.println("Expand Association: Spoke");
            context.setEdgeExpand("association", "spoke");            
        }
	
        if(e.getActionCommand().equals("AssNone")){
            System.out.print("Expand Association: None");
            context.setEdgeExpand("association", "none");
        }

        if(e.getActionCommand().equals("AssDrop")){
            System.out.print("Expand Association: Drop");
            context.setEdgeExpand("association", "drop");
        }
               
        if(e.getActionCommand().equals("PhyMatrix")){
            System.out.println("Expand Physical: Matrix");
            context.setEdgeExpand("physical association", "matrix");
        }
	
        if(e.getActionCommand().equals("PhyNone")){
            System.out.print("Expand Physical: None");
            context.setEdgeExpand("physical association", "none");            
        }

        if(e.getActionCommand().equals("PhyDrop")){            
            System.out.print("Expand Physical: Drop");
            context.setEdgeExpand("physical association", "drop");                        
        }
    }
}
