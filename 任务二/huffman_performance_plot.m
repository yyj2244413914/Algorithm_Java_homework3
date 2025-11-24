% 哈夫曼压缩性能分析绘图脚本
% 基于提供的性能数据创建可视化图表

% 清除当前工作区
clear;
clc;

% 简化版 - 移除系统检测和字体设置以避免兼容性问题

% 数据准备 - 从提供的性能数据
file_sizes = [10, 100, 1024, 4096, 10240, 32768, 65536, 1048576, 10485760];
original_sizes = file_sizes; % 原始文件大小
compressed_sizes = [58, 317, 1248, 3616, 8339, 25498, 50410, 793000, 7930000]; % 压缩文件大小
compression_rates = [-480.00, -217.00, -21.88, 11.72, 18.56, 22.19, 23.08, 24.40, 24.40]; % 压缩率(%)
compression_times = [0, 4, 1, 1, 7, 7, 16, 100, 700]; % 压缩时间(ms)
decompression_times = [10, 2, 2, 1, 17, 49, 18, 50, 350]; % 解压缩时间(ms)

% 创建图形窗口
figure('Position', [100, 100, 1000, 800]);

% 第一个子图：时间性能分析（压缩和解压缩时间）
subplot(2, 1, 1);

% 绘制压缩和解压缩时间曲线
plot(file_sizes, compression_times, 'ro-', 'LineWidth', 2, 'MarkerSize', 8);
hold on;
plot(file_sizes, decompression_times, 'bo-', 'LineWidth', 2, 'MarkerSize', 8);

% 设置双对数坐标，因为文件大小跨度很大
set(gca, 'XScale', 'log');
set(gca, 'YScale', 'log');

% 设置坐标轴范围和标签
xlabel('文件大小 (字节)', 'FontSize', 14);
ylabel('时间 (毫秒)', 'FontSize', 14);
title('哈夫曼压缩与解压缩时间性能分析', 'FontSize', 16, 'FontWeight', 'bold');

% 添加网格线
grid on;
grid minor;

% 添加图例
legend('压缩时间', '解压缩时间', 'Location', 'Best', 'FontSize', 12);

% 第二个子图：压缩率分析
subplot(2, 1, 2);

% 绘制压缩率曲线
plot(file_sizes, compression_rates, 'go-', 'LineWidth', 2, 'MarkerSize', 8);

% 设置X轴为对数坐标
set(gca, 'XScale', 'log');

% 设置坐标轴范围和标签
xlabel('文件大小 (字节)', 'FontSize', 14);
ylabel('压缩率 (%)', 'FontSize', 14);
title('哈夫曼压缩率分析', 'FontSize', 16, 'FontWeight', 'bold');

% 添加水平零线，便于观察正压缩和负压缩
hold on;
plot([min(file_sizes), max(file_sizes)], [0, 0], 'k--', 'LineWidth', 1.5);

% 添加网格线
grid on;
grid minor;

% 优化图形布局
set(gcf, 'Color', 'white');
tight_layout;

% 保存图形到文件
saveas(gcf, 'huffman_performance_plots.png', 'png');
saveas(gcf, 'huffman_performance_plots.fig', 'fig');

% 显示关键发现文本框
annotation('textbox', [0.1, 0.02, 0.8, 0.05], ...
    'String', '关键发现: 1) 小文件(<4KB)出现负压缩; 2) 大文件压缩率稳定在24.4%; 3) 压缩时间随文件大小增长而增加', ...
    'FontSize', 12, ...
    'FitBoxToText', 'on', ...
    'EdgeColor', 'none', ...
    'BackgroundColor', 'none');

% 显示结果
fprintf('图形已生成并保存为 huffman_performance_plots.png 和 huffman_performance_plots.fig\n');