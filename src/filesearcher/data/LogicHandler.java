/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesearcher.data;

import wheeler.generic.data.StringHandler;

/**
 *
 * @author Greg
 */
public class LogicHandler extends wheeler.generic.data.LogicHandler{
    
    /** An array of strings that indicate that a string is definitely a regular expression */
    protected static String[] definiteRegular = {".*"};
    /** An array of strings that indicate that a string might be a regular expression */
    protected static String[] maybeRegular = {".", "*", "+", "|", "(", ")", "[", "]", "\\"};
    
    /**
     * Check the likelihood of a string being a regular expression.
     * @param str The string being checked.
     * @return 1 if it very likely is a regular expression,
     *         -1 if it very likely isn't a regular expression,
     *         0 if it could either be or not be a regular expression.
     */
    public static int isStringARegularExpression(String str){
        
        // Check for definites
        for(String chk : definiteRegular){
            if (StringHandler.contains(str, chk, true)) return 1;
        }
        // Check for maybes
        for(String chk : maybeRegular){
            if (StringHandler.contains(str, chk, true)) return 0;
        }
        // It probably isn't
        return -1;
        
    }
    
}
