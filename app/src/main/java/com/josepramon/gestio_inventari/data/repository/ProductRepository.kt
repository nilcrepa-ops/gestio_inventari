package com.josepramon.gestio_inventari.data.repository

import com.josepramon.gestio_inventari.data.local.ProductDao
import com.josepramon.gestio_inventari.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository (private val productDao : ProductDao){

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun insert(product: ProductEntity){
        productDao.insertProduct(product)
    }

    suspend fun update(product: ProductEntity){
        productDao.updateProduct(product)
    }

    suspend fun delete(product: ProductEntity){
        productDao.deleteProduct(product)
    }

    suspend fun updateQuantity(productId: Long, newQuantity: Int){
        productDao.updateQuantity(productId, newQuantity)
    }

    suspend fun deleteById(productId: Long){
        productDao.deleteById(productId)
    }
}