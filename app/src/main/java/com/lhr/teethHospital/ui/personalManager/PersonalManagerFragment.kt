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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lhr.teethHospital.file.CsvToSql
import com.lhr.teethHospital.R
import com.lhr.teethHospital.data.GroupInfo
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.popupWindow.ImportPopupWindow
import com.lhr.teethHospital.databinding.FragmentPersonalManagerBinding
import com.lhr.teethHospital.model.Model
import com.lhr.teethHospital.room.HospitalEntity
import com.lhr.teethHospital.ui.base.BaseFragment
import com.lhr.teethHospital.ui.memberInformation.MemberInformationActivity
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.isShowCheckBox
import com.lhr.teethHospital.ui.personalManager.PersonalManagerViewModel.Companion.titleBarText
import com.lhr.teethHospital.util.recyclerViewAdapter.GroupAdapter
import com.lhr.teethHospital.util.recyclerViewAdapter.MemberAdapter
import timber.log.Timber

class PersonalManagerFragment : BaseFragment(), View.OnClickListener, GroupAdapter.Listener, MemberAdapter.Listener {

    private val viewModel: PersonalManagerViewModel by viewModels { viewModelFactory }
    lateinit var binding: FragmentPersonalManagerBinding
    lateinit var groupAdapter: GroupAdapter
    lateinit var memberAdapter: MemberAdapter
    lateinit var repository: PersonalManagerRepository

    lateinit var messageReceiver: BroadcastReceiver
    private val callback = object : OnBackPressedCallback(true /* enabled by default */) {
        override fun handleOnBackPressed() {
            onBackButtonPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_personal_manager, container, false
        )
        val view: View = binding!!.root
        repository = PersonalManagerRepository.getInstance(requireContext())

        bindViewModel()
        initView()
        repository.getAllInfo()

        isShowCheckBox.observe(viewLifecycleOwner) { newIds ->
            showCheckBox()
        }

        // 註冊 BroadcastReceiver
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                viewModel.updateRecyclerInfo()
            }
        }
        val intentFilter = IntentFilter("updateRecyclerInfo")
        requireActivity().registerReceiver(messageReceiver, intentFilter)

//        binding.imageDelete.setOnClickListener(this)
        return view
    }

    private fun initView() {
        titleBarText.postValue(getString(R.string.hospital_information))
        initRecyclerView()

        binding.titleBar.binding.imageAdd.setOnClickListener(this)
        binding.titleBar.binding.imageBack.setOnClickListener(this)
    }

    private fun initRecyclerView() {
        groupAdapter = GroupAdapter(this, requireContext())
        memberAdapter = MemberAdapter(this, requireContext())
        binding.recyclerInfo.layoutManager = LinearLayoutManager(activity)
        binding.recyclerInfo.adapter = groupAdapter
    }

    private fun bindViewModel() {
        titleBarText.observe(viewLifecycleOwner) { text ->
            binding.titleBar.binding.textTitle.text = text
            if (text != getString(R.string.hospital_information)) {
                binding.titleBar.setBack(View.VISIBLE)
                memberAdapter.submitList(repository.hospitalInfoList.value?.filter { it.hospitalName == text })
                binding.recyclerInfo.adapter = memberAdapter
            } else {
                binding.titleBar.setBack(View.GONE)
                memberAdapter.submitList(ArrayList())
                binding.recyclerInfo.adapter = groupAdapter
            }
        }

        repository.groupInfoList.observe(viewLifecycleOwner) { list ->
            groupAdapter.submitList(list)
            binding.recyclerInfo.adapter = groupAdapter
        }
    }

    fun showCheckBox() {
        if (isShowCheckBox.value!!) {
            binding.recyclerInfo.adapter?.notifyDataSetChanged()
//            binding.imageDelete.visibility = View.VISIBLE
//            binding.imageAdd.visibility = View.INVISIBLE
        } else {
            binding.recyclerInfo.adapter?.notifyDataSetChanged()
//            binding.imageDelete.visibility = View.INVISIBLE
//            binding.imageAdd.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageAdd -> {
                val choose = ImportPopupWindow(requireActivity(), this)
                val view: View = LayoutInflater.from(requireActivity()).inflate(
                    R.layout.popup_window_import,
                    null
                )
                choose.showAtLocation(view, Gravity.CENTER, 0, 0)
                //強制隱藏鍵盤
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().window.decorView.windowToken, 0)
            }

            R.id.imageBack -> {
                titleBarText.postValue(getString(R.string.hospital_information))
                onBackButtonPressed()
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
                CsvToSql().csvToHospitalSql(requireActivity(), myData.data!!)
                viewModel.updateRecyclerInfo()
            }
        }
    }


    /**
     * 處理返回按鈕的點擊事件。
     * 如果標題欄為R.string.hospital_information，則結束當前Activity。
     * 否則，將標題欄文本設定為醫院信息。
     */
    private fun onBackButtonPressed() {
        if (titleBarText.value == getString(R.string.hospital_information)) {
            requireActivity().finish()
        } else {
            titleBarText.postValue(getString(R.string.hospital_information))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.requireActivity().unregisterReceiver(messageReceiver)
    }


    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onItemClick(groupInfo: GroupInfo) {
        titleBarText.postValue(groupInfo.groupName)
    }

    override fun onItemClick(hospitalEntity: HospitalEntity) {
        val intent = Intent(requireActivity(), MemberInformationActivity::class.java)
        intent.putExtra(Model.ROOT, hospitalEntity)
        requireActivity().startActivity(intent)
    }
}
