// Copyright 2007 Google Inc.  All Rights Reserved.
package com.google.enterprise.connector.sharepoint.client;

import com.google.enterprise.connector.sharepoint.generated.SiteDataStub;
import com.google.enterprise.connector.sharepoint.generated.SiteDataStub.ArrayOf_sList;
import com.google.enterprise.connector.sharepoint.generated.SiteDataStub.ArrayOf_sWebWithTime;
import com.google.enterprise.connector.sharepoint.generated.SiteDataStub._sList;
import com.google.enterprise.connector.sharepoint.generated.SiteDataStub._sWebWithTime;

import org.apache.axis2.AxisFault;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class holds data and methods for any call to SiteData web service.
 *
 */
public class SiteDataWS {   
  
    private static final String siteDataEndpoint = "/_vti_bin/SiteData.asmx";
    private SharepointClientContext sharepointClientContext;
    private String endpoint;
    private SiteDataStub stub;
    
    public SiteDataWS(SharepointClientContext sharepointClientContext) {
      this.sharepointClientContext = sharepointClientContext;
      endpoint = "http://" + sharepointClientContext.getHost() + ":" + 
                  sharepointClientContext.getPort() + siteDataEndpoint;
      try {
        stub = new SiteDataStub(endpoint);
        sharepointClientContext.setStubWithAuth(stub, endpoint);
      } catch (AxisFault e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }     
    }
  
    /**
     * Gets all the sites from the sharepoint server.
     * @return list of sharepoint documents corresponding to sites.
     */
    public ArrayList<Document> getSites() {
      ArrayList<Document> sites = new ArrayList<Document>();
      try {
        SiteDataStub.GetSite req = new SiteDataStub.GetSite();
        SiteDataStub.GetSiteResponse res = stub.GetSite(req);
        ArrayOf_sWebWithTime webs = res.getVWebs();
        _sWebWithTime[] els = webs.get_sWebWithTime();
        for (int i = 0; i < els.length; ++i) {
          String url = els[i].getUrl();      
          Calendar lastModified = els[i].getLastModified();   
          Document doc = new Document(url, url, lastModified);
          sites.add(doc);
        }  
      } catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return sites;      
    }
    
    /**
     * Gets the collection of all the lists on the sharepoint server.
     * @return list of sharepoint baselist.
     */
    public  ArrayList<BaseList> getListCollection() {
      ArrayList<BaseList> listCollection = new ArrayList<BaseList>();
      SiteDataStub.GetListCollectionResponse res;
      try {
        SiteDataStub.GetListCollection req = 
          new SiteDataStub.GetListCollection();
        res = stub.GetListCollection(req);
        ArrayOf_sList asl = res.getVLists();
        _sList[] sl = asl.get_sList();
        for(int i=0; i<sl.length;i++) {
          BaseList list = new BaseList(sl[i].getInternalName(), 
                              sl[i].getTitle(), sl[i].getBaseType());
        
          listCollection.add(list);
        }  
      } catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }           
      return listCollection;
    }
}
