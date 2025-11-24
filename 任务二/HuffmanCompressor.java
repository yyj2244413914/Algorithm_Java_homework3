import java.io.*;
import java.util.Map;

public class HuffmanCompressor {
    
    // 压缩文件
    public static void compress(String inputFilePath, String outputFilePath) throws IOException {
        // 1. 统计频率
        Map<Byte, Integer> frequencyMap = HuffmanTree.calculateFrequencies(inputFilePath);
        
        // 2. 创建哈夫曼树
        HuffmanTree huffmanTree = new HuffmanTree(frequencyMap);
        Map<Byte, String> codeTable = huffmanTree.getCodeTable();
        
        // 3. 写入压缩文件
        try (BitInputStream bis = new BitInputStream(inputFilePath);
             BitOutputStream bos = new BitOutputStream(outputFilePath)) {
            
            // 3.1 写入频率表（用于解压缩时重建哈夫曼树）
            HuffmanTree.writeFrequencyTable(frequencyMap, bos);
            
            // 3.2 写入原始数据长度（用于处理可能的填充位）
            long originalSize = new File(inputFilePath).length();
            bos.byteWrite((int)((originalSize >> 56) & 0xFF));
            bos.byteWrite((int)((originalSize >> 48) & 0xFF));
            bos.byteWrite((int)((originalSize >> 40) & 0xFF));
            bos.byteWrite((int)((originalSize >> 32) & 0xFF));
            bos.byteWrite((int)((originalSize >> 24) & 0xFF));
            bos.byteWrite((int)((originalSize >> 16) & 0xFF));
            bos.byteWrite((int)((originalSize >> 8) & 0xFF));
            bos.byteWrite((int)(originalSize & 0xFF));
            
            // 3.3 压缩数据（按位写入）
            int byteRead;
            while ((byteRead = bis.byteRead()) != -1) {
                byte b = (byte) byteRead;
                String code = codeTable.get(b);
                if (code != null) {
                    for (char bit : code.toCharArray()) {
                        try {
                            bos.bitWrite(bit - '0'); // 将字符'0'或'1'转换为整数0或1
                        } catch (Exception e) {
                            throw new IOException("写入比特时出错: " + e.getMessage(), e);
                        }
                    }
                }
            }
            
            // 关闭输出流，确保所有位都被写入
            bos.close();
        }
    }
    
    // 解压缩文件
    public static void decompress(String inputFilePath, String outputFilePath) throws IOException {
        try (BitInputStream bis = new BitInputStream(inputFilePath);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            
            // 1. 读取频率表
            Map<Byte, Integer> frequencyMap = HuffmanTree.readFrequencyTable(bis);
            
            // 2. 重建哈夫曼树
            HuffmanTree huffmanTree = new HuffmanTree(frequencyMap);
            
            // 3. 读取原始数据长度
            long originalSize = 0;
            for (int i = 0; i < 8; i++) {
                int b = bis.byteRead();
                if (b == -1) {
                    throw new IOException("无法读取原始文件大小");
                }
                originalSize = (originalSize << 8) | (b & 0xFF);
            }
            
            // 4. 解压缩数据
            long decompressedCount = 0;
            while (decompressedCount < originalSize) {
                try {
                    byte data = huffmanTree.decodeNextByte(bis);
                    bos.write(data);
                    decompressedCount++;
                } catch (IOException e) {
                    // 如果到达文件末尾但还没解压缩完所有数据，则退出循环
                    break;
                }
            }
            
            bos.flush();
        }
    }
    
    // 主方法用于测试
    public static void main(String[] args) {
        // 确保输出目录存在
        File huffcasesDir = new File("huffcases");
        File huffToTxtDir = new File("huff_to_txt");
        huffcasesDir.mkdirs();
        huffToTxtDir.mkdirs();
        
        // 批量处理testcases文件夹中的所有文件
        File testcasesDir = new File("testcases");
        File[] testFiles = testcasesDir.listFiles((dir, name) -> name.endsWith(".txt"));
        
        if (testFiles != null) {
            for (File testFile : testFiles) {
                try {
                    String inputFileName = testFile.getName();
                    String inputFilePath = "testcases/" + inputFileName;
                    
                    // 压缩文件后缀改为.txt.huff，放到huffcases文件夹下
                    String compressedFile = "huffcases/" + inputFileName + ".huff";
                    
                    // 解压缩的文件放到huff_to_txt文件夹下
                    String decompressedFile = "huff_to_txt/" + inputFileName.replace(".txt", "_decompressed.txt");
                    
                    System.out.println("\n处理文件: " + inputFileName);
                    
                    // 压缩
                    long startTime = System.currentTimeMillis();
                    compress(inputFilePath, compressedFile);
                    long endTime = System.currentTimeMillis();
                    
                    System.out.println("  压缩完成！");
                    System.out.println("  压缩时间: " + (endTime - startTime) + " ms");
                    System.out.println("  原始文件大小: " + testFile.length() + " 字节");
                    System.out.println("  压缩文件大小: " + new File(compressedFile).length() + " 字节");
                    
                    // 解压缩
                    startTime = System.currentTimeMillis();
                    decompress(compressedFile, decompressedFile);
                    endTime = System.currentTimeMillis();
                    
                    System.out.println("  解压缩完成！");
                    System.out.println("  解压缩时间: " + (endTime - startTime) + " ms");
                    System.out.println("  解压缩文件大小: " + new File(decompressedFile).length() + " 字节");
                    
                    // 验证解压缩后的文件是否与原始文件相同
                    boolean filesMatch = compareFiles(testFile, new File(decompressedFile));
                    System.out.println("  文件验证: " + (filesMatch ? "成功" : "失败"));
                    
                } catch (IOException e) {
                    System.err.println("处理文件时出错: " + testFile.getName());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("未找到测试文件");
        }
    }
    
    // 比较两个文件是否相同
    private static boolean compareFiles(File file1, File file2) throws IOException {
        if (file1.length() != file2.length()) {
            return false;
        }
        
        try (BufferedInputStream bis1 = new BufferedInputStream(new FileInputStream(file1));
             BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(file2))) {
            int byte1, byte2;
            while ((byte1 = bis1.read()) != -1) {
                byte2 = bis2.read();
                if (byte1 != byte2) {
                    return false;
                }
            }
        }
        return true;
    }
}