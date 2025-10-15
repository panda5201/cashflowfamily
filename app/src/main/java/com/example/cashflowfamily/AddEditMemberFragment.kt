package com.example.cashflowfamily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cashflowfamily.data.MemberRepository
import com.example.cashflowfamily.Member

class AddEditMemberFragment : Fragment() {

    private var existingMember: Member? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_edit_member, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val memberId = arguments?.getLong("memberId", -1L) ?: -1L

        val etName = view.findViewById<EditText>(R.id.et_member_name)
        val etEmail = view.findViewById<EditText>(R.id.et_member_email)
        val rgRole = view.findViewById<RadioGroup>(R.id.rg_member_role)
        val rbAdmin = view.findViewById<RadioButton>(R.id.rb_admin)
        val rbMember = view.findViewById<RadioButton>(R.id.rb_member)
        val btnSave = view.findViewById<Button>(R.id.btn_save_member)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete_member)

        if (memberId != -1L) {
            // Mode Edit
            (activity as AppCompatActivity).supportActionBar?.title = "Edit Anggota"
            existingMember = MemberRepository.getMemberById(memberId)
            existingMember?.let {
                etName.setText(it.name)
                etEmail.setText(it.email)
                if (it.role == "Admin") {
                    rbAdmin.isChecked = true
                } else {
                    rbMember.isChecked = true
                }
            }
            btnDelete.visibility = View.VISIBLE
        } else {
            // Mode Tambah
            (activity as AppCompatActivity).supportActionBar?.title = "Tambah Anggota"
            btnDelete.visibility = View.GONE
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val selectedRoleId = rgRole.checkedRadioButtonId
            val role = if (selectedRoleId == R.id.rb_admin) "Admin" else "Anggota Keluarga"

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Nama dan Email harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val memberToSave = existingMember?.copy(
                name = name,
                email = email,
                role = role
            ) ?: Member(id = 0, name = name, email = email, role = role)

            if (existingMember != null) {
                MemberRepository.updateMember(memberToSave)
                Toast.makeText(requireContext(), "Anggota diperbarui", Toast.LENGTH_SHORT).show()
            } else {
                MemberRepository.addMember(memberToSave)
                Toast.makeText(requireContext(), "Anggota ditambahkan", Toast.LENGTH_SHORT).show()
            }
            findNavController().popBackStack()
        }

        btnDelete.setOnClickListener {
            existingMember?.id?.let {
                MemberRepository.deleteMember(it)
                Toast.makeText(requireContext(), "Anggota dihapus", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
}