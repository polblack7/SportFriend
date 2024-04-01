package com.polblack7.sportfriend

class ModelSport {
    var id: String? = ""
    var sport: String? = ""
    var timestamp: Long = 0
    var uid: String? = ""

    constructor() {}

    constructor(id: String?, sport: String?, timestamp: Long, uid: String?) {
        this.id = id
        this.sport = sport
        this.timestamp = timestamp
        this.uid = uid
    }
}