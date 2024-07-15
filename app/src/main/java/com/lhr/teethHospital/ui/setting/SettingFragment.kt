package com.lhr.teethHospital.ui.setting

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Tasks
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.lhr.teethHospital.file.*
import com.lhr.teethHospital.model.Model.Companion.APP_FILES_PATH
import com.lhr.teethHospital.model.Model.Companion.BACKUP_NAME
import com.lhr.teethHospital.model.Model.Companion.HOSPITAL_CSV
import com.lhr.teethHospital.model.Model.Companion.RECORD_CSV
import com.lhr.teethHospital.model.Model.Companion.TEETH_DIR
import com.lhr.teethHospital.R
import com.lhr.teethHospital.databinding.FragmentSettingBinding
import com.lhr.teethHospital.googleDrive.DriveServiceHelper
import com.lhr.teethHospital.googleDrive.GoogleDriveServiceFunction
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.ui.base.BaseFragment
import com.lhr.teethHospital.ui.main.MainViewModel.Companion.isProgressBar
import com.lhr.teethHospital.util.dialog.EnterUrlDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SettingFragment : BaseFragment(), View.OnClickListener {

     val viewModel: SettingViewModel by viewModels { viewModelFactory }
    lateinit var binding: FragmentSettingBinding
    private var mDriveServiceHelper: DriveServiceHelper? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_setting, container, false
        )
        val view: View = binding!!.root

        binding.textUploadBackup.setOnClickListener(this)
        binding.textDownloadBackup.setOnClickListener(this)
        binding.textTestLogin.setOnClickListener(this)

        // 先確認登入GOOGLE帳戶
//        requestSignIn()
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
                UnZip(uri, TEETH_DIR, this.requireActivity())
                CsvToSql().csvToHospitalSql(this.requireActivity(), File(TEETH_DIR + HOSPITAL_CSV).toUri())
                CsvToSql().csvToRecordSql(this.requireActivity(), File(TEETH_DIR + RECORD_CSV).toUri())
                DeleteFile(TEETH_DIR + HOSPITAL_CSV)
                DeleteFile(TEETH_DIR + RECORD_CSV)

                val intent = Intent("updateRecyclerInfo")
                this.requireActivity().sendBroadcast(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textUploadBackup -> {
//                isProgressBar.value = true
                // 創建要被備份的檔案
                File(APP_FILES_PATH, BACKUP_NAME).createNewFile()
                lateinit var hospitalCursor: Cursor
                lateinit var recordCursor: Cursor
                runBlocking {     // 阻塞主執行緒
                    launch(Dispatchers.IO) {
                        hospitalCursor = SqlDatabase.getInstance().getHospitalDao().getCursor()
                        recordCursor = SqlDatabase.getInstance().getRecordDao().getCursor()
                    }
                }
                SqlToCsv().hospitalSqlToCsv(this.requireContext(), hospitalCursor, TEETH_DIR + HOSPITAL_CSV)
                SqlToCsv().recordSqlToCsv(this.requireContext(), recordCursor, TEETH_DIR + RECORD_CSV)
                ZipUtils(this.requireContext().contentResolver.openOutputStream(File(APP_FILES_PATH, BACKUP_NAME).toUri())!!)
                DeleteFile(TEETH_DIR + HOSPITAL_CSV)
                DeleteFile(TEETH_DIR + RECORD_CSV)

//                viewModel.uploadBackup(this)

                // 先確認登入GOOGLE帳戶
                requestSignIn()
                // 上傳備份檔案
                GlobalScope.launch {
                    uploadBackup()
                }
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
            R.id.textTestLogin -> {
                val account = GoogleSignIn.getLastSignedInAccount(this.requireContext())
                Timber.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                Timber.d(account.toString())


                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail() // 如果你需要获取用户的电子邮件地址，请添加此请求
                    .build()

                val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this.requireContext(), gso)
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }
    }
    val googleSignInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        Timber.d("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ + ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val account2 = GoogleSignIn.getLastSignedInAccount(this.requireContext())
                Timber.d("UUUUUUUUUUUUUUUUUUUUUUUUUUUUU")
                Timber.d(account2.toString())
                Timber.d("UUUUUUUUUUUUUUUUUUUUUUUUUUUUU")
                // 登录成功，可以获取用户信息并执行相应操作
            } catch (e: ApiException) {
                Timber.d("UUUUUUUUUUUUUUUUUUUUUUUUUUUUU")
                Timber.d(e)
                Timber.d("UUUUUUUUUUUUUUUUUUUUUUUUUUUUU")
                // 登录失败，处理异常情况
            }
        }else if (result.resultCode == Activity.RESULT_CANCELED) {
            Timber.d("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ")
            // 用户取消了登录操作
        }
    }


    val signInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        Log.v("@@@@@@@@@@","" + result.resultCode)
        Log.v("@@@@@@@@@@","" + RESULT_OK)
//        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                handleSignInResult(data)
            }
//        }
    }
    /**
     * Starts a sign-in activity using [.REQUEST_CODE_SIGN_IN].
     */
    private fun requestSignIn() {
        Log.d("requestSignIn", "Requesting sign-in")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        val client = GoogleSignIn.getClient(requireActivity(), signInOptions)

        signInResult.launch(client.signInIntent)
    }

    /**
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                Log.d("handleSignInResult", "Signed in as " + googleAccount.email)

                // Use the authenticated account to sign in to the Drive service.
                val credential = GoogleAccountCredential.usingOAuth2(
                    requireContext(), setOf(DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = googleAccount.account
                val googleDriveService =
                    Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        GsonFactory(),
                        credential
                    )
                        .setApplicationName("Drive API Migration")
                        .build()

                // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                // Its instantiation is required before handling any onClick actions.
                mDriveServiceHelper = DriveServiceHelper(googleDriveService)
            }
            .addOnFailureListener { exception: Exception? ->
                Log.e(
                    "addOnFailureListener",
                    "Unable to sign in.",
                    exception
                )
            }
    }

    private suspend fun uploadBackup() {
        if (mDriveServiceHelper != null) {
            mDriveServiceHelper!!.createFile()
        }
    }
}