package com.josepramon.gestio_inventari.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.josepramon.gestio_inventari.R
import com.josepramon.gestio_inventari.data.local.ProductEntity

class ProductAdapter(
    private val onIncrease: (ProductEntity) -> Unit,
    private val onDecrease: (ProductEntity) -> Unit,
    private val onDelete: (ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<ProductEntity> = emptyList()

    fun submitList(newList: List<ProductEntity>) {
        val diffResult = DiffUtil.calculateDiff(ProductDiffCallback(products, newList))
        products = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        val btnDecrease: MaterialButton = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: MaterialButton = itemView.findViewById(R.id.btnIncrease)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.tvName.text = product.name
        holder.tvQuantity.text = "Estoc: ${product.quantity} unitats"
        holder.btnDecrease.isEnabled = product.quantity > 0

        holder.btnIncrease.setOnClickListener { onIncrease(product) }
        holder.btnDecrease.setOnClickListener { onDecrease(product) }
        holder.btnDelete.setOnClickListener { onDelete(product) }
    }

    override fun getItemCount(): Int = products.size

    private class ProductDiffCallback(
        private val oldList: List<ProductEntity>,
        private val newList: List<ProductEntity>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos].id == newList[newPos].id

        override fun areContentsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos] == newList[newPos]
    }
}