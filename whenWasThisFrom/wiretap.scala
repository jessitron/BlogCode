case class Citizen(var homePhone:String, var name:String)

class Wiretap(target: Citizen) {

    val phone = { target.homePhone }

    def name = target.name
}

val sally = Citizen("314","sally o'malley")

val w = new Wiretap(sally)
