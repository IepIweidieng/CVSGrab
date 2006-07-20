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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.apache.commons.jrcs.diff.AddDelta;
import org.apache.commons.jrcs.diff.ChangeDelta;
import org.apache.commons.jrcs.diff.DeleteDelta;
import org.apache.commons.jrcs.diff.Delta;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.myers.MyersDiff;

/**
 * Prints in the Unified print format. 
 * 
 * close() must be called on this instance after calling Revision.accept(unifiedPrint) 
 * in order to get the StringBuffer filled with all data.
 * 
 * Sample use: <pre>
 *      String[] orig = getOriginal();
 *      String[] rev = getRevised();
 *      MyersDiff diff = new MyersDiff();
 *      Revision revision = diff.diff(orig, rev);
 *      StringBuffer sb = new StringBuffer();
 *      UnifiedPrint print = new UnifiedPrint(sb, orig, rev);
 *      revision.accept(print);
 *      print.close();
 * </pre>
 * 
 * @author <a href="mailto:ludovicc@users.sourceforge.net">Ludovic Claude</a>
 * @version $Revision$ $Date$
 * @cvsgrab.created on 27 janv. 2004
 */
public class UnifiedPrint extends BasePrint {

    private int nbContextLines = 3;
    private Object[] original;
    private Object[] revision;
    private Stack changeBlocks = new Stack();
    
    /**
     * Constructor for ContextPrint
     * @param sb
     */
    public UnifiedPrint(StringBuffer sb, Object[] original, Object[] revision) {
        super(sb);
        this.original = original;
        this.revision = revision;
    }
    
    public void close() {
    	if (changeBlocks.isEmpty()) {
    	    return;
    	}
        ChangeBlock lastChangeBlock = (ChangeBlock) changeBlocks.peek();
        if (lastChangeBlock != null) {
            lastChangeBlock.fillEndBlock();
        }
        for (Iterator i = changeBlocks.iterator(); i.hasNext();) {
            ChangeBlock changeBlock = (ChangeBlock) i.next();
            changeBlock.print(getStringBuffer());
        }
    }

    /**
     * Gets the nbContextLines.
     * @return the nbContextLines.
     */
    public int getNbContextLines() {
        return nbContextLines;
    }
    
    /**
     * Sets the nbContextLines.
     * @param nbContextLines The nbContextLines to set.
     */
    public void setNbContextLines(int nbContextLines) {
        this.nbContextLines = nbContextLines;
    }
    
    protected void printHeader() {
        if (getFileName() == null) {
            return;
        }
        
        StringBuffer sb = getStringBuffer();
        sb.append("Index: ");
        sb.append(getFileName());
        sb.append(getEOL());

        sb.append("===================================================================");
        sb.append(getEOL());
        
        sb.append("RCS File: ");
        sb.append(getRCSFileName());
        sb.append(getEOL());

        if (getOriginalVersion() != null) {
            sb.append("retrieving revision ");
            sb.append(getOriginalVersion());
            sb.append(getEOL());
        }

        if (getRevisedVersion() != null) {
            sb.append("retrieving revision ");
            sb.append(getRevisedVersion());
            sb.append(getEOL());
        }

        sb.append("diff -u");
        if (getOriginalVersion() != null) {
            sb.append(" -r");
            sb.append(getOriginalVersion());
        }
        if (getRevisedVersion() != null) {
            sb.append(" -r");
            sb.append(getRevisedVersion());
        }
        sb.append(" ");
        String shortFileName = getFileName();
        int pos = shortFileName.lastIndexOf('/');
        if (pos >= 0) {
            shortFileName = shortFileName.substring(pos + 1);
        }
        sb.append(shortFileName);
        sb.append(getEOL());

        DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss -0000", Locale.US);
        sb.append("--- ");
        sb.append(getFileName());
        if (getOriginalModifDate() != null) {
            sb.append('\t');
            sb.append(df.format(getOriginalModifDate()));
            if (getOriginalVersion() != null) {
                sb.append('\t');
                sb.append(getOriginalVersion());
            }
        }
        sb.append(getEOL());

        sb.append("+++ ");
        sb.append(getFileName());
        if (getRevisedModifDate() != null) {
            sb.append('\t');
            sb.append(df.format(getRevisedModifDate()));
            if (getRevisedVersion() != null) {
                sb.append('\t');
                sb.append(getRevisedVersion());
            }
        }
        sb.append(getEOL());
    }
    
    /** 
     * {@inheritDoc}
     * @param delta
     */
    public void visit(AddDelta delta) {
        super.visit(delta);
        ChangeBlock lastChangeBlock = getLastChangeBlock(delta);
        lastChangeBlock.addChange(delta);
    }

    /** 
     * {@inheritDoc}
     * @param delta
     */
    public void visit(ChangeDelta delta) {
        super.visit(delta);
        ChangeBlock lastChangeBlock = getLastChangeBlock(delta);
        lastChangeBlock.addChange(delta);
    }

    /** 
     * {@inheritDoc}
     * @param delta
     */
    public void visit(DeleteDelta delta) {
        super.visit(delta);
        ChangeBlock lastChangeBlock = getLastChangeBlock(delta);
        lastChangeBlock.addChange(delta);
    }

    /**
     * Gets the original.
     * @return the original.
     */
    protected Object[] getOriginal() {
        return original;
    }

