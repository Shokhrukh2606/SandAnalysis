package com.example.sandanalysis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var mAdapter: AdapterSample
    private var samples: MutableList<ModelSample> = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    var page=1
    var isLoading=false
    var limit=10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper= DatabaseHelper(this)
        progressBar=findViewById(R.id.progressBar)
        mRecyclerView=findViewById(R.id.recyclerview_list)
        linearLayoutManager= LinearLayoutManager(this)
        mRecyclerView.layoutManager=linearLayoutManager
        mAdapter= AdapterSample(this,samples)
        mRecyclerView.adapter=mAdapter
        mRecyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView,dx:Int,dy:Int){
                if(dy>0){
                    val visibleItemsCount=linearLayoutManager.childCount
                    val pastVisibleItem=linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    val total=mAdapter.itemCount
                    if(!isLoading){
                        if((visibleItemsCount+pastVisibleItem)>=total){
                            page++
                            getPage()
                        }
                    }
                }
            }
        })



        fab = findViewById(R.id.addFabButton)
        fab.setOnClickListener {
            startActivity(Intent(this, AddRecordActivity::class.java))
        }
        getPage()


//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }
    @SuppressLint("NotifyDataSetChanged")
    private  fun getPage(){
        isLoading=true
        progressBar.visibility= View.VISIBLE
        val start:Int=(page-1)*limit
        val end:Int=(page)*limit
        val newSamples=dbHelper.getPaginated(Constants.C_ADD_TIMESTAMP+" DESC", start.toString(),limit.toString())
        Log.e("AddRecordActivity",newSamples.size.toString())
        for (i in newSamples){
            samples.add(i)
        }
        Handler().postDelayed({
            if (::mAdapter.isInitialized){
                mAdapter.notifyDataSetChanged()
            }else{
                samples=dbHelper.getAllSamples(Constants.C_ADD_TIMESTAMP+" DESC")
                mAdapter= AdapterSample(this,samples)
                mRecyclerView.adapter=mAdapter
            }
            isLoading=false
            progressBar.visibility=View.GONE
        },3000)
    }

    override fun onResume() {
        super.onResume()
        mRecyclerView.adapter=mAdapter
    }
}