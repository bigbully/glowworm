package com.jd.dd.glowworm.util;

import java.util.Vector;

public class CustByteBuffer {
    private Vector valueBytesVect = new Vector();
    private byte[] curUsedBytes;
    private int totalCharLength = 0;
    private int haveUsedCountInCurUC = 0;
    private int count = 0;
    // �Ƿ񱻸���
    private boolean isReUseSign = false;
    private int curUsedByteInVBytesVectIndex = 0;

    public CustByteBuffer() {
        this(1024 * 10);
    }

    public CustByteBuffer(int capacity) {
        totalCharLength = capacity;
        curUsedBytes = new byte[totalCharLength];
        valueBytesVect.add(curUsedBytes);
        curUsedByteInVBytesVectIndex = 0;
    }

    public void append(byte appendByteParm) {
        int tmpEmptyCharsCnt = curUsedBytes.length - haveUsedCountInCurUC;

        if (isReUseSign == false) {
            if (tmpEmptyCharsCnt > 0) {
                appendToCurUsedByte(appendByteParm);
            } else {
                expandCapacity(1024);
                appendToCurUsedByte(appendByteParm);
            }
        }
    }

    public void append(byte[] appendCharsParm) {
        int newCount = appendCharsParm.length;
        int tmpEmptyCharsCnt = curUsedBytes.length - haveUsedCountInCurUC;
        int tmpNewMinimumCount = newCount - tmpEmptyCharsCnt;
        if (isReUseSign == false) {
            if (tmpNewMinimumCount > 0) {
                appendToCurUsedChars(appendCharsParm, 0,
                        haveUsedCountInCurUC, tmpEmptyCharsCnt);
                expandCapacity(tmpNewMinimumCount);
                int tmpCopyToNewCurUCCnt = appendCharsParm.length - tmpEmptyCharsCnt;
                appendToCurUsedChars(appendCharsParm, tmpEmptyCharsCnt,
                        0, tmpCopyToNewCurUCCnt);
            } else {
                appendToCurUsedChars(appendCharsParm, 0,
                        haveUsedCountInCurUC, appendCharsParm.length);
            }
        } else {
            if (tmpNewMinimumCount > 0) {
                appendToCurUsedChars(appendCharsParm, 0, haveUsedCountInCurUC,
                        tmpEmptyCharsCnt);
                reuseValueBytesVect(appendCharsParm,
                        tmpEmptyCharsCnt, tmpNewMinimumCount);
            } else {
                appendToCurUsedChars(appendCharsParm, 0,
                        haveUsedCountInCurUC, appendCharsParm.length);
            }
        }
    }

    public void append(byte[] appendCharsParm, int off, int len) {
        int newCount = len;
        int tmpEmptyCharsCnt = curUsedBytes.length - haveUsedCountInCurUC;
        int tmpNewMinimumCount = newCount - tmpEmptyCharsCnt;
        if (isReUseSign == false) {
            if (tmpNewMinimumCount > 0) {
                appendToCurUsedChars(appendCharsParm, off,
                        haveUsedCountInCurUC, tmpEmptyCharsCnt);
                expandCapacity(tmpNewMinimumCount);
                int tmpCopyToNewCurUCCnt = newCount - tmpEmptyCharsCnt;
                appendToCurUsedChars(appendCharsParm, off + tmpEmptyCharsCnt,
                        0, tmpCopyToNewCurUCCnt);
            } else {
                appendToCurUsedChars(appendCharsParm, off,
                        haveUsedCountInCurUC, newCount);
            }
        } else {
            if (tmpNewMinimumCount > 0) {
                appendToCurUsedChars(appendCharsParm, off, haveUsedCountInCurUC,
                        tmpEmptyCharsCnt);
                reuseValueBytesVect(appendCharsParm,
                        tmpEmptyCharsCnt, tmpNewMinimumCount);
            } else {
                appendToCurUsedChars(appendCharsParm, off,
                        haveUsedCountInCurUC, newCount);
            }
        }
    }

