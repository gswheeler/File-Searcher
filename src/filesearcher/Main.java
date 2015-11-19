/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesearcher;

import filesearcher.data.DataFactory;
import wheeler.generic.data.DialogFactory;
import wheeler.generic.data.StringHandler;
import wheeler.generic.logging.Logger;

/**
 *
 * @author Greg
 */
public class Main {
    
    /**The function called when the program starts
     * @param args Empty array: initial call, open the interface. ["seeker"]: Perform a search
     */
    public static void main(String[] args){
        // First call from outside, open the interface
        if(args.length == 0){
            // Use the function provided by NetBeans to set the form's look and feel
            filesearcher.forms.Main.main(args);
            return;
        }
        
        // Called by the main interface to perform a search
        if((args.length == 1) && (args[0].equals("seeker"))){
            try{
                DataFactory.runSearch();
            }
            catch(Exception e){
                Logger.print("A serious error occurred performing the search.");
                Logger.print(e, 1, 0);
                DialogFactory.pressEnterToContinue("Press Enter to continue...");
            }
            return;
        }
        
        // No idea
        Logger.print(
                "The program was called with arguments it didn't recognize:\n  "
                        + StringHandler.concatStringArray(args, " ")
            );
    }
    
}
