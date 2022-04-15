package com.example.pockemonapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.fragment.app.FragmentActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : FragmentActivity(), OnMapReadyCallback  {

    //WORK WITH USER LOCATION


    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        checkPermmison()
        LoadPockemon()
    }

    var ACCESSLOCATION=123
    fun checkPermmison(){

        if(Build.VERSION.SDK_INT>=30){

            if(ActivityCompat.
                checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }

        GetUserLocation()
    }

    fun GetUserLocation(){
        Toast.makeText(this,"User location access on",Toast.LENGTH_LONG).show()
        //TODO: Will implement later

        var myLocation= MylocationListener()

        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)

        var mythread=myThread()
        mythread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            ACCESSLOCATION->{

                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                }else{
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


    }

    var location:Location?=null

    //Get user location

    inner class MylocationListener:LocationListener{


        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.longitude=0.0
        }

        override fun onLocationChanged(p0: Location) {
            TODO("Not yet implemented")
            location=p0
        }

//        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
//            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onProviderEnabled(p0: String?) {
//            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onProviderDisabled(p0: String?) {
//            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }

    }


    var oldLocation:Location?=null
    inner class myThread:Thread{

        constructor():super(){
            oldLocation= Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.longitude=0.0
        }

        override fun run(){

            while (true){

                try {

                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }

                    oldLocation=location


                    runOnUiThread {


                        mMap!!.clear()

                        // show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                            .position(sydney)
                            .title("Me")
                            .snippet(" here is my location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 7f))

                        // show pockemons

                        for(i in 0..listPockemons.size-1){

                            var newPockemon=listPockemons[i]

                            if(newPockemon.IsCatch==false){

                                val pockemonLoc = LatLng(newPockemon.location!!.latitude, newPockemon.location!!.longitude)
                                mMap!!.addMarker(MarkerOptions()
                                    .position(pockemonLoc)
                                    .title(newPockemon.name!!)
                                    .snippet(newPockemon.des!! +", power:"+ newPockemon!!.power)
                                    .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!)))


                                if (location!!.distanceTo(newPockemon.location)<2){
                                    newPockemon.IsCatch=true
                                    listPockemons[i]=newPockemon
                                    playerPower+=newPockemon.power!!
                                    Toast.makeText(applicationContext,
                                        "You catch new pockemon your new pwoer is " + playerPower,
                                        Toast.LENGTH_LONG).show()

                                }

                            }
                        }





                    }

                    Thread.sleep(1000)

                }catch (ex:Exception){}


            }

        }

    }


    var playerPower=0.0
    var listPockemons=ArrayList<pockemon>()

    fun  LoadPockemon(){


        listPockemons.add(pockemon(R.drawable.charmander,
            "Charmander", "Charmander living in japan", 55.0, 37.77, -122.40))
        listPockemons.add(pockemon(R.drawable.bulbasaur,
            "Bulbasaur", "Bulbasaur living in usa", 90.5, 37.79, -122.41))
        listPockemons.add(pockemon(R.drawable.squirtle,
            "Squirtle", "Squirtle living in iraq", 33.5, 37.78, -122.412))

    }

}