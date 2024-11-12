package com.example.quizapp.model
//
//import com.google.gson.annotations.SerializedName
//
//data class Question(
//    @SerializedName("category") val category: String,
//    @SerializedName("type") val type: String,
//    @SerializedName("difficulty") val difficulty: String,
//    @SerializedName("question") val questionText: String,
//    @SerializedName("correct_answer") val correctAnswer: String,
//    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
//) {
//    val options: List<String>
//        get() = incorrectAnswers + correctAnswer
//}

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("category") val category: String,
    @SerializedName("type") val type: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("question") val questionText: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: arrayListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(type)
        parcel.writeString(difficulty)
        parcel.writeString(questionText)
        parcel.writeString(correctAnswer)
        parcel.writeStringList(incorrectAnswers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }

    val options: List<String>
        get() = incorrectAnswers + correctAnswer
}
