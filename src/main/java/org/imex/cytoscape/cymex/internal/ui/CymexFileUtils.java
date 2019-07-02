package org.imex.cytoscape.cymex.internal.ui;

import java.io.File;
import javax.swing.ImageIcon;

public class CymexFileUtils {
    
    public final static String xml = "xml";
    public final static String mif = "mif";
    public final static String mif25 = "mif25";
    public final static String mif30 = "mif30";

    public static String getExtension( File f ){
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if( i > 0 &&  i < s.length()-1 ){
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
     }
    
    protected static ImageIcon createImageIcon( String path ){
        java.net.URL imgURL = CymexFileUtils.class.getResource( path );
        if( imgURL != null ){
            return new ImageIcon( imgURL );
        } else {
            System.err.println("Couldn't find file: " + path );
            return null;
        }
    }
}
