/*
 * CVSGrab
 * Author: Shinobu Kawai (shinobukawai@users.sourceforge.net)
 * Distributable under BSD license.
 * See terms of license at gnu.org.
 */
package net.sourceforge.cvsgrab.web;

import net.sourceforge.cvsgrab.CVSGrab;


/**
 * Support for FishEye 1.2 interfaces to a cvs repository
 *
 * @author <a href="shinobukawai@users.sourceforge.net">Shinobu Kawai</a>
 * @date June 7, 2006
 */
public class FishEye1_2Interface extends FishEye1_0Interface {

    public FishEye1_2Interface(CVSGrab grabber) {
        super(grabber);

        setWebInterfaceType("browse");
    }

}
