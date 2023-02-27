package com.lhr.teethHospital.util.Setting

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.lhr.teethHospital.File.*
import com.lhr.teethHospital.Model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.Model.Model.Companion.BACKUP_NAME
import com.lhr.teethHospital.Model.Model.Companion.HOSPITAL_CSV
import com.lhr.teethHospital.Model.Model.Companion.RECORD_CSV
import com.lhr.teethHospital.Model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.Model.Model.Companion.mainActivity
import com.lhr.teethHospital.Model.Model.Companion.personalManagerFragment
import com.lhr.teethHospital.R
import com.lhr.teethHospital.Room.SqlDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class SettingFragment : Fragment(), View.OnClickListener {

    lateinit var textUploadBackup: TextView
    lateinit var textDownloadBackup: TextView
    lateinit var presenter: SettingPresenter
    var dataBase = SqlDatabase(mainActivity)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_setting, container, false)

        presenter = SettingPresenter(this)

        textUploadBackup = view.findViewById(R.id.textUploadBackup)
        textDownloadBackup = view.findViewById(R.id.textDownloadBackup)
        textUploadBackup.setOnClickListener(this)
        textDownloadBackup.setOnClickListener(this)
        return view
    }

    val uploadBackupResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {

                presenter.handleSignInIntent(data)
            } else {
                Log.v("PPPPPPPPPPPP", "LLLLLLLLLLLLLLLLLLL")
            }
        }
    }

    val downloadBackupResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            val uri = data!!.data!!
//            var data = data!!.data!!
//            CsvToSql(this, false)
//            presenter.selectSql(adapter)

            val mExecutor: Executor = Executors.newSingleThreadExecutor()
            Tasks.call(mExecutor) {
                mainActivity.progressBar.visibility = View.VISIBLE
                mainActivity.imageViewBlack.visibility = View.VISIBLE
                UnZip(uri, TEETH_DIR, mainActivity)
                CsvToSql().csvToHospitalSql(mainActivity, File(TEETH_DIR + HOSPITAL_CSV).toUri())
                CsvToSql().csvToRecordSql(mainActivity, File(TEETH_DIR + RECORD_CSV).toUri())
                personalManagerFragment.presenter.updateRecyclerInfo()
                DeleteFile(TEETH_DIR + HOSPITAL_CSV)
                DeleteFile(TEETH_DIR + RECORD_CSV)
                mainActivity.imageViewBlack.visibility = View.INVISIBLE
                mainActivity.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textUploadBackup -> {
                // 創建檔案
                File(APP_FILES_PATH, BACKUP_NAME).createNewFile()
                lateinit var hospitalCursor: Cursor
                lateinit var recordCursor: Cursor
                runBlocking {     // 阻塞主執行緒
                    launch(Dispatchers.IO) {
                        hospitalCursor = dataBase.getHospitalDao().getCursor()
                        recordCursor = dataBase.getRecordDao().getCursor()
                    }
                }
                SqlToCsv(mainActivity, hospitalCursor, TEETH_DIR + HOSPITAL_CSV)
                SqlToCsv(mainActivity, recordCursor, TEETH_DIR + RECORD_CSV)
                ZipUtils(mainActivity.contentResolver.openOutputStream(File(APP_FILES_PATH, BACKUP_NAME).toUri())!!)
                DeleteFile(TEETH_DIR + HOSPITAL_CSV)
                DeleteFile(TEETH_DIR + RECORD_CSV)
                presenter.requestStoragePremission()
                presenter.requestSignIn()
            }
            R.id.textDownloadBackup -> {
                val picker = Intent(Intent.ACTION_GET_CONTENT)
                picker.type = "*/*"
                downloadBackupResult.launch(picker)

//                lateinit var hospitalCursor: Cursor
//                runBlocking {     // 阻塞主執行緒
//                    launch(Dispatchers.IO) {
//                        hospitalCursor = dataBase.getHospitalDao().getCursor()
//                    }
//                }
//                SqlToCsv(mainActivity, hospitalCursor, TEETH_DIR + HOSPITAL_CSV)
            }
        }
    }
}