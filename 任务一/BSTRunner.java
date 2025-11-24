import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * 二叉搜索树测试运行器类
 * 用于读取测试用例文件，执行各种BST操作，并将结果输出到文件
 */
public class BSTRunner {
    /**
     * 主方法，程序入口点
     * 负责初始化BST，处理文件IO，并执行各种操作
     */
    public static void main(String[] args) {
        // 创建二叉搜索树实例，键和值均为String类型
        BST<String, String> bst = new BST<>();
        
        System.out.println("测试运行器已启动...");

        // 定义输入和输出文件路径
        String inputFile = "BST_testcases.txt";  // 测试用例文件
        String outputFile = "BST_testresult.txt";  // 结果输出文件
        
        // 保存原始输出流
        PrintStream originalOut = System.out;
        PrintStream fileOut = null;
        
        // 使用try-with-resources语句自动关闭资源
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {  // 读取测试用例
            
            // 重定向标准输出到文件
            fileOut = new PrintStream(outputFile);
            System.setOut(fileOut);
            
            String line;
            // 逐行读取测试用例
            while ((line = reader.readLine()) != null) {
                // 去除行首尾空白字符
                line = line.trim();
                
                // 跳过空行
                if (line.isEmpty() ) {
                    continue;
                }
                
                // 提取操作符（第一个字符）和操作内容
                char operation = line.charAt(0);  // 操作符：+插入, -删除, ?查找, =更新。#显示树结构
                String content = line.substring(1).trim();  // 操作的具体内容
                
                // 根据不同的操作符执行相应的操作
                switch (operation) {
                    case '+':  // 插入操作
                        parseAndInsert(bst, content);
                        break;
                    case '-':  // 删除操作
                        parseAndRemove(bst, content);
                        break;
                    case '?':  // 查找操作
                        parseAndSearch(bst, content);
                        break;
                    case '=':  // 更新操作
                        parseAndUpdate(bst, content);
                        break;
                    case '#':  // 显示树结构操作
                        bst.showStructure();
                        break;
                    default:  // 忽略不支持的操作
                        break;
                }
            }
            
            // 将完成提示信息的输出移到finally块之后
            
        } catch (IOException e) {  // 捕获IO异常
        } finally {
            // 恢复原始输出流
            System.setOut(originalOut);
            // 关闭文件输出流
            if (fileOut != null) {
                fileOut.flush(); // 确保所有缓冲的输出都被写入文件
                fileOut.close();
            }
            // 在恢复原始输出流后，输出完成提示信息
            System.out.println("测试完成，结果已保存到 " + outputFile);
        }

        System.out.println("测试运行器已退出...");
    }
    
    /**
     * 解析并执行插入操作
     * @param bst 二叉搜索树实例
     * @param content 操作内容，格式为：(key,value)
     */
    private static void parseAndInsert(BST<String, String> bst, String content) {
        try {
            // 移除括号并按逗号分割，最多分割成两部分（键和值）
            String[] parts = content.substring(1, content.length() - 1).split(",", 2);
            // 确保成功分割出键和值
            if (parts.length == 2) {
                // 提取键和值，并去除首尾空白
                String key = parts[0].trim();
                String value = parts[1].trim();
                
                // 移除值两端可能存在的引号
                if (value.startsWith("\"")) {
                    value = value.substring(1);
                }
                if (value.endsWith("\"")) {
                    value = value.substring(0, value.length() - 1);
                }
                
                // 执行插入操作
                bst.insert(key, value);
            }
            } catch (Exception e) {  // 捕获解析异常，忽略错误的输入
        }
    }
    
    /**
     * 解析并执行删除操作
     * @param bst 二叉搜索树实例
     * @param content 操作内容，格式为：(key)
     */
    private static void parseAndRemove(BST<String, String> bst, String content) {
        try {
            // 移除括号并提取键
            String key = content.substring(1, content.length() - 1).trim();
            
            // 执行删除操作
            bst.remove(key);
        } catch (Exception e) {
        }
    }
    
    /**
     * 解析并执行查找操作
     * @param bst 二叉搜索树实例
     * @param content 操作内容，格式为：(key)
     * @param writer 输出写入器（虽然传递但实际输出由BST类内部处理）
     */
    private static void parseAndSearch(BST<String, String> bst, String content) {
        try {
            // 移除括号并提取键
            String key = content.substring(1, content.length() - 1).trim();
            
            // 执行查找操作
            bst.search(key);
        } catch (Exception e) {
        }
    }
    
    /**
     * 解析并执行更新操作
     * @param bst 二叉搜索树实例
     * @param content 操作内容，格式为：(key,newValue)
     * @param writer 输出写入器（虽然传递但实际输出由BST类内部处理）
     */
    private static void parseAndUpdate(BST<String, String> bst, String content) {
        try {
            // 移除括号并按逗号分割，最多分割成两部分（键和新值）
            String[] parts = content.substring(1, content.length() - 1).split(",", 2);
            
            // 确保成功分割出键和新值
            if (parts.length == 2) {
                // 提取键和新值，并去除首尾空白
                String key = parts[0].trim();
                String value = parts[1].trim();
                
                // 移除新值两端可能存在的引号
                if (value.startsWith("\"")) {
                    value = value.substring(1);
                }
                if (value.endsWith("\"")) {
                    value = value.substring(0, value.length() - 1);
                }
                
                // 执行更新操作
                bst.update(key, value);
            }
        } catch (Exception e) {
        }
    }
}
