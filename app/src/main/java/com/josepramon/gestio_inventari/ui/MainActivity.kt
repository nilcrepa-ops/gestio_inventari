package com.josepramon.gestio_inventari.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.josepramon.gestio_inventari.R
import com.josepramon.gestio_inventari.data.local.InventoryDatabase
import com.josepramon.gestio_inventari.data.repository.ProductRepository
import com.josepramon.gestio_inventari.ui.adapter.ProductAdapter
import com.josepramon.gestio_inventari.ui.viewmodel.ProductViewModel
import com.josepramon.gestio_inventari.ui.viewmodel.ProductViewModelFactory

class MainActivity : AppCompatActivity() {

    // Base de dades -> repositori -> fàbrica -> ViewModel.
    // "by viewModels" el crea un sol cop i sobreviu a les rotacions.
    private val viewModel: ProductViewModel by viewModels {
        val database = InventoryDatabase.getDatabase(applicationContext)
        val repository = ProductRepository(database.productDao())
        ProductViewModelFactory(repository)
    }

    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // El teu layout no té id a l'arrel, així que s'apliquen els marges
        // de les barres del sistema sobre el contenidor de la pantalla.
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View>(android.R.id.content)
        ) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        setupRecyclerView()
        observeProducts()
        setupFab()
    }

    private fun setupRecyclerView() {
        // L'adaptador només avisa dels clics; aquí els connectem al ViewModel
        adapter = ProductAdapter(
            onIncrease = { product -> viewModel.increaseQuantity(product) },
            onDecrease = { product -> viewModel.decreaseQuantity(product) },
            onDelete = { product -> viewModel.deleteProduct(product) }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.rvProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeProducts() {
        // Cada cop que canvia la taula a SQLite, Room emet una llista nova
        viewModel.allProducts.observe(this) { products ->
            adapter.submitList(products)
        }
    }

    private fun setupFab() {
        // Atenció: és un ExtendedFloatingActionButton, no un FloatingActionButton
        findViewById<ExtendedFloatingActionButton>(R.id.fabAddProduct)
            .setOnClickListener { showAddProductDialog() }
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val tilName = dialogView.findViewById<TextInputLayout>(R.id.tilProductName)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etProductName)
        val tilQuantity = dialogView.findViewById<TextInputLayout>(R.id.tilProductQuantity)
        val etQuantity = dialogView.findViewById<TextInputEditText>(R.id.etProductQuantity)

        val dialog: AlertDialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)                      // el títol ja és al XML
            .setPositiveButton("Afegir", null)        // es defineix a sota per controlar el tancament
            .setNegativeButton("Cancel·lar", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = etName.text.toString().trim()
                val quantity = etQuantity.text.toString().trim().toIntOrNull()

                tilName.error = if (name.isEmpty()) "Escriu un nom" else null
                tilQuantity.error =
                    if (quantity == null || quantity < 0) "Introdueix una quantitat vàlida" else null

                // Només es tanca si els dos camps són correctes
                if (name.isNotEmpty() && quantity != null && quantity >= 0) {
                    viewModel.insertProduct(name, quantity)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}