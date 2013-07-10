package org.jcodec.codecs.vpx;

import junit.framework.Assert;

import org.jcodec.codecs.common.biari.VPxBooleanEncoder;
import org.junit.Test;

public class TestCoeffEncoder {
    static int[][][][] binProbs1 = cloneProb();
    static int[][][][] binProbs2 = cloneProb();
    static int[][][][] binProbs3 = cloneProb();

    static {
        binProbs1[1][6][2][2] = 1;
        binProbs1[1][6][2][3] = 1;
        binProbs1[1][6][2][6] = 1;
        binProbs1[2][0][0][0] = 1;
        binProbs1[2][0][1][3] = 1;
        binProbs1[2][1][2][2] = 1;
        binProbs1[2][1][2][3] = 1;
        binProbs1[2][1][2][6] = 128;
        binProbs1[2][2][2][3] = 1;
        binProbs1[2][2][2][6] = 128;
        binProbs1[2][4][2][1] = 255;
        binProbs1[2][5][0][1] = 1;
        binProbs1[2][5][0][2] = 1;
        binProbs1[2][6][0][2] = 51;
        binProbs1[2][6][0][3] = 80;
        binProbs1[2][6][1][0] = 1;
        binProbs1[2][6][1][2] = 1;
        binProbs1[2][6][1][3] = 64;
        binProbs1[2][6][2][2] = 1;

        binProbs2[2][0][0][0] = 1;
        binProbs2[2][1][2][2] = 1;
        binProbs2[2][1][2][3] = 32;
        binProbs2[2][1][2][6] = 146;
        binProbs2[2][2][2][3] = 32;
        binProbs2[2][2][2][6] = 110;
        binProbs2[2][3][2][2] = 1;
        binProbs2[2][3][2][3] = 1;
        binProbs2[2][4][2][2] = 1;
        binProbs2[2][5][2][0] = 1;
        binProbs2[2][5][2][2] = 85;
        binProbs2[2][6][0][2] = 96;
        binProbs2[2][6][1][0] = 1;
        binProbs2[2][6][1][2] = 70;
        binProbs2[2][6][1][3] = 160;
        binProbs2[2][6][2][0] = 1;
        binProbs2[2][6][2][2] = 54;
        binProbs2[3][0][2][3] = 1;
        binProbs2[3][0][2][6] = 57;
        binProbs2[3][1][2][2] = 1;
        binProbs2[3][1][2][3] = 51;
        binProbs2[3][1][2][6] = 43;
        binProbs2[3][1][2][9] = 85;
        binProbs2[3][2][2][3] = 39;
        binProbs2[3][2][2][6] = 1;
        binProbs2[3][2][2][8] = 163;
        binProbs2[3][2][2][9] = 110;
        binProbs2[3][4][2][3] = 26;
        binProbs2[3][4][2][6] = 114;
        binProbs2[3][5][0][2] = 1;
        binProbs2[3][5][2][2] = 23;
        binProbs2[3][5][2][3] = 77;
        binProbs2[3][5][2][6] = 146;
        binProbs2[3][6][0][2] = 7;
        binProbs2[3][6][0][3] = 38;
        binProbs2[3][6][0][6] = 132;
        binProbs2[3][6][0][7] = 51;
        binProbs2[3][6][2][2] = 21;
        binProbs2[3][6][2][3] = 68;
        binProbs2[3][6][2][6] = 132;
        binProbs2[3][6][2][7] = 15;
        binProbs2[3][7][0][2] = 102;
        binProbs2[3][7][2][0] = 28;
        binProbs2[3][7][2][2] = 32;

        binProbs3[0][2][2][3] = 1;
        binProbs3[0][2][2][6] = 64;
        binProbs3[0][3][2][3] = 64;
        binProbs3[0][5][2][3] = 64;
        binProbs3[0][6][2][0] = 1;
        binProbs3[0][6][2][1] = 1;
        binProbs3[0][6][2][2] = 1;
        binProbs3[0][6][2][3] = 43;
        binProbs3[0][7][2][0] = 1;
        binProbs3[1][6][2][2] = 1;
        binProbs3[1][6][2][3] = 1;
        binProbs3[1][6][2][6] = 1;
        binProbs3[2][0][0][0] = 1;
        binProbs3[2][0][1][3] = 1;
        binProbs3[2][1][2][2] = 1;
        binProbs3[2][1][2][3] = 1;
        binProbs3[2][1][2][6] = 128;
        binProbs3[2][2][2][3] = 1;
        binProbs3[2][2][2][6] = 128;
        binProbs3[2][4][2][1] = 128;
        binProbs3[2][4][2][2] = 64;
        binProbs3[2][5][0][2] = 1;
        binProbs3[2][5][2][2] = 1;
        binProbs3[2][6][0][2] = 98;
        binProbs3[2][6][0][3] = 128;
        binProbs3[2][6][1][0] = 1;
        binProbs3[2][6][1][2] = 23;
        binProbs3[2][6][1][3] = 102;
        binProbs3[2][6][2][2] = 38;
    }

