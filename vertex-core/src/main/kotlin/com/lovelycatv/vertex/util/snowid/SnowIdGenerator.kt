package com.lovelycatv.vertex.util.snowid

/**
 * flag                  timestamp                dataCenter worker sequence gene
 *  0   10000000000000000000000000000000000000001   10001    10001  1000001 10001
 *  1   --------------------41-------------------   --5--    --5--  ---7--- --5--
 *
 * @author lovelycat
 * @since 2025-07-22 19:40
 * @version 1.0
 */
class SnowIdGenerator(
    private val startPoint: Long,
    private val timestampLength: Int,
    private val dataCenterIdLength: Int,
    private val workerIdLength: Int,
    private val sequenceIdLength: Int,
    private val geneIdLength: Int,
    private val dataCenterId: Long,
    private val workerId: Long,
    private val actualGeneLength: Int = geneIdLength
) {
    private val maxSequence: Long
    private val maxDataCenters: Long
    private val maxWorkers: Long
 
    init {
        if (timestampLength + dataCenterIdLength + workerIdLength + sequenceIdLength + geneIdLength != 63) {
            throw IllegalArgumentException("Id length must be 64 in total.")
        }
 
        maxSequence = (1 shl sequenceIdLength).toLong()
        maxDataCenters = (1 shl dataCenterIdLength).toLong()
        maxWorkers = (1 shl workerIdLength).toLong()
 
        if (dataCenterId >= maxDataCenters) {
            throw IllegalArgumentException("Data center id out of range, max $maxDataCenters but current $dataCenterId")
        }
 
        if (workerId >= maxWorkers) {
            throw IllegalArgumentException("Worker id out of range, max $maxWorkers but current $workerId")
        }
    }
 
    private var lastTimestamp = 0L
 
    private val sequenceMap = mutableMapOf<Long, Long>()
 
    private var borrowedTimestamp = 0L
 
    @Synchronized
    fun nextId(gene: Long = 0L): Long {
        var currentTimestamp = System.currentTimeMillis()
 
        val shlBitsForTimestamp = 63 - timestampLength
 
        if (currentTimestamp <= borrowedTimestamp) {
            currentTimestamp = borrowedTimestamp
        } else if (currentTimestamp < lastTimestamp) {
            throw IllegalStateException("Clock moved backwards. Refusing to generate id for timestamp $currentTimestamp. Latest generated at $lastTimestamp")
        }
 
        if (currentTimestamp == lastTimestamp) {
            with(gene) {
                val original = (sequenceMap[this] ?: 0)
                sequenceMap[this] = original + 1
 
                if (original + 1 == maxSequence) {
                    borrowedTimestamp = currentTimestamp + 1
                    currentTimestamp = borrowedTimestamp
                    sequenceMap.clear()
                }
            }
        } else {
            sequenceMap.clear()
        }
 
        lastTimestamp = currentTimestamp
 
        val timestamp = (currentTimestamp - startPoint) shl shlBitsForTimestamp
 
        val shlBitsForDataCenter = shlBitsForTimestamp - dataCenterIdLength
        val datacenterId = dataCenterId shl shlBitsForDataCenter
 
        val shlBitsForWorker = shlBitsForDataCenter - workerIdLength
        val workerId = workerId shl shlBitsForWorker
 
        val realSequence = sequenceMap[gene] ?: 0
 
        return if (geneIdLength == 0)
            timestamp or datacenterId or workerId or realSequence
        else {
            val shlBitsForSequence = shlBitsForWorker - sequenceIdLength
            val seq = realSequence shl shlBitsForSequence
            timestamp or datacenterId or workerId or seq or gene
        }
    }
 
    /**
     * Get gene piece from original sequence,
     * 1011 1001 and 0000 1111 = 0000 1001,
     * 1111 = (1 << 4) - 1
     *
     * @param origin
     * @param geneLength
     * @return
     */
    fun getGene(origin: Long, geneLength: Int = actualGeneLength): Long {
        return origin and ((1 shl geneLength) - 1).toLong()
    }

    companion object {
        /**
         * Get a default SnowIdGenerator
         *
         * @return Length: Timestamp(41), Sequence(12), Gene(0)
         */
        @JvmStatic
        fun default(dataCenterId: Long = 0L, workerId: Long = 0L): SnowIdGenerator {
            return Builder()
                .startFrom(0L)
                .timestampLength(41)
                .dataCenterIdLength(5)
                .workerIdLength(5)
                .sequenceIdLength(12)
                .dataCenterId(dataCenterId)
                .workerId(workerId)
                .build()
        }

        /**
         * Get a SnowIdGenerator with gene piece
         *
         * @return Length: Timestamp(41), Gene([geneIdLength]), Sequence(12 - [geneIdLength])
         */
        @JvmStatic
        fun withGene(dataCenterId: Long = 0L, workerId: Long = 0L, geneIdLength: Int, actualGeneLength: Int = geneIdLength): SnowIdGenerator {
            return Builder()
                .startFrom(0L)
                .timestampLength(41)
                .dataCenterIdLength(5)
                .workerIdLength(5)
                .geneLength(geneIdLength)
                .actualGeneIdLength(actualGeneLength)
                .sequenceIdLength(12 - geneIdLength)
                .dataCenterId(dataCenterId)
                .workerId(workerId)
                .build()
        }
    }

    class Builder {
        private var startPoint: Long = 0L
        private var timestampLength: Int = 0
        private var dataCenterIdLength: Int = 0
        private var workerIdLength: Int = 0
        private var sequenceIdLength: Int = 0
        private var geneIdLength: Int = 0
        private var dataCenterId: Long = 0
        private var workerId: Long = 0
        private var actualGeneLength: Int = 0

        fun startFrom(startPoint: Long): Builder {
            this.startPoint = startPoint
            return this
        }

        fun timestampLength(length: Int): Builder {
            this.timestampLength = length
            return this
        }

        fun dataCenterIdLength(length: Int): Builder {
            this.dataCenterIdLength = length
            return this
        }

        fun workerIdLength(length: Int): Builder {
            this.workerIdLength = length
            return this
        }

        fun sequenceIdLength(length: Int): Builder {
            this.sequenceIdLength = length
            return this
        }

        fun geneLength(length: Int): Builder {
            this.geneIdLength = length
            return this
        }

        fun actualGeneIdLength(length: Int): Builder {
            this.actualGeneLength = length
            return this
        }

        fun dataCenterId(id: Long): Builder {
            this.dataCenterId = id
            return this
        }

        fun workerId(id: Long): Builder {
            this.workerId = id
            return this
        }

        fun build(): SnowIdGenerator {
            return SnowIdGenerator(
                startPoint, timestampLength, dataCenterIdLength, workerIdLength,
                sequenceIdLength, geneIdLength, dataCenterId, workerId, actualGeneLength
            )
        }
    }
}