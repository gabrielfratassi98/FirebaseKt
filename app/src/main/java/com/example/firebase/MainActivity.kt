package com.example.firebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    val listaDados = mutableListOf<Produto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BuscarProdutosFirebase()

        val btnNovoProduto = findViewById<Button>(R.id.btnNovoProduto)

        btnNovoProduto.setOnClickListener{
            val intent = Intent(this, novo_produto::class.java)
            addProductRequest.launch(intent)
        }

        val listaProdutos = findViewById<ListView>(R.id.lstProduto)

        listaProdutos.setOnItemLongClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(this)
            builder
                .setTitle("Apagar produto")

                .setMessage("Deseja apagar o produto?")

                .setPositiveButton("Sim") { dialog, which ->
                    val produto = listaDados[position]

                    ExcluirProduto(produto.id)

                }.setNegativeButton("Não"){ dialog, which ->
                    Toast.makeText(this, "Produto não removido", Toast.LENGTH_LONG).show()
                }
                .show()
            true
        }
    }

    private val addProductRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            BuscarProdutosFirebase()
        }
    }

    private fun BuscarProdutosFirebase(){
        db.collection("produto")
            .get()
            .addOnSuccessListener { querySnapshot ->
                ListarProdutos(querySnapshot)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Falha ao buscar produtos", Toast.LENGTH_SHORT).show()
            }
    }

    data class Produto(
        val id: String,
        val nome: String,
        val preco: String,
    )

    private fun ListarProdutos(querySnapshot: QuerySnapshot) {
        listaDados.clear()

        for (document in querySnapshot) {
            val id = document.id
            val nome = document.getString("nomeproduto")
            val preco = document.getString("precoproduto")

            if (id != null && nome != null && preco != null) {
                val produto = Produto(id, nome, preco)
                listaDados.add(produto)
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDados.map { it.nome + " | R$ " + it.preco })

        val lista = findViewById<ListView>(R.id.lstProduto)
        lista.adapter = adapter
    }

    private fun ExcluirProduto(id: String) {
        val documento = db.collection("produto").document(id)

        documento.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Produto removido", Toast.LENGTH_SHORT).show()
                BuscarProdutosFirebase()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Falha ao remover produto", Toast.LENGTH_SHORT).show()
            }
    }
}