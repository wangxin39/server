package org.domeos.framework.api.biz;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.EventList;
import org.domeos.base.BaseTestCase;
import org.domeos.exception.K8sDriverException;
import org.domeos.framework.api.biz.event.K8SEventBiz;
import org.domeos.framework.api.model.event.EventKind;
import org.domeos.framework.engine.k8s.kubeutils.ClusterContext;
import org.domeos.framework.engine.k8s.util.KubeUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * Created by xupeng on 16-3-29.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class K8SEventBizTest extends BaseTestCase{

    @Autowired
    K8SEventBiz k8SEventBiz;

    KubeUtils client;

    int clusterId = 1;

    int deployId = 1;

    @Before
    public void setUp() throws K8sDriverException {
        client = ClusterContext.createKubeClient();
//        Properties pro = new Properties();
//        pro.put("log4j.rootLogger", "DEBUG, console");
//        pro.put("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
//        pro.put("log4j.appender.console.Target", "System.out");
//        pro.put("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
//        pro.put("log4j.appender.console.layout.ConversionPattern", "[%p]--[%d{yyyy-MM-dd HH:mm:ss,SSS}]--[%t]--[%c]--(%F\\:%L)--%m%n");
//        PropertyConfigurator.configure(pro);
    }

    @Test
    public void T010CreateEvent() throws K8sDriverException, IOException {
        EventList eventList = client.listEvent();
        List<Event> events = eventList.getItems();
//        System.out.println(events);
        for (Event event : events) {
//            System.out.println(event.getMetadata().getName());
            k8SEventBiz.createEvent(clusterId, deployId, event);
        }
    }

    @Test
    public void T020GetLatestVersion() {
        String version = k8SEventBiz.getLatestResourceVersion(clusterId);
        System.out.println("version:" + version);
    }

    @Test
    public void T030GetByNamespace() throws IOException {
        List<Event> events = k8SEventBiz.getEventsByNamespace(clusterId, "default");
//        System.out.println(eventList);
        System.out.println("by namespace:");
        for (Event event : events) {
            System.out.println(event.getMetadata().getNamespace() + event.getMetadata().getName() + event.getInvolvedObject().getKind());
        }
    }

    @Test
    public void T040GetByHost() throws IOException {
        List<Event> events = k8SEventBiz.getEventsByHost("bx-42-201");
        System.out.println("by host:");
        for (Event event : events) {
            System.out.println(event.getSource().getHost());
        }
    }

    @Test
    public void T050GetByKind() throws IOException {
        List<Event> events = k8SEventBiz.getEventsByKindAndNamespace(clusterId, "default", EventKind.Node);
        System.out.println("by Kind:");
        for (Event event : events) {
            System.out.println(event.getInvolvedObject().getKind());
//            System.out.println(event);
        }
    }

    @Test
    public void T060GetByDeploy() throws IOException {
        List<Event> events = k8SEventBiz.getEventsByDeployId(clusterId, deployId);
        System.out.println("by deploy:");
        for (Event event : events) {
            System.out.println(event.getMetadata().getName());
//            System.out.println(event);
        }
    }

    @Test
    public void T070deleteOldEvent() throws IOException {
        List<Event> events = k8SEventBiz.getEventsByDeployId(clusterId, deployId);
        System.out.println("before delete:" + events.size());
        long deleted = k8SEventBiz.deleteOldDeployEvents(clusterId, deployId, 30);
        System.out.println("deleted num:" + deleted);
        events = k8SEventBiz.getEventsByDeployId(clusterId, deployId);
        System.out.println("after delete:" + events.size());
    }
}
