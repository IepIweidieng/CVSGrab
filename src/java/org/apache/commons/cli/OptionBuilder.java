/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
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
package org.apache.commons.cli;

/**
 * <p>OptionBuilder allows the user to create Options using descriptive
 * methods.</p>
 * <p>Details on the Builder pattern can be found at
 * <a href="http://c2.com/cgi-bin/wiki?BuilderPattern">
 * http://c2.com/cgi-bin/wiki?BuilderPattern</a>.</p>
 *
 * @author John Keyes (john at integralsource.com)
 * @since 1.0
 */
public class OptionBuilder {

    /** long option */
    private String longopt;

    /** option description */
    private String description;

    /** argument name */
    private String argName;

    /** is required? */
    private boolean required;

    /** the number of arguments */
    private int numberOfArgs = Option.UNINITIALIZED;

    /** option type */
    private Object type;

    /** option can have an optional argument value */
    private boolean optionalArg;

    /** value separator for argument value */
    private char valuesep;

    /**
     * Constructor for OptionBuilder
     */
    public OptionBuilder()
    {
        // do nothing
    }

    /**
     * <p>The next Option created will have the following long option value.</p>
     *
     * @param longopt the long option value
     * @return the OptionBuilder instance
     */
    public OptionBuilder withLongOpt(String longopt)
    {
        this.longopt = longopt;

        return this;
    }

    /**
     * <p>The next Option created will require an argument value.</p>
     *
     * @return the OptionBuilder instance
     */
    public OptionBuilder hasArg()
    {
        this.numberOfArgs = 1;

        return this;
    }

    /**
     * <p>The next Option created will require an argument value if
     * <code>hasArg</code> is true.</p>
     *
     * @param hasArg if true then the Option has an argument value
     * @return the OptionBuilder instance
     */
    public OptionBuilder hasArg(boolean hasArg)
    {
        this.numberOfArgs = (hasArg == true) ? 1 : Option.UNINITIALIZED;

        return this;
    }

    /**
     * <p>The next Option created will have the specified argument value
     * name.</p>
     *
     * @param name the name for the argument value
     * @return the OptionBuilder instance
     */
    public OptionBuilder withArgName(String name)
    {
        this.argName = name;

        return this;
    }

    /**
     * <p>The next Option created will be required.</p>
     *
     * @return the OptionBuilder instance
     */
    public OptionBuilder isRequired()
    {
        this.required = true;

        return this;
    }

    /**
     * <p>The next Option created uses <code>sep</code> as a means to
     * separate argument values.</p>
     *
     * <b>Example:</b>
     * <pre>
     * Option opt = OptionBuilder.withValueSeparator(':')
     *                           .create('D');
     *
     * CommandLine line = parser.parse(args);
     * String propertyName = opt.getValue(0);
     * String propertyValue = opt.getValue(1);
     * </pre>
     *
     * @param sep The value separator to be used for the argument values.
     *
     * @return the OptionBuilder instance
     */
    public OptionBuilder withValueSeparator(char sep)
    {
        this.valuesep = sep;

        return this;
    }

    /**
     * <p>The next Option created uses '<code>=</code>' as a means to
     * separate argument values.</p>
     *
     * <b>Example:</b>
     * <pre>
     * Option opt = OptionBuilder.withValueSeparator()
     *                           .create('D');
     *
     * CommandLine line = parser.parse(args);
     * String propertyName = opt.getValue(0);
     * String propertyValue = opt.getValue(1);
     * </pre>
     *
     * @return the OptionBuilder instance
     */
    public OptionBuilder withValueSeparator()
    {
        this.valuesep = '=';

        return this;
    }

    /**
     * <p>The next Option created will be required if <code>required</code>
     * is true.</p>
     *
     * @param required if true then the Option is required
     * @return the OptionBuilder instance
     */
    public OptionBuilder isRequired(boolean required)
    {
        this.required = required;

        return this;
    }

    /**
     * <p>The next Option created can have unlimited argument values.</p>
     *
     * @return the OptionBuilder instance
     */
    public OptionBuilder hasArgs()
    {
        this.numberOfArgs = Option.UNLIMITED_VALUES;

        return this;
    }

    /**
     * <p>The next Option created can have <code>num</code>
     * argument values.</p>
     *
     * @param num the number of args that the option can have
     * @return the OptionBuilder instance
     */
    public OptionBuilder hasArgs(int num)
    {
        this.numberOfArgs = num;

        return this;
    }

    /**
     * <p>The next Option can have an optional argument.</p>
     *
     * @return the OptionBuilder instance
     */
    public OptionBuilder hasOptionalArg()
    {
        this.numberOfArgs = 1;
        this.optionalArg = true;

        return this;
    }

    /**
     * <p>The next Option can have an unlimited number of
     * optional arguments.</p>
     *
     * @return the OptionBuilder instance
     */
    public OptionBuilder hasOptionalArgs()
    {
        this.numberOfArgs = Option.UNLIMITED_VALUES;
        this.optionalArg = true;

        return this;
    }

    /**
     * <p>The next Option can have the specified number of
     * optional arguments.</p>
     *
     * @param numArgs - the maximum number of optional arguments
     * the next Option created can have.
     * @return the OptionBuilder instance
     */
    public OptionBuilder hasOptionalArgs(int numArgs)
    {
        this.numberOfArgs = numArgs;
        this.optionalArg = true;

        return this;
    }

    /**
     * <p>The next Option created will have a value that will be an instance
     * of <code>type</code>.</p>
     *
     * @param type the type of the Options argument value
     * @return the OptionBuilder instance
     */
    public OptionBuilder withType(Object type)
    {
        this.type = type;

        return this;
    }

    /**
     * <p>The next Option created will have the specified description</p>
     *
     * @param description a description of the Option's purpose
     * @return the OptionBuilder instance
     */
    public OptionBuilder withDescription(String description)
    {
        this.description = description;

        return this;
    }

    /**
     * <p>Create an Option using the current settings and with
     * the specified Option <code>char</code>.</p>
     *
     * @param opt the character representation of the Option
     * @return the Option instance
     * @throws IllegalArgumentException if <code>opt</code> is not
     * a valid character.  See Option.
     */
    public Option create(char opt)
                         throws IllegalArgumentException
    {
        return create(String.valueOf(opt));
    }

    /**
     * <p>Create an Option using the current settings</p>
     *
     * @return the Option instance
     * @throws IllegalArgumentException if <code>longOpt</code> has
     * not been set.
     */
    public Option create()
                         throws IllegalArgumentException
    {
        if (longopt == null)
        {
            throw new IllegalArgumentException("must specify longopt");
        }

        return create(null);
    }

    /**
     * <p>Create an Option using the current settings and with
     * the specified Option <code>char</code>.</p>
     *
     * @param opt the <code>java.lang.String</code> representation
     * of the Option
     * @return the Option instance
     * @throws IllegalArgumentException if <code>opt</code> is not
     * a valid character.  See Option.
     */
    public Option create(String opt)
                         throws IllegalArgumentException
    {
        // create the option
        Option option = new Option(opt, description);


        // set the option properties
        option.setLongOpt(longopt);
        option.setRequired(required);
        option.setOptionalArg(optionalArg);
        option.setArgs(numberOfArgs);
        option.setType(type);
        option.setValueSeparator(valuesep);
        option.setArgName(argName);

        // return the Option instance
        return option;
    }
}