package com.example.firebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class novo_produto : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_produto)

        val btnSalvarProduto = findViewById<Button>(R.id.btnSalvarProduto)

        btnSalvarProduto.setOnClickListener{
            val nomeProduto = findViewById<EditText>(R.id.edtNomeProduto).text.toString()
            val precoProduto = findViewById<EditText>(R.id.edtPrecoProduto).text.toString()

            val dados = hashMapOf(
                "nomeproduto" to nomeProduto,
                "precoproduto" to precoProduto
            )

            db.collection("produto").add(dados)
                .addOnSuccessListener {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Falha ao inserir produto", Toast.LENGTH_SHORT).show()
                }
        }
    }
}