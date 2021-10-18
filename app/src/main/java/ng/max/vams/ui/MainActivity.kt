package ng.max.vams.ui

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import ng.max.vams.R
import ng.max.vams.databinding.ActivityMainBinding


private const val APP_UPDATE_CODE = 9110

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bnd: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_VAMS)
        super.onCreate(savedInstanceState)

        checkAppUpdate()

        bnd = ActivityMainBinding.inflate(layoutInflater)
        val view = bnd.root
        setContentView(view)
    }


    private fun checkAppUpdate(){
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                startUpdateFlow(appUpdateManager, appUpdateInfo)
            } else if  (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                startUpdateFlow(appUpdateManager, appUpdateInfo)
            }
        }

    }

    private fun startUpdateFlow(appUpdateManager: AppUpdateManager, appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                this,
                APP_UPDATE_CODE
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_CODE) {
            when (resultCode) {
                RESULT_CANCELED -> {
                    Toast.makeText(applicationContext,
                        "Update canceled by user! Result Code: $resultCode", Toast.LENGTH_LONG).show()
                }
                RESULT_OK -> {
                    Toast.makeText(applicationContext, "Update success! Result Code: $resultCode", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(applicationContext, "Update Failed! Result Code: $resultCode", Toast.LENGTH_LONG).show()
                    checkAppUpdate()
                }
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }
}