    private void appendToCurUsedChars(byte[] appendCharsParm, int srcPos,
                                      int destPos, int appendLengthParm) {
        System.arraycopy(appendCharsParm, srcPos, curUsedBytes,
                destPos, appendLengthParm);
        haveUsedCountInCurUC += appendLengthParm;
        count += appendLengthParm;
    }

    private void appendToCurUsedByte(byte appendByteParm) {
        curUsedBytes[haveUsedCountInCurUC] = appendByteParm;
        haveUsedCountInCurUC++;
        count++;
    }

    /**
     * @param appendCharsParm          ��char����
     * @param curUsedCharsEmptyCntParm ���ڱ�ʹ�õ�char����,���ж��ٿ���char�ռ�
     * @param needMoreCharSpaceCntParm ���˵�ǰ���е�char�ռ���,������ٿ���char�ռ�
     */
    private void reuseValueBytesVect(byte[] appendCharsParm,
                                     int appendCharsBeginPosiParm, int needMoreCharSpaceCntParm) {
        int tmpTotalEmptySpaceCnt = totalCharLength - count;
        int tmpNewNeedCharPosi = count + needMoreCharSpaceCntParm;

        // �Ȱ�����µ�byte[]��䵽�Ѿ����ڵ�byte[]������
        int tmpThisCharsEmptyCnt = 0;
        int tmpBeginI = curUsedByteInVBytesVectIndex + 1;
        for (int i = tmpBeginI; i < valueBytesVect.size(); i++) {
            curUsedBytes = (byte[]) valueBytesVect.elementAt(i);
            curUsedByteInVBytesVectIndex++;
            haveUsedCountInCurUC = 0;
            tmpThisCharsEmptyCnt =
                    curUsedBytes.length - needMoreCharSpaceCntParm;
            if (tmpThisCharsEmptyCnt > 0) {
                appendToCurUsedChars(
                        appendCharsParm, appendCharsBeginPosiParm,
                        haveUsedCountInCurUC, needMoreCharSpaceCntParm);
                needMoreCharSpaceCntParm = 0;
                break;
            } else {
                appendToCurUsedChars(
                        appendCharsParm, appendCharsBeginPosiParm,
                        haveUsedCountInCurUC, curUsedBytes.length);
                needMoreCharSpaceCntParm -= haveUsedCountInCurUC;
                appendCharsBeginPosiParm += haveUsedCountInCurUC;
            }
        }

        // ���ô��ڴ��������µĿռ�4�����Щ�¼����bytes
        if (needMoreCharSpaceCntParm > 0) {
            int tmpNewAllocateCharCnt = tmpNewNeedCharPosi - totalCharLength;
            int tmpNewAppendCharsBeging =
                    appendCharsBeginPosiParm + tmpTotalEmptySpaceCnt;
            expandCapacity(tmpNewAllocateCharCnt);
            appendToCurUsedChars(appendCharsParm, tmpNewAppendCharsBeging,
                    0, tmpNewAllocateCharCnt);
        }
    }

    private void expandCapacity(int newMinimumCountParm) {
        int tmpNewCount = count;
        if (tmpNewCount < newMinimumCountParm) {
            tmpNewCount = newMinimumCountParm + 100;
        }

        curUsedBytes = new byte[tmpNewCount];
        totalCharLength += tmpNewCount;
        valueBytesVect.add(curUsedBytes);
        curUsedByteInVBytesVectIndex++;
        haveUsedCountInCurUC = 0;
    }

    public void append(String appendStrParm) {

    }

