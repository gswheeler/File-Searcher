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
import wheeler.generic.data.readers.FileWriter;
import wheeler.generic.logging.Logger;
import wheeler.generic.structs.StringList;
import wheeler.generic.structs.StringSimpleList;

/**
 * A class containing the parameters for a search.
 * Use this class to gather the parameters, save them to a session file, and retrieve them from said file
 */
public class Parameters {
    
    // File parameters
    public String searchRoot;
    public String[] includeFiles = null; public String[] excludeFiles = null;
    public String[] includePaths = null; public String[] excludePaths = null;
    public String[] includeTypes = null; public String[] excludeTypes = null;
    // Line parameters
    public boolean searchLines = false;  public String line = null;
    public boolean excludeLines = false; public String exclude = null;
    public boolean hideLines = false;
    public boolean checkCase = false;
    public boolean useRegex = false;
    
    /**
     * Don't allow this object to be created on the fly; use the static creator methods instead
     */
    private Parameters(){
        // Nothing done here
    }
    
    
    /**
     * Used by the main interface. Starts off with the fundamental parameters.
     * @param root Starting folder for the search
     * @param files String from the "Filename" line
     * @param paths String from the "Path" line
     * @param types String from the "Filetypes" line
     * @param caller The interface; used to check if leading dashes are part of the parameters or are negators
     * @return Returns a Parameters object with root/file/path/type populated, unless the search was canceled in which case null is returned.
     */
    public static Parameters create(String root, String files, String paths, String types, JFrame caller){
        // The parameters object we'll be passing back if all goes well
        Parameters parameters = new Parameters();
        
        // Set the search root
        parameters.searchRoot = root;
        
        // Set the filenames to look for
        String[][] params = getIncludeExclude(files, "file", caller);
        if (params == null) return null;
        parameters.includeFiles = params[0];
        parameters.excludeFiles = params[1];
        
        // Set the paths to look for
        params = getIncludeExclude(paths, "path", caller);
        if (params == null) return null;
        parameters.includePaths = params[0];
        parameters.excludePaths = params[1];
        
        // Set the filetypes to look for
        params = getIncludeExclude(types, "type", caller);
        if (params == null) return null;
        parameters.includeTypes = params[0];
        parameters.excludeTypes = params[1];
        
        // Return the parameters object we created
        return parameters;
    }
    
    
    /**
     * Set the "Line" parameters. Only use this if lines are being looked for (assumes yes)
     * @param lineString The string provided as the "line" search parameter
     * @param hide Are lines being hidden in the output file (filepaths only)?
     * @param caseChecked Is case being checked?
     * @param regex Is the provided search parameter a regular expression?
     */
    public void setLine(String lineString, boolean hide, boolean caseChecked, boolean regex){
        searchLines = true;
        line = lineString;
        hideLines = hide;
        checkCase = caseChecked;
        useRegex = regex;
    }
    
    
    /**
     * Set the "Exclude" line parameters. Only use this if lines are being excluded (assumes yes)
     * @param excludeString The string provided as the "exclude" search parameter
     */
    public void setExclude(String excludeString){
        excludeLines = true;
        exclude = excludeString;
    }
    
    
    /**
     * Splits a parameter string into "to include" and "to exclude".
     * Uses space-delineation, drops empty strings.
     * @param paramString The string being split into "include" and "exclude"
     * @param paramType A text representation of the parameter type; used to make questions to the user specific to the parameter type
     * @param caller The interface; used to ask the user questions
     * @return A two-item array of arrays of strings; the first is the list of strings to include, the second the list of strings to to exclude. Returns null if the user cancels.
     */
    private static String[][] getIncludeExclude(String paramString, String paramType, JFrame caller){
        // Collection variables
        String[] params = StringHandler.parseIntoArray(paramString, " ");
        StringSimpleList includes = new StringSimpleList();
        StringSimpleList excludes = new StringSimpleList();
        
        // Sort the parameters
        int dashesExclude = 0;
        for(String param : params){
            if (param.length() < 1) continue; // Drop empty strings
            if(param.startsWith("-")){ // Possible exclusion
                // Ask the user if dashes mean exclusion (allow user to cancel here)
                if(dashesExclude == 0){
                    dashesExclude = DialogFactory.optionYesNoCancel(caller,
                            "There were dashes in the " + paramType + " parameters;\nare they meant to exclude " + paramType + "s?",
                            "Do dashes indicate exclusion?");
                    if (dashesExclude == 0) return null;
                }
                // If excluding, remove the leading dash; otherwise leave it be
                excludes.add((dashesExclude > 0) ? param.substring(1) : param);
            }else{ // Definite include
                includes.add(param);
            }
        }
        
        // Check if "no" was said to exclusions
        String[] includeResult;
        String[] excludeResult;
        if(dashesExclude < 0){
            // Everything is an included parameter
            includeResult = includes.add(excludes).toArray();
            excludeResult = new String[0];
        }else{
            // Either there are no excluded parameters or we confirmed that they are excluded
            includeResult = includes.toArray();
            excludeResult = excludes.toArray();
        }
        // Put the two arrays into a single array and return
        String[][] result = new String[2][];
        result[0] = includeResult;
        result[1] = excludeResult;
        return result;
    }
    
    
    /* Parameters: file format
        Root
        Include-files (space-delineated)
        Exclude-files (space-delineated)
        Include-paths (space-delineated)
        Exclude-paths (space-delineated)
        Include-types (space-delineated)
        Exclude-types (space-delineated)
        0 or 1SearchString
        0 or 1ExcludeString
        options...
            hideLines
            checkCase
            useRegex
    */
    
    
    /**
     * Write the parameters of the search to a file that the searching process will find later
     * @throws Exception If an exception occurs writing the file
     */
    public void writeParamsFile() throws Exception{
        // Open the file for writing
        FileWriter writer = new FileWriter(FileHandler.paramsFile());
        
        // Write the stuff that we know will be there
        writer.writeLine(searchRoot);
        writer.writeLine(StringHandler.concatStringArray(includeFiles, " "));
        writer.writeLine(StringHandler.concatStringArray(excludeFiles, " "));
        writer.writeLine(StringHandler.concatStringArray(includePaths, " "));
        writer.writeLine(StringHandler.concatStringArray(excludePaths, " "));
        writer.writeLine(StringHandler.concatStringArray(includeTypes, " "));
        writer.writeLine(StringHandler.concatStringArray(excludeTypes, " "));
        writer.writeLine( (searchLines) ? "1" + line    : "0" );
        writer.writeLine((excludeLines) ? "1" + exclude : "0" );
        
        // Add extra lines for optional parameters
        if (hideLines) writer.writeLine(hideLinesKeyword);
        if (checkCase) writer.writeLine(checkCaseKeyword);
        if (useRegex)  writer.writeLine(useRegexKeyword);
        
        // Close the file when done
        writer.close();
    }
    