    /**
     * Gets the revision.
     * @return the revision.
     */
    protected Object[] getRevision() {
        return revision;
    }
    
    private ChangeBlock getLastChangeBlock(Delta delta) {
        ChangeBlock lastChangeBlock = null;
        if (!changeBlocks.isEmpty()) {
            lastChangeBlock = (ChangeBlock) changeBlocks.peek();
        }
        if (lastChangeBlock == null || lastChangeBlock.getLastChangedLine() < delta.getOriginal().anchor() - getNbContextLines() * 2) {
            if (lastChangeBlock != null) {
                lastChangeBlock.fillEndBlock();
            }
            lastChangeBlock = new ChangeBlock();
            changeBlocks.add(lastChangeBlock);
        }
        return lastChangeBlock;
    }

    private class ChangeBlock {
        private List deltas = new ArrayList();
        private List changes = new ArrayList();
        private int origNbLines = 0;
        private int revNbLines = 0;
        private int startOrig = 0;
        private int startRev = 0;
        
        public int getLastChangedLine() {
            Delta delta = (Delta) deltas.get(deltas.size() - 1);
            return delta.getOriginal().last();
        }
        
        public void addChange(AddDelta delta) {
            initChangesBefore(delta);
            deltas.add(delta);
            for (int l = delta.getRevised().first(); l <= delta.getRevised().last(); l++) {
                changes.add("+" + getRevision()[l]);
                revNbLines++;
            }
        }

        public void addChange(DeleteDelta delta) {
            initChangesBefore(delta);
            deltas.add(delta);
            for (int l = delta.getOriginal().first(); l <= delta.getOriginal().last(); l++) {
                changes.add("-" + getOriginal()[l]);
                origNbLines++;
            }
        }

        public void addChange(ChangeDelta delta) {
            initChangesBefore(delta);
            deltas.add(delta);
            for (int l = delta.getOriginal().first(); l <= delta.getOriginal().last(); l++) {
                changes.add("-" + getOriginal()[l]);
                origNbLines++;
            }
            for (int l = delta.getRevised().first(); l <= delta.getRevised().last(); l++) {
                changes.add("+" + getRevision()[l]);
                revNbLines++;
            }
        }
        
        public void print(StringBuffer sb) {
            sb.append("@@ -");
            sb.append(startOrig);
            sb.append(",");
            sb.append(origNbLines);
            sb.append(" +");
            sb.append(startRev);
            sb.append(",");
            sb.append(revNbLines);
            sb.append(" @@");
            sb.append(getEOL());
            for (Iterator i = changes.iterator(); i.hasNext();) {
                String line = (String) i.next();
                sb.append(line);
                sb.append(getEOL());
            }
        }
        
        public void fillEndBlock() {
            Delta lastDelta = (Delta) deltas.get(deltas.size() - 1);
            int l = lastDelta.getOriginal().last();
            for (int i = l + 1; i <= l + getNbContextLines() && i < getOriginal().length; i++) {
                changes.add(" " + getOriginal()[i]);
                origNbLines++;
                revNbLines++;
            }
        }
         
        /**
         * @param delta
         */
        private void initChangesBefore(Delta delta) {
            int anchor = delta.getOriginal().anchor();
            int lastChange = 0;
            boolean firstChange = changes.isEmpty();
            int border = firstChange ? getNbContextLines() : getNbContextLines() * 2;
            if (firstChange) {
                startOrig = delta.getOriginal().rcsfrom();
                startRev = delta.getRevised().rcsfrom();
            } else {
                Delta previousDelta = (Delta) deltas.get(deltas.size()-1);
                lastChange = previousDelta.getOriginal().last();
            } 
            
            for (int i = border; i > 0; i--) {
                if (anchor - i > lastChange && anchor >= i) {
                    changes.add(" " + getOriginal()[anchor - i]);
                    origNbLines++;
                    revNbLines++;
                    if (firstChange) {
                        startOrig--;
                        startRev--;
                    }
                }
            }
        }
    }
    
    private static final String[] loadFile(String name) throws IOException
    {
        BufferedReader data = new BufferedReader(new FileReader(name));
        List lines = new ArrayList();
        String s;
        while ((s = data.readLine()) != null)
        {
            lines.add(s);
        }
        return (String[])lines.toArray(new String[lines.size()]);
    }

    
    public static void main(String[] args) {
        try {
        String[] s1 = loadFile("c:/dev/CVSGrabTask.1.6.java");
        String[] s2 = loadFile("c:/dev/CVSGrabTask.1.7.java");
        MyersDiff diff = new MyersDiff();
        Revision revision = diff.diff(s1, s2);
        StringBuffer sb = new StringBuffer();
        UnifiedPrint print = new UnifiedPrint(sb, s1, s2);
        print.setFileName("src/java/net/sourceforge/cvsgrab/CVSGrabTask.java");
        print.setRCSFileName("/cvsroot/cvsgrab/cvsgrab/src/java/net/sourceforge/cvsgrab/CVSGrabTask.java,v");
        DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.US);
        print.setOriginalModifDate(df.parse("31 Oct 2003 00:26:54"));
        print.setOriginalVersion("1.6");
        print.setRevisedModifDate(df.parse("31 Jan 2004 17:37:47"));
        revision.accept(print);
        print.close();
        System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
