/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.std.jaxp;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResultType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.att.research.xacml.std.StdMutableResponse;

/**
 * JaxpResponse extends {@link com.att.research.xacml.std.StdMutableResponse} with methods for creation from
 * JAXP elements.
 */
public class JaxpResponse extends StdMutableResponse {
    private static Log logger = LogFactory.getLog(JaxpResponse.class);

    protected JaxpResponse() {
    }

    public static JaxpResponse newInstance(ResponseType responseType) {
        if (responseType == null) {
            throw new NullPointerException("Null ResponseType");
        } else if (responseType.getResult() == null || responseType.getResult().size() == 0) {
            throw new IllegalArgumentException("No ResultTypes in ResponseType");
        }
        JaxpResponse jaxpResponse = new JaxpResponse();

        Iterator<ResultType> iterResults = responseType.getResult().iterator();
        while (iterResults.hasNext()) {
            jaxpResponse.add(JaxpResult.newInstance(iterResults.next()));
        }

        return jaxpResponse;
    }

    /**
     * Creates a new <code>JaxpResponse</code> by loading it from an XML <code>File</code>.
     *
     * @param fileXmlResponse the <code>File</code> containing the Response XML
     * @return a new <code>JaxpResponse</code> generated by parsing the given XML file
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    public static JaxpResponse load(File fileXmlResponse) throws ParserConfigurationException, IOException,
        SAXException, JAXBException {
        if (fileXmlResponse == null) {
            throw new NullPointerException("Null File");
        }

        /*
         * Create XML document factory and builder
         */
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        /*
         * Parse the file into a Document
         */
        Document document = documentBuilder.parse(fileXmlResponse);
        if (document == null) {
            logger.error("No Document returned parsing \"" + fileXmlResponse.getAbsolutePath() + "\"");
            return null;
        }

        NodeList nodeListRoot = document.getChildNodes();
        if (nodeListRoot == null || nodeListRoot.getLength() == 0) {
            logger.warn("No child elements of the XML document");
            return null;
        }
        Node nodeRoot = nodeListRoot.item(0);
        if (nodeRoot == null || nodeRoot.getNodeType() != Node.ELEMENT_NODE) {
            logger.warn("Root of the document is not an ELEMENT");
            return null;
        }

        JAXBContext context = JAXBContext.newInstance(ResponseType.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<ResponseType> jaxbElementResponse = unmarshaller.unmarshal(nodeRoot,
                                                                               ResponseType.class);
        if (jaxbElementResponse == null || jaxbElementResponse.getValue() == null) {
            logger.error("JAXB unmarshalling did not return a ResponseType node");
            return null;
        }
        return JaxpResponse.newInstance(jaxbElementResponse.getValue());

    }

    public static void main(String[] args) {
        for (String fileName : args) {
            JaxpResponse jaxpResponse = null;
            try {
                jaxpResponse = JaxpResponse.load(new File(fileName));
            } catch (Exception ex) {
                logger.fatal("Failed to load \"" + fileName + "\" as a JaxpResponse", ex);
                continue;
            }
            if (jaxpResponse == null) {
                logger.warn("Null JaxpResponse returned for file \"" + fileName + "\"");
            } else {
                logger.info("JaxpResponse for file \"" + fileName + "\"=" + jaxpResponse.toString());
            }
        }
    }

}
