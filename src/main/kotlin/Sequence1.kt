fun main() {

    val otherSeq = sequence {
        var x= 0
        while(true){
            yield(x++)
            Thread.sleep(1000)
        }
    }
    val s = sequence {
        yield(1)
        yield(2)
        yield(3)
        Thread.sleep(1000)

        yieldAll(listOf(4,5,6))
        yieldAll(otherSeq)
    }

    s.take(10).forEach{println(it)}

}