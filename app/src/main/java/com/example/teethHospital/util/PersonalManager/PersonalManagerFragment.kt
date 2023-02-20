package com.example.teethHospital.util.PersonalManager

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teethHospital.Model.Model.Companion.IMPORT_CSV
import com.example.teethHospital.Model.Model.Companion.mainActivity
import com.example.teethHospital.R
import com.example.teethHospital.RecyclerViewAdapter.HospitalInfoAdapter
import com.example.teethHospital.Room.SqlDatabase
import com.example.teethHospital.Room.CsvToSql
import com.example.teethHospital.util.Camera.ChooseImagePopupWindow
import com.example.teethHospital.util.PersonalManager.Import.ImportPopupWindow


class PersonalManagerFragment : Fragment(), View.OnClickListener {
    companion object {
        const val CLASS_LIST : Int = 500
        const val CLASSMATE_LIST : Int = 501
        var recyclerInfoStatus = CLASS_LIST
    }

    lateinit var imageAdd: ImageView
    lateinit var imageBack: ImageView
    lateinit var imageDelete: ImageView
    lateinit var textClassInfo: TextView
    lateinit var textClassName: TextView
    lateinit var constrainClassmate: ConstraintLayout
    lateinit var recyclerInfo: RecyclerView
    lateinit var classInfoAdapter: HospitalInfoAdapter
    var presenter: PersonalManagerPresenter = PersonalManagerPresenter(this)
    val dataBase = SqlDatabase(mainActivity)
    var isShowCheckBox = false

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_personal_manager, container, false)

        imageAdd = view.findViewById(R.id.imageAdd)
        imageDelete = view.findViewById(R.id.imageDelete)
        imageBack = view.findViewById(R.id.imageBack)
        textClassInfo = view.findViewById(R.id.textClassInfo)
        textClassName = view.findViewById(R.id.textClassName)
        constrainClassmate = view.findViewById(R.id.constrainClassmate)
        recyclerInfo = view.findViewById(R.id.recyclerInfo)

        presenter.getClassInformation()

        recyclerInfo.layoutManager = LinearLayoutManager(mainActivity)
        recyclerInfo.addItemDecoration(
            DividerItemDecoration(
                mainActivity,
                DividerItemDecoration.VERTICAL
            )
        )
        classInfoAdapter = HospitalInfoAdapter(this)
        recyclerInfo.adapter = classInfoAdapter

        imageAdd.setOnClickListener(this)
        imageDelete.setOnClickListener(this)
        imageBack.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageAdd -> {
                val choose = ImportPopupWindow(mainActivity,this)
                val view: View = LayoutInflater.from(mainActivity).inflate(
                    R.layout.popup_window_import,
                    null
                )
                choose.showAtLocation(view, Gravity.CENTER, 0, 0)
                //強制隱藏鍵盤
                val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mainActivity.window.decorView.windowToken, 0)

//                val pickIntent = Intent(
//                    Intent.ACTION_GET_CONTENT
//                )
//                pickIntent.type = "*/*"
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                    Log.v("PPPPPPPPPPPP","Build.VERSION_CODES.Q")
//                    importResult.launch(pickIntent)
//                } else {
//                    //support for older than android 11
//                    Log.v("PPPPPPPPPPPP","support for older than android 11")
//                    ActivityCompat.startActivityForResult(mainActivity, pickIntent, IMPORT_CSV, null)
//                }
            }
            R.id.imageBack -> {
                presenter.back()
            }
            R.id.imageDelete -> {
                presenter.deleteRecord()
            }
        }
    }

    val importResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val myData: Intent? = result.data
            if (myData != null) {
                CsvToSql(mainActivity, myData.data!!)
                presenter.updateRecyclerInfo()
            }else{
                Log.v("PPPPPPPPPPPP","LLLLLLLLLLLLLLLLLLL")
            }
        }
    }
}
