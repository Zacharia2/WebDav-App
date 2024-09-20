package com.xinglan.webdavserver.corefunc;

import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.SecurityManager;
import com.bradmcevoy.http.http11.Http11ResponseHandler;
import com.ettrema.berry.Berry;
import com.ettrema.berry.simple.SimpletonServer;
import com.ettrema.common.Service;
import com.ettrema.http.fs.FileSystemResourceFactory;
import com.ettrema.http.fs.SimpleLockManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BerryUtil {
    private Berry berry = null;

    public static void init() {
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
    }

    public boolean isStarted() {
        return this.berry != null;
    }

    public boolean startBerry(int port, String homeFolder, SecurityManager securityManager) {
        try {
            FileSystemResourceFactory resourceFactory = new FileSystemResourceFactory(new File(homeFolder), securityManager, Prefs.DEFAULT_CUSTOMFOLDER);
            resourceFactory.setLockManager(new SimpleLockManager());
            HttpManager httpManager = new HttpManager(resourceFactory);
            List<Service> httpAdapters = new ArrayList<>();
            Http11ResponseHandler responseHandler = httpManager.getResponseHandler();
            SimpletonServer simpletonServer = new SimpletonServer(responseHandler);
            simpletonServer.setHttpPort(port);
            httpAdapters.add(simpletonServer);
            this.berry = new Berry(httpManager, httpAdapters);
            this.berry.start();
            return true;
        } catch (Exception e) {
            this.berry = null;
            return false;
        }
    }

    public void stopBerry() {
        if (isStarted()) {
            this.berry.stop();
        }
        this.berry = null;
    }
}