    @Test
    public void testCoeffWHT() {

        VPXBitstream bs = new VPXBitstream(binProbs1, 1);
        bs.encodeCoeffsWHT(new MockVpxBooleanEncoder(
                new int[] { 198, 35, 237, 223, 162, 145, 62, 254, 254, 243, 230, 196, 177, 153, 140, 133, 130, 129,
                        128, 81, 99, 181, 242, 249, 255, 128, 180, 157, 141, 134, 130, 128, 23, 91, 163, 242, 247, 255,
                        128, 180, 157, 141, 134, 130, 128, 44, 130, 201, 253, 255, 128, 128, 173, 148, 140, 128, 45,
                        99, 1, 1, 1, 128, 128, 254, 254, 243, 230, 196, 177, 153, 140, 133, 130, 129, 128, 22, 100,
                        174, 245, 255, 128, 128, 254, 254, 243, 230, 196, 177, 153, 140, 133, 130, 129, 128, 35, 77,
                        181, 251, 255, 128, 128, 180, 157, 141, 134, 130, 128, 45, 99, 1, 1, 1, 128, 128, 254, 254,
                        243, 230, 196, 177, 153, 140, 133, 130, 129, 128, 45, 99, 1, 1, 1, 128, 128, 254, 254, 243,
                        230, 196, 177, 153, 140, 133, 130, 129, 128, 45, 99, 1, 1, 1, 128, 128, 180, 157, 141, 134,
                        130, 128, 45, 99, 1, 1, 1, 128, 128, 254, 254, 243, 230, 196, 177, 153, 140, 133, 130, 129,
                        128, 45, 99, 1, 1, 1, 128, 128, 254, 254, 243, 230, 196, 177, 153, 140, 133, 130, 129, 128, 45,
                        99, 1, 1, 1, 128, 128, 173, 148, 140, 128, 45, 99, 1, 1, 1, 128, 128, 180, 157, 141, 134, 130,
                        128, 45, 99, 1, 1, 1, 128, 128, 180, 157, 141, 134, 130, 128, 137, 1, 177, 255, 128, 128, 128,
                        254, 254, 243, 230, 196, 177, 153, 140, 133, 130, 129, 128 }, new int[] { 1, 1, 1, 1, 1, 1, 1,
                        0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                        1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1,
                        1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0,
                        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0,
                        0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
                        0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1,
                        1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1,
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1 }), unzigzag(new int[] { 156, -58, 54,
                -15, -97, -75, -54, 91, 91, -54, -75, -97, -15, 54, -58, -363 }), 0);
    }

    @Test
    public void testCoeffDCTY15() {

        VPXBitstream bs = new VPXBitstream(binProbs3, 1);
        bs.encodeCoeffsDCT15(new MockVpxBooleanEncoder(new int[] { 253, 136, 254, 255, 128, 128, 128, 173, 148, 140,
                128, 78, 134, 202, 1, 64, 128, 128, 180, 157, 141, 134, 130, 128, 77, 110, 216, 64, 128, 128, 128, 176,
                155, 140, 135, 128, 1, 1, 1, 43, 128, 128, 128, 173, 148, 140, 128, 37, 116, 196, 243, 255, 255, 165,
                145, 128, 102, 103, 231, 64, 128, 128, 159, 128, 1, 1, 1, 43, 128, 128, 128, 173, 148, 140, 128, 1, 1,
                1, 43, 128, 128, 128, 173, 148, 140, 128, 1, 1, 1, 43, 128, 128, 128, 173, 148, 140, 128, 1, 1, 1, 43,
                128, 128, 165, 145, 128, 1, 1, 1, 43, 128, 128, 165, 145, 128, 1, 1, 1, 43, 128, 128, 165, 145, 128, 1,
                1, 1, 43, 128, 128, 159, 128, 1, 1, 1, 43, 128, 128, 159, 128, 1, 128, 128, 128, 128, 128, 128 },
                new int[] { 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0,
                        1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0,
                        0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1,
                        0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1,
                        1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1 }), unzigzag(new int[] { 0, 13, 38, -29,
                -16, 10, 5, -12, 13, 16, -7, 10, -7, 5, -5, -3 }), 0, 0, 0);
    }

