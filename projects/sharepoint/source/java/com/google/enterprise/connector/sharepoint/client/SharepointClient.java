// Copyright 2007 Google Inc.  All Rights Reserved.
package com.google.enterprise.connector.sharepoint.client;

import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.SimplePropertyMap;
import com.google.enterprise.connector.spi.SimpleResultSet;
import com.google.enterprise.connector.spi.SimpleValue;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.ValueType;

import java.util.ArrayList;

/**
 * 
 * Class which maintains has all the methods needed to get documents and sites 
 * from the sharepoint server. It is a layer between the connector and the 
 * actual web services calls .
 *
 */

public class SharepointClient {

  private SharepointClientContext sharepointClientContext;
  
  public SharepointClient(SharepointClientContext sharepointClientContext) {
    this.sharepointClientContext = sharepointClientContext;
  }
  
  /**
   * Gets all the sites on the sharepoint server.
   * @return resultSet ResultSet containing the PropertyMap for each site.
   */
  public ResultSet getSites() {
    SimpleResultSet resultSet = new SimpleResultSet();
    SiteDataWS siteDataWS = new SiteDataWS(sharepointClientContext);
    ArrayList<Document> sites = siteDataWS.getSites();
    for(int i=0; i<sites.size(); i++) {
      SimplePropertyMap pm = buildPropertyMap(sites.get(i));
      System.out.println(sites.get(i).getUrl());
      resultSet.add(pm);
    }
    return resultSet;
  } 
  
  /**
   * Gets all the docs from the Document Library in sharepoint.
   * It first calls SiteData web service to get all the Lists.
   * And then calls Lists web service to get the list items for the 
   * lists which are of the type Document Library.
   * @return resultSet 
   */
  public ResultSet getDocsFromDocumentLibrary() {
    SimpleResultSet resultSet = new SimpleResultSet();
    SiteDataWS siteDataWS = new SiteDataWS(sharepointClientContext);
    ListsWS listsWS = new ListsWS(sharepointClientContext);
    ArrayList<BaseList> listCollection = siteDataWS.getListCollection();
    for(int i=0; i<listCollection.size(); i++) {
      if(listCollection.get(i).getType().equals("DocumentLibrary")) {
        ArrayList<Document> listItems = 
            listsWS.getListItems(listCollection.get(i).getInternalName());   
        System.out.println(listCollection.get(i).getTitle());
        for(int j=0; j<listItems.size(); j++ ){
          SimplePropertyMap pm = buildPropertyMap(listItems.get(j));
          resultSet.add(pm);
        }
      }      
    }
    return resultSet;
  }
  
  /**
   * Build the Property Map for the connector manager from the 
   * sharepoint document.
   * @param doc sharepoint document
   * @return Property Map.
   */
  private SimplePropertyMap buildPropertyMap(Document doc) {
    SimplePropertyMap pm = new SimplePropertyMap();
    Property contentUrlProp = new SimpleProperty(
      SpiConstants.PROPNAME_CONTENTURL, new SimpleValue(ValueType.STRING, 
      doc.getUrl()));
    pm.put(SpiConstants.PROPNAME_CONTENTURL, contentUrlProp);
    
    Property docIdProp = new SimpleProperty(
      SpiConstants.PROPNAME_DOCID, new SimpleValue(ValueType.STRING, 
      doc.getDocId()));
    pm.put(SpiConstants.PROPNAME_DOCID, docIdProp);        
    
    Property searchUrlProp = new SimpleProperty(
      SpiConstants.PROPNAME_SEARCHURL, new SimpleValue(ValueType.STRING, 
      doc.getUrl()));
    pm.put(SpiConstants.PROPNAME_SEARCHURL, searchUrlProp);
    
    Property lastModifyProp = new SimpleProperty(
      SpiConstants.PROPNAME_LASTMODIFY, new SimpleValue(
        ValueType.DATE, SimpleValue.calendarToIso8601(doc.getDate())));  
    pm.put(SpiConstants.PROPNAME_LASTMODIFY, lastModifyProp);
    return pm;
    
  }  
}
