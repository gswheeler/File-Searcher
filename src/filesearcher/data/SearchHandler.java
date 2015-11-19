/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesearcher.data;

import filesearcher.structs.Parameters;
import wheeler.generic.data.StringHandler;
import wheeler.generic.data.readers.FileReader;
import wheeler.generic.data.readers.FileWriter;
import wheeler.generic.logging.Logger;
import wheeler.generic.structs.StringList;
import wheeler.generic.structs.StringNode;

/**
 * Performs a search.
 */
public class SearchHandler {
    
    // Variables
    
    // Related to the search in progress
    private final Parameters params;
    private FileWriter output;
    private boolean resultsFound = false;
    private final StringList linkPaths = new StringList();
    private final StringList linkTargets = new StringList();
    
    // Additional options that may be set by the user
    public static int maxPrintDepth = 3; // TODO: Allow this value to be set via the Parameters object
    public static int maxPathRepeat = 5; // TODO: Allow this value to be set via the Parameters object
    public static boolean disableLinkTargetCheck = false; // TODO: Allow this option to be set via Parameters
    
    // Options that may have internal use later
    protected boolean openOutputFile = true;
    
    
    /**
     * Create an object that handles a search.
     * @param parameters The search parameters.
     */
    public SearchHandler(Parameters parameters){
        params = parameters;
    }
    
