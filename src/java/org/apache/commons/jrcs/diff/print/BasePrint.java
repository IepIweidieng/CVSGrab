/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.jrcs.diff.print;

import java.util.Date;

import org.apache.commons.jrcs.diff.AddDelta;
import org.apache.commons.jrcs.diff.ChangeDelta;
import org.apache.commons.jrcs.diff.DeleteDelta;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.RevisionVisitor;

/**
 * Base class for printing revisions in various formats.
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @created on 17 janv. 2004
 */
public class BasePrint implements RevisionVisitor {

    private StringBuffer sb;
    private String command;
    private String fileName;
    private String rcsFileName;
    private String originalVersion;
    private String revisedVersion;
    private Date originalModifDate; 
    private Date revisedModifDate; 
    private String EOL = Diff.RCS_EOL;
    
    /**
     * Constructor for BasePrint
     * 
     */
    public BasePrint(StringBuffer sb) {
        super();
        this.sb = sb;
    }

    /**
     * Gets the command.
     * @return the command.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command.
     * @param command The command to set.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Gets the fileName.
     * @return the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the fileName.
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the rcsFileName.
     * @return the rcsFileName.
     */
    public String getRCSFileName() {
        return rcsFileName;
    }
    
    /**
     * Sets the rcsFileName.
     * @param rcsFileName The rcsFileName to set.
     */
    public void setRCSFileName(String rcsFileName) {
        this.rcsFileName = rcsFileName;
    }
    
    /**
     * Gets the originalVersion.
     * @return the originalVersion.
     */
    public String getOriginalVersion() {
        return originalVersion;
    }

    /**
     * Sets the originalVersion.
     * @param originalVersion The originalVersion to set.
     */
    public void setOriginalVersion(String originalVersion) {
        this.originalVersion = originalVersion;
    }

    /**
     * Gets the revisedVersion.
     * @return the revisedVersion.
     */
    public String getRevisedVersion() {
        return revisedVersion;
    }

    /**
     * Sets the revisedVersion.
     * @param revisedVersion The revisedVersion to set.
     */
    public void setRevisedVersion(String revisedVersion) {
        this.revisedVersion = revisedVersion;
    }

    /**
     * Gets the originalModifDate.
     * @return the originalModifDate.
     */
    public Date getOriginalModifDate() {
        return originalModifDate;
    }
    
    /**
     * Sets the originalModifDate.
     * @param originalModifDate The originalModifDate to set.
     */
    public void setOriginalModifDate(Date originalModifDate) {
        this.originalModifDate = originalModifDate;
    }
    
    /**
     * Gets the revisedModifDate.
     * @return the revisedModifDate.
     */
    public Date getRevisedModifDate() {
        return revisedModifDate;
    }
    
    /**
     * Sets the revisedModifDate.
     * @param revisedModifDate The revisedModifDate to set.
     */
    public void setRevisedModifDate(Date revisedModifDate) {
        this.revisedModifDate = revisedModifDate;
    }
    
    /**
     * Gets the EOL.
     * @return the EOL.
     */
    public String getEOL() {
        return EOL;
    }

    /**
     * Sets the EOL.
     * @param eol The EOL to set.
     */
    public void setEOL(String eol) {
        EOL = eol;
    }

    /** 
     * {@inheritDoc}
     * @param revision
     */
    public void visit(Revision revision) {
        printHeader();
    }

    /** 
     * {@inheritDoc}
     * @param delta
     */
    public void visit(DeleteDelta delta) {
    }

    /** 
     * {@inheritDoc}
     * @param delta
     */
    public void visit(ChangeDelta delta) {
    }

    /** 
     * {@inheritDoc}
     * @param delta
     */
    public void visit(AddDelta delta) {
    }
    
    protected void printHeader() {
    }
    
    protected StringBuffer getStringBuffer() {
        return sb;
    }

}
