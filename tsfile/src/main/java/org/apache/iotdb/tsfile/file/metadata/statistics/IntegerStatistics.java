/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.tsfile.file.metadata.statistics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.utils.BytesUtils;
import org.apache.iotdb.tsfile.utils.ReadWriteIOUtils;

/**
 * Statistics for int type.
 */
public class IntegerStatistics extends Statistics<Integer> {

  private int minValue;
  private int maxValue;
  private int firstValue;
  private int lastValue;
  private double sumValue;

  @Override
  public TSDataType getType() {
    return TSDataType.INT32;
  }

  @Override
  public int getStatsSize() {
    return 24;
  }

  private void initializeStats(int min, int max, int first, int last, double sum) {
    this.minValue = min;
    this.maxValue = max;
    this.firstValue = first;
    this.lastValue = last;
    this.sumValue = sum;
  }

  private void updateStats(int minValue, int maxValue, int firstValue, int lastValue,
      double sumValue) {
    if (minValue < this.minValue) {
      this.minValue = minValue;
    }
    if (maxValue > this.maxValue) {
      this.maxValue = maxValue;
    }
    this.sumValue += sumValue;
    this.lastValue = lastValue;
  }

  @Override
  public void setMinMaxFromBytes(byte[] minBytes, byte[] maxBytes) {
    minValue = BytesUtils.bytesToInt(minBytes);
    maxValue = BytesUtils.bytesToInt(maxBytes);
  }

  @Override
  void updateStats(int value) {
    if (isEmpty) {
      initializeStats(value, value, value, value, value);
      isEmpty = false;
    } else {
      updateStats(value, value, value, value, value);
      isEmpty = false;
    }
  }

  @Override
  void updateStats(int[] values, int batchSize) {
    for (int i = 0; i < batchSize; i++) {
      updateStats(values[i]);
    }
  }

  @Override
  public Integer getMinValue() {
    return minValue;
  }

  @Override
  public Integer getMaxValue() {
    return maxValue;
  }

  @Override
  public Integer getFirstValue() {
    return firstValue;
  }

  @Override
  public Integer getLastValue() {
    return lastValue;
  }

  @Override
  public double getSumValue() {
    return sumValue;
  }

  @Override
  protected void mergeStatisticsValue(Statistics stats) {
    IntegerStatistics intStats = (IntegerStatistics) stats;
    if (isEmpty) {
      initializeStats(intStats.getMinValue(), intStats.getMaxValue(), intStats.getFirstValue(), intStats.getLastValue(),
          intStats.getSumValue());
      isEmpty = false;
    } else {
      updateStats(intStats.getMinValue(), intStats.getMaxValue(), intStats.getFirstValue(), intStats.getLastValue(),
          intStats.getSumValue());
    }

  }

  @Override
  public ByteBuffer getMinValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(minValue);
  }

  @Override
  public ByteBuffer getMaxValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(maxValue);
  }

  @Override
  public ByteBuffer getFirstValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(firstValue);
  }

  @Override
  public ByteBuffer getLastValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(lastValue);
  }

  @Override
  public ByteBuffer getSumValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(sumValue);
  }

  @Override
  public byte[] getMinValueBytes() {
    return BytesUtils.intToBytes(minValue);
  }

  @Override
  public byte[] getMaxValueBytes() {
    return BytesUtils.intToBytes(maxValue);
  }

  @Override
  public byte[] getFirstValueBytes() {
    return BytesUtils.intToBytes(firstValue);
  }

  @Override
  public byte[] getLastValueBytes() {
    return BytesUtils.intToBytes(lastValue);
  }

  @Override
  public byte[] getSumValueBytes() {
    return BytesUtils.doubleToBytes(sumValue);
  }

  @Override
  public int serializeStats(OutputStream outputStream) throws IOException {
    int byteLen = 0;
    byteLen += ReadWriteIOUtils.write(minValue, outputStream);
    byteLen += ReadWriteIOUtils.write(maxValue, outputStream);
    byteLen += ReadWriteIOUtils.write(firstValue, outputStream);
    byteLen += ReadWriteIOUtils.write(lastValue, outputStream);
    byteLen += ReadWriteIOUtils.write(sumValue, outputStream);
    return byteLen;
  }

  @Override
  void deserialize(InputStream inputStream) throws IOException {
    this.minValue = ReadWriteIOUtils.readInt(inputStream);
    this.maxValue = ReadWriteIOUtils.readInt(inputStream);
    this.firstValue = ReadWriteIOUtils.readInt(inputStream);
    this.lastValue = ReadWriteIOUtils.readInt(inputStream);
    this.sumValue = ReadWriteIOUtils.readDouble(inputStream);
  }

  @Override
  void deserialize(ByteBuffer byteBuffer) {
    this.minValue = ReadWriteIOUtils.readInt(byteBuffer);
    this.maxValue = ReadWriteIOUtils.readInt(byteBuffer);
    this.firstValue = ReadWriteIOUtils.readInt(byteBuffer);
    this.lastValue = ReadWriteIOUtils.readInt(byteBuffer);
    this.sumValue = ReadWriteIOUtils.readDouble(byteBuffer);
  }

  @Override
  public String toString() {
    return super.toString() + " [minValue:" + minValue + ",maxValue:" + maxValue + ",firstValue:" + firstValue +
        ",lastValue:" + lastValue + ",sumValue:" + sumValue + "]";
  }
}
