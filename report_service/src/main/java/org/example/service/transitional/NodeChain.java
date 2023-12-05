package org.example.service.transitional;

import lombok.Getter;
import org.example.core.dto.category.CategoryDTO;

import java.util.*;

@Getter
public class NodeChain {

    private Node topNode;

    private final Node lowestNode;

    private final Map<CategoryDTO, Node> nodesMap = new HashMap<>();

    public NodeChain(ProductToBuy productToBuy) {
        lowestNode = new Node(productToBuy);
        nodesMap.put(lowestNode.getCategory(), lowestNode);
        List<CategoryDTO> categories = productToBuy.getCategories();
        for (int i = 1; i < categories.size(); i++) {
            setNodesAndMapData(categories.get(i));
        }
    }

    private NodeChain(ProductToBuy productToBuy, CategoryDTO highestCategoryDTO) {
        lowestNode = topNode = new Node(productToBuy);
        nodesMap.put(lowestNode.getCategory(), lowestNode);
        List<CategoryDTO> categories = productToBuy.getCategories();
        for (int i = 1; i < categories.size(); i++) {
            CategoryDTO categoryDTO = categories.get(i);
            if (Objects.equals(categoryDTO, highestCategoryDTO)) {
                return;
            }
            setNodesAndMapData(categoryDTO);
        }
    }

    public boolean isProductToBuyMergedIntoChain(ProductToBuy productToBuy) {
        Optional<CategoryDTO> sharedCategoryDTOOptional = productToBuy.getCategories().stream()
                .filter(nodesMap::containsKey)
                .findFirst();
        if (sharedCategoryDTOOptional.isEmpty()) {
            return false;
        }
        Node intersectionNode = nodesMap.get(sharedCategoryDTOOptional.get());
        mergeProductToBuyIntoIntersectionNode(intersectionNode, productToBuy);
        return true;
    }

    private void setNodesAndMapData(CategoryDTO categoryDTO) {
        topNode = new Node(categoryDTO);
        lowestNode.addTopLevelParent(topNode);
        nodesMap.put(topNode.getCategory(), topNode);
    }

    private void mergeProductToBuyIntoIntersectionNode(Node intersectionNode, ProductToBuy productToBuy) {
        if (Objects.equals(intersectionNode.getCategory(), productToBuy.getCategory())) {
            intersectionNode.getProducts().add(productToBuy);
            return;
        }
        NodeChain mergingNodeChain = new NodeChain(productToBuy, intersectionNode.getCategory());
        Node childForIntersectionNode = mergingNodeChain.getTopNode();
        intersectionNode.addChild(childForIntersectionNode);
        this.nodesMap.putAll(mergingNodeChain.getNodesMap());
    }
}
