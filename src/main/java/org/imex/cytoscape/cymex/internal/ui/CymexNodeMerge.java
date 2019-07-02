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

public class CymexNodeMerge extends JPanel
    implements ActionListener{

    CymexContext context ;
    
    public CymexNodeMerge( CymexContext context ){

        this.context = context;
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.25, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
	
        JRadioButton rdbtnDistinct = new JRadioButton("Distinct");
        GridBagConstraints gbc_rdbtnDistinct = new GridBagConstraints();
        gbc_rdbtnDistinct.gridwidth = 1;
        gbc_rdbtnDistinct.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnDistinct.gridx = 1;
        gbc_rdbtnDistinct.gridy = 1;
        add(rdbtnDistinct, gbc_rdbtnDistinct);
        
        JRadioButton rdbtnMerge = new JRadioButton("Merge");
        GridBagConstraints gbc_rdbtnMerge = new GridBagConstraints();
        gbc_rdbtnMerge.gridwidth = 1;
        gbc_rdbtnMerge.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnMerge.gridx = 3;
        gbc_rdbtnMerge.gridy = 1;
        add(rdbtnMerge, gbc_rdbtnMerge);

        //Group the radio buttons
        //-----------------------
        
        ButtonGroup group = new ButtonGroup();
        group.add( rdbtnDistinct );
        group.add( rdbtnMerge );

        String mergeDef = context.getNodeMerge();
        if( mergeDef.toLowerCase().equals("isoform") ) rdbtnMerge.setSelected( true );
        if( mergeDef.toLowerCase().equals("none") ) rdbtnDistinct.setSelected( true );
        

        
        //Register a listener for the radio buttons
        //-----------------------------------------
        
        rdbtnMerge.addActionListener( this );
        rdbtnDistinct.addActionListener( this );   
               
    }

    // Listen to the radio buttons
    //----------------------------
    
    public void actionPerformed(ActionEvent e) {
        
        // action here...
        //---------------
        
        if(e.getActionCommand().equals("Distinct")){
            context.setNodeMerge("none");            
            System.out.println("NodeMerge: Distinct");
        }
	
        if(e.getActionCommand().equals("Merge")){
            context.setNodeMerge("isoform");
            System.out.print("NodeMerge: isoform");
        }
    }

    
}
