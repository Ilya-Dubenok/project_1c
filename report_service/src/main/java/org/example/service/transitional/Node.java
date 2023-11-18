package org.example.service.transitional;

import lombok.Getter;
import org.example.core.dto.category.CategoryDTO;

import java.util.*;


@Getter
public class Node {

    private Node parent;

    private final Set<Node> children = new LinkedHashSet<>();

    private final CategoryDTO category;

    private final List<ProductToBuy> products = new ArrayList<>();

    public Node(CategoryDTO categoryDTO) {
        this.category = categoryDTO;
    }

    public Node(ProductToBuy product) {
        products.add(product);
        category = product.getCategory();
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void addChild(Node child) {
        child.setParent(this);
        children.add(child);
    }

    public void addTopLevelParent(Node parent) {
        Node currentNode = this;
        while (null != currentNode.getParent()) {
            currentNode = currentNode.getParent();
        }
        currentNode.setParent(parent);
        parent.addChild(currentNode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(category, node.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category);
    }

}
