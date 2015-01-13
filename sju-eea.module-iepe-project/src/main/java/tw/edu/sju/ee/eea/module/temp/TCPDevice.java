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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TCPDevice implements EEADevice {

    private int port;
//    private ServerSocket ss;
//    private Socket sc;

    public TCPDevice(int port) {
        this.port = port;
    }

    @Override
    public void openDevice() throws EEAException {
//        try {
//            ss = new ServerSocket(port);
//        } catch (IOException ex) {
//            Logger.getLogger(TCPDevice.class.getName()).log(Level.SEVERE, null, ex);
//            throw new EEAException(ex);
//        }
    }

    @Override
    public void closeDevice() throws EEAException {
//        try {
//            if (ss != null) {
//                ss.close();
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(TCPDevice.class.getName()).log(Level.SEVERE, null, ex);
//            throw new EEAException(ex);
//        }
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
//        System.out.println("===" + length);
//        return new double[][]{
//            gen(100, 3, 200, 32000),
//            gen(100, 3, 500, 32000),
//            gen(100, 3, 800, 32000)};
        ServerSocket ss = null;
        Socket sc = null;
        try {
            ss = new ServerSocket(port);
            sc = ss.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            String readLine = br.readLine();
//            do {
//                readLine = br.readLine();
//            } while (readLine == null || readLine.length() < 100);
            DataPacket packet = new DataPacket(readLine);
            return packet.getData();
        } catch (IOException ex) {
            Logger.getLogger(TCPDevice.class.getName()).log(Level.SEVERE, null, ex);
            throw new EEAException(ex);
        } finally {
            try {
                sc.close();
                ss.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void start() throws EEAException {
//        try {
//            sc = ss.accept();
//        } catch (IOException ex) {
//            Logger.getLogger(TCPDevice.class.getName()).log(Level.SEVERE, null, ex);
//            throw new EEAException(ex);
//        }
    }

    @Override
    public void stop() throws EEAException {
//        try {
//            if (sc != null) {
//                sc.close();
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(TCPDevice.class.getName()).log(Level.SEVERE, null, ex);
//            throw new EEAException(ex);
//        }
    }

    private class DataPacket {

        private static final int _HEAD = 2;
        private static final int _LENGTH = 100;

        private final String[] _TXT = new String[]{"Subsidence:", "Crevasse:"};

        private double x[];
        private double y[];
        private double z[];

        public DataPacket(String line) {
            StringTokenizer st = new StringTokenizer(line);
//            System.out.println(st.countTokens());
            InputOutput io = IOProvider.getDefault().getIO("TCP", false);
            for (int i = 0; st.hasMoreTokens() && i < 2; i++) {
                try {
                    IOColorLines.println(io, _TXT[i] + st.nextToken(), Color.BLACK);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
//            System.out.println("==========");
            x = token(st);
            y = token(st);
            z = token(st);
//            System.out.println(Arrays.toString(x));
//            System.out.println(Arrays.toString(y));
//            System.out.println(Arrays.toString(z));
        }

        private double[] token(StringTokenizer st) {
            double buff[] = new double[_LENGTH];
            for (int i = 0; st.hasMoreTokens() && i < buff.length; i++) {
                buff[i] = Double.parseDouble(st.nextToken());
            }
            return buff;
        }

        public double[][] getData() {
            return new double[][]{x, y, z};
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        DecimalFormat format = new DecimalFormat("0.##");
        double x[] = gen(100, 3, 200, 32000);
        double y[] = gen(100, 3, 500, 32000);
        double z[] = gen(100, 3, 800, 32000);
//        for (int i = 0; i < x.length; i++) {
//            System.out.print(format.format(x[i]) + " ");
//        }
        System.out.println(Arrays.toString(x));
        PrintStream ps = new PrintStream(new File("sample.txt"));
        for (int i = 0; i < x.length; i++) {
            ps.print(format.format(x[i]) + " ");
        }
        for (int i = 0; i < y.length; i++) {
            ps.print(format.format(y[i]) + " ");
        }
        for (int i = 0; i < z.length; i++) {
            ps.print(format.format(z[i]) + " ");
        }
        ps.flush();
        ps.close();
    }

    private static double[] gen(int length, double voltage, int frequency, int rate) {
        double gen[] = new double[length];
        for (int x = 0; x < gen.length; x++) {
            gen[x] = voltage * Math.sin(Math.PI * 2 * x * frequency / rate);
        }
        return gen;
    }

}
