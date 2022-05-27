package mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos

import android.R
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.time.Instant
import java.time.ZoneId
import mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos.databinding.ActivityMainBinding as ActivityMainBinding1

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding1
    val siPermiso = 1
    val siPermisoReceiver = 2
    var cadena = ArrayList<String>()
    var cadena2=""
    var i=0
    var vector = ArrayList<String>()
    var listaIDs = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding1.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("ALMACEN SMS")
        //--------------lo equivalente al snapshot
        val consulta = FirebaseDatabase.getInstance().getReference().child("sms")
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var datos = ArrayList<String>()
                listaIDs.clear()

                for(data in snapshot.children!!){
                    val id = data.key
                    listaIDs.add(id!!)
                    val telefono = data.getValue<Datos>()!!.telefono
                    val mensaje = data.getValue<Datos>()!!.mensaje
                    val fechahora = data.getValue<Datos>()!!.fechahora
                    datos.add("Teléfono: ${telefono}\nMensaje: ${mensaje}\nFecha y Hora: ${fechahora}")
                }
                mostrarLista(datos)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        }
        consulta.addValueEventListener(postListener)//equivalente a un start
        //-------------


        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS), siPermisoReceiver)
        }

       /*binding.button.setOnClickListener{
            //PARA SOLICITAR EL PERMISO DE QUE SE EJECUTA
            if(ActivityCompat.checkSelfPermission(this, //PREGUNTA SI TIENE OTORGADO UN PERMISO(DENEGADO O OTORGAADO)
                android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), siPermiso)
            }else{
                envioSMS()
            }
        }*/
        binding.button3.setOnClickListener {
            try {
                val archivo = BufferedReader(InputStreamReader(openFileInput("nomerepruebesbenigno.txt")))
                var listaContenido = archivo.readLine()//archivo de tipo list
                var arregloFrases = listaContenido.split(",")
                var cadenaprueba = ""
                (0..arregloFrases.size-1).forEach {
                    cadenaprueba += arregloFrases[it]
                    vector.add(arregloFrases[it])
                }
                Toast.makeText(this, "${listaContenido}", Toast.LENGTH_LONG).show()

            }catch (e: Exception){
                android.app.AlertDialog.Builder(this)
                    .setMessage(e.message).show()
            }
        }
        binding.button2.setOnClickListener {
            //--------------lo equivalente al snapshot
            try {
                val archivo = OutputStreamWriter(openFileOutput("nomerepruebesbenigno.txt", MODE_PRIVATE))
                val consulta = FirebaseDatabase.getInstance().getReference().child("sms")
                val postListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaIDs.clear()
                    cadena.clear()
                    for(data in snapshot.children!!){
                        i++
                        val id = data.key
                        listaIDs.add(id!!)
                        val telefono = data.getValue<Datos>()!!.telefono
                        val mensaje = data.getValue<Datos>()!!.mensaje
                        val fechahora = data.getValue<Datos>()!!.fechahora
                        cadena.add("Teléfono: ${telefono} Mensaje: ${mensaje} Fecha y Hora: ${fechahora}")
                    }
                    if(i==snapshot.children.count()-1){
                        val separador=" "
                        cadena2 = cadena.joinToString(separador)
                    }else{
                        val separador=","
                        cadena2 = cadena.joinToString(separador)
                    }
                    System.out.println(cadena2)
                    archivo.write(cadena2)
                    archivo.flush()
                    archivo.close()
                }
                override fun onCancelled(error: DatabaseError) {

                }
            }
            consulta.addValueEventListener(postListener)//equivalente a un start
            //-------------
            }catch (e: Exception){
                androidx.appcompat.app.AlertDialog.Builder(this).setMessage(e.message).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == siPermiso){
            //envioSMS()
        }
        if(requestCode == siPermisoReceiver){
            mensajeRecibir()
        }
    }

    private fun mensajeRecibir() {
        AlertDialog.Builder(this)
            .setMessage("SE OTORGO RECIBIR")
            .show()
    }

    /*private fun envioSMS() {
        SmsManager.getDefault().sendTextMessage(3113403943.toString(),null, "axel", null, null)
        Toast.makeText(this, "SE ENVIO EL SMS", Toast.LENGTH_LONG)
            .show()
    }*/
    fun mostrarLista(datos: ArrayList<String>) {
        binding.lista.adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, datos)
    }
}