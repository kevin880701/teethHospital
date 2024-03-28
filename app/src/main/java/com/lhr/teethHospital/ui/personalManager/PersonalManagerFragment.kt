package com.lhr.teethHospital.ui.personalManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lhr.teethHospital.file.CsvToSql
import com.lhr.teethHospital.R
import com.lhr.teethHospital.recyclerViewAdapter.PersonalManagerAdapter
import com.lhr.teethHospital.room.SqlDatabase
import com.lhr.teethHospital.popupWindow.ImportPopupWindow
import com.lhr.teethHospital.databinding.FragmentPersonalManagerBinding
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.isPersonalManagerBack
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.isShowCheckBox

class PersonalManagerFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentPersonalManagerBinding
    lateinit var viewModel: PersonalManagerViewModel
    lateinit var dataBase: SqlDatabase
    lateinit var personalManagerAdapter: PersonalManagerAdapter
    lateinit var messageReceiver: BroadcastReceiver
    lateinit var personalManagerFragment: PersonalManagerFragment

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_personal_manager, container, false
        )
        val view: View = binding!!.root

        viewModel = ViewModelProvider(
            this,
            PersonalManagerViewModelFactory(this.requireActivity().application)
        )[PersonalManagerViewModel::class.java]

        binding.lifecycleOwner = this
        personalManagerFragment = this
        dataBase = SqlDatabase(this.requireActivity())
        viewModel.getHospitalInfo()
        personalManagerAdapter = PersonalManagerAdapter(this)
        initRecyclerView(binding.recyclerInfo)

        isShowCheckBox.observe(viewLifecycleOwner) { newIds ->
            showCheckBox()
        }
        isPersonalManagerBack.observe(viewLifecycleOwner) { newIds ->
            if(isPersonalManagerBack.value!!) {
                viewModel.back(binding, this)
                isPersonalManagerBack.value = false
            }
        }

        // 註冊 BroadcastReceiver
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                    viewModel.updateRecyclerInfo(binding, personalManagerFragment)
            }
        }
        val intentFilter = IntentFilter("updateRecyclerInfo")
        this.requireActivity().registerReceiver(messageReceiver, intentFilter)

        binding.titleBar.binding.imageAdd.setOnClickListener(this)
        binding.titleBar.binding.imageBack.setOnClickListener(this)
//        binding.imageAdd.setOnClickListener(this)
//        binding.imageDelete.setOnClickListener(this)
//        binding.imageBack.setOnClickListener(this)
        return view
    }

    fun showCheckBox() {
        if(isShowCheckBox.value!!){
            binding.recyclerInfo.adapter?.notifyDataSetChanged()
//            binding.imageDelete.visibility = View.VISIBLE
//            binding.imageAdd.visibility = View.INVISIBLE
        }else{
            binding.recyclerInfo.adapter?.notifyDataSetChanged()
//            binding.imageDelete.visibility = View.INVISIBLE
//            binding.imageAdd.visibility = View.VISIBLE
        }
    }

    fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    DividerItemDecoration.VERTICAL
                )
            )

        recyclerView.adapter = personalManagerAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageAdd -> {
                val choose = ImportPopupWindow(this.requireActivity(),this)
                val view: View = LayoutInflater.from(this.requireActivity()).inflate(
                    R.layout.popup_window_import,
                    null
                )
                choose.showAtLocation(view, Gravity.CENTER, 0, 0)
                //強制隱藏鍵盤
                val imm = this.requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this.requireActivity().window.decorView.windowToken, 0)
            }
            R.id.imageBack -> {
                isPersonalManagerBack.value = true
            }
            R.id.imageDelete -> {
                viewModel.deleteRecord(binding, this)
            }
        }
    }

    val importResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val myData: Intent? = result.data
            if (myData != null) {
                CsvToSql().csvToHospitalSql(this.requireActivity(), myData.data!!)
                viewModel.updateRecyclerInfo(binding, this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.requireActivity().unregisterReceiver(messageReceiver)
    }
}
