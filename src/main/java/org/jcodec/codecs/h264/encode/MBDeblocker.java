package org.jcodec.codecs.h264.encode;

import static java.lang.Math.abs;

import org.jcodec.codecs.h264.decode.deblock.DeblockingFilter;
import org.jcodec.common.model.Picture;
import org.jcodec.common.tools.MathUtil;

/**
 * Contains various deblocking filter routines for deblocking on MB bases
 * 
 * @author Stan Vitvitskyy
 * 
 */
public class MBDeblocker {

    private static int[][] P_POS_V = buildPPosV();
    private static int[][] Q_POS_V = buildQPosV();
    private static int[][] P_POS_H = buildPPosH();
    private static int[][] Q_POS_H = buildQPosH();

    private static int[][] P_POS_V_CHR = buildPPosVChr();
    private static int[][] Q_POS_V_CHR = buildQPosVChr();

    private static int[][] P_POS_H_CHR = buildPPosHChr();
    private static int[][] Q_POS_H_CHR = buildQPosHChr();

    static int[][] LOOKUP_IDX_P_V = new int[][] { { 3, 7, 11, 15 }, { 0, 4, 8, 12 }, { 1, 5, 9, 13 }, { 2, 6, 10, 14 } };
    static int[][] LOOKUP_IDX_Q_V = new int[][] { { 0, 4, 8, 12 }, { 1, 5, 9, 13 }, { 2, 6, 10, 14 }, { 3, 7, 11, 15 } };
    static int[][] LOOKUP_IDX_P_H = new int[][] { { 12, 13, 14, 15 }, { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 } };
    static int[][] LOOKUP_IDX_Q_H = new int[][] { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 } };

    // Luma Chroma
    // 4444 44
    // 4333 43
    // 4333
    // 4333
    private static int[][] BS_I = { { 4, 4, 4, 4 }, { 3, 3, 3, 3 }, { 3, 3, 3, 3 }, { 3, 3, 3, 3 } };

    /**
     * Deblocks bottom edge of topOutMB, right edge of leftOutMB and left/top
     * and inner block edges of outMB
     * 
     * @param curPix
     *            Pixels of the current MB
     * @param leftPix
     *            Pixels of the leftMB
     * @param topPix
     *            Pixels of the tipMB
     * 
     * @param vertStrength
     *            Border strengths for vertical edges (filtered first)
     * @param horizStrength
     *            Border strengths for the horizontal edges
     * 
     * @param curQp
     *            Current MB's qp
     * @param leftQp
     *            Left MB's qp
     * @param topQp
     *            Top MB's qp
     */
    public void deblockMBGeneric(EncodedMB curMB, EncodedMB leftMB, EncodedMB topMB, int[][] vertStrength,
            int horizStrength[][]) {
        Picture curPix = curMB.getPixels();
        if (leftMB != null) {
            Picture leftPix = leftMB.getPixels();
            int avgQp = MathUtil.clip((leftMB.getQp() + curMB.getQp() + 1) >> 1, 0, 51);
            deblockBorder(vertStrength[0], avgQp, leftPix.getPlaneData(0), 3, curPix.getPlaneData(0), 0, P_POS_V,
                    Q_POS_V, false);
            deblockBorderChroma(vertStrength[0], avgQp, leftPix.getPlaneData(1), 3, curPix.getPlaneData(1), 0,
                    P_POS_V_CHR, Q_POS_V_CHR, false);
            deblockBorderChroma(vertStrength[0], avgQp, leftPix.getPlaneData(2), 3, curPix.getPlaneData(2), 0,
                    P_POS_V_CHR, Q_POS_V_CHR, false);
        }
        for (int i = 0; i < 3; i++) {
            deblockBorder(vertStrength[i + 1], curMB.getQp(), curPix.getPlaneData(0), i, curPix.getPlaneData(0), i + 1,
                    P_POS_V, Q_POS_V, false);
            deblockBorderChroma(vertStrength[i + 1], curMB.getQp(), curPix.getPlaneData(1), i, curPix.getPlaneData(1),
                    i + 1, P_POS_V_CHR, Q_POS_V_CHR, false);
            deblockBorderChroma(vertStrength[i + 1], curMB.getQp(), curPix.getPlaneData(2), i, curPix.getPlaneData(2),
                    i + 1, P_POS_V_CHR, Q_POS_V_CHR, false);
        }

        if (topMB != null) {
            Picture topPix = topMB.getPixels();
            int avgQp = MathUtil.clip((topMB.getQp() + curMB.getQp() + 1) >> 1, 0, 51);
            deblockBorder(horizStrength[0], avgQp, topPix.getPlaneData(0), 3, curPix.getPlaneData(0), 0, P_POS_H,
                    Q_POS_H, true);
            deblockBorderChroma(horizStrength[0], avgQp, topPix.getPlaneData(1), 3, curPix.getPlaneData(1), 0,
                    P_POS_H_CHR, Q_POS_H_CHR, true);
            deblockBorderChroma(horizStrength[0], avgQp, topPix.getPlaneData(2), 3, curPix.getPlaneData(2), 0,
                    P_POS_H_CHR, Q_POS_H_CHR, true);
        }
        for (int i = 0; i < 3; i++) {
            deblockBorder(horizStrength[i + 1], curMB.getQp(), curPix.getPlaneData(0), i, curPix.getPlaneData(0),
                    i + 1, P_POS_H, Q_POS_H, true);
            deblockBorderChroma(horizStrength[i + 1], curMB.getQp(), curPix.getPlaneData(1), i, curPix.getPlaneData(1),
                    i + 1, P_POS_H_CHR, Q_POS_H_CHR, true);
            deblockBorderChroma(horizStrength[i + 1], curMB.getQp(), curPix.getPlaneData(2), i, curPix.getPlaneData(2),
                    i + 1, P_POS_H_CHR, Q_POS_H_CHR, true);
        }
    }

    public void deblockMBI(EncodedMB outMB, EncodedMB leftOutMB, EncodedMB topOutMB) {
        deblockMBGeneric(outMB, leftOutMB, topOutMB, BS_I, BS_I);
    }

    /**
     * Deblocks P-macroblock
     * 
     * @param curMB
     *            Pixels and parameters of encoded and reconstructed current
     *            macroblock
     * @param leftOutMB
     *            Pixels and parameters of encoded and reconstructed left
     *            macroblock
     * @param topOutMB
     *            Pixels and parameters of encoded and reconstructed top
     *            macroblock
     */
    public void deblockMBP(EncodedMB curMB, EncodedMB leftOutMB, EncodedMB topOutMB) {
        int[][] vertStrength = new int[4][4];
        int[][] horizStrength = new int[4][4];

        calcStrengthForBlocks(curMB, topOutMB, horizStrength, LOOKUP_IDX_P_V, LOOKUP_IDX_Q_V);
        calcStrengthForBlocks(curMB, topOutMB, horizStrength, LOOKUP_IDX_P_H, LOOKUP_IDX_Q_H);

        deblockMBGeneric(curMB, leftOutMB, topOutMB, vertStrength, horizStrength);
    }

    private void deblockBorder(int[] boundary, int qp, int[] p, int pi, int[] q, int qi, int[][] pTab, int[][] qTab,
            boolean horiz) {
        int inc1 = horiz ? 16 : 1, inc2 = inc1 * 2, inc3 = inc1 * 3;
        for (int b = 0; b < 4; b++) {
            if (boundary[b] == 4) {
                for (int i = 0, ii = b << 2; i < 4; ++i, ++ii)
                    filterBs4(qp, qp, p, q, pTab[pi][ii] - inc3, pTab[pi][ii] - inc2, pTab[pi][ii] - inc1,
                            pTab[pi][ii], qTab[qi][ii], qTab[qi][ii] + inc1, qTab[qi][ii] + inc2, qTab[qi][ii] + inc3);
            } else if (boundary[b] > 0) {
                for (int i = 0, ii = b << 2; i < 4; ++i, ++ii)
                    filterBs(boundary[b], qp, qp, p, q, pTab[pi][ii] - inc2, pTab[pi][ii] - inc1, pTab[pi][ii],
                            qTab[qi][ii], qTab[qi][ii] + inc1, qTab[qi][ii] + inc2);

            }
        }
    }

    protected void filterBs4Chr(int indexAlpha, int indexBeta, int[] pelsP, int[] pelsQ, int p1Idx, int p0Idx,
            int q0Idx, int q1Idx) {
        DeblockingFilter.filterBs4(indexAlpha, indexBeta, pelsP, pelsQ, -1, -1, p1Idx, p0Idx, q0Idx, q1Idx, -1, -1,
                true);
    }

    protected void filterBsChr(int bs, int indexAlpha, int indexBeta, int[] pelsP, int[] pelsQ, int p1Idx, int p0Idx,
            int q0Idx, int q1Idx) {
        DeblockingFilter.filterBs(bs, indexAlpha, indexBeta, pelsP, pelsQ, -1, p1Idx, p0Idx, q0Idx, q1Idx, -1, true);
    }

    protected void filterBs4(int indexAlpha, int indexBeta, int[] pelsP, int[] pelsQ, int p3Idx, int p2Idx, int p1Idx,
            int p0Idx, int q0Idx, int q1Idx, int q2Idx, int q3Idx) {
        DeblockingFilter.filterBs4(indexAlpha, indexBeta, pelsP, pelsQ, p3Idx, p2Idx, p1Idx, p0Idx, q0Idx, q1Idx,
                q2Idx, q3Idx, false);
    }

    protected void filterBs(int bs, int indexAlpha, int indexBeta, int[] pelsP, int[] pelsQ, int p2Idx, int p1Idx,
            int p0Idx, int q0Idx, int q1Idx, int q2Idx) {
        DeblockingFilter.filterBs(bs, indexAlpha, indexBeta, pelsP, pelsQ, p2Idx, p1Idx, p0Idx, q0Idx, q1Idx, q2Idx,
                false);
    }

    private void deblockBorderChroma(int[] boundary, int qp, int[] p, int pi, int[] q, int qi, int[][] pTab,
            int[][] qTab, boolean horiz) {
        int inc1 = horiz ? 8 : 1;
        for (int b = 0; b < 4; b++) {
            if (boundary[b] == 4) {
                for (int i = 0, ii = b << 1; i < 2; ++i, ++ii)
                    filterBs4Chr(qp, qp, p, q, pTab[pi][ii] - inc1, pTab[pi][ii], qTab[qi][ii], qTab[qi][ii] + inc1);
            } else if (boundary[b] > 0) {
                for (int i = 0, ii = b << 1; i < 2; ++i, ++ii)
                    filterBsChr(boundary[b], qp, qp, p, q, pTab[pi][ii] - inc1, pTab[pi][ii], qTab[qi][ii],
                            qTab[qi][ii] + inc1);
            }
        }
    }

    private static int[][] buildPPosH() {
        int[][] qPos = new int[4][16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 16; j++) {
                qPos[i][j] = j + (i << 6) + 48;
            }
        }
        return qPos;
    }

    private static int[][] buildQPosH() {
        int[][] pPos = new int[4][16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 16; j++) {
                pPos[i][j] = j + (i << 6);
            }
        }
        return pPos;
    }

    private static int[][] buildPPosV() {
        int[][] qPos = new int[4][16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 16; j++) {
                qPos[i][j] = (j << 4) + (i << 2) + 3;
            }
        }
        return qPos;
    }

    private static int[][] buildQPosV() {
        int[][] pPos = new int[4][16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 16; j++) {
                pPos[i][j] = (j << 4) + (i << 2);
            }
        }
        return pPos;
    }

    private static int[][] buildPPosHChr() {
        int[][] qPos = new int[4][8];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                qPos[i][j] = j + (i << 4) + 8;
            }
        }
        return qPos;
    }

    private static int[][] buildQPosHChr() {
        int[][] pPos = new int[4][8];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                pPos[i][j] = j + (i << 4);
            }
        }
        return pPos;
    }

    private static int[][] buildPPosVChr() {
        int[][] qPos = new int[4][8];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                qPos[i][j] = (j << 3) + (i << 1) + 1;
            }
        }
        return qPos;
    }

    private static int[][] buildQPosVChr() {
        int[][] pPos = new int[4][8];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                pPos[i][j] = (j << 3) + (i << 1);
            }
        }
        return pPos;
    }

    static void calcStrengthForBlocks(EncodedMB cur, EncodedMB other, int[][] outStrength, int[][] LOOKUP_IDX_P,
            int[][] LOOKUP_IDX_Q) {
        if (other != null) {
            for (int i = 0; i < 4; ++i) {
                outStrength[0][i] = other.getType().isIntra() ? 4 : MathUtil.max3(
                        strengthMv(other.getMx()[LOOKUP_IDX_P[0][i]], cur.getMx()[LOOKUP_IDX_Q[0][i]]),
                        strengthMv(other.getMy()[LOOKUP_IDX_P[0][i]], cur.getMy()[LOOKUP_IDX_Q[0][i]]),
                        strengthNc(other.getNc()[LOOKUP_IDX_P[0][i]], cur.getNc()[LOOKUP_IDX_Q[0][i]]));
            }
        }

        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; ++j) {
                outStrength[i][j] = MathUtil.max3(
                        strengthMv(cur.getMx()[LOOKUP_IDX_P[i][j]], cur.getMx()[LOOKUP_IDX_Q[i][j]]),
                        strengthMv(cur.getMy()[LOOKUP_IDX_P[i][j]], cur.getMy()[LOOKUP_IDX_Q[i][j]]),
                        strengthNc(cur.getNc()[LOOKUP_IDX_P[i][j]], cur.getNc()[LOOKUP_IDX_Q[i][j]]));
            }

        }
    }

    private static int strengthNc(int ncA, int ncB) {
        return ncA > 0 || ncB > 0 ? 2 : 0;
    }

    private static int strengthMv(int v0, int v1) {
        return abs(v0 - v1) >= 4 ? 1 : 0;
    }
}