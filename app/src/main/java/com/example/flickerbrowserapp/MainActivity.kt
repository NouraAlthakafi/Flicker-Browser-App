package com.example.flickerbrowserapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var rvImages: RecyclerView
    lateinit var imageArray: ArrayList<Images>
    lateinit var imgS: ImageView
    lateinit var linearLayoutS: LinearLayout
    lateinit var etSearch: EditText
    lateinit var btnSearch: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvImages = findViewById(R.id.rvImages)
        imageArray = arrayListOf()
        rvImages.adapter = RVimages(this, imageArray)
        rvImages.layoutManager = LinearLayoutManager(this)
        imgS = findViewById(R.id.imgS)
        linearLayoutS = findViewById(R.id.llS)
        etSearch = findViewById(R.id.etS)
        btnSearch = findViewById(R.id.btnS)
        imgS.setOnClickListener{
            imageClose()
        }
        btnSearch.setOnClickListener {
            val search = "${etSearch.text}"
            if(search.isNotEmpty()){
                callAPI()
            }
            else{
                Toast.makeText(applicationContext, "Type something to start searching!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun imageClose(){
        rvImages.isVisible = true
        imgS.isVisible = false
        linearLayoutS.isVisible = true
    }

    private fun getImages(): String{
        val search = "${etSearch.text}"
        var response = ""
        try{
            response = URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=120269d539af2d614947b014d1bac957&tags=$search&per_page=100&format=json&nojsoncallback=1")
                .readText(Charsets.UTF_8)
        }catch(e: Exception){
            println("ISSUE: $e")
            Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
        return response
    }

    private suspend fun displayImages(data: String){
        withContext(Dispatchers.Main){
            val jsonObj = JSONObject(data)
            val images = jsonObj.getJSONObject("photos").getJSONArray("photo")
            for(i in 0 until images.length()){
                val title = images.getJSONObject(i).getString("title")
                val farm = images.getJSONObject(i).getString("farm")
                val server = images.getJSONObject(i).getString("server")
                val id = images.getJSONObject(i).getString("id")
                val secret = images.getJSONObject(i).getString("secret")
                val imageLink = "https://farm$farm.staticflickr.com/$server/${id}_$secret.jpg"
                imageArray.add(Images(title, imageLink))
            }
            rvImages.adapter?.notifyDataSetChanged()
        }
    }

    private fun callAPI(){
        CoroutineScope(Dispatchers.IO).launch {
            val data = async { getImages() }.await()
            if(data.isNotEmpty()){
                displayImages(data)
            }else{
                Toast.makeText(applicationContext, "No results found!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun displayFullImage(url: String){
        Glide.with(this).load(url).into(imgS)
        rvImages.isVisible = false
        imgS.isVisible = true
        linearLayoutS.isVisible = false
    }
}