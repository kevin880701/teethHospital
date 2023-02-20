package com.example.teethHospital.util.Camera

import android.util.Size

class CompareSizesByArea : Comparator<Size?> {
    override fun compare(lhs: Size?, rhs: Size?): Int {
        // We cast here to ensure the multiplications won't overflow
        return java.lang.Long.signum(
            lhs!!.width.toLong() * lhs!!.height -
                    rhs!!.width.toLong() * rhs!!.height
        )
    }
}