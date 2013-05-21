package org.opennms.netmgt.dao.topology;

import java.util.ArrayList;
import java.util.List;

import org.opennms.netmgt.dao.TopologyDao;
import org.opennms.netmgt.model.topology.Element;
import org.opennms.netmgt.model.topology.ElementIdentifier;
import org.opennms.netmgt.model.topology.EndPoint;
import org.opennms.netmgt.model.topology.Link;
import org.opennms.netmgt.model.topology.PseudoBridgeElementIdentifier;
import org.opennms.netmgt.model.topology.PseudoBridgeEndPoint;
import org.opennms.netmgt.model.topology.PseudoMacEndPoint;

public class TopologyDaoInMemoryImpl implements TopologyDao {

	private List<Element> m_elements;	
		
    public TopologyDaoInMemoryImpl() {
    	m_elements = new ArrayList<Element>();
    }

	protected Element updateElementIdentifiers(Element elementUpdated, Element elementToUpdate) {
		
		for (ElementIdentifier elementIdentifierUpdated: elementUpdated.getElementIdentifiers()) {
			if (elementToUpdate.hasElementIdentifier(elementIdentifierUpdated)) {
				for (ElementIdentifier elementIdentifierToUpdate: elementToUpdate.getElementIdentifiers()) {
					if (elementIdentifierToUpdate.equals(elementIdentifierUpdated)) {
						elementIdentifierToUpdate.update(elementIdentifierUpdated);
						break;
					}
				}
			} else {
				elementToUpdate.addElementIdentifier(elementIdentifierUpdated);
			}
		}
		return elementToUpdate;
	}

	@Override
	public void saveOrUpdate(Link link) {
		saveOrUpdate(link.getA());
		saveOrUpdate(link.getB());
	}

	@Override
	public void saveOrUpdate(EndPoint endpoint) {
		for (Element e: m_elements) {
			if (e.equals(endpoint.getElement())) {
				endpoint.setElement(updateElementIdentifiers(endpoint.getElement(),e));
				if (e.hasEndPoint(endpoint)) {
					EndPoint ep = e.getEndPoint(endpoint);
					ep.update(endpoint);
				} else {
					e.addEndPoint(endpoint);
				}
				return;
			}
		}
		m_elements.add(endpoint.getElement());
	}

	@Override
	public void delete(Element element) {
		for (Element e : m_elements) {
			if (e.equals(element)) {
				for (EndPoint ep: e.getEndpoints()) {
					if (ep.hasLink())
						delete(ep.getLink());
				}
			}
				
		}
		m_elements.remove(element);
	}

	@Override
	public void delete(Link link) {
		for (Element e: m_elements) {
			for (EndPoint ep: e.getEndpoints()) {
				if (ep.hasLink() && (ep.equals(link.getA()) || ep.equals(link.getB()))) {
					ep.setLink(null);
					break;
				}
			}
		}
	}

	@Override
	public void delete(EndPoint endpoint) {
		if (endpoint.hasLink())
			delete(endpoint.getLink());
		for (Element e: m_elements) {
			List<EndPoint> newendpoints = new ArrayList<EndPoint>(); 
			for (EndPoint ep: e.getEndpoints()) {
				if (ep.equals(endpoint)) {
					continue;
				}
				newendpoints.add(ep);
			}
			e.setEndpoints(newendpoints);
		}
	}

	@Override
	public List<Element> getTopology() {
		return m_elements;
	}

	@Override
	public void delete(ElementIdentifier elementidentifier) {
		for (Element e: m_elements) {
			if (e.hasElementIdentifier(elementidentifier)) {
				e.removeElementIdentifier(elementidentifier);
			}
		}
	}

	@Override
	public Element get(ElementIdentifier elementIdentifier) {
		for (Element e: m_elements) {
			if (e.hasElementIdentifier(elementIdentifier)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public EndPoint get(EndPoint endpoint) {
		for (Element e: m_elements) {
			if (e.hasEndPoint(endpoint)) {
				return e.getEndPoint(endpoint);
			}
		}
		return null;
	}

	@Override
	public void mergePseudoElements(PseudoBridgeElementIdentifier elementIdentifier1,
			PseudoBridgeElementIdentifier elementIdentifier2) {
		List<Element> newTopology = new ArrayList<Element>();
			Element element2 = null;
		for (Element e: m_elements) {
			if (!e.hasElementIdentifier(elementIdentifier1) && e.hasElementIdentifier(elementIdentifier2)) {
				element2=e;
			} else {
				newTopology.add(e);
			}
		}
		m_elements = newTopology;

		if (element2 != null) {
			for (Element e: m_elements) {
				if (e.hasElementIdentifier(elementIdentifier1)) {
					e.addElementIdentifier(elementIdentifier2);
					for (EndPoint ep2: element2.getEndpoints()) {
						ep2.setElement(e);
						e.addEndPoint(ep2);
					}
				}
			}
		}
	}

	@Override
	public void splitPseudoElement(PseudoBridgeElementIdentifier elementIdentifier) {

		List<Element> newTopology = new ArrayList<Element>();

		Element elementin = null;
		for (Element e: m_elements) {
			if (e.hasElementIdentifier(elementIdentifier) ) {
				elementin=e;
			} else {
				newTopology.add(e);
			}
		}

		m_elements = newTopology;
		
		Element element = new Element();
		element.addElementIdentifier(elementIdentifier);
		if (elementin == null) return;
		elementin.removeElementIdentifier(elementIdentifier);
		for (EndPoint ep: elementin.getEndpoints() ) { 
			if (ep instanceof PseudoMacEndPoint || ep instanceof PseudoBridgeEndPoint) {
				PseudoBridgeEndPoint bep = (PseudoBridgeEndPoint) ep;
				if (bep.getLinkedBridgeIdentifier().equals(elementIdentifier.getLinkedBridgeIdentifier()) 
						&& bep.getLinkedBridgePort() == elementIdentifier.getLinkedBridgePort() ) {
					elementin.removeEndPoint(ep);
					ep.setElement(element);
					element.addEndPoint(ep);
				}
			}
		}
		m_elements.add(element);
		m_elements.add(elementin);
	}


}