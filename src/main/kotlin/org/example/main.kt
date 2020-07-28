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
    val writer: PrintWriter = File(args[1]).printWriter()
    when(args[0]) {
        "semaphore" -> semaphore(writer, args[2].toInt(), args[3].toInt(), args[4].toInt(), args[5].toInt(), args[6].toInt())
        "splay" -> splay(writer, args[2].toInt(), args[3].toInt())
    }
}