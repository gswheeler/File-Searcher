/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesearcher.data;

import filesearcher.structs.Parameters;
import wheeler.generic.data.StringHandler;
import wheeler.generic.data.readers.FileWriter;
import wheeler.generic.logging.Logger;
import wheeler.generic.structs.StringList;
import wheeler.generic.structs.StringNode;

/**
 * Performs a search.
 */
public class SearchHandler {
    
    // Variables
    private final Parameters params;
    private FileWriter output;
    private boolean resultsFound = false;
    private StringList linkPaths = new StringList();
    private StringList linkTargets = new StringList();
    
    public static int maxPathRepeat = 5; // TODO: Allow this value to be set via the Parameters object
    public static boolean disableRecursionDetection = false;
    public static boolean skipBruteForceRecursionDetection = false;
    
    
    public SearchHandler(Parameters parameters){
        params = parameters;
    }
    
    public void performSearch(String session) throws Exception{
        // Open the output file for writing
        String outfile = FileHandler.outputFile(session);
        output = new FileWriter(outfile);
        
        // Start the search with the root, be it folder or file
        try{
            if(FileHandler.folderExists(params.searchRoot)){
                // Search folders starting with this one
                searchFolder(params.searchRoot);
                
            }else if(FileHandler.fileExists(params.searchRoot)){
                // Search the file
                if(!params.searchLines){
                    // If we manage to get this point, just print out the whole file (the user's asking for it)
                    params.excludeLines = false;
                    params.hideLines = false;
                    params.useRegex = true;
                    params.line = ".*";
                }
                searchFile(params.searchRoot);
                
            }else{
                // We shouldn't get here, but we should be prepared in any case
                output.write("Search root \"" + params.searchRoot + "\" not found");
                resultsFound = true;
            }
            // If no results were found, that is the result
            if (!resultsFound) output.write("No results were found.");
        }
        catch(Exception e){
            try{
                output.close();
            }
            catch(Exception e2){
                Logger.print("An extraneous exception occurred when trying to close the output file.");
                Logger.print(e2, 1, 0);
            }
            throw e;
        }
        
        // Close the writer and open the output file
        output.close();
        FileHandler.openFile(outfile);
        
    }
    
    
    protected void searchFolder(String folderpath) throws Exception{
        try{
            // First things first; make sure we haven't stumbled into a recursive loop
            String linkTarget = (FileHandler.isSymbolicLink(folderpath))
                    ? FileHandler.inspectSymbolicLink(folderpath)
                    : null;
            // Do not assume we can detect symbolic links; that's half the problem
            if (checkForProblemLink(folderpath, linkTarget)) return;
            if(linkTarget != null){
                // It's an OK symbolic link; just remember it
                linkPaths.add(folderpath); linkTargets.add(linkTarget);
            }
            
            
            
            // If we had a symbolic link earlier, remove it from the list of links we are under
            if(linkTarget != null){
                linkPaths.pullLast(); linkTargets.pullLast();
            }
            
        }
        catch(Exception e){
            output.writeLine("An error occurred while searching folder " + folderpath + ":");
            output.writeLine(StringHandler.replaceRegEx(
                    LogicHandler.exToString(e, 1, 0),
                    "\\ *(\\r|)\\n",
                    "\\n",
                    false
                ));
            output.writeLine(""); output.writeLine("");
        }
    }
    
    
    protected void searchFile(String filepath) throws Exception{
        try{
            
        }
        catch(Exception e){
            output.writeLine("An error occurred while reading file " + filepath + ":");
            output.writeLine(StringHandler.replaceRegEx(
                    LogicHandler.exToString(e, 1, 0),
                    "\\ *(\\r|)\\n",
                    "\\n",
                    false
                ));
            output.writeLine(""); output.writeLine("");
        }
    }
    
    
    /**
     * Check if a filepath passes the include/exclude criteria.
     * @param filepath The filepath being checked.
     * @param isFolder Is the filepath that of a folder? (Folders require special consideration when the type is being checked.)
     * @return True if all file criteria are met, false otherwise.
     */
    protected boolean filepathMatches(String filepath, boolean isFolder){
        // Check for a match in any exclusion criteria
        String name = FileHandler.getFileName("\\" + filepath);
        for(String exclude : params.excludeFiles){
            if (StringHandler.contains(name, exclude, false)) return false;
        }
        for(String exclude : params.excludePaths){
            if (StringHandler.contains(filepath, exclude, false)) return false;
        }
        for(String exclude : params.excludeTypes){
            if(isFolder){
                if (exclude.equals("\\")) return false;
            }else{
                if (filepath.toLowerCase().endsWith(exclude.toLowerCase())) return false;
            }
        }
        
        // Check for a failure to meet any all-match criteria
        for(String match : params.includeFiles){
            if (!StringHandler.contains(name, match, false)) return false;
        }
        for(String match : params.includePaths){
            if (!StringHandler.contains(filepath, match, false)) return false;
        }
        
        // Check for a match in the final any-match criteria
        if ((params.includeTypes.length == 0) && (params.excludeTypes.length == 0)) return true;
        for(String match : params.includeTypes){
            if(isFolder){
                if (match.equals("\\")) return true;
            }else{
                if (filepath.toLowerCase().endsWith(match.toLowerCase())) return true;
            }
        }
        return false;
    }
    
    
    /**
     * Check if a folder and its subfolders are excluded from the search by way of a path exclusion.
     * This function can cut down on unnecessary searching.
     * @param folderpath The folder being checked.
     * @return True if the folder and all subfolders will not match, false otherwise.
     */
    protected boolean folderIsExcluded(String folderpath){
        for(String exclude : params.excludePaths){
            if (StringHandler.contains(folderpath, exclude, false)) return true;
        }
        return false;
    }
    
    
    protected boolean checkForProblemLink(String folderpath, String linkTarget) throws Exception{
        // TODO: Allow this option to be set
        if (disableRecursionDetection) return false;
        
        // These tests require a symbolic link we can detect
        if(linkTarget != null){
            // Find the target folder of the symbolic link
            String linkDest = FileHandler.resolveRelativeFilepath(folderpath, linkTarget);

            // The link may be broken, or, if the link is accessed through another link, the link may not resolve properly
            if (!FileHandler.folderExists(linkDest)){
                output.writeLine("A broken link or bad link pattern was detected:");
                output.writeLine(folderpath);
                output.writeLine("");
                return true;
            }

            // If there is a direct recursive link, the target folder will be a parent of the link itself
            if (FileHandler.folderAcontainsB(linkDest, folderpath)){
                output.writeLine("A recursive link was detected:");
                output.writeLine(folderpath);
                output.writeLine("");
                return true;
            }

            // Let's not take any chances. If a link has the same target as an earlier one, be it relative or absolute, warn about it
            StringNode pathNode = linkPaths.getHeader(); StringNode targetNode = linkTargets.getHeader();
            while(((pathNode = pathNode.next) != null) && ((targetNode = targetNode.next) != null)){
                if (StringHandler.areEqual(linkTarget, targetNode.value, false)){
                    output.writeLine("A recursive link was detected:");
                    output.writeLine(pathNode.value);
                    output.writeLine("  and " + folderpath);
                    output.writeLine("  point to " + targetNode.value);
                    output.writeLine("");
                    return true;
                }
            }
        }
        
        // TODO: Put in an admin option to set this
        if (skipBruteForceRecursionDetection) return false;
        
        // Backup plan: if we can find a substring of the path that occurs X times in a row at the end, yell about it
        String[] folders = StringHandler.parseIntoArray(folderpath, "\\");
        // Start with the last folder
        String subpath = folders[folders.length - 1];
        for(int i = 1; i < folders.length; i++){
            // If the string we're looking for is bigger than the filepath, just stop now
            if (((subpath.length() * maxPathRepeat) + maxPathRepeat - 1) > folderpath.length()) break;
            
            // See if the current subpath*X occurs at the end of the filepath
            String chkStr = subpath;
            for (int j = 1; j < maxPathRepeat; j++) chkStr = FileHandler.composeFilepath(chkStr, subpath);
            if(folderpath.endsWith(chkStr)){
                output.writeLine("A recursive link was detected:");
                output.writeLine(folderpath);
                output.writeLine("");
                return true;
            }
            
            // Add the last folder before the first in the substring
            subpath = FileHandler.composeFilepath(folders[folders.length - i - 1], subpath);
        }
        
        // Link is valid, has a target value not seen before, and path doesn't scream "recursion". Can't really complain
        return false;
    }
    
}
