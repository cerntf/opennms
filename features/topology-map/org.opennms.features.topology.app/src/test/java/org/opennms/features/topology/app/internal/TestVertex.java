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

package org.opennms.features.topology.app.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.opennms.features.topology.api.topo.AbstractVertex;

public class TestVertex extends AbstractVertex {

	TestGroup m_parent = null;
	List<TestEdge> m_edges = new ArrayList<TestEdge>();

	public TestVertex(String id) {
		super("test", id);
	}

	public TestVertex(String id, int x, int y) {
		this(id);
		setX(x);
		setY(y);
	}

	public boolean isRoot() {
		return m_parent == null;
	}

	@XmlTransient
	public List<TestEdge> getEdges() {
		return m_edges;
	}

	void addEdge(TestEdge edge) {
		m_edges.add(edge);
	}

	void removeEdge(TestEdge edge) {
		m_edges.remove(edge);
	}
}