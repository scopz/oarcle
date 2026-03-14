import java.nio.ByteBuffer
import java.nio.ByteOrder

class Message(
    data: ByteArray,
    length: Int
) {
    private val mData: ByteArray = ByteArray(length)
    private var pointer: Int = 0

    init {
        System.arraycopy(data, 0, mData, 0, length)
    }

    fun readString(): String {
        val size = readU32()
        val message = String(mData, pointer, size)
        pointer += size
        return message
    }

    fun readRestAsString(): String {
        val size = mData.size - pointer
        val message = String(mData, pointer, size)
        pointer += size
        return message
    }

    fun readU8(): Byte = mData[pointer++]
    fun readU16(): Short {
        val value = ByteBuffer.wrap(mData, pointer, 2)
            .order(ByteOrder.LITTLE_ENDIAN)
            .short
        pointer += 2
        return value
    }

    fun readU32(): Int {
        val value = ByteBuffer.wrap(mData, pointer, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .int
        pointer += 4
        return value
    }
}
