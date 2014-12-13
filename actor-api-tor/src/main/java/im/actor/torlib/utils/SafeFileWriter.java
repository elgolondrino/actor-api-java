package im.actor.torlib.utils;

import java.io.*;
import java.util.Random;
import java.util.zip.CRC32;

public class SafeFileWriter {
    private Random random = new Random();
    private String path;
    private String fileName;

    public SafeFileWriter(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    private File getFile() {
        return new File(path + "/" + fileName);
    }

    private File getTempFile() {
        return new File(path + "/random_" + random.nextLong() + ".tmp");
    }

    public synchronized void saveData(byte[] data) {
        File file = getTempFile();
        if (file.exists()) {
            if (!file.delete()) {
                file.delete();
            }
        }

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            writeProtoBytes(data, os);
            CRC32 crc32 = new CRC32();
            crc32.update(data);
            writeLong(crc32.getValue(), os);
            os.flush();
            os.getFD().sync();
            os.close();
            os = null;
            file.renameTo(getFile());
        } catch (FileNotFoundException e) {
            // Logger.d(TAG, e);
        } catch (IOException e) {
            // Logger.d(TAG, e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // Logger.d(TAG, e);
                }
            }
        }
    }

    public synchronized byte[] loadData() {
        File file = getFile();
        if (!file.exists())
            return null;

        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] res = readProtoBytes(is);
            CRC32 crc32 = new CRC32();
            crc32.update(res);
            long crc = readLong(is);
            if (crc32.getValue() != crc) {
                return null;
            }
            return res;
        } catch (FileNotFoundException e) {
            // Logger.d(TAG, e);
        } catch (IOException e) {
            // Logger.d(TAG, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Logger.d(TAG, e);
                }
            }
        }
        return null;
    }

    public synchronized void remove() {
        File file = getFile();
        if (file.exists()) {
            if (!file.delete()) {
                file.delete();
            }
        }
    }

    public static byte[] readProtoBytes(InputStream stream) throws IOException {
        int arrayLength = (int) readVarInt(stream);
        return readBytes(arrayLength, stream);
    }

    public static void writeProtoBytes(byte[] data, OutputStream stream) throws IOException {
        writeVarInt(data.length, stream);
        writeBytes(data, stream);
    }

    /**
     * Reading protobuf-like varint
     *
     * @param stream source stream
     * @return varint
     * @throws IOException
     */
    public static long readVarInt(InputStream stream) throws IOException {
        long value = 0;
        long i = 0;
        int b;

        do {
            b = readByte(stream);

            if ((b & 0x80) != 0) {
                value |= (b & 0x7F) << i;
                i += 7;
                if (i > 70) {
                    throw new IOException();
                }
            } else {
                break;
            }
        } while (true);

        return value | (b << i);
    }

    /**
     * Writing protobuf-like varint
     *
     * @param i      value
     * @param stream destination stream
     * @throws IOException
     */
    public static void writeVarInt(long i, OutputStream stream) throws IOException {
        while ((i & 0xffffffffffffff80l) != 0l) {
            stream.write((byte) ((i & 0x7f) | 0x80));
            i >>>= 7;
        }

        stream.write((byte) (i & 0x7f));
    }

    /**
     * Reading single byte from stream
     *
     * @param stream source stream
     * @return read byte
     * @throws IOException
     */
    public static byte readByte(InputStream stream) throws IOException {
        int res = stream.read();
        if (res < 0) {
            throw new IOException();
        }
        return (byte) res;
    }

    /**
     * Reading uint from stream
     *
     * @param stream source stream
     * @return value
     * @throws IOException reading exception
     */
    public static long readUInt(InputStream stream) throws IOException {
        long a = stream.read();
        if (a < 0) {
            throw new IOException();
        }
        long b = stream.read();
        if (b < 0) {
            throw new IOException();
        }
        long c = stream.read();
        if (c < 0) {
            throw new IOException();
        }
        long d = stream.read();
        if (d < 0) {
            throw new IOException();
        }

        return d + (c << 8) + (b << 16) + (a << 24);
    }

    /**
     * Reading long from stream
     *
     * @param stream source stream
     * @return value
     * @throws IOException reading exception
     */
    public static long readLong(InputStream stream) throws IOException {
        long a = readUInt(stream);
        long b = readUInt(stream);

        return b + (a << 32);
    }

    /**
     * Writing long to stream
     *
     * @param v      value
     * @param stream destination stream
     * @throws IOException
     */
    public static void writeLong(long v, OutputStream stream) throws IOException {
        writeByte((byte) ((v >> 56) & 0xFF), stream);
        writeByte((byte) ((v >> 48) & 0xFF), stream);
        writeByte((byte) ((v >> 40) & 0xFF), stream);
        writeByte((byte) ((v >> 32) & 0xFF), stream);

        writeByte((byte) ((v >> 24) & 0xFF), stream);
        writeByte((byte) ((v >> 16) & 0xFF), stream);
        writeByte((byte) ((v >> 8) & 0xFF), stream);
        writeByte((byte) (v & 0xFF), stream);
    }

    /**
     * Writing byte to stream
     *
     * @param v      value
     * @param stream destination stream
     * @throws IOException
     */
    public static void writeByte(byte v, OutputStream stream) throws IOException {
        stream.write(v);
    }

    /**
     * Writing byte array to stream
     *
     * @param data   data
     * @param stream destination stream
     * @throws IOException
     */
    public static void writeBytes(byte[] data, OutputStream stream) throws IOException {
        stream.write(data);
    }

    /**
     * Reading bytes from stream
     *
     * @param count  bytes count
     * @param stream source stream
     * @return readed bytes
     * @throws IOException reading exception
     */
    public static byte[] readBytes(int count, InputStream stream) throws IOException {
        byte[] res = new byte[count];
        int offset = 0;
        while (offset < res.length) {
            int readed = stream.read(res, offset, res.length - offset);
            if (readed > 0) {
                offset += readed;
            } else if (readed < 0) {
                throw new IOException();
            } else {
                Thread.yield();
            }
        }
        return res;
    }
}
