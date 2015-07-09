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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.openide.util.Exceptions;
import tw.edu.sju.ee.eea.module.iepe.project.data.AnalyzerRule;

/**
 *
 * @author Leo
 */
public class IepeProjectProperties {

    private File confFile;
    private Document doc;

    private Device device;
    private History history;

    private List<AnalyzerRule> rules;

    public IepeProjectProperties(File confFile) {
        this.confFile = confFile;
        read();
        Element root = doc.getRootElement();
        device = new Device(root);
        history = new History(root);

        Element conf = root.element("analyzer");
        Iterator elementIterator = conf.elementIterator("rule");
        rules = new ArrayList<AnalyzerRule>();
        while (elementIterator.hasNext()) {
            rules.add(new AnalyzerRule(this, (Element) elementIterator.next()));
        }
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

    public History history() {
        return history;
    }

    public List<AnalyzerRule> rules() {
        return rules;
    }

    public class Device {

        private final Element device;
        private static final String DEVICE = "properties";
        private static final String NAME = "name";
        private static final String SAMPLERATE = "samplerate";
        private static final String CHANNELS = "channels";

        private String deviceName;
        private int sampleRate;
        private int channels;

        public Device(Element root) {
            device = root.element(DEVICE);
            read();
        }

        public void read() {
            deviceName = device.elementText(NAME);
            sampleRate = Integer.parseInt(device.elementText(SAMPLERATE));
            channels = Integer.parseInt(device.elementText(CHANNELS));
        }

        public void write() {
            device.element(NAME).setText(deviceName);
            device.element(SAMPLERATE).setText(String.valueOf(sampleRate));
            device.element(CHANNELS).setText(String.valueOf(channels));
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

        public int getChannels() {
            return channels;
        }

        public void setChannels(int channels) {
            this.channels = channels;
        }

    }

    public class History {

        private final Element history;
        private static final String RECORD = "history";
        private static final String PATTERN = "pattern";

        private String pattern;

        public History(Element root) {
            history = root.element(RECORD);
            read();
        }

        public void read() {
            pattern = history.elementText(PATTERN);
        }

        public void write() {
            history.element(PATTERN).setText(pattern);
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

    }

    public AnalyzerRule createRule() {
        Element root = doc.getRootElement();
        Element conf = root.element("analyzer");
        AnalyzerRule analyzerRule = new AnalyzerRule(this, conf.addElement("rule"));
        analyzerRule.initValue();
        rules.add(analyzerRule);
        return analyzerRule;
    }

}
