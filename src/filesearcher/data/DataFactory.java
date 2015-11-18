/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesearcher.data;

import filesearcher.structs.Parameters;
import javax.swing.JFrame;
import wheeler.generic.data.DialogFactory;
import wheeler.generic.data.StringHandler;

/**
 * Contains static functions regarding the generation of data.
 */
public class DataFactory {
    
    /**
     * Take in the search parameters, get the autorun and params files in place, call the autorun file.
     * @param root The search root
     * @param files The filename search parameters (raw interface string)
     * @param paths The filepath search parameters (raw interface string)
     * @param types The filetype search parameters (raw interface string)
     * @param searchLines Are we looking for lines within the files?
     * @param line The line search-string
     * @param excludeLines Are we excluding lines that otherwise match the search string?
     * @param exclude The exclusion search-string
     * @param hideLines Are we hiding the lines in the results file?
     * @param checkCase Are the line searches going to be case-sensitive?
     * @param useRegex Are we using a regular expression as the line's search-string?
     * @param caller The calling interface, used to ask the user questions or present warnings
     * @throws Exception If there's a problem writing one of the files or the search process fails to start
     */
    public static void startSearch(
                String root, String files, String paths, String types,
                boolean searchLines, String line, boolean excludeLines, String exclude,
                boolean hideLines, boolean checkCase, boolean useRegex, JFrame caller
            ) throws Exception{
        
        // The user MUST provide this value
        if(StringHandler.isEmpty(root, true)){
            DialogFactory.message(caller, "Please provide a search root");
            return;
        }
        
        // Check the root's value, creating the Parameters object as appropriate
        Parameters params;
        if(FileHandler.folderExists(root)){ // Is it a folder?
            params = Parameters.create(root, files, paths, types, caller);
        }else if(FileHandler.fileExists(root)){ // Is it a specific file?
            if (!DialogFactory.optionYesNo(caller,
                    "You specified a file as the search root;\nthis file will be the only thing searched\n  Continue?.")
                    ) return;
            if(!searchLines){
                DialogFactory.message(caller, "When searching a file, please provide content-based parameters.");
                return;
            }
            params = Parameters.create(root, "", "", "", caller);
        }else{ // It's missing
            DialogFactory.message(caller, "The search root could not be found.");
            return;
        }
        if (params == null) return;
        
        // Set additional parameters as appropriate
        if (searchLines) params.setLine(line, hideLines, checkCase, useRegex);
        if (excludeLines) params.setExclude(exclude);
        
        // If we're searching lines, see if the provided string matches the expected format
        if(searchLines){
            // Check that we have a search string
            if(line.length() < 1){
                DialogFactory.message(caller, "Please provide a search string for the line parameter");
                return;
            }
            
            // See if a warning is warranted for the line
            int lineIsRegex = LogicHandler.isStringARegularExpression(line);
            boolean warn = ((useRegex && (lineIsRegex == -1)) || (!useRegex && (lineIsRegex == 1)));
            
            // If we're excluding lines, see of the provided exclusion string matches the expected format
            if(excludeLines){
                // Check that we have an exclusion string
                if(exclude.length() < 1){
                    DialogFactory.message(caller, "Please provide a search string for the exclusion parameter");
                    return;
                }
                if (!warn){ // Don't bother checking if we're already warning
                    lineIsRegex = LogicHandler.isStringARegularExpression(exclude);
                    warn = ((useRegex && (lineIsRegex == -1)) || (!useRegex && (lineIsRegex == 1)));
                }
            }
            
            // If need be, check that the user is satisfied with the search string
            if(warn){
                String message = (useRegex)
                        ? "Your regular expression doesn't look like one. Continue?"
                        : "Your search string looks like a regular expression. Continue?";
                if (!DialogFactory.optionYesNo(caller, message)) return;
            }
        }
        
        // All parameters have been collected/verified; write the autorun file and run it
        params.writeParamsFile();
        FileHandler.writeAutorunFile(caller);
        FileHandler.runAutorunFile();
        
        // The first thing the seeker will do after reading the params file is delete it. Make sure it gets deleted.
        try{
            FileHandler.waitForFileDelete(FileHandler.paramsFile(), 10);
        }
        catch(Exception e){
            throw new Exception("The search program failed to start properly.", e);
        }
    }
    
}
