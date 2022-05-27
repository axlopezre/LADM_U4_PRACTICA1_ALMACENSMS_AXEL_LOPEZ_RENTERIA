package mx.tecnm.tepic.ladm_u4_ejercicio1_smspermisos

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Datos (val telefono: String?=null, val mensaje: String?=null, val fechahora: String?=null){
}