package com.hieupham.domain.interactor.usecase

import com.hieupham.domain.entity.CompositeTransactions
import com.hieupham.domain.interactor.MaybeUseCase
import com.hieupham.domain.interactor.UseCase
import com.hieupham.domain.repository.TransactionRepository
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject

/**
 * Created by hieupham on 6/26/18.
 */
class GetTransactionsUseCase @Inject constructor(
        private val transactionRepo: TransactionRepository) : MaybeUseCase<UseCase.EmptyInput, CompositeTransactions>() {

    private var blockNumber: Long? = null
    private var blockHeight: Long? = null

    fun next(): GetTransactionsUseCase {
        blockNumber = blockNumber?.dec()
        return this
    }

    fun refresh(): GetTransactionsUseCase {
        blockNumber = null
        blockHeight = null
        return this
    }

    fun fetchLatest(): GetTransactionsUseCase {
        blockHeight = null
        return this
    }

    override fun buildDataStream(input: EmptyInput): Maybe<CompositeTransactions> {
        return if (blockNumber == null || blockHeight == null) fetchLatestTransactions() else getTransactions()
    }

    private fun fetchLatestTransactions(): Maybe<CompositeTransactions> {
        return transactionRepo.getBlockHeight().flatMapMaybe { height ->
            if (blockNumber == null && height != 0L) blockNumber = height
            if (Objects.equals(blockHeight, height)) Maybe.empty()
            else {
                blockHeight = height
                transactionRepo.getTransactions(height)
            }
        }
    }

    private fun getTransactions(): Maybe<CompositeTransactions> {
        return if (blockNumber != null) transactionRepo.getTransactions(
                blockNumber!!) else Maybe.empty()
    }
}