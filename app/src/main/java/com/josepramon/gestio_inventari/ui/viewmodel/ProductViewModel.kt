package com.josepramon.gestio_inventari.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.josepramon.gestio_inventari.data.local.ProductEntity
import com.josepramon.gestio_inventari.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository): ViewModel() {

    val allProducts: LiveData<List<ProductEntity>> = repository.allProducts.asLiveData()

    fun insertProduct(name: String, quantity: Int){
        if (name.isBlank() || quantity < 0) return

        viewModelScope.launch(Dispatchers.IO){
            val newProduct = ProductEntity(name = name.trim(), quantity = quantity)
            repository.insert(newProduct)
        }
    }

    fun increaseQuantity(product: ProductEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateQuantity(product.id, product.quantity + 1)
        }
    }

    fun decreaseQuantity(product: ProductEntity){
        if(product.quantity <= 0) return

        viewModelScope.launch(Dispatchers.IO){
            repository.updateQuantity(product.id, product.quantity -1)
        }
    }

    fun deleteProduct(product: ProductEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.delete(product)
        }
    }
}