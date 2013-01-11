/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.api.topo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.opennms.features.topology.api.SimpleConnector;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

@XmlRootElement(name="edge")
public class AbstractEdge implements Edge {

	// Required
	private String m_namespace;
	// Required
	private String m_id;
	// Required
	private SimpleConnector m_source;
	// Required
	private SimpleConnector m_target;

	private String m_label;
	private String m_tooltipText;
	private String m_styleName;

	/**
	 * No-arg constructor for JAXB.
	 */
	public AbstractEdge() {}

	public AbstractEdge(String namespace, String id, Vertex source, Vertex target) {
		if (namespace == null) {
			throw new IllegalArgumentException("Namespace is null");
		} else if (id == null) {
			throw new IllegalArgumentException("ID is null");
		} else if (source == null) {
			throw new IllegalArgumentException("Source is null");
		} else if (target == null) {
			throw new IllegalArgumentException("Target is null");
		}
		m_namespace = namespace;
		m_id = id;
		m_source = new SimpleConnector(namespace, id + "::" + source.getId(), source.getLabel() + " Connector", source, this);
		m_target = new SimpleConnector(namespace, id + "::" + target.getId(), target.getLabel() + " Connector", target, this);
	}

	public AbstractEdge(String namespace, String id, SimpleConnector source, SimpleConnector target) {
		m_namespace = namespace;
		m_id = id;
		m_source = source;
		m_target = target;
	}

	/**
	 * This JAXB function is used to set the namespace since we expect it to be set in the parent object.
	 */
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (m_namespace == null) {
			try {
				BeanInfo info = Introspector.getBeanInfo(parent.getClass());
				for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
					if ("namespace".equals(descriptor.getName())) {
						m_namespace = (String)descriptor.getReadMethod().invoke(parent);
					}
				}
			} catch (IntrospectionException e) {
				LoggerFactory.getLogger(this.getClass()).warn("Exception thrown when trying to fetch namespace from parent class " + parent.getClass(), e);
			} catch (IllegalArgumentException e) {
				LoggerFactory.getLogger(this.getClass()).warn("Exception thrown when trying to fetch namespace from parent class " + parent.getClass(), e);
			} catch (IllegalAccessException e) {
				LoggerFactory.getLogger(this.getClass()).warn("Exception thrown when trying to fetch namespace from parent class " + parent.getClass(), e);
			} catch (InvocationTargetException e) {
				LoggerFactory.getLogger(this.getClass()).warn("Exception thrown when trying to fetch namespace from parent class " + parent.getClass(), e);
			}
		}
	}

	@Override
	@XmlID
	public String getId() {
		return m_id;
	}

	/**
	 * This setter is private so that it can only be used by JAXB.
	 */
	@SuppressWarnings("unused")
	private final void setId(String id) {
		m_id = id;
	}

	@Override
	@XmlTransient
	public final String getNamespace() {
		return m_namespace;
	}

	@Override
	public String getKey() {
		return m_namespace + ":" + m_id;
	}

	@Override
	public String getLabel() {
		return m_label;
	}

	@Override
	public String getTooltipText() {
		return m_tooltipText;
	}

	@Override
	public String getStyleName() {
		return m_styleName;
	}

	public final void setLabel(String label) {
		m_label = label;
	}

	@Override
	public final void setTooltipText(String tooltipText) {
		m_tooltipText = tooltipText;
	}

	@Override
	public final void setStyleName(String styleName) {
		m_styleName = styleName;
	}

	@Override
	public Item getItem() {
		return new BeanItem<AbstractEdge>(this);
	}

	@Override
	public final SimpleConnector getSource() {
		return m_source;
	}

	@Override
	public final SimpleConnector getTarget() {
		return m_target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_id == null) ? 0 : m_id.hashCode());
		result = prime * result
				+ ((m_namespace == null) ? 0 : m_namespace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof EdgeRef))	return false;
		
		EdgeRef e = (EdgeRef)obj;
		return getNamespace().equals(e.getNamespace()) && getId().equals(e.getId());
					
	}
	
	@Override
	public String toString() { return "Edge:"+getNamespace()+":"+getId() + "[label="+getLabel()+", styleName="+getStyleName()+"]"; } 

}