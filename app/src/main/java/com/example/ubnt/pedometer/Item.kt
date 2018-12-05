package com.example.ubnt.pedometer

class Item(name: String,description:String, isUnlocked: Boolean) {

    var name: String
        internal set
    var description: String
            internal set
    var isUnlocked: Boolean
        internal set

    init {
        this.isUnlocked = isUnlocked
        this.name = name
        this.description=description
    }

    override fun toString(): String {
        return name
    }

    override fun equals(obj: Any?): Boolean {
        return name == obj!!.toString()
    }
}