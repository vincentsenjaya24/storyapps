package com.example.storyapp.helper

class DateConverter {
    companion object {
        fun year(date: String?): String {
            var year = ""
            if (date != null) {
                val mDate = date.substring(0, 10)
                val list = mDate.split("-")
                year = list[0]
            }
            return year
        }

        fun mouth(date: String?): String {
            var mouth = ""
            if (date != null) {
                val mDate = date.substring(0, 10)
                val list = mDate.split("-")
                mouth = list[1]
            }
            return mouth
        }

        fun day(date: String?): String {
            var day = ""
            if (date != null) {
                val mDate = date.substring(0, 10)
                val list = mDate.split("-")
                day = list[2]
            }
            return day
        }

    }
}