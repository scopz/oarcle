import Constants.HOST
import Constants.MESSAGE_SIZE
import Constants.PORT
import java.io.InputStream
import java.net.Socket

@ExperimentalStdlibApi
fun main() {
    val socket = Socket(HOST, PORT)
    val outputStream = socket.getOutputStream()

    val inputStream = socket.getInputStream()

    Thread {
        inputStream.listen()
    }.apply { start() }

    var input: String

    while (readLine()!!.also { input = it } != "exit") {
        outputStream.write(parseInput(input))
    }

    outputStream.flush()
    outputStream.close()
    socket.close()
}

fun InputStream.listen() {
    val buffer = ByteArray(MESSAGE_SIZE)
    val bytesRead = read(buffer)

    if (bytesRead > 0) {
        readMessage(Message(buffer, bytesRead))
        listen()
    } else if (bytesRead == 0) {
        listen()
    }
}

fun readMessage(message: Message) {
    val text = message.readString()
    println(text)
}

@ExperimentalStdlibApi
fun parseInput(string: String): ByteArray = string.split("::")
    .filter { it.isNotBlank() }
    .flatMap { str ->
        if (str.startsWith("0x")) {
            str.substring(2)
                .chunked(2)
                .map { it.hexToByte() }
                .reversed() // little endian

        } else if (str.startsWith("08")) {
            val value = str.substring(2).toInt()
            val byteValue = if (value < 128) value else value - 256
            listOf(byteValue.toByte())

        } else {
            val bytes = str.toByteArray().toList()
            intToByteListLittleEndian(bytes.size) + bytes
        }
    }
    .map { it }
    .toByteArray()


fun intToByteListLittleEndian(valor: Int): List<Byte> {
    return listOf(
        (valor and 0xFF).toByte(),
        (valor ushr 8 and 0xFF).toByte(),
        (valor ushr 16 and 0xFF).toByte(),
        (valor ushr 24 and 0xFF).toByte()
    )
}