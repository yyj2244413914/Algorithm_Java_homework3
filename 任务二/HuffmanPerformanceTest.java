import java.io.*;
import java.text.DecimalFormat;

public class HuffmanPerformanceTest {
    
    public static void main(String[] args) {
        String testDir = "testcases/";
        String resultsFile = "huffman_performance_results.txt";
        String tempCompressedFile = "temp_compressed.huff";
        String tempDecompressedFile = "temp_decompressed.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFile))) {
            // 写入结果文件的标题行
            writer.write("文件大小(B),原始文件大小(B),压缩文件大小(B),压缩率(%),压缩时间(ms),解压缩时间(ms)");
            writer.newLine();
            
            // 列出testcases目录下的所有文件
            File dir = new File(testDir);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
            
            if (files == null || files.length == 0) {
                System.out.println("在目录 " + testDir + " 中未找到测试文件");
                return;
            }
            
            // 对每个文件进行测试
            for (File file : files) {
                String inputFilePath = file.getAbsolutePath();
                
                // 跳过临时解压文件
                if (inputFilePath.endsWith(tempDecompressedFile)) {
                    continue;
                }
                
                System.out.println("\n测试文件: " + file.getName());
                
                try {
                    // 测量压缩时间
                    long startTime = System.currentTimeMillis();
                    HuffmanCompressor.compress(inputFilePath, tempCompressedFile);
                    long compressTime = System.currentTimeMillis() - startTime;
                    
                    // 获取文件大小
                    long originalSize = file.length();
                    long compressedSize = new File(tempCompressedFile).length();
                    
                    // 计算压缩率
                    double compressionRatio = originalSize > 0 ? 
                            (1 - (double) compressedSize / originalSize) * 100 : 0;
                    
                    // 测量解压缩时间
                    startTime = System.currentTimeMillis();
                    HuffmanCompressor.decompress(tempCompressedFile, tempDecompressedFile);
                    long decompressTime = System.currentTimeMillis() - startTime;
                    
                    // 获取预期文件大小（从文件名解析）
                    String fileName = file.getName();
                    int sizeInBytes = extractSizeFromFileName(fileName);
                    
                    // 输出到控制台
                    System.out.println("预期文件大小: " + sizeInBytes + " B");
                    System.out.println("实际原始文件大小: " + originalSize + " B");
                    System.out.println("压缩后文件大小: " + compressedSize + " B");
                    System.out.println("压缩率: " + new DecimalFormat("#.##").format(compressionRatio) + "%");
                    System.out.println("压缩时间: " + compressTime + " ms");
                    System.out.println("解压缩时间: " + decompressTime + " ms");
                    
                    // 写入结果文件
                    writer.write(String.format("%d,%d,%d,%.2f,%d,%d", 
                            sizeInBytes, originalSize, compressedSize, compressionRatio, compressTime, decompressTime));
                    writer.newLine();
                    writer.flush(); // 确保每次写入都刷新到磁盘
                    
                    // 校验解压后的文件是否与原始文件相同
                    if (areFilesEqual(inputFilePath, tempDecompressedFile)) {
                        System.out.println("✓ 文件校验成功：解压后的文件与原始文件相同");
                    } else {
                        System.out.println("✗ 文件校验失败：解压后的文件与原始文件不同");
                    }
                    
                } catch (Exception e) {
                    System.err.println("处理文件时出错: " + inputFilePath);
                    e.printStackTrace();
                    
                    // 记录错误信息到结果文件
                    writer.write(String.format("%d,%d,error,error,error,error", extractSizeFromFileName(file.getName())));
                    writer.newLine();
                    writer.flush();
                }
            }
            
            System.out.println("\n所有测试完成！结果已保存到: " + resultsFile);
            
        } catch (IOException e) {
            System.err.println("记录测试结果时出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 清理临时文件
            new File(tempCompressedFile).delete();
            new File(tempDecompressedFile).delete();
        }
    }
    
    // 从文件名中提取预期的文件大小
    private static int extractSizeFromFileName(String fileName) {
        try {
            // 文件名格式应该是 "test_1024bytes.txt" 这样的格式
            int underscoreIndex = fileName.indexOf('_');
            int bytesIndex = fileName.indexOf("bytes");
            if (underscoreIndex > 0 && bytesIndex > underscoreIndex) {
                String sizeStr = fileName.substring(underscoreIndex + 1, bytesIndex - 1); // 去掉最后的下划线
                return Integer.parseInt(sizeStr);
            }
        } catch (Exception e) {
            // 如果解析失败，返回-1表示未知
            return -1;
        }
        return -1;
    }
    
    // 比较两个文件是否相同
    private static boolean areFilesEqual(String file1Path, String file2Path) throws IOException {
        File file1 = new File(file1Path);
        File file2 = new File(file2Path);
        
        // 首先检查文件大小是否相同
        if (file1.length() != file2.length()) {
            return false;
        }
        
        // 逐个字节比较
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