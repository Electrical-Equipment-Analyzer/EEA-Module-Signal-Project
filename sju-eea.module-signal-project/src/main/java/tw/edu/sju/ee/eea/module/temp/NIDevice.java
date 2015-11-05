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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import tw.edu.sju.ee.eea.jni.modinst.NIModinstUtils;
import tw.edu.sju.ee.eea.jni.scope.NIScope;
import tw.edu.sju.ee.eea.jni.scope.NIScopeException;
import tw.edu.sju.ee.eea.utils.io.tools.EEADevice;
import tw.edu.sju.ee.eea.utils.io.tools.EEAException;

/**
 *
 * @author Leo
 */
public class NIDevice implements EEADevice {

    private static final String DRIVER = "niScope";

//    private final List<Property> property = new ArrayList();
    private NIScope niScope;

    private NIModinstUtils.Device device;
    private int samplerate;

    public void setDevice(NIModinstUtils.Device device) {
        this.device = device;
    }

    public NIModinstUtils.Device getDevice() {
        return device;
    }

    public String getDeviceName() {
        if (device == null) {
            return null;
        }
        return device.getDeviceName();
    }

    public String getDeviceModel() {
        if (device == null) {
            return null;
        }
        return device.getDeviceModel();
    }

    public String getSerialNumber() {
        if (device == null) {
            return null;
        }
        return device.getSerialNumber();
    }

    public void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    public int getSamplerate() {
        return samplerate;
    }

    public NIDevice() {

        triggerList.add(new NIScope.Trigger.Immediate());
        triggerList.add(new NIScope.Trigger.Edge());
    }

    @Override
    public List<Sheet.Set> getProperties(PropertyChangeListener listener) {
        System.out.println("get p");
        List<Sheet.Set> properties = new ArrayList<Sheet.Set>();
        properties.add(setDevice(listener));
        properties.add(setTrigger(listener));
        return properties;
    }

    private int[] intValues(List list) {
        int[] val = new int[list.size()];
        for (int i = 0; i < val.length; i++) {
            val[i] = i;
        }
        return val;
    }

    private String[] stringKeys(List list) {
        String[] key = new String[list.size()];
        for (int i = 0; i < key.length; i++) {
            key[i] = list.get(i).toString();
        }
        return key;
    }

    List<NIModinstUtils.Device> list = NIModinstUtils.list(DRIVER);

    @NbBundle.Messages({
        "LBL_title=Device Properties",
        "LBL_name=Name",
        "LBL_model=Model",
        "LBL_serialnumber=Serial Number",
        "LBL_samplerate=Samplerate"
    })
    private Sheet.Set setDevice(PropertyChangeListener listener) {
        Sheet.Set set = new Sheet.Set();
        set.setName(Bundle.LBL_title());
        Node.Property p = new PropertySupport.ReadWrite<Integer>("nn", Integer.class, "device", "sd") {

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return device == null ? 0 : list.indexOf(device);
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                System.out.println(val);
                device = list.get(val);
                listener.propertyChange(new PropertyChangeEvent(this, this.getName(), val, val));
            }
        };
        p.setValue("intValues", intValues(list));
        p.setValue("stringKeys", stringKeys(list));
        p.getPropertyEditor().addPropertyChangeListener(listener);
        set.put(p);
        set.put(new PropertySupport.ReadOnly(
                Bundle.LBL_name(),
                String.class,
                Bundle.LBL_name(),
                Bundle.LBL_name()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return getDeviceName();
                    }
                });
        set.put(new PropertySupport.ReadOnly(
                Bundle.LBL_model(),
                String.class,
                Bundle.LBL_model(),
                Bundle.LBL_model()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return getDeviceModel();
                    }
                });
        set.put(new PropertySupport.ReadOnly(
                Bundle.LBL_serialnumber(),
                String.class,
                Bundle.LBL_serialnumber(),
                Bundle.LBL_serialnumber()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return getSerialNumber();
                    }
                });
        set.put(new PropertySupport.ReadWrite<Integer>(
                Bundle.LBL_samplerate(),
                Integer.class,
                Bundle.LBL_samplerate(),
                Bundle.LBL_samplerate()) {
                    @Override
                    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                        return getSamplerate();
                    }

                    @Override
                    public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                        setSamplerate(val);
                    }

                });
        return set;
    }

    List<NIScope.Trigger> triggerList = new ArrayList();

    private Sheet.Set setTrigger(PropertyChangeListener listener) {
        Sheet.Set set = new Sheet.Set();
        set.setName("Trigger");

        PropertySupport triggerProperty = new PropertySupport.ReadWrite<Integer>("Trigger", Integer.class, "Trigger", "trigger") {

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return trigger == null ? 0 : triggerList.indexOf(trigger);
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                System.out.println(val);
                trigger = triggerList.get(val);
                listener.propertyChange(new PropertyChangeEvent(this, this.getName(), val, val));
            }
        };
        triggerProperty.setValue("intValues", intValues(triggerList));
        triggerProperty.setValue("stringKeys", stringKeys(triggerList));
        set.put(triggerProperty);
        if (trigger != null) {
            Field[] fields = trigger.getClass().getFields();
            System.out.println(Arrays.toString(fields));
            for (Field field : fields) {
                System.out.println(field.getType());
                set.put(new PropertySupport.ReadWrite(
                        field.getName(),
                        field.getType(),
                        field.getName(),
                        field.getName()) {
                            @Override
                            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                                return field.get(trigger).toString();
                            }

                            @Override
                            public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                                field.set(trigger, val);
                            }
                        });
            }
        }
        return set;
    }

    @Override
    public void openDevice() throws EEAException {
        System.out.println("open");
        try {
            niScope = new NIScope();
            // Open the NI-SCOPE instrument handle
            niScope.init(device.getDeviceName(), false, false);
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

    String channelList = "0,1";
    NIScope.Trigger trigger;

    public NIScope.Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(NIScope.Trigger trigger) {
        this.trigger = trigger;
        Field[] fields = trigger.getClass().getFields();
        System.out.println(Arrays.toString(fields));
    }

    @Override
    public void configure() throws EEAException {
        try {
//            niScope.autoSetup();
            // Configure the acquisition type
            niScope.configureAcquisition(NIScope.VAL_NORMAL);

            // Configure the vertical parameters
            niScope.configureVertical(channelList, 10, 0, NIScope.VAL_DC, 1, true);

            // Configure the channel characteristics
            niScope.configureChanCharacteristics(channelList, NIScope.VAL_1_MEG_OHM, 0);

            // Configure the horizontal parameters
            niScope.configureHorizontalTiming(1000000, 1024, 50.0, 1, true);

            niScope.setAttributeViBoolean(channelList, NIScope.ATTR_ENABLE_TIME_INTERLEAVED_SAMPLING, false);

//            niScope.configureTriggerImmediate();
            niScope.configureTrigger(trigger);

        } catch (NIScopeException ex) {
            throw new EEAException(ex);
        }
    }

    @Override
    public double[][] read(int length) throws EEAException {
        double[][] data = new double[2][];
        try {
            // Initiate the acquisition
            niScope.initiateAcquisition();
            // Get the actual record length and actual sample rate that will be used
            int actualRecordLength = niScope.actualRecordLength();
            double sampleRate = niScope.sampleRate();

            // Read the data (Initiate the acquisition, and fetch the data)
            double waveform[] = new double[actualRecordLength * 2];
            NIScope.WFMInfo wfmInfo[] = new NIScope.WFMInfo[2];
            niScope.fetch("0,1", 5, actualRecordLength, waveform, wfmInfo);

            System.out.println(device);
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
