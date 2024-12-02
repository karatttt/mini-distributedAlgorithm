package com.example.spring.utils.mini_mapReduce;


import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// 用户定义的 Mapper 接口
interface Mapper<K, V> {
    List<Map.Entry<K, V>> map(K key, V value);
}

// 用户定义的 Reducer 接口
interface Reducer<K, V> {
    V reduce(K key, List<V> values);
}

// MapReduceJob 类，负责协调 Map 和 Reduce 过程
class MapReduceJob<K, V> {
    private final Mapper<K, V> mapper;
    private final Reducer<K, V> reducer;
    private final ExecutorService executorService;

    // 构造方法，传入自定义的 Mapper 和 Reducer
    public MapReduceJob(Mapper mapper, Reducer reducer, int threadPoolSize) {
        this.mapper = mapper;
        this.reducer = reducer;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }
    // 执行 MapReduce 任务
    public Map<K, V> execute(List<Map.Entry<K, V>> inputData) throws InterruptedException, ExecutionException {
        // Map 阶段：并行执行 Mapper
        Map<K, List<V>> intermediateData = new HashMap<>();
        List<Future<Void>> futures = new ArrayList<>();

        for (Map.Entry<K, V> entry : inputData) {
            futures.add(executorService.submit(() -> {
                List<Map.Entry<K, V>> mappedResults = mapper.map(entry.getKey(), entry.getValue());
                for (Map.Entry<K, V> result : mappedResults) {
                    intermediateData.computeIfAbsent(result.getKey(), k -> new ArrayList<>()).add(result.getValue());
                }
                return null;
            }));
        }

        // 等待所有 Map 操作完成
        for (Future<Void> future : futures) {
            future.get();
        }
        // Reduce 阶段：对 Map 阶段的结果进行聚合
        Map<K, V> finalResults = new HashMap<>();
        for (Map.Entry<K, List<V>> entry : intermediateData.entrySet()) {
            finalResults.put(entry.getKey(), reducer.reduce(entry.getKey(), entry.getValue()));
        }

        return finalResults;
    }

    // 关闭线程池
    public void shutdown() {
        executorService.shutdown();
    }
}
// 图片匹配的 Mapper 实现
class ImageMatchMapper implements Mapper<String, String> {
    private final String targetImagePath;

    public ImageMatchMapper(String targetImagePath) {
        this.targetImagePath = targetImagePath;
    }

    @Override
    public List<Map.Entry<String, String>> map(String imageName, String imagePath) {
        List<Map.Entry<String, String>> results = new ArrayList<>();

        // 加载目标图片和当前图片
        Mat targetImage = Imgcodecs.imread(targetImagePath, Imgcodecs.IMREAD_GRAYSCALE);
        Mat currentImage = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

        if (targetImage.empty() || currentImage.empty()) {
            System.err.println("Failed to load images: " + targetImagePath + " or " + imagePath);
            return results;
        }

        // ORB 特征提取和匹配
        ORB orb = ORB.create();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        orb.detectAndCompute(targetImage, new Mat(), keypoints1, descriptors1);
        orb.detectAndCompute(currentImage, new Mat(), keypoints2, descriptors2);

        // 使用 BFMatcher 进行特征点匹配
        BFMatcher matcher = BFMatcher.create(6, true);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);

        // 计算匹配度：取匹配分数的均值
        double matchScore = 0;
        List<DMatch> matchList = matches.toList();
        for (DMatch match : matchList) {
            matchScore += match.distance;
        }
        matchScore = matchList.isEmpty() ? Double.MAX_VALUE : matchScore / matchList.size();

        // 将结果记录到 Map 中，匹配度越低越好
        results.add(new AbstractMap.SimpleEntry<>(imageName, String.valueOf(matchScore)));
        return results;
    }
}

// 简单的 Reducer 实现
class ImageMatchReducer implements Reducer<String, String> {
    @Override
    public String reduce(String imageName, List<String> scores) {
        return scores.isEmpty() ? String.valueOf(Double.MAX_VALUE) : scores.get(0); // 直接返回单一的匹配度
    }
}

public class ImageMatchMapReduce {
    static {
        // 加载 OpenCV 库
        URL url = ClassLoader.getSystemResource("opencv/opencv_java4100.dll");
        System.load(url.getPath());
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 目标图片路径
        String targetImagePath = "D://1.png";

        // 输入图片路径
        List<Map.Entry<String, String>> inputData = new ArrayList<>();
        long pre = System.currentTimeMillis();
        inputData.add(new AbstractMap.SimpleEntry<>("Image1", "D://2.png"));
        inputData.add(new AbstractMap.SimpleEntry<>("Image1", "D://2.png"));
        inputData.add(new AbstractMap.SimpleEntry<>("Image1", "D://2.png"));
        inputData.add(new AbstractMap.SimpleEntry<>("Image1", "D://2.png"));
        inputData.add(new AbstractMap.SimpleEntry<>("Image1", "D://2.png"));
        inputData.add(new AbstractMap.SimpleEntry<>("Image1", "D://2.png"));
        inputData.add(new AbstractMap.SimpleEntry<>("Image1", "D://2.png"));
        inputData.add(new AbstractMap.SimpleEntry<>("Image1", "D://2.png"));

        // 创建 MapReduce 工作
        MapReduceJob<String, String> job = new MapReduceJob<>(new ImageMatchMapper(targetImagePath), new ImageMatchReducer(), 4);

        // 执行任务
        Map<String, String> result = job.execute(inputData);

        System.out.println(System.currentTimeMillis() - pre);
        // 输出结果
        result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) // 按匹配度从小到大排序
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        // 关闭框架
        job.shutdown();
    }
}