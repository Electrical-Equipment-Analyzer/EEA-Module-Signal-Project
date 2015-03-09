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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.openide.util.Exceptions;
import org.openide.windows.IOColorLines;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import tw.edu.sju.ee.eea.utils.io.tools.EEADevice;
import tw.edu.sju.ee.eea.utils.io.tools.EEAException;

/**
 *
 * @author Leo
 */
public class SerialDevice implements EEADevice, Runnable {

    private SerialPort serialPort;
    private PipedInputStream in;
    private PipedOutputStream out;
    private Thread thread;

    public SerialDevice(String device) {
        serialPort = new SerialPort(device);
        try {
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
        try {
            in = new PipedInputStream(1000000);
            out = new PipedOutputStream(in);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void openDevice() throws EEAException {
        try {
            serialPort.openPort();
        } catch (SerialPortException ex) {
            throw new EEAException(ex);
        }
    }

    @Override
    public void closeDevice() throws EEAException {
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            throw new EEAException(ex);
        }
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
        InputOutput io = IOProvider.getDefault().getIO("Sreial", false);
        double v[] = null;
        double x[] = null;
        double y[] = null;
        double z[] = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
        do {
            try {
                String readLine = br.readLine();
                System.out.println(readLine);
                String split[] = readLine.split(":");
                if (split[0].endsWith("V")) {
                    v = sp(split[1]);
                } else if (split[0].endsWith("X")) {
                    x = sp(readLine.split(": ")[1]);
                } else if (split[0].endsWith("Y")) {
                    y = sp(readLine.split(": ")[1]);
                } else if (split[0].endsWith("Z")) {
                    z = sp(readLine.split(": ")[1]);
                }
//            DataPacket packet = new DataPacket(readLine);
//            return packet.getData();
            } catch (IOException ex) {
                Logger.getLogger(TCPDevice.class.getName()).log(Level.SEVERE, null, ex);
                throw new EEAException(ex);
            } finally {
//                try {
//                    sc.close();
//                    ss.close();
//                } catch (IOException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
            }
        } while (v == null || x == null || y == null || z == null);
        String s = "V1:" + v[0] + ", V2:" + v[1] + ", X:" + x[0] + ", Y:" + y[0] + ", Z:" + z[0];
        try {
            IOColorLines.println(io, s, Color.BLACK);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return new double[][]{x, y, z};
    }

    private double[] sp(String s) {
        System.out.println(s);
        String sp[] = s.split(" ");
        double buff[] = new double[sp.length];
        for (int i = 0; i < buff.length; i++) {
            buff[i] = Double.parseDouble(sp[i]);
        }
        return buff;
    }

    @Override
    public void start() throws EEAException {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() throws EEAException {
        thread.stop();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                byte[] readBytes = serialPort.readBytes();
                if (readBytes != null) {
                    out.write(readBytes);
                }
            } catch (SerialPortException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
