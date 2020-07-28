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
    when(args[0]) {
        "semaphore" -> semaphore(args[1].toInt(), args[2].toInt(), args[3].toInt(), args[4].toInt(), args[5].toInt())
        "splay" -> splay(args[1].toInt(), args[2].toInt())
    }
}