package com.bihe0832.android.lib.utils;

import java.util.Random;

/**
 * @author zixie
 */
public class MathUtils {

    /**
     * 获取两个数字区间的随机数
     *
     * @param min 下限
     * @param max 上限
     * @return 最终数
     */
    public static int getRandNumByLimit(int min, int max) {
        return (int) Math.round(Math.random() * (max - min) + min);
    }

    /**
     * 从非负整数 [0, N) 中等概率不重复地选择 K 个数,N > K
     */
    public static int[] sample(int N, int K) {
        Random random = new Random();
        int result[] = new int[K];
        int selection = K, remains = N;
        for (int i = 0; i < N; i++) {
            if (random.nextInt(remains) < selection) {
                result[K - selection] = i;
                selection--;
            }
            remains--;
        }
        return result;
    }

    public static int getMin(int... params) {
        int min = params[0];
        for (int para : params) {
            if (para < min) {
                min = para;
            }
        }
        return min;
    }

    public static int getMax(int... params) {
        int max = params[0];
        for (int para : params) {
            if (para > max) {
                max = para;
            }
        }
        return max;
    }
}
