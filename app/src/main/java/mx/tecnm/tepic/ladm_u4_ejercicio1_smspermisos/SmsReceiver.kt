package mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.RuntimeException
import java.time.Instant
import java.time.ZoneId

/*
    RECEIVER = evento o eyente de android que permite la lectura de archivos del sistema operativo
 */
class SmsReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) { //EL INTENT TRAE LA DATA DE LO QUE SUCEDIO
        val extras = intent.extras //SON PARAMETROS DE ENVIO DE ALGO A UN ACTIVITY
        var fecha=""
        if(extras!=null){
            try {
                var sms = extras.get("pdus") as Array<Any> //recupero el sms que entra
                for (indice in sms.indices) {
                    var formato = extras.getString("format")
                    var smsMensaje = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                    } else {
                        SmsMessage.createFromPdu(sms[indice] as ByteArray)
                    }
                    var celularOrigen = smsMensaje.originatingAddress
                    var contenidoSMS = smsMensaje.messageBody.toString()
                    Toast.makeText(context, "ENTRO CONTENIDO ${celularOrigen}", Toast.LENGTH_LONG)
                        .show()

                    var basedatos = Firebase.database.reference
                    var fechaActual = Instant.now()
                    val mexico = fechaActual.atZone(ZoneId.of("America/Mazatlan")).toString()
                    val split = mexico.split(".")
                    fecha = split[0]
                    val datos = Datos(celularOrigen, contenidoSMS, fecha)

                    basedatos.child("sms").push().setValue(datos)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {

                        }

                }
            }catch (err:RuntimeException){

            }
        }
    }
}