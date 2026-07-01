package com.doctoramily;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DoctorAmilyAnalyzer extends JFrame {

    private static final double HEALTH_MAX = 1.0;
    private static final double HEAL_PER_SHOT = 0.25;
    static final double BASE_TIME = 5.43;
    static final double PENALTY_RATE = 0.05;
    static final double MIN_SPEED_RATIO = 0.50;

    public DoctorAmilyAnalyzer() {
        setTitle("医生自疗数值对比分析");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(240, 242, 245));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createCenter(), BorderLayout.CENTER);

        add(root);
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("医生艾米丽自疗惩罚机制数值分析", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        panel.add(title, BorderLayout.CENTER);

        JPanel params = new JPanel(new GridLayout(2, 4, 15, 5));
        params.setBackground(Color.WHITE);
        params.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        String[][] data = {
                {"健康血量", String.format("%.0f%%", HEALTH_MAX * 100)},
                {"每次自疗回复", String.format("%.0f%% (1/4 血)", HEAL_PER_SHOT * 100)},
                {"初始自疗时间", BASE_TIME + " 秒"},
                {"每次惩罚", String.format("速度 × %.2f (降低 %d%%)", 1 - PENALTY_RATE, (int)(PENALTY_RATE * 100))},
                {"惩罚上限", String.format("速度降至初始 %d%%", (int)(MIN_SPEED_RATIO * 100))},
                {"达到上限所需次数", calcTimesToMaxPenalty() + " 次"},
                {"上限后单次自疗时间", String.format("%.2f 秒", calcTimeAtMaxPenalty())},
                {"上限后两次自疗时间", String.format("%.2f 秒", calcTwoHealsAtMaxPenalty())},
        };

        for (String[] row : data) {
            JLabel key = new JLabel(row[0] + " :");
            key.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            key.setForeground(Color.GRAY);
            key.setHorizontalAlignment(SwingConstants.RIGHT);
            JLabel val = new JLabel(row[1]);
            val.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
            val.setForeground(new Color(44, 62, 80));
            params.add(key);
            params.add(val);
        }

        panel.add(params, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCenter() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));

        panel.add(createStepPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(new Color(240, 242, 245));

        double t1 = calcTimeAtMaxPenalty();
        double t2 = calcTwoHealsAtMaxPenalty();
        double t3 = calcTwoHealsFromStart();

        panel.add(buildCard("问题一", "惩罚叠满后\n单次自疗时间",
                String.format("%.2f 秒", t1), new Color(231, 76, 60),
                String.format("公式: %.2f ÷ %.0f%% = %.2f", BASE_TIME, MIN_SPEED_RATIO * 100, t1)));

        panel.add(buildCard("问题二", "惩罚叠满后\n连续两次自疗",
                String.format("%.2f 秒", t2), new Color(155, 89, 182),
                String.format("公式: %.2f × 2 = %.2f", t1, t2)));

        panel.add(buildCard("问题三", "无惩罚开始\n连续两次自疗（惩罚累计）",
                String.format("%.2f 秒", t3), new Color(52, 152, 219),
                String.format("第1次: %.2f秒  第2次: %.2f秒", BASE_TIME, calcSecondHealFromStart())));

        return panel;
    }

    // --- Step-by-step animated progress ---

    private JPanel createStepPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JLabel title = new JLabel("自疗过程逐段演示", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        title.setForeground(new Color(44, 62, 80));
        panel.add(title, BorderLayout.NORTH);

        StepAnimGraph graph = new StepAnimGraph();
        panel.add(graph, BorderLayout.CENTER);

        // Controls
        JPanel ctrl = new JPanel(new GridBagLayout());
        ctrl.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel statusLabel = new JLabel("点击下一段开始第1次自疗", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        statusLabel.setForeground(new Color(44, 62, 80));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JButton playBtn = new JButton("->");
        JButton replayBtn = new JButton("重播");
        JButton resetBtn = new JButton("重置");
        JButton nextBtn = new JButton("下一段 →");

        Font btnFont = new Font("Microsoft YaHei", Font.BOLD, 13);
        playBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        replayBtn.setFont(btnFont);
        resetBtn.setFont(btnFont);
        nextBtn.setFont(btnFont);
        playBtn.setFocusPainted(false);
        replayBtn.setFocusPainted(false);
        resetBtn.setFocusPainted(false);
        nextBtn.setFocusPainted(false);
        playBtn.setEnabled(false);
        replayBtn.setEnabled(false);

        playBtn.setBackground(new Color(52, 152, 219));
        playBtn.setForeground(Color.WHITE);
        playBtn.setOpaque(true);
        playBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        replayBtn.setBackground(new Color(46, 204, 113));
        replayBtn.setForeground(Color.WHITE);
        replayBtn.setOpaque(true);
        replayBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        resetBtn.setBackground(new Color(149, 165, 166));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setOpaque(true);
        resetBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        nextBtn.setBackground(new Color(52, 152, 219));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setOpaque(true);
        nextBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        ctrl.add(statusLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 4, 0, 4);
        gbc.gridx = 0; ctrl.add(playBtn, gbc);
        gbc.gridx = 1; ctrl.add(replayBtn, gbc);
        gbc.gridx = 2; ctrl.add(resetBtn, gbc);
        gbc.gridx = 3; ctrl.add(nextBtn, gbc);

        panel.add(ctrl, BorderLayout.SOUTH);

        // Wire
        nextBtn.addActionListener(e -> {
            if (graph.isAllDone()) return;
            int step = graph.getCurrentStep() + 1;
            graph.goToStep(step);
            statusLabel.setText(String.format("第 %d 次自疗 · 自疗中...", step));
            nextBtn.setEnabled(false);
            replayBtn.setEnabled(false);
            playBtn.setEnabled(true);
            playBtn.setText("停");
            graph.start();
        });

        replayBtn.addActionListener(e -> {
            int step = graph.getCurrentStep();
            if (step < 1) return;
            graph.goToStep(step);
            statusLabel.setText(String.format("第 %d 次自疗 · 重播中...", step));
            nextBtn.setEnabled(false);
            replayBtn.setEnabled(false);
            playBtn.setEnabled(true);
            playBtn.setText("停");
            graph.start();
        });

        resetBtn.addActionListener(e -> {
            graph.stop();
            graph.goToStep(0);
            statusLabel.setText("点击「下一段」开始第 1 次自疗");
            nextBtn.setEnabled(true);
            replayBtn.setEnabled(false);
            playBtn.setEnabled(false);
            playBtn.setText("->");
        });

        playBtn.addActionListener(e -> {
            if (graph.isRunning()) {
                graph.stop();
                playBtn.setText("->");
            } else if (graph.isSegmentFinished()) {
                // do nothing, waiting for replay/next
            } else {
                graph.start();
                playBtn.setText("停");
            }
        });

        // Listen for segment completion
        Timer pollTimer = new Timer(50, null);
        pollTimer.addActionListener(ev -> {
            if (graph.isSegmentFinished() && graph.getCurrentStep() >= 1) {
                if (graph.isAllDone()) {
                    statusLabel.setText("全部自疗完成 ✓");
                    nextBtn.setEnabled(false);
                    replayBtn.setEnabled(false);
                    playBtn.setEnabled(false);
                } else {
                    statusLabel.setText(String.format("第 %d 次自疗完成 ✓  重播或继续", graph.getCurrentStep()));
                    nextBtn.setEnabled(true);
                    replayBtn.setEnabled(true);
                }
                playBtn.setText("->");
                playBtn.setEnabled(false);
                graph.stop();
                pollTimer.stop();
            }
        });

        // When animation stops due to segment end, restart poll
        graph.setSegmentEndCallback(() -> {
            SwingUtilities.invokeLater(() -> {
                if (!pollTimer.isRunning()) pollTimer.start();
            });
        });

        return panel;
    }

    // --- Card ---

    private JPanel buildCard(String title, String desc, String value, Color color, String formula) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel ttl = new JLabel(title, SwingConstants.CENTER);
        ttl.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        ttl.setForeground(color);
        card.add(ttl, BorderLayout.NORTH);

        JLabel dsc = new JLabel("<html><center>" + desc.replace("\n", "<br>") + "</center></html>", SwingConstants.CENTER);
        dsc.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        dsc.setForeground(Color.GRAY);
        card.add(dsc, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
        val.setForeground(color);
        bottom.add(val, BorderLayout.NORTH);
        JLabel fmt = new JLabel(formula, SwingConstants.CENTER);
        fmt.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        fmt.setForeground(new Color(170, 170, 170));
        bottom.add(fmt, BorderLayout.SOUTH);

        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    // --- Step animation graph ---

    private static class StepAnimGraph extends JPanel implements ActionListener {
        private static final int PAD_L = 75, PAD_R = 55, PAD_T = 50, PAD_B = 40;
        private static final int TRACK_H = 48;
        private static final int TRACK_GAP = 20;

        private final Timer timer;
        private int currentStep = 0;       // 0 = none, 1-4
        private double elapsed = 0;
        private boolean running = false;
        private boolean segmentFinished = false;
        private boolean allDone = false;
        private Runnable segmentEndCallback;

        private static final int TOTAL_HEALS = 9;
        private static final double[] SEG_TIMES_ORIG = new double[TOTAL_HEALS];
        private final double[] SEG_TIMES_NEW = new double[TOTAL_HEALS];

        StepAnimGraph() {
            timer = new Timer(33, this);
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(900, 280));
            for (int i = 0; i < TOTAL_HEALS; i++) {
                SEG_TIMES_ORIG[i] = BASE_TIME;
                SEG_TIMES_NEW[i] = timeForHeal(i);
            }
        }

        int getCurrentStep() { return currentStep; }
        boolean isRunning() { return running; }
        boolean isSegmentFinished() { return segmentFinished; }
        boolean isAllDone() { return allDone; }
        void setSegmentEndCallback(Runnable r) { segmentEndCallback = r; }

        void goToStep(int step) {
            if (step < 0 || step > TOTAL_HEALS) return;
            if (step > 0 && allDone) return;
            currentStep = step;
            elapsed = 0;
            running = false;
            segmentFinished = false;
            if (step == 0) allDone = false;
            repaint();
        }

        void start() {
            if (currentStep < 1 || currentStep > TOTAL_HEALS || allDone || segmentFinished) return;
            running = true;
            timer.start();
        }

        void stop() {
            running = false;
            timer.stop();
        }

        void resetAll() {
            stop();
            currentStep = 0;
            elapsed = 0;
            segmentFinished = false;
            allDone = false;
            repaint();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!running || currentStep < 1) return;

            double segOrig = SEG_TIMES_ORIG[currentStep - 1];
            double segNew = SEG_TIMES_NEW[currentStep - 1];
            double displayTotal = Math.max(segOrig, segNew);

            elapsed += 0.033;

            if (elapsed >= displayTotal) {
                elapsed = displayTotal;
                segmentFinished = true;
                stop();
                if (currentStep >= TOTAL_HEALS) allDone = true;
                if (segmentEndCallback != null) segmentEndCallback.run();
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (currentStep < 1) {
                g2.setColor(new Color(180, 180, 180));
                g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
                String msg = "点击下方「下一段」开始演示";
                g2.drawString(msg, (getWidth() - g2.getFontMetrics().stringWidth(msg)) / 2, PAD_T + 40);
                return;
            }

            int w = getWidth() - PAD_L - PAD_R;
            int y = PAD_T;

            double segOrig = SEG_TIMES_ORIG[currentStep - 1];
            double segNew = SEG_TIMES_NEW[currentStep - 1];
            double displayTotal = Math.max(segOrig, segNew);

            // Heal number badge
            g2.setColor(new Color(44, 62, 80));
            g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
            String badge;
            if (currentStep <= 4) {
                badge = "第 " + currentStep + " 次自疗  (" + (currentStep * 25) + "%)";
            } else {
                badge = "第 " + currentStep + " 次自疗  (超出满血)";
            }
            int bw = g2.getFontMetrics().stringWidth(badge);
            g2.drawString(badge, (getWidth() - bw) / 2, y - 12);

            // Original track
            paintSegTrack(g2, "原有 (无惩罚)", new Color(52, 152, 219),
                    segOrig, displayTotal, Math.min(elapsed, segOrig), y, w, false);

            // New track
            paintSegTrack(g2, "现有 (惩罚累计)", new Color(231, 76, 60),
                    segNew, displayTotal, Math.min(elapsed, segNew), y + TRACK_H + TRACK_GAP, w, true);

            // Elapsed time display (right side)
            g2.setFont(new Font("Consolas", Font.BOLD, 11));
            String origT = String.format("%.2fs / %.2fs", Math.min(elapsed, segOrig), segOrig);
            g2.setColor(new Color(52, 152, 219));
            g2.drawString(origT, PAD_L + w + 8, y + TRACK_H / 2 + 4);

            String newT = String.format("%.2fs / %.2fs", Math.min(elapsed, segNew), segNew);
            g2.setColor(new Color(231, 76, 60));
            g2.drawString(newT, PAD_L + w + 8, y + TRACK_H + TRACK_GAP + TRACK_H / 2 + 4);

            // Time marker scale
            g2.setColor(new Color(180, 180, 180));
            g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 9));
            int mx = y + 2 * (TRACK_H + TRACK_GAP) + 12;
            for (double t = 0; t <= displayTotal + 0.1; t += 1) {
                int x = PAD_L + (int) (w * t / displayTotal);
                g2.drawLine(x, mx - 3, x, mx + 3);
                String st = String.format("%.0fs", t);
                g2.drawString(st, x - g2.getFontMetrics().stringWidth(st) / 2, mx + 14);
            }
            g2.drawLine(PAD_L, mx, PAD_L + w, mx);

            // Difference & speed ratio
            double diff = segNew - segOrig;
            double pct = (segNew / segOrig - 1) * 100;
            double ratio = speedAfterHeals(currentStep - 1);

            g2.setColor(new Color(231, 76, 60));
            g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
            String diffStr = String.format("差值: +%.2fs  (+%.1f%%)", diff, pct);
            if (Math.abs(diff) < 0.001) {
                diffStr = "差值: 0s（无惩罚）";
                g2.setColor(new Color(150, 150, 150));
            }
            g2.drawString(diffStr, PAD_L, mx + 30);

            g2.setColor(new Color(100, 100, 100));
            g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            g2.drawString(String.format("速度倍率: %.0f%%", ratio * 100), PAD_L, mx + 50);
        }

        private void paintSegTrack(Graphics2D g2, String label, Color color,
                                    double segTime, double displayTotal, double fillTime,
                                    int y, int w, boolean isNew) {
            // Label
            g2.setColor(new Color(80, 80, 80));
            g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
            g2.drawString(label, 5, y + TRACK_H / 2 + 4);

            // Track bg
            g2.setColor(new Color(242, 242, 242));
            g2.fillRoundRect(PAD_L, y, w, TRACK_H, 8, 8);
            g2.setColor(new Color(210, 210, 210));
            g2.drawRoundRect(PAD_L, y, w, TRACK_H, 8, 8);

            // Segment end marker (vertical dashed line)
            int segEndX = PAD_L + (int) (w * segTime / displayTotal);
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3,3}, 0));
            g2.drawLine(segEndX, y + 4, segEndX, y + TRACK_H - 4);
            g2.setStroke(new BasicStroke(1));

            // Original end arrow label
            if (!isNew) {
                g2.setColor(new Color(52, 152, 219, 200));
                g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
                String tag = "原有结束 →";
                int tw = g2.getFontMetrics().stringWidth(tag);
                int tx = segEndX - tw - 4;
                int ty = y + TRACK_H / 2 + 4;
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(segEndX - 2, ty - 6, segEndX - 2, ty + 6);
                g2.drawLine(segEndX - 6, ty - 2, segEndX - 2, ty);
                g2.drawLine(segEndX - 6, ty + 2, segEndX - 2, ty);
                g2.setStroke(new BasicStroke(1));
                g2.drawString(tag, tx, ty);
            }

            // Fill
            if (fillTime > 0) {
                int fillW = Math.max(2, (int) (w * fillTime / displayTotal));
                g2.setColor(color);
                g2.fillRoundRect(PAD_L, y, fillW, TRACK_H, 8, 8);

                // Remaining time label
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Consolas", Font.BOLD, 11));
                String ft = String.format("%.1fs", fillTime);
                int ftw = g2.getFontMetrics().stringWidth(ft);
                if (fillW > ftw + 16) {
                    g2.drawString(ft, PAD_L + 8, y + TRACK_H / 2 + 4);
                }
            }
        }
    }

    // --- Calculations ---

    static int calcTimesToMaxPenalty() {
        double ratio = 1.0;
        for (int i = 0; i < 1000; i++) {
            if (ratio <= MIN_SPEED_RATIO) return i;
            ratio *= (1 - PENALTY_RATE);
        }
        return -1;
    }

    static double speedAfterHeals(int n) {
        double ratio = 1.0;
        for (int i = 0; i < n; i++) ratio *= (1 - PENALTY_RATE);
        return Math.max(ratio, MIN_SPEED_RATIO);
    }

    static double timeForHeal(int previousHeals) {
        double t = BASE_TIME / speedAfterHeals(previousHeals);
        return Math.round(t * 1000000.0) / 1000000.0;
    }

    static double calcTimeAtMaxPenalty() {
        return BASE_TIME / MIN_SPEED_RATIO;
    }

    static double calcTwoHealsAtMaxPenalty() {
        return 2 * calcTimeAtMaxPenalty();
    }

    static double calcSecondHealFromStart() {
        return BASE_TIME / speedAfterHeals(1);
    }

    static double calcTwoHealsFromStart() {
        return timeForHeal(0) + timeForHeal(1);
    }

     public static void main(String[] args) {
        SwingUtilities.invokeLater(DoctorAmilyAnalyzer::new);
    }
}
