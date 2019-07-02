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

public class CymexEdgeSelf extends JPanel
    implements ActionListener{

    CymexContext context ;
    
    public CymexEdgeSelf( CymexContext context ){

        this.context = context;
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.25, 0.0,0.25, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
	
        JRadioButton rdbtnInclude = new JRadioButton("Include");
        GridBagConstraints gbc_rdbtnInclude = new GridBagConstraints();
        gbc_rdbtnInclude.gridwidth = 1;
        gbc_rdbtnInclude.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnInclude.gridx = 1;
        gbc_rdbtnInclude.gridy = 1;
        add(rdbtnInclude, gbc_rdbtnInclude);
        
        JRadioButton rdbtnIgnore = new JRadioButton("Ignore");
        rdbtnIgnore.setEnabled( false );
        GridBagConstraints gbc_rdbtnIgnore = new GridBagConstraints();
        gbc_rdbtnIgnore.gridwidth = 1;
        gbc_rdbtnIgnore.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnIgnore.gridx = 3;
        gbc_rdbtnIgnore.gridy = 1;
        add(rdbtnIgnore, gbc_rdbtnIgnore);

        JRadioButton rdbtnDrop = new JRadioButton("Drop");
        GridBagConstraints gbc_rdbtnDrop = new GridBagConstraints();
        gbc_rdbtnDrop.gridwidth = 1;
        gbc_rdbtnDrop.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnDrop.gridx = 5;
        gbc_rdbtnDrop.gridy = 1;
        add(rdbtnDrop, gbc_rdbtnDrop);


        //Group the radio buttons
        //-----------------------
        
        ButtonGroup group = new ButtonGroup();
        group.add( rdbtnInclude );
        group.add( rdbtnIgnore );
        group.add( rdbtnDrop );

        String selfDef = context.getEdgeSelf();
        if( selfDef.toLowerCase().equals("all") ) rdbtnInclude.setSelected( true );
        if( selfDef.toLowerCase().equals("none") ) rdbtnDrop.setSelected( true );
        if( selfDef.toLowerCase().equals("ignode") ) rdbtnIgnore.setSelected( true );
        
        
        //Register a listener for the radio buttons
        //-----------------------------------------
        
        rdbtnInclude.addActionListener( this );
        rdbtnIgnore.addActionListener( this );   
        rdbtnDrop.addActionListener( this );   
        
    }
    
    // Listen to the radio buttons
    //----------------------------
    
    public void actionPerformed(ActionEvent e) {
        
        // action here...
        //---------------
        
        if(e.getActionCommand().equals("Include")){
            context.setEdgeSelf("all");            
            System.out.println("SelfEdge: Include");
        }
	
        if(e.getActionCommand().equals("Ignore")){
            context.setEdgeSelf("ignore");
            System.out.print("SelfEdge: ignore");
        }

        if(e.getActionCommand().equals("Drop")){
            context.setEdgeSelf("none");
            System.out.print("SelfEdge: none");
        }
    }

    
}
