package com.polblack7.sportfriend.models

class ModelForm {

    var uid: String = ""
    var id: String = ""
    var name: String = ""
    var phone: String = ""
    var location: String = ""
    var description: String = ""
    var sportId: String = ""
    var timestamp: Long = 0
    var viewsCount: Long = 0

    constructor()

    constructor(
        uid: String,
        id: String,
        name: String,
        phone: String,
        location: String,
        description: String,
        sportId: String,
        timestamp: Long,
        viewsCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.name = name
        this.phone = phone
        this.location = location
        this.description = description
        this.sportId = sportId
        this.timestamp = timestamp
        this.viewsCount = viewsCount
    }

}