package org.imex.cytoscape.cymex.internal.ui;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class CymexMifFileFilter extends FileFilter {

    // Accept all directories and all xml, mif, mif25, mif30 files.
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = CymexFileUtils.getExtension(f);
        if( extension != null ){
            if( extension.equalsIgnoreCase(CymexFileUtils.xml) ||
                extension.equalsIgnoreCase(CymexFileUtils.mif) ||
                extension.equalsIgnoreCase(CymexFileUtils.mif25) ||
                extension.equalsIgnoreCase(CymexFileUtils.mif30) ){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public String getDescription() {
        return "MIF Files (*.xml, *.mif, *.mif25, *.mif30)";
    }
}
