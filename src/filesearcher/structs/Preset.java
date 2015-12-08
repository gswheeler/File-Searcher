/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesearcher.structs;

import filesearcher.data.FileHandler;
import javax.swing.JFrame;
import wheeler.generic.data.DialogFactory;
import wheeler.generic.data.StringHandler;
import wheeler.generic.structs.StringList;
import wheeler.generic.structs.StringSimpleList;

/**
 * Contains raw parameter Strings for/from the interface
 */
public class Preset {
    
    // Variables
    /** The raw string from the search-root field */
    public String searchRoot;
    /** The raw string from the filenames field */
    public String files;
    /** The raw string from the path field */
    public String paths;
    /** The raw string from the filetypes field */
    public String types;
    /** The state of the "find lines" checkbox */
    public boolean searchLines;
    /** The raw string from the "line contains" field */
    public String line;
    /** The state of the "exclude" checkbox */
    public boolean excludeLines;
    /** The raw string from the "exclude" field */
    public String exclude;
    /** The state of the hide-lines checkbox */
    public boolean hideLines;
    /** The state of the check-case checkbox */
    public boolean checkCase;
    /** The state of the use-regex checkbox */
    public boolean useRegex;
    
    /**Constructor that uses raw values from the interface.
     * @param strRoot The raw string from the search-root field
     * @param strFiles The raw string from the filenames field
     * @param strPaths The raw string from the path field
     * @param strTypes The raw string from the filetypes field
     * @param blnLine The state of the "find lines" checkbox
     * @param strLine The raw string from the "line contains" field
     * @param blnExclude The state of the "exclude" checkbox
     * @param strExclude The raw string from the "exclude" field
     * @param blnHide The state of the hide-lines checkbox
     * @param blnCase The state of the check-case checkbox
     * @param blnRegex The state of the use-regex checkbox
     */
    public Preset(String strRoot, String strFiles, String strPaths, String strTypes,
            boolean blnLine, String strLine, boolean blnExclude, String strExclude,
            boolean blnHide, boolean blnCase, boolean blnRegex){
        
        searchRoot = strRoot;
        files = strFiles;
        paths = strPaths;
        types = strTypes;
        searchLines = blnLine;
        line = strLine;
        excludeLines = blnExclude;
        exclude = strExclude;
        hideLines = blnHide;
        checkCase = blnCase;
        useRegex = blnRegex;
        
    }
    protected Preset(){}
    
    /* Presets file layout
     *  searchRoot
     *  filenames string
     *  path string
     *  filetypes string
     *  0/1line_string
     *  0/1exclude_string
     *  line flags (hide/case/regex)
     */
    
    /**Parse a presets file and return the interface values.
     * @param name The name of the presets session
     * @return A Presets object with the values of the session
     * @throws Exception If an error occurs reading the file
     */
    public static Preset fromPresetFile(String name) throws Exception{
        // Get the filepath of the specified session preset file, get its data
        String path = getPresetsFile(name);
        StringList data = FileHandler.readFile(path, true, true);
        
        // Parse the data
        Preset preset = new Preset();
        preset.searchRoot = data.pullFirst();
        preset.files = data.pullFirst();
        preset.paths = data.pullFirst();
        preset.types = data.pullFirst();
        preset.searchLines = data.getFirst().startsWith("1");
        preset.line = data.pullFirst().substring(1);
        preset.excludeLines = data.getFirst().startsWith("1");
        preset.exclude = data.pullFirst().substring(1);
        String lineFlags = data.pullFirst();
        preset.hideLines = StringHandler.charAt(lineFlags, 0).equals("1");
        preset.checkCase = StringHandler.charAt(lineFlags, 1).equals("1");
        preset.useRegex = StringHandler.charAt(lineFlags, 2).equals("1");
        
        // Return the presets object
        return preset;
    }
    
    /**Write the preset data to file for later use.
     * @param name The name under which to save the presets.
     * @param caller The calling interface; used to ask if pre-existing presets should be overwritten.
     * @return True if the data was written, false if the user canceled.
     * @throws Exception If there was a problem writing the file
     */
    public boolean writeToFile(String name, JFrame caller) throws Exception{
        // Get (and check) the filepath
        String path = getPresetsFile(name);
        if ((caller != null) && FileHandler.fileExists(path)){
            String message = "Presets under the name \"" + name + "\" already exist. Overwrite?";
            if (!DialogFactory.optionYesNo(caller, message)) return false;
        }
        
        // Write the sucker
        StringSimpleList data = new StringSimpleList();
        data.add(searchRoot); data.add(files); data.add(paths); data.add(types);
        data.add(((searchLines) ? "1" : "0") + line);
        data.add(((excludeLines) ? "1" : "0") + exclude);
        data.add(
                ((hideLines) ? "1" : "0")
                + ((checkCase) ? "1" : "0")
                + ((useRegex) ? "1" : "0")
            );
        FileHandler.writeFile(data, true, path);
        return true;
    }
    
    protected static String getPresetsFile(String name) throws Exception{
        return FileHandler.composeFilepath(FileHandler.presetsFolder(), name + ".txt");
    }
    
}
