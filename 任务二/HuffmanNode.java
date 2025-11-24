import java.util.Comparator;

public class HuffmanNode implements Comparable<HuffmanNode> {
    private byte data;         // 节点存储的数据（字节）
    private int frequency;     // 数据出现的频率
    private HuffmanNode left;  // 左子节点
    private HuffmanNode right; // 右子节点
    
    // 构造函数 - 用于创建叶子节点
    public HuffmanNode(byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }
    
    // 构造函数 - 用于创建内部节点
    public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
        this.data = -1; // 内部节点数据设为-1
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }
    
    // getter和setter方法
    public byte getData() {
        return data;
    }
    
    public void setData(byte data) {
        this.data = data;
    }
    
    public int getFrequency() {
        return frequency;
    }
    
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    
    public HuffmanNode getLeft() {
        return left;
    }
    
    public void setLeft(HuffmanNode left) {
        this.left = left;
    }
    
    public HuffmanNode getRight() {
        return right;
    }
    
    public void setRight(HuffmanNode right) {
        this.right = right;
    }
    
    // 判断是否为叶子节点
    public boolean isLeaf() {
        return left == null && right == null;
    }
    
    // 实现Comparable接口，按频率排序（用于优先队列）
    @Override
    public int compareTo(HuffmanNode other) {
        return Integer.compare(this.frequency, other.frequency);
    }
    
    // 为了调试方便的toString方法
    @Override
    public String toString() {
        if (isLeaf()) {
            return "HuffmanNode(data=" + (data == -1 ? "-1" : (char)data) + ", freq=" + frequency + ")";
        } else {
            return "HuffmanNode(freq=" + frequency + ", left=" + left + ", right=" + right + ")";
        }
    }
}