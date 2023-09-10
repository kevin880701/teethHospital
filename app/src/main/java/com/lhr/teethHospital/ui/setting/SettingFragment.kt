package com.lhr.teethHospital.ui.setting

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Tasks
import com.lhr.teethHospital.file.*
import com.lhr.teethHospital.model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.model.Model.Companion.BACKUP_NAME
import com.lhr.teethHospital.model.Model.Companion.HOSPITAL_CSV
import com.lhr.teethHospital.model.Model.Companion.RECORD_CSV
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.FragmentSettingBinding
import com.lhr.teethHospital.googleDrive.GoogleDriveServiceFunction
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.ui.main.MainViewModel.Companion.isProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class SettingFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentSettingBinding
    lateinit var viewModel: SettingViewModel
    lateinit var dataBase: SqlDatabase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_setting, container, false
        )
        val view: View = binding!!.root

        viewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(this.requireActivity().application)
        )[SettingViewModel::class.java]
        binding.viewModel = viewModel


        dataBase = SqlDatabase(this.requireActivity())

        binding.textUploadBackup.setOnClickListener(this)
        binding.textDownloadBackup.setOnClickListener(this)
        return view
    }

    val uploadBackupResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                GoogleDriveServiceFunction().uploadFile(this.requireActivity())
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
//                mainActivity.progressBar.visibility = View.VISIBLE
//                mainActivity.imageViewBlack.visibility = View.VISIBLE
                UnZip(uri, TEETH_DIR, this.requireActivity())
                CsvToSql().csvToHospitalSql(this.requireActivity(), File(TEETH_DIR + HOSPITAL_CSV).toUri())
                CsvToSql().csvToRecordSql(this.requireActivity(), File(TEETH_DIR + RECORD_CSV).toUri())
//                this.presenter.updateRecyclerInfo()
                DeleteFile(TEETH_DIR + HOSPITAL_CSV)
                DeleteFile(TEETH_DIR + RECORD_CSV)
//                mainActivity.imageViewBlack.visibility = View.INVISIBLE
//                mainActivity.progressBar.visibility = View.INVISIBLE

                val intent = Intent("updateRecyclerInfo")
                this.requireActivity().sendBroadcast(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textUploadBackup -> {
                isProgressBar.value = true
                // 創建要被備份的檔案
                File(APP_FILES_PATH, BACKUP_NAME).createNewFile()
                lateinit var hospitalCursor: Cursor
                lateinit var recordCursor: Cursor
                runBlocking {     // 阻塞主執行緒
                    launch(Dispatchers.IO) {
                        hospitalCursor = dataBase.getHospitalDao().getCursor()
                        recordCursor = dataBase.getRecordDao().getCursor()
                    }
                }
                SqlToCsv().hospitalSqlToCsv(this.requireContext(), hospitalCursor, TEETH_DIR + HOSPITAL_CSV)
                SqlToCsv().recordSqlToCsv(this.requireContext(), recordCursor, TEETH_DIR + RECORD_CSV)
                ZipUtils(this.requireContext().contentResolver.openOutputStream(File(APP_FILES_PATH, BACKUP_NAME).toUri())!!)
                DeleteFile(TEETH_DIR + HOSPITAL_CSV)
                DeleteFile(TEETH_DIR + RECORD_CSV)
                viewModel.uploadBackup(this)
            }
            R.id.textDownloadBackup -> {
                isProgressBar.value = true
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