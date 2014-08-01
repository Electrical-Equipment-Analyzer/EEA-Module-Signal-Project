/*
 * Copyright (C) 2014 Leo
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
package tw.edu.sju.ee.eea.module.iepe.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.openide.util.Exceptions;

/**
 *
 * @author Leo
 */
public class IepeProjectProperties {

    private File confFile;
    private Document doc;

    private Device device;

    public IepeProjectProperties(File confFile) {
        this.confFile = confFile;
        read();
        device = new Device(doc.getRootElement());
//        Element root = doc.getRootElement();
//        Element conf = root.element("properties");
//        Element samplerate = conf.element("samplerate");
//        System.out.println(samplerate.getText());
    }

    private IepeProjectProperties(Device device) {
        this.device = device;
    }

    public void read() {
        try {
            doc = new SAXReader().read(confFile);
        } catch (DocumentException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void write() {
        try {
            XMLWriter writer = new XMLWriter(new FileWriter(confFile));
            writer.write(doc);
            writer.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public Document doc() {
        return doc;
    }

    public Device device() {
        return device;
    }

    public class Device {

        private final Element device;
        private static final String DEVICE = "properties";
        private static final String NAME = "name";
        private static final String SAMPLERATE = "samplerate";

        private String deviceName;
        private int sampleRate;

        public Device(Element root) {
            device = root.element(DEVICE);
            read();
        }

        public void read() {
            deviceName = device.elementText(NAME);
            sampleRate = Integer.parseInt(device.elementText(SAMPLERATE));
        }

        public void write() {
            device.element(NAME).setText(deviceName);
            device.element(SAMPLERATE).setText(String.valueOf(sampleRate));
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public int getSampleRate() {
            return sampleRate;
        }

        public void setSampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
        }

    }

}
