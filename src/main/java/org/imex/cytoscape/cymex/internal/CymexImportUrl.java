package org.imex.cytoscape.cymex.internal;

import java.awt.event.ActionEvent;
import java.util.List;

//import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.*;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;


public class CymexImportUrl {

    private CymexImportUrlDialog importDialog;
    
    public CymexImportUrl( CymexContext context ){       	
	importDialog = new CymexImportUrlDialog( context );
    }
    
    public void setVisible( boolean visible ){
	importDialog.setVisible( visible );
    }

    public void setLocationRelativeTo( Component c ){
	importDialog.setLocationRelativeTo( c );
    }
    
    private class CymexImportUrlDialog extends JDialog {

        String[] urlList = {"https://imex.mbi.ucla.edu/files/example1.mif"};

        
	private CymexImportUrlDialog( CymexContext context ){
	    	    
            setTitle("Cymex: Import Network");
	    Container contentPane = getContentPane();
            
            contentPane.setLayout(new BorderLayout());

            // row setup
            
            Box verticalBox = Box.createVerticalBox();            
            contentPane.add(verticalBox, BorderLayout.CENTER);

            Component vGlue_1 = Box.createVerticalGlue();
            verticalBox.add(vGlue_1);

            Component vStrut_1 = Box.createVerticalStrut(10);
            verticalBox.add(vStrut_1);

            Box horizontalBox1 = Box.createHorizontalBox();
            verticalBox.add(horizontalBox1);

            Component vStrut_2 = Box.createVerticalStrut(10);
            verticalBox.add(vStrut_2);
                        
            Box horizontalBox2 = Box.createHorizontalBox();
            verticalBox.add(horizontalBox2);

            Component vStrut_3 = Box.createVerticalStrut(10);
            verticalBox.add(vStrut_3);

            Component vGlue_2 = Box.createVerticalGlue();
            verticalBox.add(vGlue_2);
                       
            // 1st row

            //Component horizontalGlue = Box.createHorizontalGlue();
            //verticalBox.add(horizontalGlue);

            
            Component horizontalStrut_2 = Box.createHorizontalStrut(10);
            horizontalBox1.add(horizontalStrut_2);
                        
            JLabel lblNewLabel = new JLabel("Import data from URL:");
            horizontalBox1.add(lblNewLabel);

            Component horizontalStrut_3 = Box.createHorizontalStrut(20);
            horizontalBox1.add(horizontalStrut_3);
            
            JComboBox comboBox = new JComboBox( urlList );
            comboBox.setEditable(true);
            //comboBox.addActionListener(this);

            horizontalBox1.add(comboBox);
            
            Component horizontalStrut_4 = Box.createHorizontalStrut(10);
            horizontalBox1.add(horizontalStrut_4);
            
            // 2nd row: OK, Cancel buttons
            
            Component horizontalGlue_1 = Box.createHorizontalGlue();
            horizontalBox2.add(horizontalGlue_1);
            
            JButton btnNewButton_1 = new JButton("OK");
            horizontalBox2.add(btnNewButton_1);
            
            Component horizontalStrut = Box.createHorizontalStrut(10);
            horizontalBox2.add(horizontalStrut);
            
            JButton btnNewButton_2 = new JButton("Cancel");
            horizontalBox2.add(btnNewButton_2);
            
            Component horizontalStrut_1 = Box.createHorizontalStrut(10);
            horizontalBox2.add(horizontalStrut_1);
            
            // finalize layout

	    Dimension size= getSize();
	    //size.setSize(size.getWidth()+20,size.getHeight()+20);
	    //setSize(size);
            pack();
                        
	    setResizable(false);
	    setModal(true);
            
        }            
    }
}
