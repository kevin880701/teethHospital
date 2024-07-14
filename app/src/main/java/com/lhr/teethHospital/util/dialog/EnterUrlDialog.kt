package com.lhr.teethHospital.util.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.fragment.app.DialogFragment
import com.lhr.teethHospital.R
import com.lhr.teethHospital.data.PersonalManagerRepository
import com.lhr.teethHospital.databinding.DialogEnterUrlBinding
import com.lhr.teethHospital.util.SharedPreferencesManager.saveText

class EnterUrlDialog(
) : DialogFragment(), View.OnClickListener {

    private var dialog: AlertDialog? = null
    lateinit var repository: PersonalManagerRepository
    private var _binding: DialogEnterUrlBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEnterUrlBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)
        repository = PersonalManagerRepository.getInstance(this.requireContext())

        initView()
        builder.setView(binding.root)
        dialog = builder.create()
        return builder.create()
    }

    private fun initView() {
//        binding.editUrl.text = Editable.Factory.getInstance().newEditable(repository.baseUrl)

        binding.buttonOk.setOnClickListener(this)
        binding.buttonCancel.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonOk -> {
                saveText(this.requireContext(),"URL", binding.editUrl.text.toString())
//                repository.baseUrl = binding.editUrl.text.toString()
                this.dismiss()
            }

            R.id.buttonCancel -> {
                this.dismiss()
            }
        }
    }
}