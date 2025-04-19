package com.leon.common;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class AudioUtils {

    public static byte[] mergeWavByteArrays(List<byte[]> wavByteArrays) throws IOException, UnsupportedAudioFileException {
        if (wavByteArrays == null || wavByteArrays.isEmpty()) {
            return new byte[0];
        }
        if (wavByteArrays.size() == 1) {
            return wavByteArrays.get(0);
        }

        AudioInputStream firstStream = null;
        AudioFormat audioFormat = null;
        long totalFrameLength = 0;
        List<AudioInputStream> audioStreamList = new ArrayList<>();
        Vector<ByteArrayInputStream> inputStreamVector = new Vector<>();

        try {
            for (byte[] wavData : wavByteArrays) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(wavData);
                inputStreamVector.add(byteArrayInputStream);

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);
                audioStreamList.add(audioInputStream);

                if (firstStream == null) {
                    firstStream = audioInputStream;
                    audioFormat = firstStream.getFormat();
                } else {
                    if (!audioFormat.matches(audioInputStream.getFormat())) {
                        for (AudioInputStream openedStream : audioStreamList) {
                            openedStream.close();
                        }
                        for(ByteArrayInputStream bais : inputStreamVector) {
                            bais.close();
                        }
                        throw new UnsupportedAudioFileException("Audio formats do not match for merging.");
                    }
                }
                long frameLength = audioInputStream.getFrameLength();
                if (frameLength == AudioSystem.NOT_SPECIFIED) {
                    for (AudioInputStream openedStream : audioStreamList) {
                        openedStream.close();
                    }
                    for(ByteArrayInputStream bais : inputStreamVector) {
                        bais.close();
                    }
                    throw new IOException("Cannot determine frame length for one of the audio segments.");
                }
                totalFrameLength += frameLength;
            }

            SequenceInputStream sequenceInputStream = new SequenceInputStream(Collections.enumeration(audioStreamList));

            AudioInputStream mergedAudioInputStream = new AudioInputStream(sequenceInputStream, audioFormat, totalFrameLength);

            try (ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
                 mergedAudioInputStream) {
                AudioSystem.write(mergedAudioInputStream, AudioFileFormat.Type.WAVE, mergedOutputStream);
                return mergedOutputStream.toByteArray();
            }

        } finally {
            for (AudioInputStream stream : audioStreamList) {
                if (stream != null) {
                    try { stream.close(); } catch (IOException ignored) {}
                }
            }
            for (ByteArrayInputStream bais : inputStreamVector) {
                if (bais != null) {
                    try { bais.close(); } catch (IOException ignored) {}
                }
            }
        }
    }
}