    /**
     * Perform the search using the parameters provided when the object was created.
     * @param session The tag for the session.
     * @throws Exception Something went terribly wrong.
     */
    public void performSearch(String session) throws Exception{
        // HELLO WORLD!!!
        Logger.print("Searching under session " + session);
        
        // Open the output file for writing
        String outfile = FileHandler.outputFile(session);
        output = new FileWriter(outfile);
        
        // Start the search with the root, be it folder or file
        try{
            if(FileHandler.folderExists(params.searchRoot)){
                // Search folders starting with this one
                searchFolder(params.searchRoot, 0);
                
            }else if(FileHandler.fileExists(params.searchRoot)){
                // Search the file
                if(!params.searchLines){
                    // If we manage to get this point, just print out the whole file (the user's asking for it)
                    params.searchLines = true;
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
        if (openOutputFile) FileHandler.openFile(outfile);
        
    }
    
    
    /**
     * Check a folder for a match and check its contents.
     * @param folderpath The folder being checked.
     * @param depth The current depth of the folder being checked. Used when printing the current search directory to the console.
     * @throws Exception Something went wrong writing to the output file (all other errors caught and written to output).
     */
    protected void searchFolder(String folderpath, int depth) throws Exception{
        try{
            // First order of business; see if this is a folder we are skipping along with its contents (path is excluded)
            if (folderIsExcluded(folderpath)) return;
            
            // Next order of business; make sure we haven't stumbled into a recursive loop
            String linkTarget = (FileHandler.isSymbolicLink(folderpath))
                    ? FileHandler.inspectSymbolicLink(folderpath)
                    : null;
            // Do not assume we can detect symbolic links; that's half the problem
            if (checkForProblemLink(folderpath, linkTarget)) return;
            if(linkTarget != null){
                // It's an OK symbolic link; just remember it
                linkPaths.add(folderpath); linkTargets.add(linkTarget);
            }
            
            // Let the user know where we are in the search (if we aren't too deep)
            if (!(depth > maxPrintDepth)) Logger.print("Checking folder " + folderpath);
            
            // Here's our time to shine: if we aren't looking inside files for matches, see if we are a match
            if (!params.searchLines && filepathMatches(folderpath, true)){
                output.writeLine(folderpath);
                resultsFound = true;
            }
            
            // Time to check the contents of the folder
            String[] contents = FileHandler.getContents(folderpath);
            for(String filepath : contents){
                if(FileHandler.folderExists(filepath)){
                    // It's a folder; check it and its contents
                    searchFolder(filepath, depth + 1);
                }else if(FileHandler.fileExists(filepath)){
                    // It's a file; check it and its contents if appropriate
                    searchFile(filepath);
                }else if(!params.searchLines){
                    // It isn't actually there; treat it as a file if we don't have to look inside it
                    searchFile(filepath);
                }
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
    
    
    /**
     * Checks a file for a match, checking its contents if appropriate.
     * @param filepath The file being checked.
     * @throws Exception Something went wrong writing to output: all other errors are caught and written to output.
     */
    protected void searchFile(String filepath) throws Exception{
        try{
            // First things first: do we have a match?
            if (!filepathMatches(filepath, false)) return;
            
            // We have a match: if we aren't checking the file's contents, that's all we need
            if(!params.searchLines){
                output.writeLine(filepath);
                resultsFound = true;
                return;
            }
            
            // We have a match and now need to check the contents of the file
            // Handle the case-sensitivity of the search criteria now, rather than re-doing it for every line
            String include = params.line; String exclude = (params.excludeLines) ? params.exclude : null;
            if(!params.checkCase){
                // Search strings go all-lowercase,
                // regular expressions go lowercase except for special characters
                include = StringHandler.toLowerCase(include, params.useRegex);
                if (exclude != null) exclude = StringHandler.toLowerCase(exclude, params.useRegex);
            }
            // Check every line
            FileReader reader = new FileReader(filepath);
            long filesize = FileHandler.getFileSize(filepath);
            String line; long count = 0; long matches = 0;
            while((line = reader.readLine(true)) != null){
                // Keep track of the line number
                count++;
                
                // Handle case-sensitivity; if case-insensitive, the line being searched goes all-lowercase
                // Note: have to leave the case of the line itself alone for when we print it
                String test = (!params.checkCase) ? StringHandler.toLowerCase(line, false) : line;
                
                // Check this line for a match (or lack thereof)
                if(params.useRegex){
                    if (!test.matches(include)) continue;
                }else{
                    if (!StringHandler.contains(test, include)) continue;
                }
                
                // Check if this line is being excluded (if appropriate)
                if(exclude != null){
                    if(params.useRegex){
                        if (test.matches(exclude)) continue;
                    }else{
                        if (StringHandler.contains(test, exclude)) continue;
                    }
                }
                
                // The line passed the test; add it to the output file
                resultsFound = true;
                // If this is the first match, print the filepath
                if (matches == 0) output.writeLine(filepath);
                // If we're hiding lines in the output, skip printing the line (also, can stop searching now)
                if(params.hideLines){
                    reader.close();
                    return;
                }
                output.writeLine(" " + Long.toString(count) + "\t" + line);
                
                // Make sure the file isn't growing on us (indicates we're searching our own output file)
                if((++matches % 25) == 0){ // Check every 25 matches
                    // If the file changed in size, bail NOW
                    long newSize = FileHandler.getFileSize(filepath);
                    if(filesize != newSize){
                        reader.close();
                        output.writeLine("The file changed in size as we were reading it ("
                                + StringHandler.commaDelineate64(filesize) + "B to "
                                + StringHandler.commaDelineate64(newSize) + "B)");
                        output.writeLine("");
                        return;
                    }
                    // Otherwise, sit tight for the next 25 matches
                }
            }
            
            // Finished reading the file; if we found a match, put a line between this file and the next
            if (matches > 0) output.writeLine("");
            
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
        
        // Check for a match in the final, any-match criteria
        // Start by checking the case where we don't "include" any filetypes
        if(params.includeTypes.length == 0){
            if(isFolder){
                // No includes or excludes validates folders
                // If there were excludes, need to have folders included (problem is, we're here because there aren't any includes)
                return (params.excludeTypes.length == 0);
            }else{
                // Zero includes and no matching excludes validates files
                return true;
            }
        }
        // We have included filetypes; check for a match
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
    
    
    /**
     * Checks if the path appears to be part of a recursive loop due to a symbolic link.
     * If the folder is a symbolic link, the destination folder is
     *   resolved,
     *   checked for existence, and
     *   checked to make sure it isn't a parent of the link itself.
     * Further, the link's target path is checked against the target paths of any previous links.
     * In either case, the end of the filepath is checked for any obvious signs of recursion
     *   (namely, the path ending with a path segment that repeats a number of times determined by maxPathRepeat).
     * @param folderpath The folder being checked, symbolic link or otherwise.
     * @param linkTarget The target path if the folder is a symbolic link, null otherwise.
     * @return True if there appears to be a recursive link somewhere, false otherwise.
     * @throws Exception If something goes wrong getting data from a symbolic link or writing to the output file.
     */
    protected boolean checkForProblemLink(String folderpath, String linkTarget) throws Exception{
        // These tests require a symbolic link we can detect
        if(!disableLinkTargetCheck && (linkTarget != null)){
            // Find the target folder of the symbolic link
            String linkDest = FileHandler.resolveSymbolicLinkTarget(folderpath);

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
            // Note: not just checking parent folders using the filepath in case the user has purposefully started us below two valid links with the same target path
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
        
        // Backup plan: if we can find a substring of the path that occurs X times in a row at the end, yell about it
        String[] folders = StringHandler.parseIntoArray(folderpath, "\\");
        // Start with the last folder
        String subpath = folders[folders.length - 1];
        for(int i = 1; i < folders.length; i++){
            // If the string we're looking for is bigger than the filepath, just stop now
            if (((subpath.length()+1) * maxPathRepeat) > folderpath.length()) break;
            
            // See if the current subpath*X occurs at the end of the filepath
            String chkStr = "";
            for (int j = 0; j < maxPathRepeat; j++) chkStr = FileHandler.composeFilepath(chkStr, subpath);
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
