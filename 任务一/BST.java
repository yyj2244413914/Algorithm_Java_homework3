public class BST<K extends Comparable<K>, V> {
    // 节点内部类
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node<K, V> root; // 根节点
    private int size; // 节点数量

    // 构造方法
    public BST() {
        root = null;
        size = 0;
    }

    // 插入元素（符合insert方法定义）
    public void insert(K key, V value) {
        // 前置条件检查（键值非空）
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value cannot be null");
        }
        root = insert(root, key, value);
    }

    // 递归插入辅助方法
    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            // 键已存在，更新值
            node.value = value;
        }
        return node;
    }

    // 删除元素（符合remove方法定义）
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        // 先查找值（用于返回）
        V oldValue = searchForValue(root, key);
        if (oldValue != null) {
            root = remove(root, key);
            System.out.println("remove success ---" + key + " " + oldValue);
            size--;
        } else {
            System.out.println("remove unsuccess ---" + key);
        }
        return oldValue;
    }
    
    // 无输出的查找辅助方法（用于remove内部调用）
    private V searchForValue(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return searchForValue(node.left, key);
        } else if (cmp > 0) {
            return searchForValue(node.right, key);
        } else {
            return node.value; // 找到键
        }
    }

    // 递归删除辅助方法
    private Node<K, V> remove(Node<K, V> node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            // 找到待删除节点
            if (node.left == null) return node.right; // 左子树为空
            if (node.right == null) return node.left; // 右子树为空

            // 左右子树都不为空：用中序后继（右子树最小节点）替换
            Node<K, V> successor = minNode(node.right);
            node.key = successor.key;
            node.value = successor.value;
            // 删除后继节点
            node.right = remove(node.right, successor.key);
        }
        return node;
    }

    // 查找最小节点（辅助删除）
    private Node<K, V> minNode(Node<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // 查找元素（符合search方法定义）
    public V search(K key) {
        if (key == null) {
            System.out.println("search unsuccess ---" + key);
            return null;
        }
        return search(root, key);
    }

    // 递归查找辅助方法
    private V search(Node<K, V> node, K key) {
        if (node == null) {
            System.out.println("search unsuccess ---" + key);
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return search(node.left, key);
        } else if (cmp > 0) {
            return search(node.right, key);
        } else {
            System.out.println("search success ---" + key + " " + node.value);
            return node.value; // 找到键
        }
    }

    // 更新元素（符合update方法定义）
    public boolean update(K key, V value) {
        if (key == null || value == null) {
            System.out.println("update unsuccess ---" + key);
            return false;
        }
        return update(root, key, value);
    }

    // 递归更新辅助方法
    private boolean update(Node<K, V> node, K key, V value) {
        if (node == null) {
            System.out.println("update unsuccess ---" + key);
            return false;
        }
        
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return update(node.left, key, value);
        } else if (cmp > 0) {
            return update(node.right, key, value);
        } else {
            node.value = value; // 更新值
            System.out.println("update success ---" + key + " " + value);
            return true;
        }
    }

    // 判断树是否为空
    public boolean isEmpty() {
        return size == 0;
    }

    // 清空树
    public void clear() {
        root = null;
        size = 0;
    }

    // 输出树结构（节点数和高度）
    public void showStructure() {
        int height = getHeight(root);
        System.out.println("-----------------------------\n" + //
                        "There are " + size + " nodes in this BST.\n" + //
                        "The height of this BST is " + height + ".\n" + //
                        "-----------------------------");
    }

    // 计算树高（空树高度为0，单节点高度为1）
    private int getHeight(Node<K, V> node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }
}
