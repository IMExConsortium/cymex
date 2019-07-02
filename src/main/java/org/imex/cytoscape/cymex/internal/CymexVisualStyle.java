package org.imex.cytoscape.cymex.internal;

import java.awt.Color;
import java.awt.Font;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.*;

import org.cytoscape.view.presentation.property.*;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;


import java.util.*;

class CymexVisualStyle{
    
    public static final String NODE_LABEL = "label";
    
    private static final Color NODE_COLOR = new Color( 153, 204, 255 );
    private static final Color NODE_LABEL_COLOR = new Color( 0, 0, 0 );
    private static final Color NODE_BORDER_COLOR = new Color( 0, 102, 153 );

    private static final Color EDGE_COLOR = new Color( 153, 153, 153 );
    private static final Color EDGE_LABEL_COLOR = new Color( 0, 0, 0 );   
    
    private static Font NODE_LABEL_FONT;
    static {
        NODE_LABEL_FONT = new Font( "HelveticaNeue-UltraLight", Font.PLAIN, 10 );
        if( NODE_LABEL_FONT == null )
            NODE_LABEL_FONT = new Font( "SansSerif", Font.PLAIN, 10 );
    }

    
    private  CymexContext context;
    
    public CymexVisualStyle( CymexContext context ){
        this.context = context;
        
    }

    public VisualStyle build( String name ){

        final VisualStyle defStyle 
            = context.getVisualStyleFactory().createVisualStyle( name );
        
        final Set<VisualPropertyDependency<?>> deps 
            = defStyle.getAllVisualPropertyDependencies();

        // Disable add deps
        
        for( VisualPropertyDependency<?> dep: deps) {
            dep.setDependency( false );
        }

        // Network VS
        //-----------

        final Color backGroundColor = Color.white;
        defStyle.setDefaultValue( BasicVisualLexicon.NETWORK_BACKGROUND_PAINT, 
                                  backGroundColor );

        // Node Label Mapping
        //-------------------

        final PassthroughMapping<String, String> labelPassthrough 
            = (PassthroughMapping<String, String>)  
            context.getPassthroughMappingFactory()
            .createVisualMappingFunction( NODE_LABEL , String.class,
                                          BasicVisualLexicon.NODE_LABEL);

        defStyle.addVisualMappingFunction( labelPassthrough );


        // Node VS
        //--------
        
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_SHAPE, 
                                  NodeShapeVisualProperty.ELLIPSE );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_BORDER_PAINT, 
                                  NODE_BORDER_COLOR );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_FILL_COLOR, 
                                  NODE_COLOR );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_LABEL_COLOR, 
                                  NODE_LABEL_COLOR );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_LABEL_FONT_FACE, 
                                  NODE_LABEL_FONT );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_LABEL_TRANSPARENCY, 
                                  230 );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_BORDER_WIDTH, 
                                  4.0d );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_BORDER_TRANSPARENCY, 
                                  240 );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_WIDTH, 
                                  75d );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_HEIGHT, 
                                  24d );
        defStyle.setDefaultValue( BasicVisualLexicon.NODE_TRANSPARENCY, 
                                  220 );

        // Edge VS
        //--------

        defStyle.setDefaultValue( BasicVisualLexicon.EDGE_TRANSPARENCY, 
                                  180 );
        defStyle.setDefaultValue( BasicVisualLexicon.EDGE_WIDTH, 
                                  2.0d );
        defStyle.setDefaultValue( BasicVisualLexicon.EDGE_PAINT, 
                                  EDGE_COLOR );
        defStyle.setDefaultValue( BasicVisualLexicon.EDGE_LABEL_TRANSPARENCY, 
                                  80 );
        defStyle.setDefaultValue( BasicVisualLexicon.EDGE_LABEL_FONT_SIZE, 
                                  8 );
        defStyle.setDefaultValue( BasicVisualLexicon.EDGE_LABEL_COLOR, 
                                  EDGE_LABEL_COLOR );
        
        return defStyle;
    }    
}
