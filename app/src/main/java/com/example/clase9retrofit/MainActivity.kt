package com.example.clase9retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BreedsAdapter
    private lateinit var spinner: Spinner
    private var imagesByBreedList = mutableListOf<String>()
    private var breedsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler)
        spinner = findViewById(R.id.spinner)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BreedsAdapter(imagesByBreedList)
        recyclerView.adapter = adapter

        getListOfBreeds()
    }

    private fun getListOfBreeds() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getListOfBreed("breeds/list/all")
            val response = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    val breedsMap = response?.message
                    if (breedsMap != null) {
                        for (breed in breedsMap.keys)
                            breedsList.add(breed)
                        setSpinner()

                    }
                } else {
                    showError()
                }
            }
        }
    }

    private fun setSpinner() {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, breedsList)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                getImagesBy(breedsList[p2])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun getImagesBy(breed: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getImagesByBreeds("breed/$breed/images")
            val response = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    val images = response?.images ?: emptyList()
                    imagesByBreedList.clear()
                    imagesByBreedList.addAll(images)
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "fallo en la llamada", Toast.LENGTH_SHORT).show()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}