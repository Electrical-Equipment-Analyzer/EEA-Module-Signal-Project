/*
 * Copyright (C) 2015 Leo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package tw.edu.sju.ee.eea.module.temp;

import com.codeminders.hidapi.HIDManager;
import java.io.IOException;
import java.util.ArrayList;
import org.openide.util.Exceptions;
import tw.edu.ntust.ee.ee305.monitoring.motor.embedded.MDESDrive;
import tw.edu.ntust.ee.ee305.monitoring.motor.embedded.MDESFile;
import tw.edu.sju.ee.eea.utils.io.tools.EEADevice;
import tw.edu.sju.ee.eea.utils.io.tools.EEAException;

/**
 *
 * @author Leo
 */
public class MDESDevice implements EEADevice {

    MDESDrive mdes;

    public MDESDevice() {
    }

    @Override
    public void openDevice() throws EEAException {
        try {
            mdes = new MDESDrive(HIDManager.getInstance());
        } catch (IOException ex) {
            throw new EEAException(ex);
        }
    }

    @Override
    public void closeDevice() throws EEAException {
    }

    @Override
    public int getDeviceId() throws EEAException {
        return 0;
    }

    @Override
    public int getChannelLength() throws EEAException {
        return 3;
    }

    @Override
    public void configure() throws EEAException {
    }

    @Override
    public double[][] read(int length) throws EEAException {
        try {
            ArrayList<Double>[] readAcc = mdes.readAcc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            MDESFile file = new MDESFile(readAcc);
            return file.getData();
        } catch (IOException ex) {
            throw new EEAException(ex);
        }
    }

    @Override
    public void start() throws EEAException {
    }

    @Override
    public void stop() throws EEAException {
    }

}
