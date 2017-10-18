package com.zzc.ason.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : ParseXMLUtil
 * remark: xml文件解析助手
 */
@Slf4j
public final class ParseXMLUtil {

    private Map<String, Map<String, String>> itemXML = Maps.newHashMap();

    private ParseXMLUtil() {
    }

    public ParseXMLUtil(String xmlPath) {
        Document doc = loadDoc(xmlPath);
        parseXML(doc);
    }

    public Map<String, String> acquireLocalProp(String itemName) {
        return itemXML.get(itemName);
    }

    public Map<String, Map<String, String>> acquireAllProps() {
        return itemXML;
    }

    private Document loadDoc(String xmlPath) {
        InputStream input = ParseXMLUtil.class.getClassLoader().getResourceAsStream(xmlPath);
        Document doc = null;
        try {
            SAXReader saxReader = new SAXReader();
            doc = saxReader.read(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    private void parseXML(Document doc) {
        Element root = doc.getRootElement();
        for (Iterator props = root.elementIterator(); props.hasNext(); ) {
            Element itemElem = (Element) props.next();
            if (itemElem.getName() == null || !"project".equals(itemElem.getName())) {
                log.warn("[project node is invalid. node name must be 'project']");
                continue;
            }
            String projName = itemElem.attributeValue("name");
            if (projName == null || "".equals(projName.trim())) {
                log.warn("[project name is invalid]");
                continue;
            }
            if (!itemXML.containsKey(projName.trim())) {
                itemXML.put(projName.trim(), Maps.<String, String>newHashMap());
            }
            Map<String, String> propsXML = itemXML.get(projName.trim());
            for (Iterator node = itemElem.elementIterator(); node.hasNext(); ) {
                Element propElem = (Element) node.next();
                if (propElem.getName() != null && !"prop".equals(propElem.getName())) {
                    log.warn("[prop node of " + projName + " is invalid. node name must be 'prop']");
                    continue;
                }
                String name = propElem.attributeValue("name");
                String value = propElem.attributeValue("value");
                if (name == null || "".equals(name.trim()) || value == null || "".equals(value.trim())) {
                    log.warn("[name or value node is invalid]");
                    continue;
                }
                propsXML.put(name.trim(), value.trim());
            }
        }
    }
}