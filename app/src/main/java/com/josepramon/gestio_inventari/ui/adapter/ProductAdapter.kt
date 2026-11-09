package com.josepramon.gestio_inventari.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.josepramon.gestio_inventari.R
import com.josepramon.gestio_inventari.data.local.ProductEntity

class ProductAdapter(
    private val onIncrease: (ProductEntity) -> Unit,
    private val onDecrease: (ProductEntity) -> Unit,
    private val onDelete: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)

        holder.tvName.text = product.name
        holder.tvQuantity.text = "Cantidad: ${product.quantity}"

        holder.btnDecrease.isEnabled = product.quantity > 0

        holder.btnIncrease.setOnClickListener { onIncrease(product) }
        holder.btnDecrease.setOnClickListener { onDecrease(product) }
        holder.btnDelete.setOnClickListener { onDelete(product) }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(old: ProductEntity, new: ProductEntity) =
            old.id == new.id

        override fun areContentsTheSame(old: ProductEntity, new: ProductEntity) =
            old == new
    }
}