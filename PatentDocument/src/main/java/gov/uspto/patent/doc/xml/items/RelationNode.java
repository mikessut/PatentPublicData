package gov.uspto.patent.doc.xml.items;

import java.util.ArrayList;
import java.util.List;

import gov.uspto.patent.model.RelationType;
import org.dom4j.Node;

import gov.uspto.parser.dom4j.ItemReader;
import gov.uspto.patent.model.DocumentId;
import gov.uspto.patent.model.DocumentIdType;

/*
 * Used by:
 * 		addition
 * 		continuation
 * 		continuation-in-part
 * 		continuing-reissue
 * 		division
 * 		reexamination	
 * 		reissue
 * 		substitution
 * 		us-reexamination-reissue-merger
 * 		utility-model-basis
 * 
 * 		us-divisional-reissue/us-relation
 */

public class RelationNode extends ItemReader<List<DocumentId>> {
	private static final String ITEM_NODE_NAME = "relation";
	private static final String REL_PARENT = "parent-doc/document-id";
	private static final String REL_PARENT_GRANT = "parent-doc/parent-grant-document/document-id";
	private static final String REL_PCT_PARENT = "parent-doc/parent-pct-document/document-id";

	private static final String REL_CHILD = "child-doc/document-id";

	private final DocumentIdType docIdType;

	public RelationNode(Node itemNode, DocumentIdType docIdType) {
		super(itemNode, ITEM_NODE_NAME);
		this.docIdType = docIdType;
	}

	@Override
	public List<DocumentId> read() {
		List<DocumentId> docIds = new ArrayList<DocumentId>();

		Node parentN = itemNode.selectSingleNode(REL_PARENT);
		DocumentId parentDocId = new DocumentIdNode(parentN).read();
		parentDocId.setType(docIdType);
		parentDocId.setRelType(RelationType.PARENT_APP);
		docIds.add(parentDocId);

		Node parentGrantN = itemNode.selectSingleNode(REL_PARENT_GRANT);
		if (parentGrantN != null) {
			DocumentId parentGrantId = new DocumentIdNode(parentGrantN).read();
			parentGrantId.setType(docIdType);
			parentGrantId.setRelType(RelationType.PARENT_GRANT);
			docIds.add(parentGrantId);
		}

		Node pctdN = itemNode.selectSingleNode(REL_PCT_PARENT);
		if (pctdN != null) {
			DocumentId docId = new DocumentIdNode(pctdN).read();
			docId.setType(docIdType);
			docId.setRelType(RelationType.PARENT_PCT);
			if (!docId.equals(parentDocId)) {
				docIds.add(docId);
			}
		}

		Node childN = itemNode.selectSingleNode(REL_CHILD);
		if (childN != null) {
			DocumentId childDocId = new DocumentIdNode(childN).read();
			childDocId.setType(docIdType);
			childDocId.setRelType(RelationType.CHILD);
			if (!childDocId.equals(parentDocId)) {
				docIds.add(childDocId);
			}
		}

		return docIds;
	}
}
