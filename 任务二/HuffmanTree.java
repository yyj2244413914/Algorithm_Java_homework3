import java.util.*;
import java.io.*;

public class HuffmanTree {
    private HuffmanNode root;
    private Map<Byte, String> codeTable; // 存储每个字节对应的哈夫曼编码
    
    // 构造函数，从频率表构建哈夫曼树
    public HuffmanTree(Map<Byte, Integer> frequencyMap) {
        this.codeTable = new HashMap<>();
        buildTree(frequencyMap);
        generateCodeTable();
    }
    
    // 构建哈夫曼树
    private void buildTree(Map<Byte, Integer> frequencyMap) {
        // 使用优先队列（最小堆）来构建哈夫曼树
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
        
        // 创建所有叶子节点并加入优先队列
        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            priorityQueue.add(node);
        }
        
        // 特殊情况：如果只有一个字符
        if (priorityQueue.size() == 1) {
            HuffmanNode singleNode = priorityQueue.poll();
            root = new HuffmanNode(singleNode.getFrequency(), singleNode, null);
        } else {
            // 合并节点直到队列中只剩一个节点（根节点）
            while (priorityQueue.size() > 1) {
                HuffmanNode left = priorityQueue.poll();
                HuffmanNode right = priorityQueue.poll();
                
                // 创建一个内部节点，频率为两个子节点的频率之和
                HuffmanNode parent = new HuffmanNode(left.getFrequency() + right.getFrequency(), left, right);
                priorityQueue.add(parent);
            }
            
            // 最后剩下的节点就是根节点
            if (!priorityQueue.isEmpty()) {
                root = priorityQueue.poll();
            }
        }
    }
    
    // 生成编码表
    private void generateCodeTable() {
        if (root == null) {
            return;
        }
        
        StringBuilder codeBuilder = new StringBuilder();
        generateCodes(root, codeBuilder);
    }
    
    // 递归生成编码表
    private void generateCodes(HuffmanNode node, StringBuilder currentCode) {
        if (node == null) {
            return;
        }
        
        // 如果是叶子节点，将当前编码存入编码表
        if (node.isLeaf()) {
            codeTable.put(node.getData(), currentCode.toString());
            return;
        }
        
        // 遍历左子树，添加0
        currentCode.append('0');
        generateCodes(node.getLeft(), currentCode);
        currentCode.deleteCharAt(currentCode.length() - 1); // 回溯
        
        // 遍历右子树，添加1
        currentCode.append('1');
        generateCodes(node.getRight(), currentCode);
        currentCode.deleteCharAt(currentCode.length() - 1); // 回溯
    }
    
    // 获取编码表
    public Map<Byte, String> getCodeTable() {
        return codeTable;
    }
    
    // 获取根节点
    public HuffmanNode getRoot() {
        return root;
    }
    
    // 从比特流解码
    public byte decodeNextByte(BitInputStream bis) throws IOException {
        if (root == null) {
            throw new IllegalStateException("哈夫曼树未初始化");
        }
        
        HuffmanNode current = root;
        
        // 特殊情况：如果根节点是叶子节点（只有一个字符）
        if (current.isLeaf()) {
            return current.getData();
        }
        
        // 遍历树直到找到叶子节点
        while (true) {
            int bit = bis.bitRead();
            if (bit == -1) {
                throw new IOException("文件结束，无法解码完所有比特");
            }
            
            if (bit == 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
            
            // 找到叶子节点
            if (current.isLeaf()) {
                return current.getData();
            }
        }
    }
    
    // 将频率表写入文件（用于解压缩时重建哈夫曼树）
    public static void writeFrequencyTable(Map<Byte, Integer> frequencyMap, BitOutputStream bos) throws IOException {
        // 首先写入频率表的大小
        bos.byteWrite(frequencyMap.size());
        
        // 然后写入每个字节及其频率
        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
            bos.byteWrite(entry.getKey() & 0xFF); // 写入字节值（确保是0-255）
            // 写入频率（使用4个字节）
            bos.byteWrite((entry.getValue() >> 24) & 0xFF);
            bos.byteWrite((entry.getValue() >> 16) & 0xFF);
            bos.byteWrite((entry.getValue() >> 8) & 0xFF);
            bos.byteWrite(entry.getValue() & 0xFF);
        }
    }
    
    // 从文件读取频率表
    public static Map<Byte, Integer> readFrequencyTable(BitInputStream bis) throws IOException {
        Map<Byte, Integer> frequencyMap = new HashMap<>();
        
        // 读取频率表的大小
        int size = bis.byteRead();
        if (size == -1) {
            throw new IOException("无法读取频率表大小");
        }
        
        // 读取每个字节及其频率
        for (int i = 0; i < size; i++) {
            int byteValue = bis.byteRead();
            if (byteValue == -1) {
                throw new IOException("无法读取字节值");
            }
            
            // 读取4字节频率
            int b1 = bis.byteRead();
            int b2 = bis.byteRead();
            int b3 = bis.byteRead();
            int b4 = bis.byteRead();
            if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1) {
                throw new IOException("无法读取频率值");
            }
            
            int frequency = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
            frequencyMap.put((byte)byteValue, frequency);
        }
        
        return frequencyMap;
    }
    
    // 统计文件中字节的频率
    public static Map<Byte, Integer> calculateFrequencies(String inputFilePath) throws IOException {
        Map<Byte, Integer> frequencyMap = new HashMap<>();
        try (BufferedInputStream bis = new BufferedInputStream(new java.io.FileInputStream(inputFilePath))) {
            int byteRead;
            while ((byteRead = bis.read()) != -1) {
                byte b = (byte) byteRead;
                frequencyMap.put(b, frequencyMap.getOrDefault(b, 0) + 1);
            }
        }
        return frequencyMap;
    }
}