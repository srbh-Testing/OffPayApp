package com.offpay.app.platform

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

/**
 * SPIKE (temporary): does sendUssdRequest keep the session open between calls?
 * Step 1: "*99#" -> toast menu text. Step 2: "3" -> toast reply.
 * No PIN is sent anywhere in this test.
 */
object UssdSpike {

    @SuppressLint("MissingPermission")
    fun run(context: Context) {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val h = Handler(Looper.getMainLooper())

        fun show(msg: String) {
            Log.d("OffPayUSSD", msg)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        try {
            tm.sendUssdRequest("*99#", object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(
                    t: TelephonyManager, req: String, res: CharSequence
                ) {
                    show("STEP1 OK: $res")
                    try {
                        t.sendUssdRequest("3", object : TelephonyManager.UssdResponseCallback() {
                            override fun onReceiveUssdResponse(
                                t2: TelephonyManager, r2: String, res2: CharSequence
                            ) {
                                show("STEP2 OK: $res2")
                            }

                            override fun onReceiveUssdResponseFailed(
                                t2: TelephonyManager, r2: String, code: Int
                            ) {
                                show("STEP2 FAIL code=$code")
                            }
                        }, h)
                    } catch (e: Exception) {
                        show("STEP2 EXC: ${e.javaClass.simpleName} ${e.message}")
                    }
                }

                override fun onReceiveUssdResponseFailed(
                    t: TelephonyManager, req: String, code: Int
                ) {
                    show("STEP1 FAIL code=$code")
                }
            }, h)
        } catch (e: Exception) {
            show("STEP1 EXC: ${e.javaClass.simpleName} ${e.message}")
        }
    }
}
