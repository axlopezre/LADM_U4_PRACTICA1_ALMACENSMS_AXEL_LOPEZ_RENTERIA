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
import androidx.core.view.size
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.io.*
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.InvocationTargetException
import java.time.Instant
import java.time.ZoneId
import mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos.databinding.ActivityMainBinding as ActivityMainBinding1

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding1
    val siPermiso = 1
    val siPermisoReceiver = 2
    var cadena = ArrayList<String>()
    var cadena2=""
    var cadena3=""
    var i=0
    private var err=""
    var vector = ArrayList<String>()
    var listaIDs = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding1.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("ALMACEN SMS")
        AlertDialog.Builder(this)
            .setTitle("VENTANA INFORMATIVA")
            .setMessage("CUANDO SE LE DE CLIC EN DESCARGAR LISTADO SMS AUTOMATICAMENTE SE CREARÁ UN ARCHIVO EXCEL EN LA SIGUIENTE RUTA(TOMAR EN CUENTA QUE ES NECESARIO DARLE CLIC EN LOS 3 PUNTITOS DE ARRIBA Y ACTIVAR SHOW INTERNAL STORAGE): SETTINGS->STORAGE->SDCARD->Android->data->mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos->files->DirectorioSMS->ListaSMS.csv/\n" +
                    "\nUNA VES QUE PUEDA CREAR EL ARCHIVO EXCEL DE LA RUTA ESPECIFICADA PODRÁ VISUALIZAR TODOS LOS SMS ENTRANTES QUE SE HAN REGISTRADO EN LA BD REALTIME)" +
                    "\nPOR ULTIMO EL BOTON DE LEER LISTADO SMS ES PARA VER EN UN MAKETEXT LOS SMS QUE TIENE EL ARCHIVO EXCEL PREVIAMENTE CREADO.")
            .show()
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),2)
        }
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE),2)
        }
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),2)
        }
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
        binding.button3.setOnClickListener {
           try{
            try {
                val archivo = InputStreamReader(openFileInput("nomerepruebesbenigno.csv"))
                var listaContenido = archivo.readLines()


                var cadena2 = ""
                (0.. listaContenido.size-1).forEach {
                    cadena2 = cadena2 + listaContenido.get(it)
                }
                Toast.makeText(this, "${cadena2}", Toast.LENGTH_LONG).show()

            }catch (e: Exception){
                android.app.AlertDialog.Builder(this)
                    .setMessage(e.message).show()
            }
           }catch (err: RuntimeException){
               this.err = err.message!!
           }
        }
        binding.button2.setOnClickListener {
            //--------------lo equivalente al snapshot
            try {
                val path = this.getExternalFilesDir(null)

                val letDirectory = File(path, "DirectorioSMS")
                letDirectory.mkdirs()

                val file = File(letDirectory, "ListaSMS.csv")

                if(file.exists()){
                    file.delete()
                }else{
                    File(letDirectory, "ListaSMS.csv")
                }

                val archivo = OutputStreamWriter(openFileOutput("nomerepruebesbenigno.csv", MODE_PRIVATE))
                    try{
                        try{
                            (0.. binding.lista.size-1).forEach {
                                cadena2 = cadena2 + binding.lista.getItemAtPosition(it).toString()+",\n"
                            }
                            cadena2.replace("\n","")
                            archivo.write(cadena2)
                            archivo.flush()
                            archivo.close()
                            android.app.AlertDialog.Builder(this)
                                .setMessage("SE GUARDARON LOS DATOS(EXCEL)").show()

                            var cadena3 = ""
                            (0.. binding.lista.size-1).forEach {
                                cadena3 = cadena3 + binding.lista.getItemAtPosition(it).toString()+",\n"
                            }
                            cadena3.replace("\n","")

                            file.appendText(cadena3)


                    }catch (er: InvocationTargetException){

                    }
                    }catch (er: IOException){

                    }
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

    fun mostrarLista(datos: ArrayList<String>) {
        binding.lista.adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, datos)
    }
}