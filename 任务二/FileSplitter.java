import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileSplitter {
    public static void main(String[] args) {
        String sourceFilePath = "唐家三少《斗罗大陆》精校全本.txt";
        String outputDirPath = "testcases/";
        
        // 确保输出目录存在
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        // 定义要生成的不同文件大小（单位：字节）
        // 生成合理大小的测试文件，避免过大文件导致Git问题
        int[] fileSizes = {10, 100, 1024, 4096, 10240, 32768, 65536, 1048576, 10485760}; // 10B, 100B, 1KB, 4KB, 10KB, 32KB, 64KB, 1MB, 10MB
        
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFilePath))) {
            // 创建足够大的缓冲区以读取文件
            byte[] sourceBuffer = new byte[(int) new File(sourceFilePath).length()];
            int bytesRead = bis.read(sourceBuffer);
            
            System.out.println("源文件读取完成，读取了 " + bytesRead + " 字节的数据");
            
            // 为每个指定的大小生成文件
            for (int targetSize : fileSizes) {
                String outputFileName = outputDirPath + "test_" + targetSize + "bytes.txt";
                
                // 创建恰好为targetSize大小的文件内容
                byte[] targetBuffer = new byte[targetSize];
                
                // 如果源数据足够，使用源数据；否则循环使用源数据填充
                for (int i = 0; i < targetSize; i++) {
                    targetBuffer[i] = sourceBuffer[i % bytesRead];
                }
                
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFileName))) {
                    bos.write(targetBuffer);
                    bos.flush(); // 确保数据完全写入
                }
                
                // 验证文件大小是否正确
                File createdFile = new File(outputFileName);
                long actualSize = createdFile.length();
                
                System.out.println("已创建文件: " + outputFileName);
                System.out.println("  目标大小: " + targetSize + " 字节");
                System.out.println("  实际大小: " + actualSize + " 字节");
                System.out.println("  状态: " + (actualSize == targetSize ? "✓ 大小正确" : "✗ 大小不匹配"));
            }
            
            System.out.println("\n文件分割完成！所有文件的实际大小已确保与文件名中指定的大小完全一致。");
            
        } catch (IOException e) {
            System.err.println("文件处理过程中出现错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 调整现有文件大小的辅助方法（如果需要）
    public static void adjustExistingFiles() {
        String testDirPath = "testcases/";
        
        // 获取testcases目录下的所有.txt文件
        File testDir = new File(testDirPath);
        File[] files = testDir.listFiles((d, name) -> name.endsWith(".txt") && name.startsWith("test_"));
        
        if (files == null || files.length == 0) {
            System.out.println("在目录 " + testDirPath + " 中未找到测试文件");
            return;
        }
        
        System.out.println("开始调整现有文件大小...");
        
        for (File file : files) {
            String fileName = file.getName();
            int targetSize = extractSizeFromFileName(fileName);
            
            if (targetSize <= 0) {
                System.out.println("无法从文件名 " + fileName + " 中提取大小信息");
                continue;
            }
            
            try {
                // 读取文件内容
                byte[] content;
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                    content = new byte[(int) file.length()];
                    bis.read(content);
                }
                
                // 创建恰好为targetSize大小的文件内容
                byte[] newContent = new byte[targetSize];
                for (int i = 0; i < targetSize; i++) {
                    newContent[i] = content[i % content.length];
                }
                
                // 重写文件
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                    bos.write(newContent);
                    bos.flush();
                }
                
                System.out.println("已调整文件: " + fileName + " 大小为 " + targetSize + " 字节");
                
            } catch (IOException e) {
                System.err.println("调整文件 " + fileName + " 大小时出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("所有文件大小调整完成！");
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
}