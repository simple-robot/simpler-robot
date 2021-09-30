rootProject.name = "simply-robot"
include("api")
include("commons:utils")
findProject(":commons:utils")?.name = "utils"
