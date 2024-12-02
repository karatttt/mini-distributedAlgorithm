package com.example.spring.utils.mini_gossip;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Gossip {
    int nodeCount = 10;
    double k = 1.1;
    int[] nums;
    double error;
    int time;
    double originAverage;
    double newAverage;
    List<MyNode> nodeList = new ArrayList<>();

    public void startGossipExampleSameK(int nodeCount) throws InterruptedException {
        initSameK(nodeCount);
        // start
        CountDownLatch countDownLatchForMain;
        CountDownLatch countDownLatchForStart;
        while (true) {

            countDownLatchForMain = new CountDownLatch(nodeCount);
            countDownLatchForStart = new CountDownLatch(nodeCount);
            int removeNodeCount = 0;
            for (MyNode node : nodeList) {
                if (node.p == 0) {
                    removeNodeCount++;
                }
                node.setLatch(countDownLatchForMain, countDownLatchForStart); // 设置 latch
                new Thread(node).start();
            }
            countDownLatchForMain.await(); // 这里可以让主线程等待直到所有节点启动完成
            time++;
            if (removeNodeCount == nodeCount) {
                break;
            }
        }
        for (MyNode myNode : nodeList) {
            error += Math.abs(myNode.num - originAverage);
        }
    }

    private void initSameK(int nodeCount) {
        this.nodeCount = nodeCount;
        Random random = new Random();
        // create nums
        double sum = 0;
        nums = new int[nodeCount];
        for (int i = 0; i < this.nodeCount; i++) {
            nums[i] = random.nextInt(100);
            sum += nums[i];
        }
        originAverage = sum / this.nodeCount;
        // add Node

        for (int i = 0; i < this.nodeCount; i++) {
            nodeList.add(getNode(nums[i], k, nodeList));
        }

    }

    public void startGossipExampleSameNode(double k) throws InterruptedException {
        initSameNode(k);
        // start
        CountDownLatch countDownLatchForMain;
        CountDownLatch countDownLatchForStart;
        while (true) {

            countDownLatchForMain = new CountDownLatch(nodeCount);
            countDownLatchForStart = new CountDownLatch(nodeCount);
            int removeNodeCount = 0;
            for (MyNode node : nodeList) {
                if (node.p == 0) {
                    removeNodeCount++;
                }
                node.setLatch(countDownLatchForMain, countDownLatchForStart); // 设置 latch
                new Thread(node).start();
                countDownLatchForStart.countDown();
            }
            countDownLatchForMain.await(); // 这里可以让主线程等待直到所有节点启动完成
            time++;
            if (removeNodeCount == nodeCount) {
                break;
            }
        }
        double[] finalNums = new double[nums.length];
        int i = 0;
        for (MyNode myNode : nodeList) {
            BigDecimal bd = new BigDecimal(myNode.num).setScale(6, RoundingMode.HALF_UP);
            finalNums[i++] = bd.doubleValue();
            error += Math.abs(myNode.num - originAverage);
        }
        System.out.println("最终数组：" + Arrays.toString(finalNums));
        System.out.println("误差：" + error);
        System.out.println("收敛轮数" + time);


    }

    private void initSameNode(double k) {
        this.k = k;
        error = 0;
        Random random = new Random(1);
        // create nums
        double sum = 0;
        nums = new int[nodeCount];
        for (int i = 0; i < this.nodeCount; i++) {
            nums[i] = random.nextInt(100);
            sum += nums[i];
        }
        originAverage = sum / this.nodeCount;
        // add Node
        for (int i = 0; i < this.nodeCount; i++) {
            nodeList.add(getNode(nums[i], k, nodeList));
        }


        System.out.println("k值:" + this.k);
        System.out.println("节点数:" + this.nodeCount);
        System.out.println("初始数组：" + Arrays.toString(nums));
        System.out.println("初始平均数：" + originAverage);


    }

    private MyNode getNode(double num, double k, List<MyNode> nodeList) {
        return new MyNode(num, k, nodeList) {
        };
    }

    private class MyNode implements Runnable {
        double num;
        double k;
        double p = 1L;
        List<MyNode> nodeList;
        CountDownLatch latchForMain;
        CountDownLatch latchForStart;
        public void setLatch(CountDownLatch countDownLatchForMain, CountDownLatch countDownLatchForStart) {
            this.latchForMain = countDownLatchForMain;
            this.latchForStart = countDownLatchForStart;
        }
        MyNode(double num, double k, List<MyNode> nodeList) {
            this.num = num;
            this.k = k;
            this.nodeList = nodeList;
        }
        @Override
        public void run() {
            try {
                latchForStart.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Random random = new Random();
            int i = random.nextInt(nodeList.size() - 1);
            double nextDouble = random.nextDouble();
//            if (new BigDecimal(nodeList.get(i).num).compareTo(new BigDecimal(num)) == 0) {
//                double nextDouble1 = random.nextDouble();
//                if (nextDouble1 < 1 / k) {
//                    p = 0;
//                }
//            } else {
//                double average = (nodeList.get(i).num + num) / 2;
//                nodeList.get(i).num = average;
//                num = average;
//            }
            if (nextDouble < p) {
                if (new BigDecimal(nodeList.get(i).num).compareTo(new BigDecimal(num)) == 0) {
                    p = p / k;
                } else {
                    double average = (nodeList.get(i).num + num) / 2;
                    nodeList.get(i).num = average;
                    num = average;
                }
            } else {
                p = 0;
            }
            latchForMain.countDown(); // 减少计数
        }
    }

    public static void main(String[] args) throws InterruptedException {

//        ArrayList<Double> errors = new ArrayList<>();
//        ArrayList<Integer> times = new ArrayList<>();
//        ArrayList<Integer> nodeCounts = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            Gossip gossip = new Gossip();
//            gossip.startGossipExampleSameK(5 + i * 5);
//            errors.add(gossip.error);
//            times.add(gossip.time);
//            nodeCounts.add(5 + i * 5);
//        }
////        GossipChart.createErrorDataset(errors, nodeCounts);
////        GossipChart.createTimesDataset(times, nodeCounts);
//        GossipChart.picture(times,nodeCounts, errors);

        ArrayList<Double> errors = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();
        ArrayList<Double> ks = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            Gossip gossip = new Gossip();
            gossip.startGossipExampleSameNode(1.02 + i * 0.05);
            errors.add(gossip.error);
            times.add(gossip.time);
            ks.add(1.02 + i * 0.05);
        }
//        com.example.spring.utils.mini_gossip.GossipChart.picture2(times, ks, errors);
//        Gossip gossip = new Gossip();
//        gossip.startGossipExampleSameNode(1.1);
    }

}

