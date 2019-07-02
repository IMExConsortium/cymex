package org.imex.cytoscape.cymex.internal.ui;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class CymexMif30FileFilter extends FileFilter {

    // Accept all directories and mif30.
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = CymexFileUtils.getExtension(f);
        if( extension != null ){
            if( extension.equalsIgnoreCase(CymexFileUtils.mif30) ){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public String getDescription() {
        return "MIF30 Files (*.mif30)";
    }
}
