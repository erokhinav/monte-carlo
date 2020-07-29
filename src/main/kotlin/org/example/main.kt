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
    val writerMonteCarlo: PrintWriter = File(args[2]).printWriter()
    val writerCorrect: PrintWriter = File(args[3]).printWriter()
    val runMonteCarlo = args[0]
    when(args[1]) {
        "semaphore" -> semaphore(writerMonteCarlo, writerCorrect, args[4].toInt(), args[5].toInt(), args[6].toInt(), args[7].toInt(), args[8].toInt())
        "splay" -> splay(writerMonteCarlo, writerCorrect, args[3].toInt(), args[4].toInt())
    }
}