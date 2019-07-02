package org.imex.cytoscape.cymex.internal.ui;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class CymexMif25FileFilter extends FileFilter {

    // Accept all directories and mif25.
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = CymexFileUtils.getExtension(f);
        if( extension != null ){
            if( extension.equalsIgnoreCase(CymexFileUtils.mif25) ){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public String getDescription() {
        return "MIF25 Files (*.mif25)";
    }
}
