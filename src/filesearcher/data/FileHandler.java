/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesearcher.data;

import javax.swing.JFrame;
import wheeler.generic.data.DialogFactory;
import wheeler.generic.data.StringHandler;
import wheeler.generic.structs.StringList;

/**
 *
 * @author Greg
 */
public class FileHandler extends wheeler.generic.data.FileHandler {
    
    protected static String programFolder = "C:\\Program Files\\Wheeler\\File Searcher";
    protected static String _jarLocation = null;
    
    public static boolean testProgramFolder(JFrame caller) throws Exception{
        return testProgramFolder(programFolder, caller);
    }
    
    // The folder with the output, signal, and parameters files
    public static String dataFolder() throws Exception{
        String path = composeFilepath(programFolder, "data");
        ensureFolderExists(path);
        return path;
    }
    public static String outputFile(String session) throws Exception{
        return composeFilepath(dataFolder(), "output" + session + ".txt");
    }
    public static String signalFile(String session) throws Exception{
        return composeFilepath(dataFolder(), "signal" + session + ".txt");
    }
    public static String paramsFile() throws Exception{
        return composeFilepath(dataFolder(), "params.txt");
    }
    
    // The folder with the autorun batchfile
    public static String batchFolder() throws Exception{
        String path = composeFilepath(programFolder, "batch");
        ensureFolderExists(path);
        return path;
    }
    protected static String autorunFile() throws Exception{
        return composeFilepath(batchFolder(), "autorun.cmd");
    }
    
    // Configuration/presets filepaths
    protected static String configFolder() throws Exception{
        String path = composeFilepath(programFolder, "data");
        ensureFolderExists(path);
        return path;
    }
    public static String presetsFolder() throws Exception{
        String path = composeFilepath(configFolder(), "presets");
        ensureFolderExists(path);
        return path;
    }
    public static String lineFile() throws Exception{
        return composeFilepath(configFolder(), "line.txt");
    }
    protected static String jarLocationFile() throws Exception{
        return composeFilepath(configFolder(), "jar.txt");
    }
    
    // Where we expect the JAR file to be
    protected static String defaultJarFile(){
        return composeFilepath(programFolder, "File_Searcher.jar");
    }
    
    
    // Get the location of the JAR file, asking the user if need be
    public static String getJarLocation(JFrame caller) throws Exception{
        // Have we set the location yet?
        String jarLocation;
        if (_jarLocation == null){
            if (fileExists(jarLocationFile())){
                // Have we previously pointed out the location?
                jarLocation = readFile(jarLocationFile(), true, true).getFirst();
            }else{
                // Guess our default location
                jarLocation = defaultJarFile();
            }
        }else{
            // Already set and unlikely to change while program is still running
            return _jarLocation;
        }
        
        // Is the file there?
        while(true){
            // If we've found it, run with it
            if (fileExists(jarLocation)) break;
            
            // Ask the user for help
            String[] options = {"Locate JAR", "Try again", "Cancel"};
            int choice = DialogFactory.customOption(caller, options,
                    "Could not find the File Searcher JAR file at\n" + jarLocation,
                    "JAR file not found");
            
            if (choice == 1) continue; // Look again
            if(choice == 0){ // User will help us
                jarLocation = DialogFactory.chooseFile(caller, getParentFolder(jarLocation));
                if (jarLocation == null) return null;
                continue;
            }
            return null; // User canceled
        }
        
        // Store the location for later (even if it's our default)
        writeToFile(jarLocation, jarLocationFile());
        _jarLocation = jarLocation;
        return _jarLocation;
    }
    
    
    // Set up the autorun file (don't bother writing it if its contents are already what they need to be)
    public static boolean writeAutorunFile(JFrame caller) throws Exception{
        // Our two-line autorun file
        StringList contents =
                new StringList("java -jar " + StringHandler.addQuotes(getJarLocation(caller) + " seeker"))
                        .add("exit");
        
        // See if the file already exists and has the correct contents
        if (fileExists(autorunFile()) && readFile(autorunFile(), true, true).equals(contents)) return false;
        
        // Write the contents
        writeFile(contents, true, autorunFile());
        return true;
    }
    
    // Run the autorun file
    public static void runAutorunFile() throws Exception{
        runBatchFile(autorunFile());
    }
    
    
    /**
     * Set the slashes in a String to the Windows filepath divider character.
     * Changes '/' characters to '\' characters
     * @param text The String to set the slashes of.
     * @return The provided string with all slashes facing the right direction
     */
    public static String setSlashes(String text){
        return StringHandler.replace(text, "/", "\\", true);
    }
    
}