    @Test
    public void testCoeffDCTY16() {

        VPXBitstream bs = new VPXBitstream(binProbs2, 1);
        bs.encodeCoeffsDCT16(new MockVpxBooleanEncoder(new int[] { 202, 24, 213, 235, 220, 240, 255, 254, 254, 243,
                230, 196, 177, 153, 140, 133, 130, 129, 128, 39, 77, 1, 51, 43, 255, 85, 176, 155, 140, 135, 128, 24,
                71, 130, 39, 1, 163, 110, 176, 155, 140, 135, 128, 28, 108, 121, 7, 38, 132, 128, 128, 176, 155, 140,
                135, 128, 20, 95, 222, 1, 255, 128, 128, 165, 145, 128, 42, 80, 121, 121, 7, 38, 132, 51, 165, 145,
                128, 42, 80, 21, 68, 132, 15, 165, 145, 128, 42, 80, 121, 7, 38, 132, 51, 165, 145, 128, 42, 80, 121,
                1, 102, 128, 128, 128, 128 }, new int[] { 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1,
                1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0,
                0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0,
                1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1 }), unzigzag(new int[] { 83, 21, 21, 0, -19, 0, -9, 0, 0, -9,
                8, 0, 8, 0, 0, -3 }), 0, 0, 0);
    }

    @Test
    public void testCoeffDCTU() {

        VPXBitstream bs = new VPXBitstream(binProbs1, 1);
        bs.encodeCoeffsDCTUV(new MockVpxBooleanEncoder(new int[] { 1, 9, 248, 251, 255, 128, 128, 176, 155, 140, 135,
                128, 155, 77, 1, 1, 128, 128, 128, 176, 155, 140, 135, 128, 69, 46, 190, 1, 128, 128, 128, 176, 155,
                140, 135, 128, 141, 124, 134, 51, 80, 128, 128, 128, 173, 148, 140, 128, 149, 255, 1, 1, 128, 128, 128,
                128, 173, 148, 140, 128, 55, 93, 134, 134, 51, 80, 128, 128, 128, 173, 148, 140, 128, 55, 93, 1, 128,
                128, 128, 159, 128, 55, 93, 134, 51, 80, 128, 128, 159, 128, 55, 93, 134, 128, 128, 128, 128, 128 },
                new int[] { 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0,
                        1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0,
                        0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1,
                        1, 0, 0, 0 }), unzigzag(new int[] { 19, 34, 34, 0, 16, 0, -14, 0, 0, -11, -5, 0, -6, 0, 0, 2 }), 1, 0, 0,
                0);
    }

    public static class MockVpxBooleanEncoder extends VPxBooleanEncoder {
        private int[] probs;
        private int[] bits;
        private int cur;

        public MockVpxBooleanEncoder(int[] probs, int[] bits) {
            super(null);
            this.probs = probs;
            this.bits = bits;
        }

        public void writeBit(int prob, int bit) {
            Assert.assertEquals(probs[cur], prob);
            Assert.assertEquals(bits[cur], bit);
            cur++;
        }
    }

    public static int[][][][] cloneProb() {
        int[][][][] ref = VPXConst.tokenDefaultBinProbs;
        int[][][][] result = new int[4][8][3][11];
        for (int i = 0; i < ref.length; i++)
            for (int j = 0; j < ref[i].length; j++)
                for (int k = 0; k < ref[i][j].length; k++)
                    for (int l = 0; l < ref[i][j][k].length; l++)
                        result[i][j][k][l] = ref[i][j][k][l];
        return result;
    }

    public static int[] unzigzag(int[] zz) {
        int[] res = new int[16];
        for (int i = 0; i < 16; i++)
            res[VPXConst.zigzag[i]] = zz[i];
        return res;
    }

    // public static void main(String[] args) {
    // int[][][][] ref = VPXConst.tokenDefaultBinProbs;
    // int[][][][] comp = binProbs3;
    // for (int i = 0; i < ref.length; i++) {
    // for (int j = 0; j < ref[i].length; j++) {
    // for (int k = 0; k < ref[i][j].length; k++) {
    // for (int l = 0; l < ref[i][j][k].length; l++)
    // if (ref[i][j][k][l] != comp[i][j][k][l]) {
    // System.out.println("binProbs3[" + i + "][" + j + "][" + k + "][" + l +
    // "] = "
    // + comp[i][j][k][l] + ";");
    // }
    // }
    // }
    // }
    // }
}