    /** Keyword in the parameters file for the hide-lines option */
    private static final String hideLinesKeyword = "hideLines";
    /** Keyword in the parameters file for the check-case option */
    private static final String checkCaseKeyword = "checkCase";
    /** Keyword in the parameters file for the use-regular-expression option */
    private static final String useRegexKeyword  = "useRegex";
    
    /**
     * Derives a set of search parameters from the designated parameters file.
     * @return A Parameters object holding the search parameters.
     * @throws Exception If the file is missing or if the data is seriously mis-formatted
     */
    public static Parameters getParametersFromFile() throws Exception{
        // Get the contents of the file
        StringList contents = FileHandler.readFile(FileHandler.paramsFile(), true, true);
        
        // The Parameters object we will be returning
        Parameters parameters = new Parameters();
        
        // Get the parameters we know will be there
        parameters.searchRoot = contents.pullFirst();
        parameters.includeFiles = StringHandler.parseIntoArray(contents.pullFirst(), " ");
        parameters.excludeFiles = StringHandler.parseIntoArray(contents.pullFirst(), " ");
        parameters.includePaths = StringHandler.parseIntoArray(contents.pullFirst(), " ");
        parameters.excludePaths = StringHandler.parseIntoArray(contents.pullFirst(), " ");
        parameters.includeTypes = StringHandler.parseIntoArray(contents.pullFirst(), " ");
        parameters.excludeTypes = StringHandler.parseIntoArray(contents.pullFirst(), " ");
        String line = contents.pullFirst();
        if(line.startsWith("1")){ parameters.searchLines = true; parameters.line = line.substring(1); }
        line = contents.pullFirst();
        if(line.startsWith("1")){ parameters.excludeLines = true; parameters.exclude = line.substring(1); }
        
        // Get any optional parameters
        while(contents.any()){
            line = contents.pullFirst();
            switch(line){
                case hideLinesKeyword:
                    parameters.hideLines = true;
                    break;
                case checkCaseKeyword:
                    parameters.checkCase = true;
                    break;
                case useRegexKeyword:
                    parameters.useRegex = true;
                    break;
                case "":
                    break;
                default:
                    Logger.print("Did not recognize optional parameter \"" + line + "\"");
            }
        }
        
        // Have all the parameters: return them
        return parameters;
    }
    
}
