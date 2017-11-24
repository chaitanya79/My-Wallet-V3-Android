package piuk.blockchain.android.ui.shapeshift.overview.adapter

import android.app.Activity
import piuk.blockchain.android.ui.adapters.AdapterDelegatesManager
import piuk.blockchain.android.ui.adapters.DelegationAdapter
import piuk.blockchain.android.util.extensions.autoNotify
import kotlin.properties.Delegates

class TradesAdapter(
        activity: Activity,
        btcExchangeRate: Double,
        ethExchangeRate: Double,
        showCrypto: Boolean,
        listClickListener: TradesListClickListener
) : DelegationAdapter<Any>(AdapterDelegatesManager(), emptyList()) {

    private val tradesDelegate = TradesDisplayableDelegate<Any>(activity, btcExchangeRate, ethExchangeRate, showCrypto, listClickListener)

    init {
        // Add all necessary AdapterDelegate objects here
        delegatesManager.addAdapterDelegate(TradesHeaderDelegate(listClickListener))
        delegatesManager.addAdapterDelegate(tradesDelegate)
        setHasStableIds(true)
    }

    /**
     * Observes the items list and automatically notifies the adapter of changes to the data based
     * on the comparison we make here, which is a simple equality check.
     */
    override var items: List<Any> by Delegates.observable(emptyList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o == n }
    }

    /**
     * Required so that [setHasStableIds] = true doesn't break the RecyclerView and show duplicated
     * layouts.
     */
    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    fun updateTradeList(trades: List<Any>) {
        var mutableList = trades.toMutableList()
        mutableList.add(0, "")
        items = mutableList
    }

    /**
     * Notifies the adapter that the View format (ie, whether or not to show BTC) has been changed.
     * Will rebuild the entire adapter.
     */
    fun onViewFormatUpdated(isBtc: Boolean, btcFormat: Int) {
        tradesDelegate.onViewFormatUpdated(isBtc, btcFormat)
        notifyDataSetChanged()
    }

    /**
     * Notifies the adapter that the BTC & ETH exchange rate for the selected currency has been updated.
     * Will rebuild the entire adapter.
     */
    fun onPriceUpdated(lastBtcPrice: Double, lastEthPrice: Double) {
        tradesDelegate.onPriceUpdated(lastBtcPrice, lastEthPrice)
        notifyDataSetChanged()
    }
}

interface TradesListClickListener {

    fun onTradeClicked(correctedPosition: Int, absolutePosition: Int)

    fun onValueClicked(isBtc: Boolean)

    fun onNewExchangeClicked()

}