    public void getValueBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin)
            throws Exception {
        int tmpTotalCopyCnt = srcEnd - srcBegin;
        if (srcBegin > count) {
            throw new Exception("srcBegin ���� size");
        }

        if (tmpTotalCopyCnt > (count - srcBegin)) {
            throw new Exception("Ҫcopy�ĳ���:"
                    + tmpTotalCopyCnt + "���ڸö������г���:" + (count - srcBegin));
        }

        int tmpDstBegin = dstBegin;
        byte[] tmpUsedBytes = null;
        int tmpValueCharsVectSize = valueBytesVect.size();
        int tmpValueBytesVectBeginIdx = 0;
        int tmpByteSkipCnt = 0;

        for (int i = 0; i < tmpValueCharsVectSize; i++) {
            tmpUsedBytes = (byte[]) valueBytesVect.elementAt(i);
            tmpByteSkipCnt += tmpUsedBytes.length;
            if (tmpByteSkipCnt > srcBegin) {
                int tmpVectBeginPosi = srcBegin -
                        tmpByteSkipCnt + tmpUsedBytes.length;
                int tmpLength = tmpUsedBytes.length - tmpVectBeginPosi;

                if (tmpLength >= tmpTotalCopyCnt) {
                    System.arraycopy(tmpUsedBytes, tmpVectBeginPosi,
                            dst, tmpDstBegin, tmpTotalCopyCnt);
                    return;
                } else {
                    System.arraycopy(tmpUsedBytes, tmpVectBeginPosi,
                            dst, tmpDstBegin, tmpLength);
                    tmpTotalCopyCnt -= tmpLength;
                    tmpDstBegin += tmpLength;
                    tmpValueBytesVectBeginIdx = i + 1;
                }

                break;
            }
        }

        for (int i = tmpValueBytesVectBeginIdx;
             i < tmpValueCharsVectSize - 1; i++) {
            tmpUsedBytes = (byte[]) valueBytesVect.elementAt(i);
            if (tmpUsedBytes.length >= tmpTotalCopyCnt) {
                System.arraycopy(tmpUsedBytes, 0,
                        dst, tmpDstBegin, tmpTotalCopyCnt);
                return;
            } else {
                System.arraycopy(tmpUsedBytes, 0,
                        dst, tmpDstBegin, tmpUsedBytes.length);
                tmpTotalCopyCnt -= tmpUsedBytes.length;
                tmpDstBegin += tmpUsedBytes.length;
            }
        }

        if (tmpTotalCopyCnt > 0) {
            tmpUsedBytes = (byte[]) valueBytesVect
                    .elementAt(tmpValueCharsVectSize - 1);
            System.arraycopy(tmpUsedBytes, 0,
                    dst, tmpDstBegin, tmpTotalCopyCnt);
        }
    }

    public byte[] getValueBytes() {
        byte[] retBytes = new byte[count];
        try {
            byte[] tmpUsedBytes = null;
            int tmpHaveCopyCnt = 0;
            int tmpValueCharsVectSize = valueBytesVect.size();
            if (tmpValueCharsVectSize == 1) {
                tmpUsedBytes = (byte[]) valueBytesVect.elementAt(0);
                System.arraycopy(tmpUsedBytes, 0, retBytes, 0, count);
            } else {
                int tmpHaveRemainBytesLength = count;
                for (int i = 0; i < tmpValueCharsVectSize; i++) {
                    tmpUsedBytes = (byte[]) valueBytesVect.elementAt(i);
                    if (tmpHaveRemainBytesLength >= tmpUsedBytes.length) {
                        System.arraycopy(tmpUsedBytes, 0,
                                retBytes, tmpHaveCopyCnt, tmpUsedBytes.length);
                        tmpHaveRemainBytesLength -= tmpUsedBytes.length;
                        tmpHaveCopyCnt += tmpUsedBytes.length;
                    } else {
                        System.arraycopy(tmpUsedBytes, 0,
                                retBytes, tmpHaveCopyCnt, tmpHaveRemainBytesLength);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            retBytes = new byte[0];
        }

        return retBytes;
    }

    public String toString() {
        byte[] tmpValueBytes = getValueBytes();
        return new String(tmpValueBytes, 0, count);
    }

    public void reset() {
        isReUseSign = true;
        this.count = 0;
        haveUsedCountInCurUC = 0;
        curUsedBytes = (byte[]) valueBytesVect.get(0);
        curUsedByteInVBytesVectIndex = 0;
    }

    public void clear() {
        valueBytesVect.clear();
        valueBytesVect = null;
        curUsedBytes = null;
    }

    public int size() {
        return count;
    }
}
