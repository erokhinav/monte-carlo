package org.example

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.scheduling.ExperimentalCoroutineDispatcher
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList
import kotlin.random.Random


//enum class DispatcherCreator(public val create: (parallelism: Int) -> CoroutineDispatcher) {
//    //    ForkJoin({ parallelism ->  ForkJoinPool(parallelism).asCoroutineDispatcher() }),
//    @InternalCoroutinesApi
//    Experimental({ parallelism -> kotlinx.coroutines.scheduling.ExperimentalCoroutineDispatcher(corePoolSize = parallelism, maxPoolSize = parallelism) })
//}


@InternalCoroutinesApi
fun main(args: Array<String>) {
    run(arrayOf("map", "map-all.csv", "0", "0", "0", "true"))
    run(arrayOf("map", "map-n-20-70.csv", "-1", "20", "70", "false"))
    run(arrayOf("map", "map-n-80-10.csv", "-1", "80", "10", "false"))
    run(arrayOf("map", "map-4-n-30.csv", "4", "-1", "30", "false"))
    run(arrayOf("map", "map-16-n-80.csv", "16", "-1", "80", "false"))
    run(arrayOf("map", "map-1-70-n.csv", "1", "70", "-1", "false"))
    run(arrayOf("map", "map-32-20-n.csv", "32", "20", "-1", "false"))
}

@InternalCoroutinesApi
fun run(args: Array<String>) {
    val writer: PrintWriter = File(args[1]).printWriter()
//    val writerCorrect: PrintWriter = File(args[2]).printWriter()
    when(args[0]) {
//        "semaphore" -> semaphore(writerMonteCarlo, writerCorrect, args[3].toInt(), args[4].toInt(), args[5].toInt(), args[6].toInt(), args[7].toInt())
//        "splay" -> splay(writerMonteCarlo, writerCorrect, args[3].toInt(), args[4].toInt())
        "map" -> map(writer, args[2].toInt(), args[3].toInt(), args[4].toInt(), args[5].toBoolean())
    }
}