package com.example.cc17favoriteytchannels.models

class Channel (var id: String? = "", var name: String? = "", var link: String? = "", var rank: Int = 0 , var reason: String? = "") : Comparable<Channel>{
    override fun toString(): String {
        return "$rank : $name \n\t : $link \n\t : $reason"
    }
    override fun compareTo(other: Channel): Int {
        return if(this.rank != other.rank){
            this.rank - other.rank
        }else{
            0
        }
    }
}