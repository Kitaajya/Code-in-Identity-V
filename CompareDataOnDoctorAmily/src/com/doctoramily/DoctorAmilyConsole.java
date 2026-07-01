package com.doctoramily;

public class DoctorAmilyConsole {

    private static final double BASE_TIME = 5.43;
    private static final double PENALTY_RATE = 0.05;
    private static final double MIN_SPEED_RATIO = 0.50;
    private static final int TOTAL_HEALS = 9;

    public static void main(String[] args) {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  医生艾米丽 · 自疗惩罚机制数值分析（控制台版）");
        System.out.println("═══════════════════════════════════════════════════\n");

        System.out.printf("  基础参数:%n");
        System.out.printf("    初始自疗时间 : %.2fs%n", BASE_TIME);
        System.out.printf("    每次惩罚     : 速度 × %.2f (降低 %d%%)%n", 1 - PENALTY_RATE, (int)(PENALTY_RATE * 100));
        System.out.printf("    惩罚上限     : 速度降至初始 %d%%%n", (int)(MIN_SPEED_RATIO * 100));
        System.out.printf("    达到上限次数 : %d 次%n%n", DoctorAmilyAnalyzer.calcTimesToMaxPenalty());
        System.out.printf("    上限后单次   : %.2fs  上限后两次: %.2fs%n%n",
                DoctorAmilyAnalyzer.calcTimeAtMaxPenalty(),
                DoctorAmilyAnalyzer.calcTwoHealsAtMaxPenalty());

        System.out.println("  ┌──────┬──────────┬──────────┬────────┬────────────┬────────────┬──────────┐");
        System.out.println("  │ 次数 │ 原有时间  │ 现有时间  │ 速度   │ 原有累计   │ 现有累计   │ 差值     │");
        System.out.println("  ├──────┼──────────┼──────────┼────────┼────────────┼────────────┼──────────┤");

        double cumOrig = 0, cumNew = 0;
        double totalOrig = 0, totalNew = 0;

        for (int i = 0; i < TOTAL_HEALS; i++) {
            double orig = BASE_TIME;
            double cur = DoctorAmilyAnalyzer.timeForHeal(i);
            double ratio = DoctorAmilyAnalyzer.speedAfterHeals(i) * 100;
            cumOrig += orig;
            cumNew += cur;
            totalOrig += orig;
            totalNew += cur;
            double diff = cur - orig;

            String diffStr = String.format("+%.2f", diff);
            String cumDiffStr = String.format("+%.2f", cumNew - cumOrig);

            System.out.printf("  │  %d  │  %.2f   │  %.2f   │ %.1f%%  │   %.2f     │   %.2f     │ %s  │%n",
                    i + 1, orig, cur, ratio, cumOrig, cumNew, cumDiffStr);
        }

        System.out.println("  ├──────┼──────────┼──────────┼────────┼────────────┼────────────┼──────────┤");
        System.out.printf("  │ 合计 │  %.2f   │  %.2f   │        │            │            │ +%.2f   │%n",
                totalOrig, totalNew, totalNew - totalOrig);
        System.out.println("  └──────┴──────────┴──────────┴────────┴────────────┴────────────┴──────────┘");

        System.out.printf("%n  原有 4 次总时间: %.2fs   现有 4 次总时间: %.2fs   削弱: +%.2fs (+%.1f%%)%n%n",
                totalOrig, totalNew, totalNew - totalOrig, (totalNew / totalOrig - 1) * 100);

        // Per-heal diff breakdown
        System.out.println("  逐次差值明细:");
        for (int i = 0; i < TOTAL_HEALS; i++) {
            double orig = BASE_TIME;
            double cur = DoctorAmilyAnalyzer.timeForHeal(i);
            double diff = cur - orig;
            String bar = diffBar(diff, 0.9);
            System.out.printf("    第%d次 (惩罚后速度%.0f%%):  %.2fs → %.2fs  %s +%.2fs%n",
                    i + 1, DoctorAmilyAnalyzer.speedAfterHeals(i) * 100, orig, cur, bar, diff);
        }

        System.out.println();
        System.out.println("  ── 进度条对比 ──");
        for (int i = 0; i < TOTAL_HEALS; i++) {
            double orig = BASE_TIME;
            double cur = DoctorAmilyAnalyzer.timeForHeal(i);
            double max = Math.max(orig, DoctorAmilyAnalyzer.timeForHeal(TOTAL_HEALS - 1));
            int barW = 30;

            int origBars = (int)(orig / max * barW);
            int curBars = (int)(cur / max * barW);

            System.out.printf("  第%d次: %n", i + 1);
            System.out.printf("    原有 %s %.2fs%n",
                    "█".repeat(origBars), orig);
            System.out.printf("    现有 %s %.2fs%n",
                    "█".repeat(curBars), cur);
        }

        System.out.println("\n═══════════════════════════════════════════════════\n");
    }

    private static String diffBar(double diff, double maxDiff) {
        int n = (int)(Math.min(Math.abs(diff), maxDiff) / maxDiff * 10);
        if (diff <= 0) return "";
        return "▌" + "▓".repeat(Math.max(n, 1)) + "░".repeat(Math.max(10 - n, 0)) + "▐";
    }
}
