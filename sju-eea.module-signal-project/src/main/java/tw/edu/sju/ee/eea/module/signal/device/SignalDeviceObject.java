/*
 * Copyright (c) 2015, National Taiwan University of Science and Technology,
 * Department of Electrical Engineering EE-305. All rights reserved.
 */
package tw.edu.sju.ee.eea.module.signal.device;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import tw.edu.sju.ee.eea.core.math.SineSimulator;
import tw.edu.sju.ee.eea.module.iepe.channel.ChannelList;
import tw.edu.sju.ee.eea.module.signal.oscillogram.ZoomRenderer;
import tw.edu.sju.ee.eea.module.signal.temp.Channel;
import tw.edu.sju.ee.eea.module.signal.temp.SineInputStream;

/**
 *
 * @author 薛聿明
 */
public class SignalDeviceObject extends AbstractNode implements tw.edu.sju.ee.eea.module.signal.io.ChannelList, NavigatorLookupHint, Serializable {

    public static final String MIMETYPE = "application/device";
    private TopComponent tc;
    private ArrayList<Channel> channels = new ArrayList<Channel>();

    public SignalDeviceObject(Project project) {
        super(Children.LEAF);
        setDisplayName("NI1234");
        setIconBaseWithExtension("tw/edu/sju/ee/eea/module/iepe/project/iepe_project.png");
        
        channels.add(new Channel("A", channels.size(), new ZoomRenderer(),
                new SineInputStream(new SineSimulator(16384, 200, 5))));
    }

    @Override
    public List<Channel> getChannels() {
        return channels;
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tc == null) {
                    tc = MultiViews.createMultiView("application/function", SignalDeviceObject.this);
                }
                tc.open();
                tc.requestActive();
            }
        };
    }

    @Override
    public String getContentType() {
        return MIMETYPE; // NOI18N
    }
}
