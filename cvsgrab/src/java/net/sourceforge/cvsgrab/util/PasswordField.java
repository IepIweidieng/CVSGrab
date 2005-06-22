/*
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions are met:
 *  - Redistribution of source code must retain the above copyright notice, this list 
 *    of conditions and the following disclaimer.
 *  - Redistribution in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY 
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR 
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND 
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS 
 * A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. 
 * IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT 
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR 
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, 
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended for 
 * use in the design, construction, operation or maintenance of any nuclear facility. 
 */

package net.sourceforge.cvsgrab.util;

import java.io.*;

/**
 * This class prompts the user for a password and attempts to mask input with ""
 * 
 * @author <a href=mailto:"qmahmoud@javacourses.com">Qusay H. Mahmoud</a>
 */

public class PasswordField {

    /**
     * Gets the passowrd while masking the user input.
     * 
	 * @param prompt The prompt to display to the user.
	 * @return The password as entered by the user.
	 */
    public String getPassword(String prompt) throws IOException {
        // password holder
        String password = "";
        MaskingThread maskingthread = new MaskingThread(prompt);
        Thread thread = new Thread(maskingthread);
        thread.start();
        // block until enter is pressed
        while (true) {
            char c = (char) System.in.read();
            // assume enter pressed, stop masking
            maskingthread.stopMasking();

            if (c == '\r') {
                c = (char) System.in.read();
                if (c == '\n') {
                    break;
                } else {
                    continue;
                }
            } else if (c == '\n') {
                break;
            } else {
                // store the password
                password += c;
            }
        }
        return password;
    }
}