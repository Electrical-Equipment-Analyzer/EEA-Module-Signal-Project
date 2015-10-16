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

import java.util.Arrays;
import tw.edu.sju.ee.eea.jni.scope.NIScope;
import tw.edu.sju.ee.eea.jni.scope.NIScopeException;
import tw.edu.sju.ee.eea.utils.io.tools.EEADevice;
import tw.edu.sju.ee.eea.utils.io.tools.EEAException;

/**
 *
 * @author Leo
 */
public class NIDevice implements EEADevice {

    private NIScope niScope;

    public NIDevice() {
    }

    @Override
    public String getDeviceName() {
        return "NI";
    }

    @Override
    public String getDeviceModel() {
        return "5105";
    }

    @Override
    public String getSerialNumber() {
        return "aabbcc";
    }

    @Override
    public void openDevice() throws EEAException {
        try {
            niScope = new NIScope();
            // Open the NI-SCOPE instrument handle
            niScope.init("Dev1", false, false);
        } catch (NIScopeException ex) {
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
        return 2;
    }

    @Override
    public void configure() throws EEAException {
        try {
            niScope.autoSetup();
        } catch (NIScopeException ex) {
            throw new EEAException(ex);
        }
    }

    @Override
    public double[][] read(int length) throws EEAException {
        double[][] data = new double[2][];
        try {
            // Get the actual record length and actual sample rate that will be used
            int actualRecordLength = niScope.actualRecordLength();
            double sampleRate = niScope.sampleRate();

            // Read the data (Initiate the acquisition, and fetch the data)
            double waveform[] = new double[actualRecordLength * 2];
            NIScope.WFMInfo wfmInfo[] = new NIScope.WFMInfo[2];
            niScope.read("0,1", 5, actualRecordLength, waveform, wfmInfo);

            System.out.println("Actual record length: " + actualRecordLength);
            System.out.println("Actual sample rate: " + sampleRate);
            for (int i = 0; i < wfmInfo.length; i++) {
                System.out.println(wfmInfo[i]);
            }

            data[0] = Arrays.copyOfRange(waveform, 0, actualRecordLength);
            data[1] = Arrays.copyOfRange(waveform, actualRecordLength, actualRecordLength * 2);
//            System.out.println(Arrays.toString(Arrays.copyOfRange(waveform, 0, actualRecordLength)));
//            System.out.println(Arrays.toString(Arrays.copyOfRange(waveform, actualRecordLength, actualRecordLength * 2)));
            return data;
        } catch (NIScopeException ex) {
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
